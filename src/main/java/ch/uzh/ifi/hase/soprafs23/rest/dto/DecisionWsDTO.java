package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.Decision;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


/** has no corresponding entity --> no mapper function */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class DecisionWsDTO {

    @JsonCreator
    public DecisionWsDTO(@JsonProperty("decision") Decision decision) {
        this.decision = decision;
    }

    @JsonProperty("playerToken")
    private Decision decision;

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }
}
