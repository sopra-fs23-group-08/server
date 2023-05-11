package ch.uzh.ifi.hase.soprafs23.websocket; // or socket?


import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


//configuration class for WebSocket messaging using the Spring Framework.

@Configuration //indicate that it is a Spring configuration class.
@EnableWebSocketMessageBroker // enables WebSocket message handling, backed by a message broker.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    private static final String WEBSOCKET_PREFIX = "/topic"; //prefix used for the destinations of WebSocket messages.In this case, it is set to "/topic".
    private static final String WEBSOCKET_MESSAGE_PREFIX = "/app"; //prefix used to filter destinations handled by methods annotated with @MessageMapping.
    private static final String WEBSOCKET_SUFFIX = "/sopra-websocket"; //suffix for the WebSocket connection endpoint. In this case, it is set to "/sopra-websocket".
    private static final String ORIGIN_LOCALHOST = "http://localhost:3000"; //
    private static final String ORIGIN_PROD = "https://sopra-fs23-group-08-client.oa.r.appspot.com";
    // private static final String LOCAL_TEST = "*";

    @Override
    public void configureMessageBroker(@NotNull MessageBrokerRegistry config) { // implements the default method in 
        //WebSocketMessageBrokerConfigurer to configure the message broker. It starts by calling enableSimpleBroker() to enable 
        //a simple memory-based message broker to carry the greeting messages back to the client on destinations prefixed with /topic.
        config.enableSimpleBroker(WEBSOCKET_PREFIX);
        config.setApplicationDestinationPrefixes(WEBSOCKET_MESSAGE_PREFIX);
    }

    @Override
    public void registerStompEndpoints(@NotNull StompEndpointRegistry registry) { 
        //registers the /sopra-websocket endpoint, enabling SockJS fallback 
        //options so that alternate transports can be used if WebSocket is not available.
        registry.addEndpoint(WEBSOCKET_SUFFIX)
                .setAllowedOrigins(ORIGIN_LOCALHOST, ORIGIN_PROD)
                .withSockJS(); //The SockJS client will attempt to connect to /sopra-websocket and use the best available transport
    }
}