package ch.uzh.ifi.hase.soprafs23.controller;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import net.bytebuddy.asm.Advice.Return;

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {
 
    final String serverURL = "http://localhost:8080";
    final String serverWsURL = "ws://localhost:8080/sopra-websocket";
    
    @LocalServerPort
    private Integer port;
    private WebSocketStompClient webSocketStompClient;
    private String gameId;
    private StompSession stompSession;
 
    @BeforeEach
    void setup() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        gameId = newGame();

        this.webSocketStompClient.setMessageConverter(new StringMessageConverter());
                
        this.stompSession = this.webSocketStompClient
                .connect(serverWsURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
    }
    
    @Test
    void connectionPossible() throws InterruptedException, ExecutionException, TimeoutException {
        StompSession session = webSocketStompClient
                .connect(serverWsURL, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        ;
        assertDoesNotThrow(() -> session.send("/app/echo", ""), "");
    }
    
    @Test
    void newGameTest() throws IOException, InterruptedException {
        System.out.println(gameId);
        assertNotEquals("No message available", gameId);
    }
    
    @Test
    void echoTest() throws Exception {

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
    public void conciseEchoTest() {
        var echoIn = subscribe(stompSession, "/echo", newBQ());
        stompSession.send("/app/echo", "body");
        responseAssert("pongbody", echoIn);

        stompSession.send("/app/echo", "2");
        responseAssert("pong2", echoIn);
    }
    
    private void responseAssert(String expected, BlockingQueue<String> destinationBQ){
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertEquals(expected, destinationBQ.poll()));
    }

    private BlockingQueue<String> subscribe(StompSession session, String topic, BlockingQueue<String> destinationBQ){
        session.subscribe("/topic" + topic, new StompFrameHandler() {
            
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }
            
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                destinationBQ.add((String) payload);
            }
        });
        return destinationBQ;
    }
    
    
    private BlockingQueue<String> newBQ(){
        return new ArrayBlockingQueue<>(1);
    }
    private String newGame() throws IOException, InterruptedException { //returns game id
        String URL = serverURL + "/games";
        var body = "{  \"Token\": \"testPlayer\", \"Name\": \"Tobias Peter\" }";

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

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:8080/games"))
    .header("Accept", "*/*")
    // .header("User-Agent", "Thunder Client (https://www.thunderclient.com)")
    .header("Content-Type", "application/json")
    .method("POST", HttpRequest.BodyPublishers.ofString("{  \"Token\": \"null\", \"Name\": \"hah\" }"))
    .build();
HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
    }
}