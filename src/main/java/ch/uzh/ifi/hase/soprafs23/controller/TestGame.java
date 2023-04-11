package ch.uzh.ifi.hase.soprafs23.controller;

import java.util.ArrayList;
import java.util.UUID;

public class TestGame {

    private TestPlayer host;
    private String id;

    private ArrayList<TestPlayer> players = new ArrayList<TestPlayer>();

    public TestGame() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void addPlayer(TestPlayer player) {
        players.add(player);
    }

    public ArrayList<TestPlayer> getPlayers() {
        return players;
    }

    public TestPlayer getHost() {
        return host;
    }

    public void setHost(TestPlayer host) {
        this.host = host;
    }
}
