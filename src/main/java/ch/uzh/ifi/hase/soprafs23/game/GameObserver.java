package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

public interface GameObserver {

    void playerScoreChanged(String gameId, Player player, Integer score);

    void newHand(String gameId, Player player, Hand hand);

    // void handEvaluated(Player player, Hand hand); //don't use this maybe?

    void playerDecisionChanged(String gameId, Player player, Decision decision);

    void currentPlayerChange(String gameId, Player player);

    void roundWinnerIs(String gameId, Player player);

    void gameGettingClosed(String gameId);

    void gamePhaseChange(String gameId, GamePhase gamePhase);

    void potScoreChange(String gameId, Integer score);

    void callAmountChanged(String gameId, Integer newCallAmount);

    void newPlayerBigBlindNSmallBlind(String gameId, Player smallBlind, Player bigBlind);

    void newVideoData(String gameId, VideoData videoData); //not revealed fields are set to null

    void updatePlayerPotScore(String gameId, Player player, Integer scorePutIntoPot);

    void playerLeft(String gameId, Player p);
}
