package ch.uzh.ifi.hase.soprafs23.game;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.DecisionWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameStateWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.VideoDataWsDTO;

public interface GameController {

    String createGame(PlayerDTO playerDTO);

    PlayerDTO getHost(String gameId);

    Collection<PlayerWsDTO> addPlayer(String gameId, PlayerDTO playerDTO);

    Collection<PlayerWsDTO> removePlayer(String gameId, PlayerDTO playerDTO);

    SettingsWsDTO updateSettings(String gameId, SettingsWsDTO settingsWsDTO);

    void startGame(String gameId);

    void endGame(String gameId);

    void handlePlayerDecision(String gameId,
            String playerToken,
            DecisionWsDTO decisionWsDTO);

    void nextRound(String gameId);


}