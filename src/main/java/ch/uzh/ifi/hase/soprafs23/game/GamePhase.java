package ch.uzh.ifi.hase.soprafs23.game;

public enum GamePhase {
    LOBBY(0), FIRST_BETTING_ROUND(0), SECOND_BETTING_ROUND(1), THIRD_BETTING_ROUND(2), FOURTH_BETTING_ROUND(3),
    END_ALL_FOLDED(4), END_AFTER_FOURTH_BETTING_ROUND(4), CLOSED(99);

    final int val;

    GamePhase(int i) {
        val = i;
    }
    
    public int getVal() {
        return val;
    }
    
    public GamePhase nextPhase() throws IllegalStateException {
        switch (this) {
            case FIRST_BETTING_ROUND:
                return GamePhase.SECOND_BETTING_ROUND;
            case SECOND_BETTING_ROUND:
                return GamePhase.THIRD_BETTING_ROUND;
            case THIRD_BETTING_ROUND:
                return GamePhase.FOURTH_BETTING_ROUND;
            default:
                throw new IllegalStateException("nextPhase is invalid for " + this);
        }
    }
}
