package ch.uzh.ifi.hase.soprafs23.controller;

import java.util.ArrayList;
import java.util.UUID;

public class TestGame {
    private ArrayList<TestPlayer> players = new ArrayList<TestPlayer>();
    private String gameId;

    public TestGame(TestPlayer host) {
        players.add(host);
        this.gameId = UUID.randomUUID().toString();
    }

    public String getGameId() {
        return gameId;
    }

    public ArrayList<TestPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(TestPlayer player) {
        players.add(player);
    }
}
