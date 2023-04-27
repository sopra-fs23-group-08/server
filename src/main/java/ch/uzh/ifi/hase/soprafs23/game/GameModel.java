package ch.uzh.ifi.hase.soprafs23.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import ch.uzh.ifi.hase.soprafs23.entity.Player;


class GameModel { //protected (Package Private)
    public final String gameId;
    private final Map<String, PlayerData> playersData; //mutable but address cannot be changed
    private final List<Player> playerOrder;
    private Random rand;
    private VideoData videoData;
    private GamePhase gamePhase;
    private Player currentPlayer;
    private Player dealerPlayer;
    private Player smallBlindPlayer; //automatically set when dealer is set
    private Player bigBlindPlayer; //automatically set when dealer is set
    private int potAmount;
    private int callAmount = 0;
    private Player lastRaisingPlayer;
    private int foldCount;
    private Player winner;
    private Player host;

    private List<GameObserver> observers;

    public GameModel() {
        gameId = UUID.randomUUID().toString(); //generates a unique identifier
        gamePhase = GamePhase.LOBBY;
        playersData = new HashMap<>();
        observers = new ArrayList<>();
        playerOrder = new ArrayList<>();
        rand = new Random();
    }

    public void resetTable() {//call before playing, players stay
        resetSmallBigBlind();
        setCurrentPlayer(new Player());
        setGamePhase(GamePhase.FIRST_BETTING_ROUND);
        setPotAmount(0);
        setWinner(null);
        setCallAmount(0);
    }

    public void resetRound() {
        setGamePhase(GamePhase.FIRST_BETTING_ROUND);
        setWinner(null);
        setCallAmount(0);
    }

    public void resetBettingRound() {
        setCurrentPlayer(dealerPlayer);
        nextPlayer();
        setLastRaisingPlayer(currentPlayer);
    }
    
    //observer stuff------------------------------
    public void addObserver(GameObserver o) {
        observers.add(o);
        for (PlayerData p : playersData.values()) {
            p.addObserver(gameId, o);
        }
    }

    public void removeObserver(GameObserver o) {
        observers.remove(o);
        for (PlayerData p : playersData.values()) {
            p.removeObserver(o);
        }
    }

    //player stuff-------------------------------
    public void addPlayerData(PlayerData p) {
        playersData.put(p.token ,p);
        playerOrder.add(p.getPlayer());
        for (GameObserver o : observers) {
            p.addObserver(gameId, o);
        }
    }
    
    public void removePlayerData(PlayerData p) {
        playersData.remove(p.token);
        playerOrder.remove(p.getPlayer());
        for (GameObserver o : observers) {
            p.removeObserver(o);
        }
    }

    //other getters and setters------------------------------
    public Player getSmallBlindPlayer() {
        return smallBlindPlayer;
    }

    public Player getBigBlindPlayer() {
        return bigBlindPlayer;
    }
    
    public Player getDealerPlayer() {
        return dealerPlayer;
    }

    public void nextDealer() {
        setDealerPlayer(playerOrder.get((playerOrder.indexOf(dealerPlayer) + 1) % playerOrder.size()));
    }

    public void nextPlayer() {
        setCurrentPlayer(playerOrder.get((playerOrder.indexOf(currentPlayer) + 1) % playerOrder.size()));
        while (Decision.FOLD == playersData.get(currentPlayer.getToken()).getDecision()) {
            setCurrentPlayer(playerOrder.get((playerOrder.indexOf(currentPlayer) + 1) % playerOrder.size()));
        }
    }
    
    public void setDealerPlayer() {
        if (dealerPlayer == null) {
            setDealerPlayer(playerOrder.get(rand.nextInt(playerOrder.size())));
        } else {
            setDealerPlayer(dealerPlayer);
        }
    }

    public void setDealerPlayer(Player dealer) {
        var indexDealer = playerOrder.indexOf(dealer);
        smallBlindPlayer = playerOrder.get((indexDealer + 1) % playerOrder.size());
        bigBlindPlayer = playerOrder.get((indexDealer + 2) % playerOrder.size());
        for (GameObserver o : observers) {
            o.newPlayerBigBlindNSmallBlind(gameId, smallBlindPlayer, bigBlindPlayer);
        }
        this.dealerPlayer = dealer;
    }

    private void resetSmallBigBlind() {
        smallBlindPlayer = null;
        bigBlindPlayer = null;
        for (GameObserver o : observers) {
            o.newPlayerBigBlindNSmallBlind(gameId, smallBlindPlayer, bigBlindPlayer);
        }
    }

    public List<HandOwnerWinner> getHands() throws Exception {
        var l = new ArrayList<HandOwnerWinner>();
        for (PlayerData pd : playersData.values()) {
            var how = new HandOwnerWinner();
            how.setHand(pd.getHand());
            how.setPlayer(pd.getPlayer());
            how.setIsWinner(false);
            if (how.getPlayer() == winner) {
                how.setIsWinner(true);
            }
            l.add(how);
        }
        if (winner == null) {
            throw new Exception("There is currently no Winner in Game: " + gameId);
        }
        return l;
    }


    public String getGameId() {
        return gameId;
    }

    public Collection<PlayerData> getPlayerDataCollection() {
        return new ArrayList<PlayerData>(playersData.values());
    }

    public PlayerData getPlayerData(Player player) {
        return playersData.get(player.getToken());
    }

    public VideoData getVideoData() {
        return videoData;
    }

    public void setVideoData(VideoData videoData) {
        for (GameObserver o : observers) {
            //not observed ?? good design doubt it :) 
        }
        this.videoData = videoData;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        for (GameObserver o : observers) {
            o.gamePhaseChange(gameId, gamePhase);
            try {
                o.newVideoData(gameId, videoData.getPartialVideoData(gamePhase.getVal()));
            } catch (Exception e) {
                System.out.println("Sending video Data did not work: " + e);
            }
        }
        this.gamePhase = gamePhase;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        for (GameObserver o : observers) {
            o.currentPlayerChange(gameId, currentPlayer);
        }
        this.currentPlayer = currentPlayer;
    }


    public int getPotAmount() {
        return potAmount;
    }

    public void setPotAmount(int pot) {
        for (GameObserver o : observers) {
            o.potScoreChange(gameId, pot);
        }
        potAmount = pot;
    }

    public int getCallAmount() {
        return callAmount;
    }

    public void setCallAmount(int callAmount) {
        for (GameObserver o : observers) {
            o.callAmountChanged(gameId, callAmount);
        }
        this.callAmount = callAmount;
    }

    public List<GameObserver> getObservers() {
        return observers;
    }

    public Player getLastRaisingPlayer() {
        return lastRaisingPlayer;
    }

    public void setLastRaisingPlayer(Player player) {
        this.lastRaisingPlayer = player;
    }

    public int getFoldCount() {
        return foldCount;
    }

    public void setFoldCount(int foldCount) {
        this.foldCount = foldCount;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        //update points
        if (winner != null) {
            int score = getPlayerData(winner).getScore();
            getPlayerData(winner).setScore(score + potAmount);
            setPotAmount(0);
        }
        //declare winner
        for (GameObserver o : observers) {
            o.roundWinnerIs(gameId, winner);
        }
        this.winner = winner;
    }

    public Player getHost() {
        return host;
    }

    public void setHost(Player host) {
        this.host = host;
    }

}
