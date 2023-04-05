package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

class GameController {
    
    private GameModel gm;

    GameController(GameModel gm){
        this.gm = gm;
    }

    void startBettingRound() {
        //setup
        // for (PlayerData p : gm.players) {
        //     p.setDecision(Decision.NOT_DECIDED);
        // }
        // gm.pot = 0;
        //...
    }

    void playerDecision(Player p, Decision d){
        // if (p != gm.getCurrentPlayer()) {
        //     throw new Exception("Youre not the current player");
        // }
    }

    
}
