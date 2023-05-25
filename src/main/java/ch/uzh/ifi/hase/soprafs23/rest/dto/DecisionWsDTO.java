package ch.uzh.ifi.hase.soprafs23.rest.dto;


/** has no corresponding entity --> no mapper function */
public class DecisionWsDTO {
    private String decision;
    private Integer raiseAmount;

    public DecisionWsDTO(){}
    public DecisionWsDTO(String decision, Integer raiseAmount) {
        this.decision = decision;
        this.raiseAmount = raiseAmount;
    }
    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public Integer getRaiseAmount() {
        return raiseAmount;
    }

    public void setRaiseAmount(Integer raiseAmount) {
        this.raiseAmount = raiseAmount;
    }
}
