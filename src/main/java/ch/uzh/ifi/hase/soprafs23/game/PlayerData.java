package ch.uzh.ifi.hase.soprafs23.game;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

class PlayerData { //protected (Package Private)
    private final Player player;
    final String token;
    final String name;
    private String gameId;
    private Integer score = 0;
    private final Object scoreLock = new Object();
    private Integer scorePutIntoPot = 0;
    private final Object scorePutIntoPotLock = new Object();
    private Hand hand;
    private final Object decisionLock = new Object();
    private Decision decision = Decision.NOT_DECIDED;

    private List<GameObserver> observersPlayer;

    PlayerData(Player aPlayer) {
        player = aPlayer;
        token = aPlayer.getToken();
        name = aPlayer.getName();
        observersPlayer = new ArrayList<>();
    }

    PlayerData() {
        player = new Player();
        token = null;
        name = null;
        observersPlayer = new ArrayList<>();
    }

    public void addObserver(String gameId, GameObserver o) {
        synchronized (observersPlayer) {
            this.gameId = gameId;
            observersPlayer.add(o);
        }
    }

    public void removeObserver(GameObserver o) {
        synchronized (observersPlayer){
            observersPlayer.remove(o);
        }
    }

    //setters and getter-------------------------------------

    public Integer getScorePutIntoPot() {
        synchronized (scorePutIntoPotLock) {
            return scorePutIntoPot;
        }
    }

    public void setScorePutIntoPot(Integer scorePutIntoPot) {
        synchronized (scorePutIntoPotLock) {
            for(var o : observersPlayer){
                o.updatePlayerPotScore(gameId, player, scorePutIntoPot);
            }
            this.scorePutIntoPot = scorePutIntoPot;
        }
    }

    public Player getPlayer() {
        return player;
    }


    public void setScore(Integer score) {
        synchronized (scoreLock) {
            if (score == null) {
                return;
            }
            if (this.score != null && score.compareTo(this.score) == 0) {
                return;
            }

            for (GameObserver o : observersPlayer) {
                o.playerScoreChanged(gameId, player, score);
            }
            this.score = score;
        }
    }

    public int getScore() {
        synchronized (scoreLock) {
            return score;
        }
    }
    
    public synchronized void setNewHand(Hand hand) {

        if (hand == this.hand) {
            return;}
            for (GameObserver o : observersPlayer) {
                o.newHand(gameId, player, hand);
            }
            this.hand = hand;

    }

    public Hand getHand() {
        return hand;
    }

    public void setDecision(Decision d) {
        synchronized (decisionLock) {
            
            if (d == this.decision) {
                return;}
                for (GameObserver o : observersPlayer) {
                    o.playerDecisionChanged(gameId, player, d);
                }
                this.decision = d;
        }
    }

    public Decision getDecision() {
        synchronized (decisionLock) {
            return decision;
        }
    }
    
}
