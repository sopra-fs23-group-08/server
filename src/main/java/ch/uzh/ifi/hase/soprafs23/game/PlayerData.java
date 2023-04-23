package ch.uzh.ifi.hase.soprafs23.game;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

class PlayerData { //protected (Package Private)
    private final Player player;
    final String id;
    final String name;
    private String gameId;
    private Integer score;
    private Hand hand;
    private Decision decision;

    private List<GameObserver> observers;

    PlayerData(Player aPlayer) {
        player = aPlayer;
        id = aPlayer.id;
        name = aPlayer.name;
        observers = new ArrayList<>();
    }

    PlayerData() {
        player = new Player();
        id = null;
        name = null;
        observers = new ArrayList<>();
    }

    public void addObserver(String gameId, GameObserver o) {
        this.gameId = gameId;
        observers.add(o);
    }

    public void removeObserver(GameObserver o) {
        observers.remove(o);
    }

    //setters and getter-------------------------------------
    public Player getPlayer() {
        return player;
    }


    public void setScore(Integer score) {
        for (GameObserver o : observers) {
            o.playerScoreChanged(gameId, player, score);
        }
        this.score = score;
    }

    public int getScore() {
        return score;
    }
    
    public void setNewHand(Hand hand) {
        for (GameObserver o : observers) {
            o.newHand(gameId, player, hand);
        }
        this.hand = hand;
    }

    // public void setEvaluatedHand(Hand hand) {
    //     for (GameObserver o : observers) {
    //         o.handEvaluated(player, hand);
    //     }
    //     this.hand = hand;
    // }

    public Hand getHand() {
        return hand;
    }

    public void setDecision(Decision d) {
        for (GameObserver o : observers) {
            o.playerDecisionChanged(gameId, player, d);
        }
        this.decision = d;
    }

    public Decision getDecision() {
        return decision;
    }
    
}
