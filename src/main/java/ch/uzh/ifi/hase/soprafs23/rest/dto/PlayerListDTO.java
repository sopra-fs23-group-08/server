package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.controller.TestPlayer;

import java.util.ArrayList;

public class PlayerListDTO {

    private ArrayList<TestPlayer> players;

    public PlayerListDTO(ArrayList<TestPlayer> players) {
        this.players = players;
    }

    public ArrayList<TestPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<TestPlayer> players) {
        this.players = players;
    }
}
