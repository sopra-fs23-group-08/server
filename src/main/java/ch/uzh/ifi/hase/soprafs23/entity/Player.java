package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.UUID;

public class Player {

    private String token;

    private String name;

    public Player(String name, String token) {
        this.token = token;
        this.name = name;
    }

    public Player(String name) {
        this.token = UUID.randomUUID().toString();
        this.name = name;
    }

    public Player() {
        this.token = null;
        this.name = null;
    }
    @Override
    public String toString() {
        return "Player "+ name +"(token=" + token + ")";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
