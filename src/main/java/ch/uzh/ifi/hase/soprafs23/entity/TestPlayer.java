package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.Column;

public class TestPlayer extends User {
    @Column
    private int score;

    @Column
    private TestGame currentGame;

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public TestGame getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(TestGame currentGame) {
        this.currentGame = currentGame;
    }
}
