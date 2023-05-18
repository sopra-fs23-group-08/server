package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.GamePhase;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class GameStateWsDTO {

    @JsonCreator
    public GameStateWsDTO(@JsonProperty("currentBet") Integer currentBet,
                          @JsonProperty("currentPot") Integer currentPot,
                          @JsonProperty("hasStarted") boolean hasStarted,
                          @JsonProperty("roundWinner") String roundWinnerToken,
                          @JsonProperty("gamePhase") GamePhase gamePhase) {
        this.currentBet = currentBet;
        this.currentPot = currentPot;
        this.hasStarted = hasStarted;
        this.gamePhase = gamePhase;
        this.roundWinnerToken = roundWinnerToken;
    }

    private Integer currentBet;
    private Integer currentPot;
    private boolean hasStarted;
    private GamePhase gamePhase;
    private String roundWinnerToken; 

    public String getRoundWinnerToken() {
        return roundWinnerToken;
    }

    public void setRoundWinnerToken(String roundWinnerToken) {
        this.roundWinnerToken = roundWinnerToken;
    }

    public Integer getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(Integer currentBet) {
        this.currentBet = currentBet;
    }

    public Integer getCurrentPot() {
        return currentPot;
    }

    public void setCurrentPot(Integer currentPot) {
        this.currentPot = currentPot;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
    }
}
