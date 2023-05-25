package ch.uzh.ifi.hase.soprafs23.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final String CLIENT_ID_PARAM = "clientId";
    private static final String GAME_ID_PARAM = "gameId";
    private static final String PLAYER_TOKEN_PARAM = "playerToken";


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String clientId = httpServletRequest.getParameter(CLIENT_ID_PARAM);
            String gameId = httpServletRequest.getParameter(GAME_ID_PARAM);
            String playerToken = httpServletRequest.getParameter(PLAYER_TOKEN_PARAM);
            if(clientId != null) { // Only add clientId if it exists
                attributes.put(CLIENT_ID_PARAM, clientId);
            }
            if(gameId != null) { // Only add gameId if it exists
                attributes.put(GAME_ID_PARAM, gameId);
            }
            if(playerToken != null) { // Only add playerToken if it exists
                attributes.put(PLAYER_TOKEN_PARAM, playerToken);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception exception) {
        // No need to implement anything here
    }
}
