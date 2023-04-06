package ch.uzh.ifi.hase.soprafs23.game;

public enum GamePhase {
    WAITING_FOR_PLAYERS, FIRST_ROUND, SECOND_ROUND, THIRD_ROUND, FOURTH_ROUND, END_ALL_FOLDED, END_AFTER_FOURTH_ROUND;

    public GamePhase nextPhase() throws Exception {
        switch (this) {
            case FIRST_ROUND:
                return GamePhase.SECOND_ROUND;
            case SECOND_ROUND:
                return GamePhase.THIRD_ROUND;
            case THIRD_ROUND:
                return GamePhase.FOURTH_ROUND;
            default:
                throw new Exception("nextPhase is invalid for " + this);
        }
    }
}
