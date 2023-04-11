package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

import ch.uzh.ifi.hase.soprafs23.YTAPIManager.*; // import all classes from the YTAPIManager package


public interface GameObserver {

    void playerScoreChanged(Player player, Integer score);

    void newHand(Player player, Hand hand);

    // void handEvaluated(Player player, Hand hand); //don't use this maybe?

    void playerDecisionChanged(Player player, Decision decision);

    void currentPlayerChange(Player player);

    void winnerIs(Player player);

    void gameGettingClosed();

    void gamePhaseChange(GamePhase gamePhase);

    void potScoreChange(Integer score);

    void callAmountChanged(Integer newCallAmount);

    void newPlayerBigBlindNSmallBlind(Player smallBlind, Player bigBlind);

    
}

    APIController apiController = new APIController();
    Pair<VideoData, List<Hand>> gameData = apiController.getGameDataByQuery("query", Language.ENGLISH);


