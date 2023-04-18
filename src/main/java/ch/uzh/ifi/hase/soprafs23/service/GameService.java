package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.TestGame;
import ch.uzh.ifi.hase.soprafs23.entity.TestPlayer;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TestPlayerWsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.HashMap;

@Service //part of the Spring Framework, and you will use it to mark a class as a service layer component. 
@Transactional // transactions should be managed for this service via @Transactional annotation.

// TODO: start games
// TODO: end/delete games
public class GameService {
    private HashMap<String, TestGame> games = new HashMap<String, TestGame>();
    //The service maintains a HashMap of games with game IDs as keys and TestGame objects as values.

    public TestGame createGame(TestPlayer host){
        // create new game
        TestGame newGame = new TestGame();

        // set host (host is not added to player list here)
        // FE sends a WS message to add the host to the player list
        newGame.setHost(host);

        // add game to list of games
        games.put(newGame.getId(), newGame);

        return newGame;
    }

    public TestPlayer getHost(String gameId){
        if (!games.containsKey(gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + gameId + " does not exist.");
        }
        return games.get(gameId).getHost();
    }

    public TestGame getGame(String gameId) {
        return games.get(gameId);
    }

    //returns a list of players for a specified game.
    public ArrayList<TestPlayer> getPlayers(String gameId) {
        return games.get(gameId).getPlayers();
    }

    // adds a new player to a specified game.
    public void addPlayer(String gameId, TestPlayer player) {
        // TODO deal with case where player is registered
        // check if game exists
        if (!games.containsKey(gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + gameId + " does not exist.");
        }

        TestGame game = games.get(gameId);
        player.setCurrentGame(game);
        // add player to game
        ArrayList<TestPlayer> players = game.getPlayers();
        // check if player has already been added to game
        for(TestPlayer p : players){
            if(p.getToken().equals(player.getToken())){
                // maybe throw a conflict error here
                return;
            }
        }
        players.add(player);
        game.setPlayers(players);
    }
}

