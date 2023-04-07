package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private String lobbyId;
    private List<Player> players;
    private int smallBlindAmount;
    private String videoQuery;

    public Lobby(String lobbyId) {
        this.lobbyId = lobbyId;
        players = new ArrayList<>();
    }

    public void joinPlayer(Player player) {
        players.add(player);
    }

    public void setSmallBlindAmount(int smallBlindAmount) {
        this.smallBlindAmount = smallBlindAmount;
    }

    public void setVideoQuery(String videoQuery) {
        this.videoQuery = videoQuery;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getSmallBlindAmount() {
        return smallBlindAmount;
    }

    public String getVideoQuery() {
        return videoQuery;
    }
}
