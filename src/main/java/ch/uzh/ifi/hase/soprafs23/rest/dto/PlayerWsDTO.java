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
                       @JsonProperty("lastDecision") Decision lastDecision,
                       @JsonProperty("isBigBlind") boolean isBigBlind,
                       @JsonProperty("isSmallBlind") boolean isSmallBlind,
                       @JsonProperty("isCurrentPlayer") boolean isCurrentPlayer) {
        this.token = token;
        this.username = username;
        this.score = Score;
        this.lastDecision = lastDecision;
        this.isBigBlind = isBigBlind;
        this.isSmallBlind = isSmallBlind;
        this.isCurrentPlayer = isCurrentPlayer;
    }

    private String token;
    private String username;
    private Integer score;
    private Decision lastDecision;
    private boolean isBigBlind;
    private boolean isSmallBlind;
    private boolean isCurrentPlayer;
    private Integer scorePutIntoPot;


    public Integer getScorePutIntoPot() {
        return scorePutIntoPot;
    }

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

    public boolean isCurrentPlayer() {
        return isCurrentPlayer;
    }

    public void setCurrentPlayer(boolean currentPlayer) {
        isCurrentPlayer = currentPlayer;
    }

    public boolean isSmallBlind() {
        return isSmallBlind;
    }

    public void setSmallBlind(boolean smallBlind) {
        isSmallBlind = smallBlind;
    }

    public boolean isBigBlind() {
        return isBigBlind;
    }

    public void setBigBlind(boolean bigBlind) {
        isBigBlind = bigBlind;
    }

    public void setScorePlayerPutIntoPot(Integer scorePutIntoPot) {
        this.scorePutIntoPot = scorePutIntoPot;
    }
}
