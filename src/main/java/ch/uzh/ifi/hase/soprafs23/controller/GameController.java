package ch.uzh.ifi.hase.soprafs23.controller;
//responsible for handling incoming HTTP requests related to games and player information. 

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.TestGame;
import ch.uzh.ifi.hase.soprafs23.entity.TestPlayer;
import ch.uzh.ifi.hase.soprafs23.game.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TestGameGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TestPlayerWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

@CrossOrigin(origins = { "http://localhost:3000", "https://sopra-fs23-group-08-client.oa.r.appspot.com/" }) //used to specify the allowed origins for cross-origin resource sharing.
@RestController //This annotation is applied to a class to mark it as a request handler. 
//Spring RestController annotation is used to create RESTful web services using Spring MVC.
public class GameController{

    SimpMessagingTemplate messagingTemplate; //Spring utility class that can be used to send messages to WebSocket clients.


    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TestGameGetDTO createGame(TestPlayerWsDTO playerWsDTO) {
        echoResponse();
        // convert DTO to entity
        TestPlayer player = DTOMapper.INSTANCE.convertTestPlayerWsDTOtoEntity(playerWsDTO);

        // create new Game
        TestGame newGame = gameService.createGame(player);

        return DTOMapper.INSTANCE.convertEntityToTestGameGetDTO(newGame);
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

    @GetMapping("/games/{gameId}/host")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TestPlayer getHost(@PathVariable String gameId) {
        return gameService.getHost(gameId);
    }

    //maps a STOMP message to a specific handler method. In this case, /games/{gameId}/players is mapped to the addPlayer method.
    @MessageMapping("/games/{gameId}/players/join")
    @SendTo("/topic/games/{gameId}/players")
    public ArrayList<TestPlayerWsDTO> addPlayer(@DestinationVariable String gameId, TestPlayerWsDTO playerWsDTO) {
        // convert DTO to entity
        TestPlayer player = DTOMapper.INSTANCE.convertTestPlayerWsDTOtoEntity(playerWsDTO);
        // add player to correct game
        gameService.addPlayer(gameId, player);
        // convert player entities to DTOs
        ArrayList<TestPlayerWsDTO> playerWsDTOS = new ArrayList<>();
        ArrayList<TestPlayer> players = gameService.getPlayers(gameId);

        for (TestPlayer p : players) {
            playerWsDTOS.add(DTOMapper.INSTANCE.convertEntityToTestPlayerWsDTO(p));
        }
        return playerWsDTOS;
    }

    @MessageMapping("/echo")
    public void echo(){
        echoResponse();
    }

    

    public void echoResponse(){
        messagingTemplate.convertAndSend("/topic/test", "Hello World");
    }

    @MessageMapping("/games/{gameId}/players/leave")
    @SendTo("/topic/games/{gameId}/players")
    public ArrayList<TestPlayerWsDTO> removePlayer(@DestinationVariable String gameId, TestPlayerWsDTO playerWsDTO) {
        // convert DTO to entity
        TestPlayer player = DTOMapper.INSTANCE.convertTestPlayerWsDTOtoEntity(playerWsDTO);
        // add player to correct game
        gameService.removePlayer(gameId, player);
        // convert player entities to DTOs
        ArrayList<TestPlayerWsDTO> playerWsDTOS = new ArrayList<>();
        ArrayList<TestPlayer> players = gameService.getPlayers(gameId);

        for (TestPlayer p : players) {
            playerWsDTOS.add(DTOMapper.INSTANCE.convertEntityToTestPlayerWsDTO(p));
        }
        return playerWsDTOS;
    }


    @MessageMapping("/games/{gameId}/settings")
    @SendTo("/topic/games/{gameId}/settings")
    public void updateSettings(@DestinationVariable String gameId, SettingsDTO settingsDTO) {
        // TODO come up with a DTO for settings --> part of a game entity or a separate entity?
    }


    /* OBSERVER METHODS
    *  Maybe it makes sense to fuse some of these methods into one, e.g. gameStateChanged
    * */

    @SendTo("/topic/games/{gameId}/state/general")
    public void gameStateChanged(String gameId, Integer score) {
        // send GameStateDTO
    }

    @SendTo("/topic/games/{gameId}/players/{playerId}/hand")
    public void newHand(String gameId, Player player, Hand hand) {
        // send PlayerHandDTO
    }

}
