package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Comment;
import ch.uzh.ifi.hase.soprafs23.entity.User;


public class Game {

    private GameModel gameModel;
    private VideoSelection videoSelection;

    public void game(Game game){
    }
    
    public void joinGame(User user){
    }

    public void leaveGame(User user){
        
    }

    public void setSmallBlind(int smallBlind) {

    }

    public void setBigBlind(int bigBlind) {

    }

    public void setStartScore(int startScore) {

    }


    public void setScorePlayer(int scorePlayer, User user) {

    }

    public void infoFirstRound(boolean bool){

    }

    public void start(User user){

    }

    public void call(User user){

    }

    public void raise(User user){

    }

    public void fold(User user){

    }

    public void addObserver(GameObserver o) {
    }

    public Game getGameId(Game game) {
        return game; 
    }


    public VideoData getVideoData(VideoData videoData) {
        return videoData;
    }

    public GamePhase getGamePhase(GamePhase gamePhase) {
        return gamePhase;
    }

    public Player getPlayer(Player player) {
        return player;
    }


    
}
