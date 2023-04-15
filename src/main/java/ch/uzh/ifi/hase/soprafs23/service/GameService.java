package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.controller.TestGame;
import ch.uzh.ifi.hase.soprafs23.controller.TestPlayer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;

@Service //part of the Spring Framework, and you will use it to mark a class as a service layer component. 
@Transactional // transactions should be managed for this service via @Transactional annotation.

public class GameService {
    private HashMap<String, TestGame> games = new HashMap<String, TestGame>();
    //The service maintains a HashMap of games with game IDs as keys and TestGame objects as values.

    public TestGame createGame(String hostUsername){
        TestPlayer host = new TestPlayer();
        host.setUsername(hostUsername);
        // create new game
        TestGame newGame = new TestGame();
        // set host (not yet added to player list)
        newGame.setHost(host);
        // add game to list of games
        games.put(newGame.getId(), newGame);

        return newGame;
    }

    public TestGame getGame(String gameId) {
        return games.get(gameId);
    }

    //returns a list of players for a specified game.
    public ArrayList<String> getPlayerUsernames(String gameId) {
        ArrayList<TestPlayer> players = games.get(gameId).getPlayers();

        // get only the usernames
        ArrayList<String> usernames = new ArrayList<>();
        for (TestPlayer player : players) {
            usernames.add(player.getUsername());
        }
        return usernames;
    }

    // adds a new player to a specified game.
    public void addPlayer(String gameId, String username) {
        //TODO throw error if game doesn't exist; Like this?
        if (!games.containsKey(gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + gameId + " does not exist.");
        } 
        
        TestGame game = games.get(gameId);
        TestPlayer player = new TestPlayer();
        player.setUsername(username);
        //TODO check if username exists; if yes, add corresponding player; Like this? 
        if (game.getPlayers().stream().anyMatch(p -> p.getUsername().equals(username))) {
            throw new IllegalArgumentException("Player with username " + username + " already exists in the game.");
        }
        game.addPlayer(player);
    }

}
