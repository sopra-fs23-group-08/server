package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.hase.soprafs23.entity.Comment;


public class Game {

    private GameModel gameModel;
    public VideoSelection videoSelection;
    private GameController gameController;
    private int startScore = 0;

    public Game() {
        gameModel = new GameModel();
        videoSelection = new VideoSelection();
        gameController = new GameController(gameModel);
    }
    
    public void joinGame(Player player){
        gameModel.getSetupData().addPlayer(player, startScore);
    }

    public void leaveGame(Player player){
        gameModel.getSetupData().removePlayer(player);
    }

    public void setSmallBlind(int smallBlind) {
        gameModel.getSetupData().setSmallBlind(smallBlind);
    }

    public void setBigBlind(int bigBlind) {
        gameModel.getSetupData().setBigBlind(bigBlind);
    }

    public void setStartScore(int startScore) {
        this.startScore = startScore;
        gameModel.getSetupData().changeInitialScoreForAll(startScore);
    }


    public void setScorePlayer(int scorePlayer, Player player) {
        gameModel.getSetupData().changeInitialScore(player, scorePlayer);
    }

    public void infoFirstRound(boolean shouldThereBeInfoFirstRound){
        gameModel.getSetupData().setInfoFirstRound(shouldThereBeInfoFirstRound);
    }

    public void startGame() throws IOException, InterruptedException, Exception {
        gameController.startGame();
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
        for (PlayerData p : gameModel.getPlayers()) {
            l.add(p.getPlayer()); //convert PlayerData to Player
        }
        return l;
    }


    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        Game game = new Game();
        Player playerA = new Player("A",false);
        Player playerB = new Player("B",false);
        Player playerC = new Player("C",false);

        
        game.joinGame(playerC);
        game.joinGame(playerB);
        game.joinGame(playerA);
        game.startGame();
    }
    
}
