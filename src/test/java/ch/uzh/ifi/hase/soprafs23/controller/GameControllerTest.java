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

import org.junit.jupiter.api.BeforeAll;

public class GameControllerTest {
    GameService gameService;

    @BeforeAll
    public void setup() {
        gameService = new GameService();
    }

    @Test
    public void createGameTest() {
        var p = new Player("A", "A");
        var gameId = gameService.createGame(p);
        assertNotEquals(null, gameId);
        assertEquals(gameService.getHost(gameId),p);
    }
}
