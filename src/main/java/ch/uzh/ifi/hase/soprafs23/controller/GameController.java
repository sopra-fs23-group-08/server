package ch.uzh.ifi.hase.soprafs23.controller;


import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Settings;
import ch.uzh.ifi.hase.soprafs23.game.Decision;
import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Hand;
import ch.uzh.ifi.hase.soprafs23.game.VideoData;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000", "https://sopra-fs23-group-08-client.oa.r.appspot.com/" })
@RestController
public class GameController {

    // Field injection might lead to issues, maybe change to constructor injection
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createGame(@RequestBody PlayerWsDTO playerWsDTO) {

        Player player = DTOMapper.INSTANCE.convertPlayerWsDTOtoEntity(playerWsDTO);
        Game newGame = gameService.createGame(player);

        return String.format("{\"id\":\"%s\"}", newGame.getGameId());
    }

    @GetMapping("/games/{gameId}/host")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerWsDTO getHost(@PathVariable String gameId) {
        Game game = gameService.getGame(gameId);
        Player host = game.getHost();
        return DTOMapper.INSTANCE.convertEntityToPlayerWsDTO(host);
    }

    @MessageMapping("/games/{gameId}/players/add")
    @SendTo("/topic/games/{gameId}/players")
    public ArrayList<PlayerWsDTO> addPlayer(@DestinationVariable String gameId, PlayerWsDTO Player) {
        // convert DTO to entity
        Player player = DTOMapper.INSTANCE.convertPlayerWsDTOtoEntity(Player);

        // add player to game
        gameService.addPlayer(gameId, player);

        // convert player-list to DTOs
        return convertListToDTOs(gameService.getPlayers(gameId));
    }

    @MessageMapping("/games/{gameId}/players/remove")
    @SendTo("/topic/games/{gameId}/players")
    public ArrayList<PlayerWsDTO> removePlayer(@DestinationVariable String gameId, PlayerDTO playerDTO) {
        // convert DTO to entity
        Player player = new Player(playerDTO.getUsername(), playerDTO.getToken());

        // remove player from game
        gameService.removePlayer(gameId, player);

        // convert player-list to DTOs
        return convertListToDTOs(gameService.getPlayers(gameId));
    }

    @MessageMapping("/games/{gameId}/settings")
    @SendTo("/topic/games/{gameId}/settings")
    public SettingsWsDTO updateSettings(@DestinationVariable String gameId, SettingsWsDTO settingsWsDTO) {
        // update settings
        gameService.setGameSettings(gameId, settingsWsDTO);

        // send new settings to all players
        return settingsWsDTO;
    }

    // TODO not sure if the sendTo is required
    @MessageMapping("/games/{gameId}/start")
    @SendTo("/topic/games/{gameId}/start")
    public void startGame(@DestinationVariable String gameId) {
        // start game
        gameService.startGame(gameId);
    }

    @MessageMapping("/games/{gameId}/end")
    @SendTo("/topic/games/{gameId}/end")
    public void endGame(@DestinationVariable String gameId) {
        // end game
        // TODO create gameService method & notify all players
        // gameService.endGame(gameId);
    }

    @MessageMapping("/games/{gameId}/players/{playerToken}/decision")
    public void handlePlayerDecision(@DestinationVariable String gameId,
                                     @DestinationVariable String playerToken,
                                     DecisionWsDTO decisionWsDTO)
    {
        gameService.playerDecision(gameId, playerToken, decisionWsDTO);
    }

    @MessageMapping("/games/{gameId}/rounds/next")
    public void nextRound(@DestinationVariable String gameId) {
        gameService.nextRound(gameId);
    }

    /** OBSERVER ENDPOINT METHODS
     * these methods are invoked by gameService */
    public void gameStateChanged(String gameId, GameStateWsDTO gameStateWsDTO) {
        String destination = String.format("/topic/games/%s/state", gameId);
        messagingTemplate.convertAndSend(destination, gameStateWsDTO);
    }

    //TODO clarify the payload for this message
    public void playerStateChanged(String gameId) {
        String destination = String.format("/topic/games/%s/players", gameId);
        messagingTemplate.convertAndSend(destination, "");
    }

    public void newHand(String gameId, Player player, Hand hand) {
        String destination = String.format("/topic/games/%s/players/%s/hand", gameId, player.getToken());
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(hand.getComments());
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not convert hand to JSON");
        }

        messagingTemplate.convertAndSend(destination, responseBody);
    }

    public void newVideoData(String gameId, VideoData videoData) {
        String destination = String.format("/topic/games/%s/video", gameId);
        // convert to videoDataDTO
        // TODO figure out conversion with VideoData public attributes
    }

    /** HELPER METHODS */
    private ArrayList<PlayerWsDTO> convertListToDTOs(List<Player> players) {
        ArrayList<PlayerWsDTO> playerDTOs = new ArrayList<>();
        for (Player p : players) {
            playerDTOs.add(DTOMapper.INSTANCE.convertEntityToPlayerWsDTO(p));
        }
        return playerDTOs;
    }
}


