package ch.uzh.ifi.hase.soprafs23.controller;


import ch.uzh.ifi.hase.soprafs23.entity.MutablePlayer;
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
import java.util.Collection;
//todo showdown

@CrossOrigin(origins = { "http://localhost:3000/", "https://sopra-fs23-group-08-client.oa.r.appspot.com/" })
@RestController
public class GameController {

    // Field injection might lead to issues, maybe change to constructor injection
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    private final GameService gameService;

    GameController() {
        this.gameService = new GameService(this);
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String createGame(@RequestBody PlayerDTO playerDTO) {

        MutablePlayer player = DTOMapper.INSTANCE.convertPlayerDTOtoEntity(playerDTO);
        String gameId = gameService.createGame(new Player(player));
        return String.format("{\"id\":\"%s\"}", gameId);
    }

    @GetMapping("/games/{gameId}/host")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerDTO getHost(@PathVariable String gameId) {
        MutablePlayer host = gameService.getHost(gameId);
        return DTOMapper.INSTANCE.convertEntityToPlayerDTO(host);
    }

    @MessageMapping("/games/{gameId}/players/add")
    @SendTo("/topic/games/{gameId}/players")
    public Collection<PlayerWsDTO> addPlayer(@DestinationVariable String gameId, PlayerDTO playerDTO) {
        MutablePlayer player = DTOMapper.INSTANCE.convertPlayerDTOtoEntity(playerDTO);
        // add player to game
        gameService.addPlayer(gameId, new Player(player));
        // convert player-list to DTOs
        return gameService.getPlayers(gameId);
    }
    
    @MessageMapping("/echo")
    @SendTo("/topic/echo")
    public String echo(String msg) {
        return "pong" + msg;
    }

    @MessageMapping("/games/{gameId}/players/remove")
    @SendTo("/topic/games/{gameId}/players")
    public Collection<PlayerWsDTO> removePlayer(@DestinationVariable String gameId, PlayerDTO playerDTO) {
        // convert DTO to entity
        MutablePlayer player = DTOMapper.INSTANCE.convertPlayerDTOtoEntity(playerDTO);
        // remove player from game
        gameService.removePlayer(gameId, new Player(player));
        // convert player-list to DTOs
        return gameService.getPlayers(gameId);
    }

    @MessageMapping("/games/{gameId}/settings")
    @SendTo("/topic/games/{gameId}/settings")
    public SettingsWsDTO updateSettings(@DestinationVariable String gameId, SettingsWsDTO settingsWsDTO) {
        // update settings
        gameService.setGameSettings(gameId, settingsWsDTO);
        // send new settings to all players
        return settingsWsDTO;
    }

    @MessageMapping("/games/{gameId}/start")
    @SendTo("/topic/games/{gameId}/start")
    public String startGame(@DestinationVariable String gameId) {
        // start game
        // TODO fix gameService method
        gameService.startGame(gameId);
        return "Game started.";
    }

    @MessageMapping("/games/{gameId}/end")
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

    public void playerStateChanged(String gameId, Collection<PlayerWsDTO> playersDTOList) {
        String destination = String.format("/topic/games/%s/players", gameId);
        messagingTemplate.convertAndSend(destination, playersDTOList);
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

    public void newVideoData(String gameId, VideoDataWsDTO videoData) {
        String destination = String.format("/topic/games/%s/video", gameId);
        messagingTemplate.convertAndSend(destination, videoData );
    }

}


