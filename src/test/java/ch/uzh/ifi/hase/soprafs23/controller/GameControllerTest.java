package ch.uzh.ifi.hase.soprafs23.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.service.GameService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

public class GameControllerTest {
    GameService gameService;
    String gameId;

    @BeforeEach
    public void setup() {
        gameService = new GameService();
        gameId = gameService.createGame(new Player("A"));
    }

    @Test
    public void createGameTest() {
        var p = new Player("A", "A");
        var localGameId = gameService.createGame(p);
        assertNotEquals(null, localGameId);
        assertEquals(gameService.getHost(localGameId), p);
    }
    
    @Test
    public void getHostTest() {
        assertEquals("A", gameService.getHost(gameId).getName());
    }

    @Test
    public void addPlayer() {
        gameService.addPlayer(gameId, new Player("B"));
        var lp = gameService.getPlayers(gameId);
        for (var p : lp) {
            List<String> a = new ArrayList<>();
            a.add("A");
            a.add("B");
            assertTrue(a.contains(p.getUsername()));
        }
    }
}
