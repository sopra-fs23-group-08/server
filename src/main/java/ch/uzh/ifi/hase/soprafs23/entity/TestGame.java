package ch.uzh.ifi.hase.soprafs23.entity;

import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.UUID;

public class TestGame {
    @Column
    private String id;

    @OneToMany(mappedBy = "currentGame")
    private ArrayList<TestPlayer> players;

    @ManyToOne
    private TestPlayer host;

    //TODO add all settings as fields: initial balance, small&big blind etc.
    //TODO add field indicating if game is on lobby/started
    public TestGame() {
        this.id = UUID.randomUUID().toString();
        this.players = new ArrayList<TestPlayer>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<TestPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<TestPlayer> players) {
        this.players = players;
    }

    public TestPlayer getHost() {
        return host;
    }

    public void setHost(TestPlayer host) {
        this.host = host;
    }


}
