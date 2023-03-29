package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

public interface GameObserver {

    void playerScoreChanged(Player player, Integer score);

    void newHand(Player player, Hand hand);

    void handEvaluated(Player player, Hand hand);

    void playerDecisionChanged(Player player, Decision decision);
    
}
