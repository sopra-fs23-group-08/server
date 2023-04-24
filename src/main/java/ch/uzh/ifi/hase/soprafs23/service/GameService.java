package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.controller.GameController;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.game.Decision;
import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.GameObserver;
import ch.uzh.ifi.hase.soprafs23.game.GamePhase;
import ch.uzh.ifi.hase.soprafs23.game.Hand;
import ch.uzh.ifi.hase.soprafs23.game.VideoData;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;

import org.springframework.beans.factory.annotation.Autowired;
// import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerWsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service //part of the Spring Framework, and you will use it to mark a class as a service layer component. 
@Transactional // transactions should be managed for this service via @Transactional annotation.

// TODO: start games
// TODO: end/delete games
public class GameService implements GameObserver{

    @Autowired
    public GameController gameController; 

    private HashMap<String, Game> games = new HashMap<>();
    //The service maintains a HashMap of games with game IDs as keys and Game objects as values.

    private HashMap<String, GameData> gamesData = new HashMap<>();

    public Game createGame(Player host){
        // create new game
        Game newGame = new Game(host);

        // set host (host is not added to player list here)
        // FE sends a WS message to add the host to the player list
        
        
        // add game to list of games
        games.put(newGame.getGameId(), newGame);

        return newGame;
    }

    public Player getHost(String gameId){
        if (!games.containsKey(gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + gameId + " does not exist.");
        }
        return games.get(gameId).getHost();
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    //returns a list of players for a specified game.
    public List<Player> getPlayers(String gameId) {
        return games.get(gameId).getPlayers();
    }

    // adds a new player to a specified game.
    public void addPlayer(String gameId, Player player) throws Exception {
        // TODO deal with case where player is registered
        // check if game exists
        checkIfGameExists(gameId);

        Game game = games.get(gameId);
     
        game.setup.joinGame(player);
        
    }

    public void removePlayer(String gameId, Player player) throws Exception {
        // check if game exists
        checkIfGameExists(gameId);
    
        Game game = games.get(gameId);

        game.setup.leaveGame(player);
    

    }



    @Override
    public void playerScoreChanged(String gameId, Player player, Integer score) {
        checkIfGameExists(gameId);
        //update GameData
        GameData gameData = gamesData.get(gameId);
        gameData.playersData.get(player.getToken()).score = score;

        //send GameData to front end
        gameController.gameStateChanged(gameId, null); //todo create Setting DTO

        throw new UnsupportedOperationException("not working yet");
    }

    @Override
    public void newHand(String gameId, Player player, Hand hand) {
        checkIfGameExists(gameId);
        //update GameData
        GameData gameData = gamesData.get(gameId);
        gameData.playersData.get(player.getToken()).hand = hand;
        
        //send GameData to front end
        gameController.newHand(gameId, player, hand); //todo create Setting DTO


        throw new UnsupportedOperationException("Unimplemented method 'newHand'");
    }

    @Override
    public void playerDecisionChanged(String gameId, Player player, Decision decision) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'playerDecisionChanged'");
    }

    @Override
    public void currentPlayerChange(String gameId, Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'currentPlayerChange'");
    }

    @Override
    public void roundWinnerIs(String gameId, Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'roundWinnerIs'");
    }

    @Override
    public void gameGettingClosed(String gameId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gameGettingClosed'");
    }

    @Override
    public void gamePhaseChange(String gameId, GamePhase gamePhase) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gamePhaseChange'");
    }

    @Override
    public void potScoreChange(String gameId, Integer score) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'potScoreChange'");
    }

    @Override
    public void callAmountChanged(String gameId, Integer newCallAmount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'callAmountChanged'");
    }

    @Override
    public void newPlayerBigBlindNSmallBlind(String gameId, Player smallBlind, Player bigBlind) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newPlayerBigBlindNSmallBlind'");
    }

    @Override
    public void newVideoData(String gameId, VideoData videoData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newVideoData'");
    }

    private void checkIfGameExists(String gameId) {
        if (!games.containsKey(gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + gameId + " does not exist.");
        }
        
    }

    private void informClient(){
    // TODO
    }
    
}

