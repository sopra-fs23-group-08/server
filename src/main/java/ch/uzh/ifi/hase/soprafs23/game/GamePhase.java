package ch.uzh.ifi.hase.soprafs23.game;

public enum GamePhase {
    LOBBY(1), FIRST_BETTING_ROUND(2), SECOND_BETTING_ROUND(3), THIRD_BETTING_ROUND(4), FOURTH_BETTING_ROUND(5),
    END_ALL_FOLDED(5), END_AFTER_FOURTH_BETTING_ROUND(5);

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
