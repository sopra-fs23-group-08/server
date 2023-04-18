package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.UUID;

public class Player {
    @Override
    public String toString() {
        return "Player "+ name +"(id=" + id + ")";
    }

    public final String id;
    public final String name;

    public Player(String aName, String aId) {
        id = aId;
        name = aName;
    }

    public Player(String aName) {
        id = UUID.randomUUID().toString();
        name = aName;
    }

    public Player() {
        id = null;
        name = null;
    }

}
