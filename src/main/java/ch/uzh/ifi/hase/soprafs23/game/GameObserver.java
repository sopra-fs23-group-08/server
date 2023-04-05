package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;

public interface GameObserver {

    void playerScoreChanged(Player player, Integer score);

    void newHand(Player player, Hand hand);

    void handEvaluated(Player player, Hand hand);

    void playerDecisionChanged(Player player, Decision decision);

    void currentPlayerChange(User user);

    void winnerIs(User user);

    void gameGettingClosed();

    void gamePhaseChange(GamePhase gamePhase);

    void potScoreChange(Integer score);
    
}
