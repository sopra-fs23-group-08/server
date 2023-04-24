package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//this is the interface for the Game Component. Setup and VideoSetup are also part of the interface.
//basic usage:
//###################################################
//Game game = new Game();
//game.addObserver(GameObserver);
//...
//game.setup.join(playerA);
//...
//game.setup.setSmallBlindAmount(10);
//...
//game.setup.video.setQuery("LoFi HipHop");
//...
//game.startGame(); //note after the game is started no changes to the setup are allowed
//
//game.call(playerA);
//...
//###################################################

public class Game {

    private GameModel gameModel;
    public Setup setup;
    private GameLogic gameController;


    public Game(Player host) {
        setup();
        gameModel.setHost(host);
    }

    public Game() {
        setup();
    }

    private void setup() { 
        gameModel = new GameModel();
        setup = new SetupData(); //create a new setup data 
        gameController = new GameLogic(gameModel, setup);
    }

    public void startGame() throws IOException, InterruptedException, Exception { //this ends the setup phase. No changes to setup are possible
        gameController.startGame();
        setup = new SetupClosed();
        gameController.startBettingRound();
    }
    
    public void startBettingRound() { //this is not needed at the current state but would start the betting round
        gameController.startBettingRound();
    }

    public void call(String playerId) throws Exception{ //this should be called of player player decides to call
        gameController.playerDecision(playerId, Decision.CALL);
    }

    public void raise(String playerId, int newCallAmount) throws Exception{ //this should be called of player player decides to raise
        Decision d = Decision.RAISE;
        gameController.playerDecision(playerId, d);
    }

    public void fold(String playerId) throws Exception { //this should be called of player player decides to fold
        gameController.playerDecision(playerId, Decision.FOLD);
    }

    public void nexRound() throws IOException, InterruptedException, Exception { // this should be called after a round to play a second round
        gameController.startRound();
        gameController.startBettingRound();
    }

    public void addObserver(GameObserver o) { //adding game observer. Game observer are classes which implement GameObserver most of the data traffic happens there
        gameModel.addObserver(o);
    }

    public String getGameId() { // to get the game uuid
        return gameModel.getGameId();
    }

    public Player getHost() {
        return gameModel.getHost();
    }

    public List<HandOwnerWinner> getHands() throws Exception{
        return gameModel.getHands();
    }

    public List<Player> getPlayers() { //to get all the players which are currently in the game. Not working during setup!!
        List<Player> l = new ArrayList<>();
        for (PlayerData pd : gameModel.getPlayerDataCollection()) {
            l.add(pd.getPlayer()); //convert PlayerData to Player
        }
        return l;
    }

    public GamePhase getGamePhase() {
        return gameModel.getGamePhase();
    }


    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        Game game = new Game();
        Player playerA = new Player("A");
        Player playerB = new Player("B");
        Player playerC = new Player("C");

        
        game.setup.joinGame(playerC);
        game.setup.joinGame(playerB);
        game.setup.joinGame(playerA);
        game.startGame();
    }
    
}
