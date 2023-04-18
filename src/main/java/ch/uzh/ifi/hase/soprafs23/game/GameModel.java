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
    private int callAmount;
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
        setFoldCount(0);
        setWinner(null);
    }

    public void resetRound() {
        setGamePhase(GamePhase.FIRST_BETTING_ROUND);
        setFoldCount(0);
        setWinner(null);
    }

    public void resetBettingRound() {
        setCurrentPlayer(dealerPlayer);
        nextPlayer();
        setLastRaisingPlayer(currentPlayer);
        setCallAmount(0);
    }
    
    //observer stuff------------------------------
    public void addObserver(GameObserver o) {
        observers.add(o);
        for (PlayerData p : playersData.values()) {
            p.addObserver(o);
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
        playersData.put(p.id ,p);
        playerOrder.add(p.getPlayer());
        for (GameObserver o : observers) {
            p.addObserver(o);
        }
    }
    
    public void removePlayerData(PlayerData p) {
        playersData.remove(p.id);
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
        while (Decision.FOLD == playersData.get(currentPlayer.id).getDecision()) {
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
            o.newPlayerBigBlindNSmallBlind(smallBlindPlayer, bigBlindPlayer);
        }
        this.dealerPlayer = dealer;
    }

    private void resetSmallBigBlind() {
        smallBlindPlayer = null;
        bigBlindPlayer = null;
        for (GameObserver o : observers) {
            o.newPlayerBigBlindNSmallBlind(smallBlindPlayer, bigBlindPlayer);
        }
    }


    public String getGameId() {
        return gameId;
    }

    public Collection<PlayerData> getPlayerDataCollection() {
        return playersData.values();
    }

    public PlayerData getPlayerData(Player p) {
        return playersData.get(p.id);
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
            o.gamePhaseChange(gamePhase);
            try {
                o.newVideoData(videoData.getPartialVideoData(gamePhase.getVal()));
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
            o.currentPlayerChange(currentPlayer);
        }
        this.currentPlayer = currentPlayer;
    }


    public int getPotAmount() {
        return potAmount;
    }

    public void setPotAmount(int pot) {
        for (GameObserver o : observers) {
            o.potScoreChange(pot);
        }
        potAmount = pot;
    }

    public int getCallAmount() {
        return callAmount;
    }

    public void setCallAmount(int callAmount) {
        for (GameObserver o : observers) {
            o.callAmountChanged(callAmount);
        }
        this.callAmount = callAmount;
    }

    public List<GameObserver> getObservers() {
        return observers;
    }

    public Player getLastRaisingPlayer() {
        return lastRaisingPlayer;
    }

    public void setLastRaisingPlayer(Player lastRaisingPlayer) {
        this.lastRaisingPlayer = lastRaisingPlayer;
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
        for (GameObserver o : observers) {
            o.roundWinnerIs(winner);
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
