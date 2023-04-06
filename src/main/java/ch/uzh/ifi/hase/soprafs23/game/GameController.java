package ch.uzh.ifi.hase.soprafs23.game;

import java.io.IOException;
import java.util.Currency;
import java.util.Random;

import org.springframework.data.util.Pair;

import com.fasterxml.jackson.databind.ser.std.RawSerializer;

import java.util.List;
import ch.uzh.ifi.hase.soprafs23.YTAPIManager.YTAPIManager;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

//todo testing :)

class GameController {
    
    private Random rand = new Random();
    private GameModel gm;

    GameController(GameModel gm) {
        this.gm = gm;
    }

    void startGame() throws IOException, InterruptedException, Exception {//Creating playerData and stuff
        var setupData = gm.getSetupData();

        Pair<VideoData, java.util.List<Hand>> ytData = gm.getSetupData().getYTData();

        gm.setVideoData(ytData.getFirst());

        for (Pair<Player, Integer> pair : setupData.getPlayers()) {
            var playerData = new PlayerData(pair.getFirst());
            gm.addPlayer(playerData);

            playerData.setScore(pair.getSecond());
            playerData.setDecision(Decision.NOT_DECIDED);

            Hand hand = ytData.getSecond().get(rand.nextInt(ytData.getSecond().size()));
            ytData.getSecond().remove(hand);
            playerData.setNewHand(hand);
        }

        gm.setDealer(gm.getPlayers().get(rand.nextInt(gm.getPlayers().size())));
        gm.setCurrentPlayer(new PlayerData());
        gm.setGamePhase(GamePhase.FIRST_ROUND);
        gm.setPot(0);
        gm.setCallAmount(0);
        gm.setFoldCount(0);
        gm.setWinner(null);
    }
    
    void startBettingRound() {
        for (PlayerData p : gm.getPlayers()) {
            if (p.getDecision() != Decision.FOLD) {
                p.setDecision(Decision.NOT_DECIDED);
            }
        }
        // gm.nextDealer();
        gm.setCurrentPlayer(gm.getDealer());
        gm.nextPlayer();
        gm.setLastRaisingPlayer(gm.getCurrentPlayer().getPlayer());
        gm.setCallAmount(0);
    }

    void playerDecision(Player p, Decision d) throws Exception {
        playerDecision(p, d, null);
    }
    void playerDecision(Player p, Decision d, Integer newCallAmount) throws Exception {
        if (p != gm.getCurrentPlayer().getPlayer()) {
            throw new Exception("You're not the current player");
        }
        if (gm.getPlayer(p).getDecision() == Decision.FOLD) {
            throw new Exception("After you folded you can't do anything until next game");
        }
        switch (d) {
            case CALL:
                addToPot(p);
                break;
            case FOLD:
                gm.setFoldCount(gm.getFoldCount() + 1);
                gm.getPlayer(p).setDecision(Decision.FOLD);
                break;

            case RAISE:
                var playerData = gm.getPlayer(p);
                if (playerData.getScore() < newCallAmount) {
                    throw new Exception(
                            "Player score(" + playerData.getScore() + ") is not high enough to raise(" + newCallAmount
                                    + ").");
                }else if (gm.getCallAmount() > newCallAmount){
                    throw new Exception("The CallAmount must be higher after a raise. CallAmountBefore: " + gm.getCallAmount() + " NewCallAmount: " + newCallAmount);
                }
                gm.setCallAmount(newCallAmount);
                addToPot(p);
                gm.setLastRaisingPlayer(p);

                break;
            default:
                throw new Exception("Illegal decision " + d);
        }
        gm.getPlayer(p).setDecision(d);
        gm.nextPlayer();
        
        if (allFoldedButOne()) {
            winOtherFolded();
        }else if (isBettingRoundOver()) {
            endOfBettingRound();
        }
    }

    void winOtherFolded() throws Exception {
        gm.setGamePhase(GamePhase.END_ALL_FOLDED);
        Player winner = null;
        for (PlayerData p : gm.getPlayers()) {
            if (p.getDecision() != Decision.FOLD) {
                if (winner != null) {
                    throw new Exception("There can not be two winner");
                }
                winner = p.getPlayer();
            }
        }

        if (winner == null) {
            throw new Exception("There must be a winner");
        }

        gm.setWinner(winner);

    }
    
    void evaluateWinner() throws Exception {
        gm.setGamePhase(GamePhase.END_ALL_FOLDED);
        Player winner = null;
        int maxCorrect = -1;
        for (PlayerData p : gm.getPlayers()) {
            if (p.getDecision() != Decision.FOLD && p.getHand().getCountCorrect() > maxCorrect) {
                maxCorrect = p.getHand().getCountCorrect();
                winner = p.getPlayer();
            }
        }

        if (winner == null) {
            throw new Exception("There must be a winner");
        }

        gm.setGamePhase(GamePhase.END_AFTER_FOURTH_ROUND);
        gm.setWinner(winner);
    }
    
    void endOfBettingRound() throws Exception {
        if (gm.getGamePhase() == GamePhase.FOURTH_ROUND) {
            evaluateWinner();
        } else {
            gm.setGamePhase(gm.getGamePhase().nextPhase());
            startBettingRound();
        }
    }
    

    void addToPot(Player p) throws Exception {
        enforceBigAndSmallBlind(p);

        var playerData = gm.getPlayer(p);
        if (playerData.getScore() < gm.getCallAmount()) {
            throw new Exception(
                    p + " not enough score(" + playerData.getScore() + ") to call(" + gm.getCallAmount() + ")");
        }
        playerData.setScore(playerData.getScore() - gm.getCallAmount());
        gm.setPot(gm.getPot() + gm.getCallAmount());
    }

    boolean isSmallBlind(Player p) {
        return (gm.getSmallBlind().getPlayer().id == p.id);
    }

    boolean isBigBlind(Player p) {
        return (gm.getBigBlind().getPlayer().id == p.id);
    }

    void enforceBigAndSmallBlind(Player p) throws Exception {
        if (isBigBlind(p) && gm.getCallAmount() < gm.getSetupData().getBigBlind() && gm.getGamePhase() == GamePhase.FIRST_ROUND) {
            throw new Exception("BigBlind must raise. currentCallAmount: " + gm.getCallAmount() + " BigBlindAmount: "
                    + gm.getSetupData().getBigBlind());
        } else if (isSmallBlind(p) && gm.getCallAmount() < gm.getSetupData().getSmallBlind() && gm.getGamePhase() == GamePhase.FIRST_ROUND) {
            throw new Exception("SmallBlind must raise. currentCallAmount: " + gm.getCallAmount()
                    + " SmallBlindAmount: " + gm.getSetupData().getSmallBlind());
        }
    }

    boolean isBettingRoundOver() {
        if (gm.getCurrentPlayer().getPlayer().id == gm.getLastRaisingPlayer().id) {
            return true;
        }
        return false;
    }

    boolean allFoldedButOne() {
        return gm.getFoldCount() >= gm.getPlayers().size()-1;
    }
}
