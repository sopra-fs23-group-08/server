package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

public interface GameObserver {

    void playerScoreChanged(Player player, Integer score);

    void newHand(Player player, Hand hand);

    // void handEvaluated(Player player, Hand hand); //don't use this maybe?

    void playerDecisionChanged(Player player, Decision decision);

    void currentPlayerChange(Player player);

    void roundWinnerIs(Player player);

    void gameGettingClosed();

    void gamePhaseChange(GamePhase gamePhase);

    void potScoreChange(Integer score);

    void callAmountChanged(Integer newCallAmount);

    void newPlayerBigBlindNSmallBlind(Player smallBlind, Player bigBlind);

    void newVideoData(VideoData videoData); //not revealed fields are set to null
}
