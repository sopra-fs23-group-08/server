package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.controller.TestGame;
import ch.uzh.ifi.hase.soprafs23.controller.TestPlayer;
import ch.uzh.ifi.hase.soprafs23.game.Game;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional
public class GameService {
    private HashMap<String, TestGame> games = new HashMap();

    public TestGame createGame(String username) {
        TestGame newGame = new TestGame(new TestPlayer(username));
        games.put(newGame.getGameId(), newGame);
        return newGame;
    }

    public TestGame getGame(String gameId) {
        return games.get(gameId);
    }

}
