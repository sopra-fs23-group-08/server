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

//todo allow to play a second round. By placing game back into setup mode.
//todo not automatically jump to next betting round. Wait for betting round start. Maybe add Enum with BettingRound.running/ended
public class Game {

    private GameModel gameModel;
    public Setup setup;
    private GameController gameController;

    public Game() {
        gameModel = new GameModel();
        setup = new SetupData();
        gameController = new GameController(gameModel, setup);
    }

    public void startGame() throws IOException, InterruptedException, Exception {
        gameController.startGame();
        setup = new SetupClosed();
    }
    
    public void startBettingRound() {
        gameController.startBettingRound();
    }

    public void call(Player player) throws Exception{
        gameController.playerDecision(player, Decision.CALL);
    }

    public void raise(Player player, int newCallAmount) throws Exception{
        Decision d = Decision.RAISE;
        gameController.playerDecision(player, d, newCallAmount);
    }

    public void fold(Player player) throws Exception{
        gameController.playerDecision(player, Decision.FOLD);
    }

    public void addObserver(GameObserver o) {
        gameModel.addObserver(o);
    }

    public String getGameId() {
        return gameModel.getGameId(); 
    }


    public VideoData getVideoData() {
        return gameModel.getVideoData();
    }

    public GamePhase getGamePhase() {
        return gameModel.getGamePhase();
    }

    public List<Player> getPlayers() {
        List<Player> l = new ArrayList<>();
        for (PlayerData pd : gameModel.getPlayerDataCollection()) {
            l.add(pd.getPlayer()); //convert PlayerData to Player
        }
        return l;
    }


    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        Game game = new Game();
        Player playerA = new Player("A",false);
        Player playerB = new Player("B",false);
        Player playerC = new Player("C",false);

        
        game.setup.joinGame(playerC);
        game.setup.joinGame(playerB);
        game.setup.joinGame(playerA);
        game.startGame();
    }
    
}
