package ch.uzh.ifi.hase.soprafs23.entity;

public class Player {
    public final Long id;
    public final String name;
    public final boolean loggedIn;

    Player(Long aId, String aName, boolean aLoggedIn){
        id = aId;
        name = aName;
        loggedIn = aLoggedIn;
    }
}
