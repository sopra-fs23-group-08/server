package ch.uzh.ifi.hase.soprafs23.game;

import java.util.List;
import java.util.UUID;

import org.h2.command.dml.Set;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

class GameModel { //protected (Package Private)
    public final String gameId;
    private List<PlayerData> players;
    private VideoData videoData;
    private GamePhase gamePhase;
    private PlayerData currentPlayer;
    private int Pot;
    private int callAmount;
    private SetupData setupData;

    private List<GameObserver> observers;

    public GameModel() {
        gameId = UUID.randomUUID().toString();
    }

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
            //todo wait for rola
        }
        this.videoData = videoData;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        for (GameObserver o : observers) {
            //todo wait for rola
        }
        this.gamePhase = gamePhase;
    }

    public PlayerData getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerData currentPlayer) {
        for (GameObserver o : observers) {
            //todo wait for rola
        }
        this.currentPlayer = currentPlayer;
    }


    public int getPot() {
        return Pot;
    }

    public void setPot(int pot) {
        for (GameObserver o : observers) {
            //todo wait for rola
        }
        Pot = pot;
    }

    public int getCallAmount() {
        return callAmount;
    }

    public void setCallAmount(int callAmount) {
        for (GameObserver o : observers) {
            //todo wait for rola
        }
        this.callAmount = callAmount;
    }

    public SetupData getSetupData() {
        return setupData;
    }

    public void setSetupData(SetupData setupData) {
        this.setupData = setupData;
    }
}
