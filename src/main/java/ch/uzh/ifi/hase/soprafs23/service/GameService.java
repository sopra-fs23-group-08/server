package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.controller.TestGame;
import ch.uzh.ifi.hase.soprafs23.controller.TestPlayer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

@Service
@Transactional
public class GameService {
    private HashMap<String, TestGame> games = new HashMap<String, TestGame>();

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

    public ArrayList<String> getPlayers(String gameId) {
        ArrayList<TestPlayer> players = games.get(gameId).getPlayers();

        // get only the usernames
        ArrayList<String> usernames = new ArrayList<>();
        for (TestPlayer player : players) {
            usernames.add(player.getUsername());
        }
        return usernames;
    }

    public void addPlayer(String gameId, String username) {
        //TODO throw error if game doesn't exist
        //TODO check if username exists; if yes, add corresponding player
        TestGame game = games.get(gameId);
        TestPlayer player = new TestPlayer();
        player.setUsername(username);
        game.addPlayer(player);
    }

}
