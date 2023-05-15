package ch.uzh.ifi.hase.soprafs23.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.DecisionWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsWsDTO;

// makes use of functions declared in GameControllerTest
import static ch.uzh.ifi.hase.soprafs23.controller.GameControllerTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.awaitility.Awaitility.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExtendedGameControllerTest {

    private String gameId;
    private StompSession session;
    private StompSession sessionString;
    private BlockingQueue<Exception> errorObserver;
    private String topic;
    private String app;

    @BeforeEach
    public void setup() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        gameId = newGame();
        session = createStompSession(new MappingJackson2MessageConverter());
        sessionString = createStompSession(new StringMessageConverter());
        errorObserver = subscribe(session, "/topic/games/" + gameId + "/error", Exception.class);
        topic = "/topic/games/" + gameId;
        app = "/app/games/" + gameId;
    }
    
    @Test
    public void setupTest() throws IOException, InterruptedException {
        assertNotEquals(null, gameId);
        session.send("/app/games/" + gameId + "/start", ""); //works

        var response = errorObserver.poll(1, TimeUnit.SECONDS);
        assertEquals(null, response);

        var settings = new SettingsWsDTO();
        settings.setBigBlind(5);
        session.send("/app/games/" + gameId + "/settings", settings); //works

        var response2 = errorObserver.poll(10, TimeUnit.SECONDS);
        assertNotEquals(null, response2);
    }

    @Test
    public void setupTest2() throws InterruptedException, ExecutionException, TimeoutException {
        var handObserver = subscribe(sessionString,
                String.format("/topic/games/%s/players/%s/hand", gameId, "Host"), String.class);
        session.send("/app/games/" + gameId + "/start", "");

        var response = handObserver.poll(2, TimeUnit.SECONDS);
        assertNotEquals(null, response);
    }
    
    @Test
    public void basicAddPlayerTest() throws InterruptedException {
        var playersObserver = subscribe(session, String.format("/topic/games/%s/players", gameId), List.class);
        var player = new PlayerDTO();
        player.setName("Sebastian Pamela");
        player.setToken("secondPlayer");
        session.send("/app/games/" + gameId + "/players/add", player);

        var response = playersObserver.poll(10, TimeUnit.SECONDS);
        assertNotEquals(null, response);
        assertNotEquals(0, response.size());
    }

    @Test
    public void maxPlayers() throws InterruptedException {
        var p1 = new PlayerDTO("FredericA", "A");
        var p2 = new PlayerDTO("FredericB", "B");
        var p3 = new PlayerDTO("FredericC", "C");
        var p4 = new PlayerDTO("FredericD", "D");
        var p5 = new PlayerDTO("FredericE", "E");
        var p6 = new PlayerDTO("FredericF", "F");
        var players = List.of(p1, p2, p3, p4, p5, p6);
        List response;
        
        var playersObserver = subscribe(session, String.format("/topic/games/%s/players", gameId), List.class);
        var player = new PlayerDTO();
        player.setName("Sebastian Pamela");
        player.setToken("secondPlayer");
        // session.send("/app/games/" + gameId + "/players/add", p1);

        // response = playersObserver.poll(10, TimeUnit.SECONDS);
        
        // session.send("/app/games/" + gameId + "/players/add", p2);
        // session.send("/app/games/" + gameId + "/players/add", p3);

        // response = playersObserver.poll(10, TimeUnit.SECONDS);
        // session.send("/app/games/" + gameId + "/players/add", p4);
        // session.send("/app/games/" + gameId + "/players/add", p5);
        // session.send("/app/games/" + gameId + "/players/add", p6);
        
        for (var p : players) {
                // session.send("/app/games/" + gameId + "/players/add", player);
                session.send("/app/games/" + gameId + "/players/add", p);
                // Thread.sleep(1000);
                // await().atMost(1, TimeUnit.SECONDS);
        }
        Thread.sleep(5000);
        
        response = getNewest(playersObserver);
        // assertEquals("Host", ((HashMap) response.get(0)).get("token"));
        // assertNotEquals(null, errorObserver.poll(1, TimeUnit.SECONDS));
        assertEquals(6, response.size());
        
    }
    
    @Test
    public void runThrough() {

    }

    
    static private <T> T getNewest(BlockingQueue<T> bq) throws InterruptedException {
        while (bq.size() > 1) {
            bq.poll();
        }
        return bq.poll(1, TimeUnit.SECONDS);
    }
    
    public static void main(String[] args) throws InterruptedException {
        var b = new ArrayBlockingQueue<Integer>(10); //size of queue matters !!!
        for (int i = 0; i < 10; i++) {
            b.add(i);
        }
        var x = getNewest(b);
        assertEquals(9, x);
        System.out.println(x);
    }
}
