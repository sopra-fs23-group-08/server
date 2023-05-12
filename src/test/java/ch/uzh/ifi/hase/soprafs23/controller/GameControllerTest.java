package ch.uzh.ifi.hase.soprafs23.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import ch.uzh.ifi.hase.soprafs23.YTAPIManager.Language;
import ch.uzh.ifi.hase.soprafs23.game.GamePhase;
import ch.uzh.ifi.hase.soprafs23.rest.dto.DecisionWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameStateWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.VideoDataWsDTO;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.expr.NewArray;

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {
 
    final String serverURL = "http://localhost:8080";
    // final String serverWsURL = "ws://localhost:8080/sopra-websocket";
    static final String serverWsURL = "ws://localhost:8080/sopra-websocket";
    
    @LocalServerPort
    private Integer port;
    private String gameId;
    private StompSession stompSession;
 
    @BeforeEach
    void setup() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        gameId = newGame();
        stompSession = createStompSession(new MappingJackson2MessageConverter());
    }
    
    @Test
    void connectionPossible() throws InterruptedException, ExecutionException, TimeoutException {
        var webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        StompSession session = webSocketStompClient
                .connect(serverWsURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        ;
        assertDoesNotThrow(() -> session.send("/app/echo", ""), "");
    }
    
    @Test
    void newGameTest() throws IOException, InterruptedException {
        assertNotEquals("No message available", gameId);
        assertNotEquals(null , gameId);
    }
    
    @Test
    void echoTest() throws Exception {
        var webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new StringMessageConverter());

        StompSession session = webSocketStompClient
                .connect(serverWsURL, new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/topic/echo", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((String) payload);
            }
        });

        var msg = "ping";
        session.send("/app/echo", msg);

        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals("pong" + msg, blockingQueue.poll()));
    }

    @Test
    public void basicVideoDataReceiveTest() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        BlockingQueue<VideoDataWsDTO> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(serverWsURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/topic/echoVideoData", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return VideoDataWsDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                VideoDataWsDTO videoData = (VideoDataWsDTO) payload;
                blockingQueue.add(videoData);
            }
        });
        session.send("/app/echoDTO", "");

        VideoDataWsDTO response = blockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotEquals(null, response);
    }

    @Test
    public void sendDtoTest() throws InterruptedException, ExecutionException, TimeoutException {
        var webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        BlockingQueue<SettingsWsDTO> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(serverWsURL, new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/topic/echoSettings", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return SettingsWsDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                SettingsWsDTO settings = (SettingsWsDTO) payload;
                blockingQueue.add(settings);
            }
        });
        session.send("/app/echoDTO", "");

        var response = blockingQueue.poll(20, TimeUnit.SECONDS);
        assertNotEquals(null, response);

        var queue = subscribe(session, "/topic/echoSettings", SettingsWsDTO.class);
        session.send("/app/echoDTO", "");
        var response2 = queue.poll(1, TimeUnit.SECONDS);
        assertNotEquals(null, response2);
    }
    
    @Test
    public void receiveSettingsTest() throws InterruptedException {

        var settingsObserver = subscribe(stompSession, "/topic/echoSettings", SettingsWsDTO.class);
        stompSession.send("/app/echoDTO", "");
        var settings = settingsObserver.poll(1, TimeUnit.SECONDS);
        assertNotEquals(null, settings);
    }

    @Test
    public void receiveDecisionTest() throws InterruptedException {
        var decisionObserver = subscribe(stompSession, "/topic/echoDecision", DecisionWsDTO.class);
        stompSession.send("/app/echoDTO", "");
        var decision = decisionObserver.poll(1, TimeUnit.SECONDS);
        assertNotEquals(null, decision);
    }

    @Test
    public void receiveGameStateTest() throws InterruptedException {
        var gameStateObserver = subscribe(stompSession, "/topic/echoGameState", GameStateWsDTO.class);
        stompSession.send("/app/echoDTO", "");
        var gameState = gameStateObserver.poll(1, TimeUnit.SECONDS);
        assertNotEquals(null, gameState);
    }

    @Test
    public void receivePlayerTest() throws InterruptedException {
        var playerObserver = subscribe(stompSession, "/topic/echoPlayer", PlayerWsDTO.class);
        stompSession.send("/app/echoDTO", "");
        var player = playerObserver.poll(1, TimeUnit.SECONDS);
        assertNotEquals(null, player);
    }

    @Test
    public void receivePlayerCollectionTest() throws InterruptedException {
        var playerCollectionObserver = subscribe(stompSession, "/topic/echoPlayerCollection", List.class);
        stompSession.send("/app/echoDTO", "");
        var playerCollection = playerCollectionObserver.poll(1, TimeUnit.SECONDS);
        assertNotEquals(null, playerCollection);
        assertNotEquals(0, playerCollection.size());
    }

    @Test
    public void receiveVideoDataTest() throws InterruptedException {
        var videoDataObserver = subscribe(stompSession, "/topic/echoVideoData", VideoDataWsDTO.class);
        stompSession.send("/app/echoDTO", "");
        var videoData = videoDataObserver.poll(1, TimeUnit.SECONDS);
        assertNotEquals(null, videoData);
    }

    @Test
    public void conciseEchoTest() throws InterruptedException, ExecutionException, TimeoutException {
        stompSession = createStompSession(new StringMessageConverter());

        var echoIn = subscribe(stompSession, "/topic/echo", String.class);
        stompSession.send("/app/echo", "body");
        responseAssert("pongbody", echoIn);

        stompSession.send("/app/echo", "2");
        responseAssert("pong2", echoIn);

        stompSession.send("/app/echo", "2");
        var response = echoIn.poll(2, TimeUnit.SECONDS);
        assertEquals("pong2", response);
    }

    @Test
    public void basicSettingTest() throws InterruptedException {
        var settingsObsesrver = subscribe(stompSession, String.format("/topic/games/%s/settings", gameId),
                SettingsWsDTO.class);
        
        var settings = new SettingsWsDTO();
        settings.setPlaylistUrl("list=PL6HF94r1ogByYa2xFAXIE_1Pw-K0AU_Vd");
        settings.setLanguage(Language.ENGLISH);
        settings.setBigBlind(100);
        settings.setSmallBlind(100);
        settings.setInitialBalance(100);

        stompSession.send(String.format("/app/games/%s/settings", gameId), settings);
        var response = settingsObsesrver.poll(10, TimeUnit.SECONDS);
        assertNotEquals(null, response);
    }
    
    @Test
    public void basicStartGameTest() throws InterruptedException {
        var gameStateObserver = subscribe(stompSession, "/topic/games/" + gameId + "/state", GameStateWsDTO.class);
        var videoDataObserver = subscribe(stompSession, "/topic/games/" + gameId + "/video", VideoDataWsDTO.class);

        
        stompSession.send("/app/games/" + gameId + "/start", "");
        var response = gameStateObserver.poll(10, TimeUnit.SECONDS);
        var response2 = videoDataObserver.poll(10, TimeUnit.SECONDS);

        assertNotEquals(null, response2);
        assertNotEquals(null, response);
    }

    @Test
    public void basicPlaylistCheckTest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/games/helpers/playlist"))
            .header("Accept", "*/*")
            .header("User-Agent", "Thunder Client (https://www.thunderclient.com)")
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"playlistUrl\":\"list=PL6HF94r1ogByYa2xFAXIE_1Pw-K0AU_Vd\"\n}"))
            .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("", response.body());

        HttpRequest request2 = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/games/helpers/playlist"))
            .header("Accept", "*/*")
            .header("User-Agent", "Thunder Client (https://www.thunderclient.com)")
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"playlistUrl\":\"lis=PL6HF94r1ogByYa2xFAXIE_1Pw-K0AU_Vd\"\n}"))
            .build();
        HttpResponse<String> response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());
        assertNotEquals("", response2.body());
    }
    
    @Test
    public void basicGetHostTest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/games/" + gameId + "/host"))
                .header("Accept", "*/*")
                .header("User-Agent", "Thunder Client (https://www.thunderclient.com)")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("{\"name\":\"Tobias Peter\",\"token\":\"testPlayer\"}", response.body());
    }
    

    @Test
    public void basicAddPlayerTest() throws InterruptedException {
        var playersObserver = subscribe(stompSession, String.format("/topic/games/%s/players", gameId), List.class);
        var player = new PlayerDTO();
        player.setName("Sebastian Pamela");
        player.setToken("secondPlayer");
        stompSession.send("/app/games/" + gameId + "/players/add", player);

        var response = playersObserver.poll(10, TimeUnit.SECONDS);
        assertNotEquals(null, response);
        assertNotEquals(0, response.size());
    }
    
    @Test
    public void basicRemovePlayerTest() throws InterruptedException {
        var playersObserver = subscribe(stompSession, String.format("/topic/games/%s/players", gameId), List.class);
        var player = new PlayerDTO();
        player.setName("Sebastian Pamela");
        player.setToken("secondPlayer");
        stompSession.send("/app/games/" + gameId + "/players/add", player);
        stompSession.send("/app/games/" + gameId + "/players/remove", player);

        var response = playersObserver.poll(10, TimeUnit.SECONDS);
        assertNotEquals(null, response);
        assertNotEquals(0, response.size());
    }

    @Test
    public void basicDecisionPlayerTest() throws InterruptedException {
        var playersObserver = subscribe(stompSession, String.format("/topic/games/%s/players", gameId), List.class);

        var decision = new DecisionWsDTO();
        decision.setDecision("FOLD"); //change to decision
        decision.setRaiseAmount(100);
        stompSession.send(String.format("/app/games/%s/players/%s", gameId, "testPlayer"), decision);


        var response = playersObserver.poll(10, TimeUnit.SECONDS);
        assertNotEquals(null, response);
        assertNotEquals(0, response.size());
    }

    @Test
    public void basicNextRoundTest() throws InterruptedException {
        var stateObserver = subscribe(stompSession, String.format("/topic/games/%s/state", gameId),
                GameStateWsDTO.class);

        stompSession.send(String.format("/app/games/%s/rounds/next", gameId), "");


        var response = stateObserver.poll(10, TimeUnit.SECONDS);
        assertNotEquals(null, response);
    }
    //todo
    // String destination = String.format("/topic/games/%s/state", gameId);
    //todo
    // String destination = String.format("/topic/games/%s/players", gameId);
    //todo
    // String destination = String.format("/topic/games/%s/players/%s/hand", gameId, player.getToken());
    //todo
    // String destination = String.format("/topic/games/%s/video", gameId);
    //todo
    //more complex game run trough


    
    private <T> void responseAssert(Object expected, BlockingQueue<T> destinationBQ){
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(expected, destinationBQ.poll()));
    }

    static private <T> BlockingQueue<T> subscribe(StompSession session, String endPoint,
            Class<T> classType) {
        var destinationBQ = new ArrayBlockingQueue<T>(1);
        session.subscribe(endPoint, new StompFrameHandler() {
            
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return classType;
            }
            
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                destinationBQ.add((T) payload);
            }
        });
        return destinationBQ;
    }
    

    private String newGame() throws IOException, InterruptedException { //returns game id
        String URL = serverURL + "/games";
        var body = "{\n  \"name\":\"Tobias Peter\",\n  \"token\":\"testPlayer\"\n}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Accept", "*/*")
                // .header("User-Agent", "Thunder Client (https://www.thunderclient.com)")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Pattern pattern = Pattern.compile("\"id\":\"");
        String[] s1 = pattern.split(response.body());
        String[] s2;
        if (s1.length < 2) {
            throw new IllegalStateException("There is no message in response: " + URL);
        } else {
            s2 = s1[1].split("\"");
        }
        return s2[0];
    }
    
    static private StompSession createStompSession(MessageConverter converter) throws InterruptedException, ExecutionException, TimeoutException {
        var webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));


        webSocketStompClient.setMessageConverter(converter);
                
        var stompSession = webSocketStompClient
                .connect(serverWsURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        return stompSession;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        
        var webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        BlockingQueue<SettingsWsDTO> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(serverWsURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/topic/echoSettings", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return SettingsWsDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                SettingsWsDTO settings = (SettingsWsDTO) payload;
                blockingQueue.add(settings);
            }
        });
        session.send("/app/echoSettings", "");

        var response = blockingQueue.poll(20, TimeUnit.SECONDS);
        System.out.println(response);

        var queue = subscribe(session, "/topic/echoSettings", SettingsWsDTO.class);
        session.send("/app/echoSettings", "");
        var response2 = queue.poll(1, TimeUnit.SECONDS);
        System.out.println(response2);
    }
}