package ch.uzh.ifi.hase.soprafs23.controller;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.catalina.connector.OutputBuffer;
// import org.h2.engine.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ch.uzh.ifi.hase.soprafs23.YTAPIManager.Language;
import ch.uzh.ifi.hase.soprafs23.YTAPIManager.YTAPIManager;
import ch.uzh.ifi.hase.soprafs23.game.Decision;
import ch.uzh.ifi.hase.soprafs23.game.GamePhase;
import ch.uzh.ifi.hase.soprafs23.rest.dto.DecisionWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameStateWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsWsDTO;
import javassist.expr.NewArray;

// makes use of functions declared in GameControllerTest
import static ch.uzh.ifi.hase.soprafs23.controller.GameControllerTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
@EnabledOnOs({OS.WINDOWS, OS.MAC})//cloud runs on linux therefore this is only runs locally
public class ExtendedGameControllerTest {

    private String gameId;
    private StompSession session;
    private StompSession sessionString;
    private BlockingQueue<Exception> errorObserver;
    private String topic;
    private String app;
    private BlockingQueue<List> playerObserver;
    private List<LinkedHashMap<String,Object>> playerList;

    @BeforeEach
    public void setup() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        try {
            gameId = newGame();
            session = createStompSession(new MappingJackson2MessageConverter());
            sessionString = createStompSession(new StringMessageConverter());
            errorObserver = subscribe(session, "/topic/games/" + gameId + "/error", Exception.class);
            topic = "/topic/games/" + gameId;
            app = "/app/games/" + gameId;
            playerObserver = subscribe(session, topic + "/players", List.class);
        } catch (Exception e) {
            System.err.println(e);
            assertEquals("Needs running Backend. Probably your not running a local Backend", e);
        }
    }
    
    @Test
    public void setupTest() throws IOException, InterruptedException {
        assertNotEquals(null, gameId);
        errorObserver.clear();

        session.send("/app/games/" + gameId + "/noYtApi", "");
        Thread.sleep(500);
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
        session.send("/app/games/" + gameId + "/noYtApi", "");
        Thread.sleep(500);
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

        for (var p : players) {
            session.send("/app/games/" + gameId + "/players/add", p);
        }
        Thread.sleep(1000);

        response = getNewest(playersObserver);
        // assertEquals("Host", ((HashMap) response.get(0)).get("token"));
        assertEquals(6, response.size());
        var error = errorObserver.poll(1, TimeUnit.SECONDS);
        assertNotEquals(null, error);
    }
    

    @Test
    public void doubleLeave() throws InterruptedException {
        fillGame();
        Thread.sleep(500);
        var player = new PlayerDTO("abc", "A");
        var playersObserver = subscribe(session, topic + "/players", List.class);

        session.send(app + "/players/remove", player);

        var playerList = getNewest(playersObserver);
        assertNotEquals(null, playerList);
        assertEquals(5, playerList.size());

        session.send(app + "/players/remove", player);
        playerList = playersObserver.poll(1, TimeUnit.SECONDS);
        var error = errorObserver.poll(1, TimeUnit.SECONDS);

        assertNotEquals(null, playerList);
        assertEquals(5, playerList.size());
        assertNotEquals(null, error);
    }
    
    @Test
    public void differentComments() throws InterruptedException, JsonMappingException, JsonProcessingException {
        var playerObserver = subscribe(session, topic + "/players", List.class);
        var gameStateObserver = subscribe(session, topic + "/state", GameStateWsDTO.class);
        var pA = new HandObserver("A");
        var pB = new HandObserver("B");
        var pC = new HandObserver("C");
        var pD = new HandObserver("D");
        var pE = new HandObserver("E");
        var pHost = new HandObserver("Host");

        var handObservers = List.of(pA, pB, pC, pD, pE, pHost);

        fillGame();
        Thread.sleep(500);
        session.send(app + "/start", "");
        Thread.sleep(10000); // wait for be to fetch comments

        var playerList = getNewest(playerObserver);
        var gameState = getNewest(gameStateObserver);
        
        var hands = new ArrayList<>();
        for (var o : handObservers) {
            hands.add(o.getHand());
        }

        var content = getContent(0, 0, hands);
        assertEquals(getContent(0, 0, hands), content);

        for (int i = 0; i < 6; i++) {
            for (int j = i + 1; j < 6; j++) {
                var A = getContent(i, 0, hands);
                var B = getContent(j, 0, hands);
                assertNotEquals(A, B);
            }
        }
        
        for (int i = 0; i < 6; i++) {
            for (int j = i + 1; j < 6; j++) {
                var A = getContent(0, i, hands);
                var B = getContent(0, j, hands);
                assertNotEquals(A , B);
            }
        }
    }
    
    
    @Test
    public void isThereACurrentPlayer() throws InterruptedException, JsonMappingException, JsonProcessingException {
        fillGame();
        Thread.sleep(500);
        var playerObserver = subscribe(session, topic + "/players", List.class);
        var gameStateObserver = subscribe(session, topic + "/state", GameStateWsDTO.class);
        var hands = startGame();
        Thread.sleep(500);

        List<LinkedHashMap<String, Object>> playerList = getNewest(playerObserver);
        LinkedHashMap<String, Object> currentPlayer = null;
        for (var p : playerList) {
            var token = p.get("token");
            if ((Boolean) p.get("currentPlayer")) {
                currentPlayer = p;
            }
        }
        assertNotEquals(null, currentPlayer);
    }
    
    //this test needs the sleep times to ensure server responses. but maybe there are multi threading issues.
    @Test
    public void runThrough1() throws InterruptedException, JsonMappingException, JsonProcessingException {
        final int sleepTime = 100; //increase this when inconsistent errors occur

        fillGame();
        Thread.sleep(sleepTime); //waiting for server to update. If started before all players have joined maybe only 3 players are in game
        var playerList = setupGame();
        var hands = startGame();

        Thread.sleep(sleepTime); //waiting for server to update. If next action is done before game has properly started some unexpected errors might happen. Like no player is able to do a decision, before the game has properly started.
        errorObserver.clear();
        assertEquals(null, errorObserver.poll(sleepTime, TimeUnit.MILLISECONDS));
        decision(Decision.CALL, 0);
        decision(Decision.CALL, 0); //this is too fast and sends the second decision with the no longer current player gives an error
        // assertEquals("You're not the current player",
        //         errorObserver.poll(sleepTime * 10, TimeUnit.MILLISECONDS).getMessage()); //since a message is expected the poll time is longer
        Thread.sleep(sleepTime);
        decision(Decision.CALL, 0); //is fine down here after currentPlayer is updated
        assertEquals(null, errorObserver.poll(sleepTime, TimeUnit.MILLISECONDS));

        var gameStateObserver = subscribe(session, topic + "/state", GameStateWsDTO.class);
        GamePhase gamePhase = GamePhase.FIRST_BETTING_ROUND;
        while (gamePhase != GamePhase.END_AFTER_FOURTH_BETTING_ROUND && gamePhase != GamePhase.END_ALL_FOLDED) {
            decision(Decision.CALL, 0);

            var response1 = errorObserver.poll(sleepTime, TimeUnit.MILLISECONDS);
            if (response1 != null) {
                assertEquals("You're not the current player", response1.getMessage());
            }

            var response = getNewest(gameStateObserver);
            gamePhase = response == null ? gamePhase : response.getGamePhase();
        }
    }

    @Test
    public void runthrough2() throws InterruptedException, JsonMappingException, JsonProcessingException {
        final int sleepTime = 100; //increase this when inconsistent errors occur

        fillGame();
        Thread.sleep(sleepTime); //waiting for server to update. If started before all players have joined maybe only 3 players are in game
        var playerList = setupGame();
        var hands = startGame();

        Thread.sleep(sleepTime); //waiting for server to update. If next action is done before game has properly started some unexpected errors might happen. Like no player is able to do a decision, before the game has properly started.
        errorObserver.clear();
        assertEquals(null, errorObserver.poll(sleepTime, TimeUnit.MILLISECONDS)); //check for errors and give time to breathe for server

        var decisionStack = new LinkedList<Pair<Decision, Integer>>();

        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.FOLD, 0));
        decisionStack.add(Pair.of(Decision.FOLD, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.RAISE, 50));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));
        decisionStack.add(Pair.of(Decision.CALL, 0));

        var gameStateObserver = subscribe(session, topic + "/state", GameStateWsDTO.class);
        GamePhase gamePhase = GamePhase.FIRST_BETTING_ROUND;
        while (gamePhase != GamePhase.END_AFTER_FOURTH_BETTING_ROUND && gamePhase != GamePhase.END_ALL_FOLDED) {
            var temp = decisionStack.removeFirst();
            var currentPlayer = decision(temp.getFirst(), temp.getSecond());

            var response1 = errorObserver.poll(sleepTime, TimeUnit.MILLISECONDS);
            if (response1 != null) {
                assertEquals("You're not the current player", response1.getMessage());
            }

            var response = getNewest(gameStateObserver);
            gamePhase = response == null ? gamePhase : response.getGamePhase();
        }
    }

    @Test
    public void basicPlaylistTest() throws InterruptedException {
        var settingsObsesrver = subscribe(session, String.format("/topic/games/%s/settings", gameId),
                SettingsWsDTO.class);

        var settings = new SettingsWsDTO();
        settings.setPlaylistUrl("https://www.youtube.com/watch?v=HnIdtbV_TDU&list=PLjT6ePOFLFf3gHO_fXXmikcipOV3ZLYB0");
        settings.setLanguage(Language.ENGLISH);
        settings.setBigBlind(100);
        settings.setSmallBlind(100);
        settings.setInitialBalance(100);

        session.send(String.format("/app/games/%s/settings", gameId), settings);
        var response = settingsObsesrver.poll(10, TimeUnit.SECONDS);
        session.send(String.format("/app/games/%s/start", gameId), "");
        assertNotEquals(null, response);
    }
   
    @Test
    public void tinyPlaylistTest() throws InterruptedException {
        var settingsObsesrver = subscribe(session, String.format("/topic/games/%s/settings", gameId),
                SettingsWsDTO.class);

        var settings = new SettingsWsDTO();
        settings.setPlaylistUrl("https://www.youtube.com/playlist?list=PL3LitEY3lTxVfnzAcIdwygQfsrpiggNzn");
        settings.setLanguage(Language.ENGLISH);
        settings.setBigBlind(100);
        settings.setSmallBlind(100);
        settings.setInitialBalance(100);

        session.send(String.format("/app/games/%s/settings", gameId), settings);
        var response = settingsObsesrver.poll(10, TimeUnit.SECONDS);
        session.send(String.format("/app/games/%s/start", gameId), "");
        assertNotEquals(null, response);
    }

    @Test
    public void privatePlaylistTest() throws IllegalStateException, IOException, InterruptedException {
        var playlistUrl = "https://www.youtube.com/watch?v=8RTR1Ag0rhQ&list=PLS9WZcsKGOeCVp6uMq3gROdIBTQFrR6ma";

        assertThrows(IllegalStateException.class, () -> YTAPIManager.checkPlaylistUrl(playlistUrl));
    }

    private void fillGame() {
        var p1 = new PlayerDTO("FredericA", "A");
        var p2 = new PlayerDTO("FredericB", "B");
        var p3 = new PlayerDTO("FredericC", "C");
        var p4 = new PlayerDTO("FredericD", "D");
        var p5 = new PlayerDTO("FredericE", "E");
        var players = List.of(p1, p2, p3, p4, p5);

        for (var p : players) {
            session.send("/app/games/" + gameId + "/players/add", p);
        }
    }
    
    private List<HashMap<String,Object>> setupGame() throws InterruptedException {
        var settings = new SettingsWsDTO();
        settings.setBigBlind(20);
        settings.setSmallBlind(10);
        settings.setLanguage(Language.GERMAN);
        settings.setInitialBalance(500);
        settings.setPlaylistUrl(null);
        session.send(app + "/settings", settings);
        Thread.sleep(500);
        return getNewest(playerObserver);
    }
    
    private List<ArrayNode> startGame() throws JsonMappingException, JsonProcessingException, InterruptedException {
        var pA = new HandObserver("A");
        var pB = new HandObserver("B");
        var pC = new HandObserver("C");
        var pD = new HandObserver("D");
        var pE = new HandObserver("E");
        var pHost = new HandObserver("Host");

        var handObservers = List.of(pA, pB, pC, pD, pE, pHost);

        session.send("/app/games/" + gameId + "/noYtApi", "");
        Thread.sleep(500);
        session.send(app + "/start", "");

        List<ArrayNode> hands = new ArrayList<>();
        for (var o : handObservers) {
            hands.add((ArrayNode) o.getHand());
        }

        return hands;
    }

    int nullListRuns = 0;
    private List playerLog = new LinkedList();

    private void updatePlayerList() throws InterruptedException {
        var response = getNewest(playerObserver);
        if (response != null) {
            playerList = response;
        } else {
            nullListRuns++;
            if (nullListRuns > 20) {
                throw new IllegalStateException("There have been more than 20 PlayerList updates which did not change anything");
            }
        }
    }
    
    private void decision(Decision d) throws InterruptedException {
        decision(d, 0);
    }
    private String decision(Decision d, int raiseAmount) throws InterruptedException{
        updatePlayerList();
        LinkedHashMap<String,Object> currentPlayer = null;
        for (var p : playerList) {
            if ((Boolean) p.get("currentPlayer")) {
                currentPlayer = p;
                playerLog.add(p);
            }
        }

        if (currentPlayer == null) {
            throw new IllegalStateException("there must be a current player to make a move");
        }

        var decision = new DecisionWsDTO();
        decision.setDecision(d.toString());
        decision.setRaiseAmount(raiseAmount);
        session.send(app + "/players/" + currentPlayer.get("token") + "/decision", decision);
        return (String) currentPlayer.get("token");
    }

    static private <T> T getNewest(BlockingQueue<T> bq) throws InterruptedException {
        while (bq.size() > 1) {
            bq.poll();
        }
        return bq.poll(1, TimeUnit.SECONDS);
    }

    private String getContent(int handIndex, int commentIndex, List hands) {
        return ((ArrayNode) hands.get(handIndex)).get(commentIndex).get("first").get("content").asText();
    }

    class HandObserver {
        BlockingQueue<String> observer;
        ObjectMapper objectMapper;

        HandObserver(String playerId) {
            objectMapper = new ObjectMapper();
            observer = subscribe(sessionString, topic + "/players/" + playerId + "/hand", String.class);
        }

        public JsonNode getHand() throws InterruptedException, JsonMappingException, JsonProcessingException {
            var response = getNewest(observer);
            if (response == null) {
                throw new IllegalStateException("Hand is empty for some Reason");
            }
            return objectMapper.readTree(response);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.getenv("Config"));
        // var b = new ArrayBlockingQueue<Integer>(10); //size of queue matters !!!
        // for (int i = 0; i < 10; i++) {
        //     b.add(i);
        // }
        // var x = getNewest(b);
        // assertEquals(9, x);
        // System.out.println(x);
    }
}
