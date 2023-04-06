package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.UUID;

public class Player {
    @Override
    public String toString() {
        return "Player "+ name +"(id=" + id + ")";
    }

    public final String id;
    public final String name;
    public final boolean loggedIn;

    public Player(String aName, boolean aLoggedIn){
        id = UUID.randomUUID().toString();
        name = aName;
        loggedIn = aLoggedIn;
    }

}
