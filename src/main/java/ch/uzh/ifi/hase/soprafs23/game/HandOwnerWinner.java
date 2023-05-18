package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

//mutable only use this to send data
public class HandOwnerWinner {
    private Hand hand;
    private Player player;
    private Boolean isWinner;
    
    public Hand getHand() {
        return hand;
    }
    public void setHand(Hand hand) {
        this.hand = hand;
    }
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public Boolean getIsWinner() {
        return isWinner;
    }
    public void setIsWinner(Boolean isWinner) {
        this.isWinner = isWinner;
    }
}
