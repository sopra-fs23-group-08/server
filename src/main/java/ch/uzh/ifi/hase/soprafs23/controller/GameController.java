package ch.uzh.ifi.hase.soprafs23.controller;
//responsible for handling incoming HTTP requests related to games and player information. 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs23.service.GameService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = { "http://localhost:3000", "https://sopra-fs23-group-08-client.oa.r.appspot.com/" }) //used to specify the allowed origins for cross-origin resource sharing.
@RestController //This annotation is applied to a class to mark it as a request handler. 
//Spring RestController annotation is used to create RESTful web services using Spring MVC.
public class GameController {

    @Autowired
    SimpMessagingTemplate messagingTemplate; //Spring utility class that can be used to send messages to WebSocket clients.

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public HashMap<String, String> createGame(String username) {
        TestGame newGame = gameService.createGame(username);
        String id = newGame.getId();
        HashMap<String, String> response = new HashMap<>();
        response.put("id", id);
        return response;
    }

    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TestGame getGame(@PathVariable String gameId) {
        return gameService.getGame(gameId);
    }
    /*
    @PostMapping("/games/{gameId}/players")
    public void addPlayer(@PathVariable String gameId, @RequestBody String username) {
        TestGame game = gameService.getGame(gameId);
        game.addPlayer(new TestPlayer(username));
    }
    */

    //maps a STOMP message to a specific handler method. In this case, /games/{gameId}/players is mapped to the addPlayer method.
    @MessageMapping("/games/{gameId}/players")
    @SendTo("/topic/games/{gameId}/players")
    public ArrayList<String> addPlayer(@DestinationVariable String gameId, String username) {
        gameService.addPlayer(gameId, username);
        ArrayList<String> players = gameService.getPlayerUsernames(gameId);
        return players;
    }
}
