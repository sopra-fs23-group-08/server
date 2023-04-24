package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.Decision;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class PlayerWsDTO {

    @JsonCreator
    public PlayerWsDTO(@JsonProperty("token") String token,
                       @JsonProperty("username") String username,
                       @JsonProperty("score") Integer Score,
                       @JsonProperty("lastDecision") Decision lastDecision) {
        this.token = token;
        this.username = username;
        this.score = Score;
        this.lastDecision = lastDecision;
    }

    private String token;
    private String username;
    private Integer score;
    private Decision lastDecision;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Decision getLastDecision() {
        return lastDecision;
    }

    public void setLastDecision(Decision lastDecision) {
        this.lastDecision = lastDecision;
    }
}
