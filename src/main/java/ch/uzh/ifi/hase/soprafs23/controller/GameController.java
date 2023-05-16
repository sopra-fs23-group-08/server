package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.MutablePlayer;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.game.Decision;
import ch.uzh.ifi.hase.soprafs23.game.GamePhase;
import ch.uzh.ifi.hase.soprafs23.game.Hand;
import ch.uzh.ifi.hase.soprafs23.game.HandOwnerWinner;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
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
    public synchronized String createGame(@RequestBody PlayerDTO playerDTO) {
        MutablePlayer player = DTOMapper.INSTANCE.convertPlayerDTOtoEntity(playerDTO);
        String gameId = gameService.createGame(new Player(player));
        return String.format("{\"id\":\"%s\"}", gameId);
    }

    @GetMapping("/games/{gameId}/lobby")
    @ResponseStatus(HttpStatus.OK)
    public synchronized void isLobbyJoinable(@PathVariable String gameId) {
        gameService.isLobbyJoinable(gameId);
    }
    @PostMapping("/games/helpers/playlist")
    @ResponseStatus(HttpStatus.OK)
    public synchronized void checkPlaylistUrl(@RequestBody PlaylistDTO playlistDTO) {
        gameService.checkPlaylist(playlistDTO.getPlaylistUrl());
    }

    @GetMapping("/games/{gameId}/host")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public synchronized PlayerDTO getHost(@PathVariable String gameId) {
        MutablePlayer host = gameService.getHost(gameId);
        return DTOMapper.INSTANCE.convertEntityToPlayerDTO(host);
    }

    @MessageMapping("/games/{gameId}/players/add")
    @SendTo("/topic/games/{gameId}/players")
    public synchronized Collection<PlayerWsDTO> addPlayer(@DestinationVariable String gameId, PlayerDTO playerDTO) {
        try {
            MutablePlayer player = DTOMapper.INSTANCE.convertPlayerDTOtoEntity(playerDTO);
            // add player to game
            gameService.addPlayer(gameId, new Player(player));
            // convert player-list to DTOs
        } catch (ResponseStatusException e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", new Exception(e.getMessage(), e.getCause()));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", e);
        }
        return gameService.getPlayers(gameId);
    }
    

    @MessageMapping("/games/{gameId}/players/remove")
    @SendTo("/topic/games/{gameId}/players")
    public synchronized Collection<PlayerWsDTO> removePlayer(@DestinationVariable String gameId, PlayerDTO playerDTO) {
        try {
            // convert DTO to entity
            MutablePlayer player = DTOMapper.INSTANCE.convertPlayerDTOtoEntity(playerDTO);
            // remove player from game
            gameService.removePlayer(gameId, new Player(player));
            // convert player-list to DTOs
        } catch (ResponseStatusException e) {
                    messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", new Exception(e.getMessage(), e.getCause()));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", e);
        }
        return gameService.getPlayers(gameId);
    }

    @MessageMapping("/games/{gameId}/settings")
    @SendTo("/topic/games/{gameId}/settings")
    public synchronized SettingsWsDTO updateSettings(@DestinationVariable String gameId, SettingsWsDTO settingsWsDTO) {
        try {
            // update settings
            gameService.setGameSettings(gameId, settingsWsDTO);
            // send new settings to all players
        } catch (ResponseStatusException e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", new Exception(e.getMessage(), e.getCause()));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", e);
        }
        return settingsWsDTO;
    }

    @MessageMapping("/games/{gameId}/start")
    @SendTo("/topic/games/{gameId}/start")
    public synchronized String startGame(@DestinationVariable String gameId) {
        // start game
        try {
            gameService.startGame(gameId);
        } catch (ResponseStatusException e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", new Exception(e.getMessage(), e.getCause()));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", e);
        }
        return "Game started.";
    }

    @MessageMapping("/games/{gameId}/end")
    public synchronized void endGame(@DestinationVariable String gameId) {
        // end game
        // TODO create gameService method & notify all players
        // gameService.endGame(gameId);
    }

    @MessageMapping("/games/{gameId}/players/{playerToken}/decision")
    public synchronized void handlePlayerDecision(@DestinationVariable String gameId,
                                     @DestinationVariable String playerToken,
                                     DecisionWsDTO decisionWsDTO)
    {
        try {
            gameService.playerDecision(gameId, playerToken, decisionWsDTO);
        } catch (ResponseStatusException e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", new Exception(e.getMessage(), e.getCause()));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", e);
        }
    }

    @MessageMapping("/games/{gameId}/rounds/next")
    public synchronized void nextRound(@DestinationVariable String gameId) {
        try {
            gameService.nextRound(gameId);
        } catch (ResponseStatusException e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error",
                    new Exception(e.getMessage(), e.getCause()));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", e);
        }
    }

    @MessageMapping("/games/{gameId}/close")
    public void closeGame(@DestinationVariable String gameId) {
        try {
            gameService.closeGame(gameId);
        } catch (ResponseStatusException e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error",
                    new Exception(e.getMessage(), e.getCause()));
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/games/" + gameId + "/error", e);
        }
    }

    /** OBSERVER ENDPOINT METHODS
     * these methods are invoked by gameService */
    public synchronized void gameStateChanged(String gameId, GameStateWsDTO gameStateWsDTO) {
        String destination = String.format("/topic/games/%s/state", gameId);
        messagingTemplate.convertAndSend(destination, gameStateWsDTO);
    }

    public synchronized void playerStateChanged(String gameId, Collection<PlayerWsDTO> playersDTOList) {
        String destination = String.format("/topic/games/%s/players", gameId);
        messagingTemplate.convertAndSend(destination, playersDTOList);
    }

    public synchronized void showdown(String gameId, Collection<HandOwnerWinner> handOwnerWinners) {
        String destination = String.format("/topic/games/%s/showdown", gameId);
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(handOwnerWinners);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not convert showdown to JSON");
        }

        messagingTemplate.convertAndSend(destination, responseBody);
    }

    public synchronized void newHand(String gameId, Player player, Hand hand) {
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

    public synchronized void newVideoData(String gameId, VideoDataWsDTO videoData) {
        String destination = String.format("/topic/games/%s/video", gameId);
        messagingTemplate.convertAndSend(destination, videoData);
    }

    @MessageMapping("/echo")
    public synchronized void echo(String msg) {
        echoResponse(msg);
    }

    public synchronized void echoResponse(String msg) {
        messagingTemplate.convertAndSend("/topic/echo", "pong" + msg);
    }

    /**
     * @throws JsonProcessingException
     * @throws MessagingException
     * 
     */
    @MessageMapping("/echoDTO")
    public synchronized void settingsEcho() throws MessagingException, JsonProcessingException {

        var player = new PlayerWsDTO("playerToken", "Peter Toggenburger", 0, Decision.NOT_DECIDED, false, false, false);
        Collection<PlayerWsDTO> playerCollection = new ArrayList<>();
        playerCollection.add(player);
        ObjectMapper objectMapper = new ObjectMapper();
        var hand = new Hand();

        messagingTemplate.convertAndSend("/topic/echoVideoData", new VideoDataWsDTO());
        messagingTemplate.convertAndSend("/topic/echoPlayer", player);
        messagingTemplate.convertAndSend("/topic/echoPlayerCollection", playerCollection);
        messagingTemplate.convertAndSend("/topic/echoGameState",
                new GameStateWsDTO(0, 0, false, "playerToken", GamePhase.LOBBY));
        messagingTemplate.convertAndSend("/topic/echoDecision", new DecisionWsDTO());
        messagingTemplate.convertAndSend("/topic/echoSettings", new SettingsWsDTO());
        messagingTemplate.convertAndSend("/topic/echoHand", objectMapper.writeValueAsString(hand.getComments()));
        messagingTemplate.convertAndSend("/topic/echoErrorA", new IllegalStateException("Test error"));
    }

    String abc = "abc";
    @MessageMapping("/mutexA")
    public void mutexA() throws InterruptedException {
        
        synchronized (abc) {
            if (abc == "abc") {
                Thread.sleep(1000);
                abc = abc + "d";
            }
        }
        System.out.println(abc);
    }

    @MessageMapping("/mutexB")
    public void mutexB(){
        abc = "hacke_";
    }
}


