package ch.uzh.ifi.hase.soprafs23.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import ch.uzh.ifi.hase.soprafs23.YTAPIManager.Language;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.DecisionWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsWsDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

// @WebMvcTest(GameController.class)
public class GameServiceTest {

    // @Autowired
    // private MockMvc mockMvc;

    // @MockBean
    // private GameService gameService;
    
    String gameId;
    Player host;

    @BeforeEach
    public void setup() {
        gameService = new GameService(new GameController());
        host = new Player("host");
        gameId = gameService.createGame(host);
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

    @Test
    void removePlayer() {
        gameService.removePlayer(gameId, host);
        assertEquals(0, gameService.getPlayers(gameId).size());
    };

    @Test
    void updateSettings() {
        var setting =  new SettingsWsDTO();
        setting.setSmallBlind(5);
        setting.setBigBlind(10);
        setting.setInitialBalance(500);
        setting.setLanguage(Language.GERMAN);
        setting.setPlaylistUrl("https://www.youtube.com/playlist?list=PLx0sYbCqOb8QTF1DCJVfQrtWknZFzuoAE");
        gameService.setGameSettings(gameId, setting);
    }

    @Test
    void startGame() {
        //mockito stuff here :)
        //given(messagingTemplate.convertAndSend(Mockito.any(), Mockito.any())).willReturn();
        gameService.startGame(gameId);
    }

    @Test
    void endGame() {
        // gameService.endGame(gameId);
    }

    @Test
    void handlePlayerDecision() {
        var decision = new DecisionWsDTO();
        decision.setDecision("CALL");
        decision.setRaiseAmount(null);
        gameService.playerDecision(gameId, host.getToken(), decision);

    }   

    @Test
    void nextRound() {
        gameService.nextRound(gameId);
    }
}

