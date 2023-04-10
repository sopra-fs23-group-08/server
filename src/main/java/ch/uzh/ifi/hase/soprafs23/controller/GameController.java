package ch.uzh.ifi.hase.soprafs23.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
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
    public String createGame(@RequestBody String username) {
        TestGame newGame = gameService.createGame(username);
        return String.format("{gameId: %s}", newGame.getGameId());
    }

    /*
    @PostMapping("/games/{gameId}/players")
    public void addPlayer(@PathVariable String gameId, @RequestBody String username) {
        TestGame game = gameService.getGame(gameId);
        game.addPlayer(new TestPlayer(username));
    }*/

    @MessageMapping("/games/{gameId}/players")
    @SendTo("/topic/games/{gameId}/players")
    public String addPlayer(@DestinationVariable String gameId, String username) {
        TestGame game = gameService.getGame(gameId);
        game.addPlayer(new TestPlayer(username));
        return game.getPlayers().toString();
    }
}
