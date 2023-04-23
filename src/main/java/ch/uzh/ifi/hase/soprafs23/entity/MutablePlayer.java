package ch.uzh.ifi.hase.soprafs23.entity;
import java.util.UUID;

public class MutablePlayer {

    private String token;

    private String name;

    public MutablePlayer(String name, String token) {
        this.token = token;
        this.name = name;
    }

    public MutablePlayer(String name) {
        this.token = UUID.randomUUID().toString();
        this.name = name;
    }

    public MutablePlayer() {
        this.token = null;
        this.name = null;
    }

    public MutablePlayer(Player p) {
        this.token = p.getToken();
        this.name = p.getName();
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
