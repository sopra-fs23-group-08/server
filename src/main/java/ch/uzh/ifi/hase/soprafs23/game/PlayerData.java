package ch.uzh.ifi.hase.soprafs23.game;

import java.util.List;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

class PlayerData { //protected (Package Private)
    private final Player player;
    final Long id;
    final String name;
    final boolean loggedIn;
    private Integer score;
    private Hand hand;
    private Decision decision;

    private List<GameObserver> observers;

    PlayerData(Player aPlayer) {
        player = aPlayer;
        id = aPlayer.id;
        name = aPlayer.name;
        loggedIn = aPlayer.loggedIn;
    }

    public void addObserver(GameObserver o) {
        observers.add(o);
    }

    public void removeObserver(GameObserver o) {
        observers.remove(o);
    }

    //setters and getter-------------------------------------

    public void setScore(Integer score) {
        for (GameObserver o : observers) {
            o.playerScoreChanged(player, score);
        }
        this.score = score;
    }

    public int getScore() {
        return score;
    }
    
    public void setNewHand(Hand hand) {
        for (GameObserver o : observers) {
            o.newHand(player, hand);
        }
        this.hand = hand;
    }

    public void setEvaluatedHand(Hand hand) {
        for (GameObserver o : observers) {
            o.handEvaluated(player, hand);
        }
        this.hand = hand;
    }

    public Hand getHand() {
        return hand;
    }

    public void setDecision(Decision d) {
        for (GameObserver o : observers) {
            o.playerDecisionChanged(player, d);
        }
        this.decision = d;
    }

    public Decision getDecision() {
        return decision;
    }
    
}
