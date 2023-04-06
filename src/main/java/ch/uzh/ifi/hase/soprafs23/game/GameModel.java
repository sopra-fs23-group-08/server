package ch.uzh.ifi.hase.soprafs23.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import ch.uzh.ifi.hase.soprafs23.entity.Player;


class GameModel { //protected (Package Private)
    public final String gameId;
    private final List<PlayerData> players; //mutable but address can not be changed
    private VideoData videoData;
    private GamePhase gamePhase;
    private PlayerData currentPlayer;
    private PlayerData dealer;
    private PlayerData smallBlind; //automatically set when dealer is set
    private PlayerData bigBlind; //automatically set when dealer is set
    private int Pot;
    private int callAmount;
    private SetupData setupData;
    private Player lastRaisingPlayer;
    private int foldCount;
    private Player winner;

    private List<GameObserver> observers;

    public GameModel() {
        gameId = UUID.randomUUID().toString();
        gamePhase = GamePhase.WAITING_FOR_PLAYERS;
        setupData = new SetupData();
        players = new ArrayList<>();
        observers = new ArrayList<>();
    }
    
    //observer stuff------------------------------
    public void addObserver(GameObserver o) {
        observers.add(o);
        for (PlayerData p : players) {
            p.addObserver(o);
        }
    }

    public void removeObserver(GameObserver o) {
        observers.remove(o);
        for (PlayerData p : players) {
            p.removeObserver(o);
        }
    }

    //player stuff-------------------------------
    public void addPlayer(PlayerData p) {
        players.add(p);
        for (GameObserver o : observers) {
            p.addObserver(o);
        }
    }
    
    public void removePlayer(PlayerData p) {
        players.add(p);
        for (GameObserver o : observers) {
            p.removeObserver(o);
        }
    }

    //other getters and setters------------------------------
    public PlayerData getSmallBlind() {
        return smallBlind;
    }

    public PlayerData getBigBlind() {
        return bigBlind;
    }
    
    public PlayerData getDealer() {
        return dealer;
    }

    public void nextDealer() {
        setDealer(players.get((players.indexOf(dealer) + 1) % players.size()));
    }

    public void nextPlayer() {
        setCurrentPlayer(players.get((players.indexOf(currentPlayer)+1) % players.size()));
        while (currentPlayer.getDecision() == Decision.FOLD) {
            setCurrentPlayer(players.get((players.indexOf(currentPlayer)+1) % players.size()));
        }
    }

    public void setDealer(PlayerData dealer) {
        var indexDealer = players.indexOf(dealer);
        smallBlind = players.get((indexDealer + 1) % players.size());
        bigBlind = players.get((indexDealer + 2) % players.size());
        for (GameObserver o : observers) {
            o.newPlayerBigBlindNSmallBlind(smallBlind.getPlayer(), bigBlind.getPlayer());
        }
        this.dealer = dealer;
    }


    public String getGameId() {
        return gameId;
    }

    public List<PlayerData> getPlayers() {
        return players;
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
        }
        this.gamePhase = gamePhase;
    }

    public PlayerData getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerData currentPlayer) {
        for (GameObserver o : observers) {
            o.currentPlayerChange(currentPlayer.getPlayer());
        }
        this.currentPlayer = currentPlayer;
    }


    public int getPot() {
        return Pot;
    }

    public void setPot(int pot) {
        for (GameObserver o : observers) {
            o.potScoreChange(pot);
        }
        Pot = pot;
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

    public SetupData getSetupData() {
        return setupData;
    }

    public void setSetupData(SetupData setupData) {
        this.setupData = setupData;
    }

    public List<GameObserver> getObservers() {
        return observers;
    }

    public PlayerData getPlayer(Player p) throws Exception {
        for (PlayerData playerData : players) {
            if (playerData.getPlayer().id == p.id) {
                return playerData;
            }
        }
        throw new Exception(p + "not found");
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
            o.winnerIs(winner);
        }
        this.winner = winner;
    }
}
