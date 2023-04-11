package ch.uzh.ifi.hase.soprafs23.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs23.service.GameService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = { "http://localhost:3000", "https://sopra-fs23-group-08-client.oa.r.appspot.com/" })
@RestController
public class GameController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

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

    /*
    @PostMapping("/games/{gameId}/players")
    public void addPlayer(@PathVariable String gameId, @RequestBody String username) {
        TestGame game = gameService.getGame(gameId);
        game.addPlayer(new TestPlayer(username));
    }
    */

    // the problem is: i return a strange JSON file here
    @MessageMapping("/games/{gameId}/players")
    @SendTo("/topic/games/{gameId}/players")
    public ArrayList<String> addPlayer(@DestinationVariable String gameId, String username) {
        gameService.addPlayer(gameId, username);
        return gameService.getPlayers(gameId);
    }
}
