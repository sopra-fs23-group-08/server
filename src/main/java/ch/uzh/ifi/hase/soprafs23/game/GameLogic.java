package ch.uzh.ifi.hase.soprafs23.game;

import java.io.IOException;
import java.util.Random;

import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

//todo testing :)

class GameLogic {
    
    private Random rand = new Random();
    private GameModel gm;
    private Setup sd;

    GameLogic(GameModel gm, Setup sd) {
        this.gm = gm;
        this.sd = sd;
    }

    void startGame() throws IOException, InterruptedException, Exception {//Creating playerData and stuff
        
        Pair<VideoData, java.util.List<Hand>> ytData = sd.getYTData();

        gm.setVideoData(ytData.getFirst());

        for (Pair<Player, Integer> pair : sd.getPlayers()) {
            var playerData = new PlayerData(pair.getFirst());
            gm.addPlayerData(playerData);

            playerData.setScore(pair.getSecond());
            playerData.setDecision(Decision.NOT_DECIDED);

            Hand hand = ytData.getSecond().get(rand.nextInt(ytData.getSecond().size()));
            ytData.getSecond().remove(hand);
            playerData.setNewHand(hand);
        }

        gm.resetTable();
        gm.setDealerPlayer(); //random dealer if not set before
    }
    
    void startRound() throws IOException, InterruptedException, Exception {
        Pair<VideoData, java.util.List<Hand>> ytData = sd.getYTData();

        gm.setVideoData(ytData.getFirst());
        gm.setFoldCount(0);

        for (PlayerData playerData : gm.getPlayerDataCollection()) {
            playerData.setDecision(Decision.NOT_DECIDED);
            if (playerData.getScore() < sd.getBigBlindAmount()) {//player is not allowed to play since he has not enough points
                leaveGame(playerData);
            }
            Hand hand = ytData.getSecond().get(rand.nextInt(ytData.getSecond().size()));
            ytData.getSecond().remove(hand);
            playerData.setNewHand(hand);
            playerData.setScorePutIntoPot(0);
        }

        gm.resetRound();
        gm.nextDealer(); //random dealer if not set before
    }

    void startBettingRound() {
        for (PlayerData pd : gm.getPlayerDataCollection()) {
            if (pd.getDecision() != Decision.FOLD) {
                pd.setDecision(Decision.NOT_DECIDED);
            }
        }
        gm.resetBettingRound();
    }

    void playerDecision(Player player, Decision d) throws Exception {
        playerDecision(player, d, gm.getCallAmount());
    }
    void playerDecision(Player player, Decision d, Integer newCallAmount) throws Exception {
        if (player != gm.getCurrentPlayer()) {
            throw new Exception("You're not the current player");
        }
        if (gm.getPlayerData(player).getDecision() == Decision.FOLD) {
            throw new Exception("After you folded you can't do anything until next game");
        }
        enforceBigAndSmallBlind(player, newCallAmount);

        switch (d) {
            case CALL:
                addToPot(player);
                break;
            case FOLD:
                gm.setFoldCount(gm.getFoldCount() + 1);
                gm.getPlayerData(player).setDecision(Decision.FOLD);
                break;

            case RAISE:
                var playerData = gm.getPlayerData(player);
                if (playerData.getScore() < newCallAmount) {
                    throw new Exception(
                            "Player score(" + playerData.getScore() + ") is not high enough to raise(" + newCallAmount
                                    + ").");
                }else if (gm.getCallAmount() > newCallAmount){
                    throw new Exception("The CallAmount must be higher after a raise. CallAmountBefore: " + gm.getCallAmount() + " NewCallAmount: " + newCallAmount);
                }
                gm.setCallAmount(newCallAmount);
                addToPot(player);
                gm.setLastRaisingPlayer(player);

                break;
            default:
                throw new Exception("Illegal decision " + d);
        }
        gm.getPlayerData(player).setDecision(d);
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
        for (PlayerData pd : gm.getPlayerDataCollection()) {
            if (pd.getDecision() != Decision.FOLD) {
                if (winner != null) {
                    throw new Exception("There can not be two winner");
                }
                winner = pd.getPlayer();
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
        for (PlayerData pd : gm.getPlayerDataCollection()) {
            if (pd.getDecision() != Decision.FOLD && pd.getHand().getCountCorrect() > maxCorrect) {
                maxCorrect = pd.getHand().getCountCorrect();
                winner = pd.getPlayer();
            }
        }

        if (winner == null) {
            throw new Exception("There must be a winner");
        }

        gm.setGamePhase(GamePhase.END_AFTER_FOURTH_BETTING_ROUND);
        gm.setWinner(winner);
    }
    
    void endOfBettingRound() throws Exception {
        if (gm.getGamePhase() == GamePhase.FOURTH_BETTING_ROUND) {
            evaluateWinner();
        } else {
            gm.setGamePhase(gm.getGamePhase().nextPhase());
            startBettingRound();
        }
    }
    

    void addToPot(Player player) throws Exception {
        

        var playerData = gm.getPlayerData(player);
        if (playerData.getScore() < gm.getCallAmount()) {
            throw new Exception(
                    player + " not enough score(" + playerData.getScore() + ") to call(" + gm.getCallAmount() + ")");
        }

        playerData.setScore(playerData.getScore() - (gm.getCallAmount() - playerData.getScorePutIntoPot()));
        gm.setPotAmount(gm.getPotAmount() + (gm.getCallAmount() - playerData.getScorePutIntoPot()));
        playerData.setScorePutIntoPot(gm.getCallAmount());
    }

    boolean isSmallBlind(Player player) {
        return (gm.getSmallBlindPlayer() == player);
    }

    boolean isBigBlind(Player player) {
        return (gm.getBigBlindPlayer() == player);
    }

    void enforceBigAndSmallBlind(Player player, Integer newCallAmount) throws Exception {
        if (isBigBlind(player) && newCallAmount < sd.getBigBlindAmount() && gm.getGamePhase() == GamePhase.FIRST_BETTING_ROUND) {
            throw new Exception("BigBlind must raise. currentCallAmount: " + newCallAmount + " BigBlindAmount: "
                    + sd.getBigBlindAmount());
        } else if (isSmallBlind(player) && newCallAmount < sd.getSmallBlindAmount() && gm.getGamePhase() == GamePhase.FIRST_BETTING_ROUND) {
            throw new Exception("SmallBlind must raise. currentCallAmount: " + newCallAmount
                    + " SmallBlindAmount: " + sd.getSmallBlindAmount());
        }
    }

    boolean isBettingRoundOver() {
        if (gm.getCurrentPlayer().getToken() == gm.getLastRaisingPlayer().getToken()) {
            return true;
        }
        return false;
    }

    boolean allFoldedButOne() {
        return gm.getFoldCount() >= gm.getPlayerDataCollection().size() - 1;
    }
    
    void leaveGame(PlayerData playerData) {
        switch (gm.getGamePhase()) {
            case LOBBY:
            case END_AFTER_FOURTH_BETTING_ROUND:
            case END_ALL_FOLDED:
                gm.removePlayerData(playerData);
                break;
        
            default:
                gm.setPotAmount(gm.getPotAmount() + playerData.getScore());
                playerData.setDecision(Decision.FOLD);
                gm.setFoldCount(gm.getFoldCount() + 1);
                gm.removePlayerData(playerData);
                break;
        }
    }
}