package ch.uzh.ifi.hase.soprafs23.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.uzh.ifi.hase.soprafs23.YTAPIManager.Language;
import ch.uzh.ifi.hase.soprafs23.controller.GameController;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.DecisionWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsWsDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

// @WebMvcTest(GameService.class)
public class GameServiceTestUnit {

    // @Autowired
    // private MockMvc mockMvc;

    // @MockBean
    private GameService gameService;
    
    String gameId;
    Player host;

    @BeforeEach
    public void setup() {
        gameService = new GameService(new GameController("null"));
        host = new Player("host");
        gameId = gameService.createGame(host);
    }

    @Test
    public void createGameTest() {
        var p = new Player("A", "A");
        var localGameId = gameService.createGame(p);
        assertNotEquals(null, localGameId);
        assertEquals(gameService.getHost(localGameId).getToken(), p.getToken());
    }
    
    @Test
    public void getHostTest() {
        assertEquals("host", gameService.getHost(gameId).getName());
    }

    @Test
    public void addPlayer() {
        gameService.addPlayer(gameId, new Player("B"));
        var lp = gameService.getPlayers(gameId);
        for (var p : lp) {
            List<String> a = new ArrayList<>();
            a.add("host");
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
        //gameService.startGame(gameId);
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
        // gameService.startGame(gameId);
        // gameService.playerDecision(gameId, host.getToken(), decision);

    }   

    @Test
    void nextRound() {
        //mockito problem
        //gameService.nextRound(gameId);
    }
}

