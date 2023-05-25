package ch.uzh.ifi.hase.soprafs23.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.HashMap;

import org.aspectj.lang.annotation.Before;
import org.aspectj.weaver.ast.Call;
import org.hibernate.mapping.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs23.YTAPIManager.Language;
import ch.uzh.ifi.hase.soprafs23.controller.GameController;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.game.Decision;
import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.GamePhase;
import ch.uzh.ifi.hase.soprafs23.game.Hand;
import ch.uzh.ifi.hase.soprafs23.game.HandOwnerWinner;
import ch.uzh.ifi.hase.soprafs23.rest.dto.DecisionWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameStateWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SettingsWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.VideoDataWsDTO;

public class GameServiceTest {

    static GameService gameService;
    static TestGameController gc;

    @BeforeAll
    static public void setup() {
        gc = new TestGameController();
        gameService = new GameService(gc);
    }
    

    @Test
    public void oneTestToRuleThemAll() {
        assertThrows(Exception.class, () -> gameService.addPlayer("gameId", new Player("A")));// game does not exist

        var gameId = gameService.createGame(new Player("Host"));
        var A = new Player("A");
        assertDoesNotThrow(() -> gameService.addPlayer(gameId, A));
        assertDoesNotThrow(() -> gameService.removePlayer(gameId, A));
        assertThrows(Exception.class, () -> gameService.removePlayer(gameId, A)); //cant remove non existing player
        assertDoesNotThrow(() -> gameService.addPlayer(gameId, A));
        assertThrows(Exception.class, () -> gameService.addPlayer(gameId, A)); //cant add same player twice
        assertThrows(Exception.class, () -> gameService.addPlayer(gameId, new Player())); //cant add null player
        assertDoesNotThrow(() -> gameService.addPlayer(gameId, new Player("rando")));
        assertDoesNotThrow(() -> gameService.addPlayer(gameId, new Player("rando")));
        assertDoesNotThrow(() -> gameService.addPlayer(gameId, new Player("rando")));
        assertDoesNotThrow(() -> gameService.addPlayer(gameId, new Player("rando")));
        assertThrows(Exception.class, () -> gameService.addPlayer(gameId, new Player("rando"))); //max six players

        var settings = new SettingsWsDTO();
        settings.setBigBlind(20);
        settings.setSmallBlind(10);
        settings.setInitialBalance(100);
        settings.setLanguage(Language.ENGLISH);
        settings.setPlaylistUrl("");
        gameService.noYtApi(gameId);
        assertThrows(Exception.class, () -> gameService.checkPlaylist("gameId, settings"));
        assertDoesNotThrow(() -> gameService.checkPlaylist("list=PL6HF94r1ogByYa2xFAXIE_1Pw-K0AU_Vd"));

        settings.setPlaylistUrl("list=PL6HF94r1ogByYa2xFAXIE_1Pw-K0AU_Vd");
        assertDoesNotThrow(() -> gameService.setGameSettings(gameId, settings));

        assertThrows(Exception.class,
                () -> gameService.playerDecision(gameId, A.getToken(), new DecisionWsDTO("CALL", 0)));
        assertThrows(Exception.class,
                () -> gameService.playerDecision(gameId, "A.getToken()", new DecisionWsDTO("CALL", 0)));

        assertDoesNotThrow(() -> gameService.startGame(gameId));
        assertThrows(Exception.class, () -> gameService.startGame(gameId)); //game already started

        assertThrows(Exception.class, () -> gameService.setGameSettings(gameId, settings)); // settings can't be changed after game start
        assertThrows(Exception.class, () -> gameService.addPlayer(gameId, new Player("rando"))); //none can join after game has started.
        assertEquals(gc.playerList.size(), 6);
        var winnerTracker = new HashMap<>();

        //first round playing
        while (gc.gameState.getGamePhase() != GamePhase.END_AFTER_FOURTH_BETTING_ROUND) {
            for (var player : gc.playerList) {
                var playerId = player.getToken();
                if (player.isCurrentPlayer()) {
                    gameService.playerDecision(gameId, playerId, new DecisionWsDTO("CALL", 0));
                } else {
                    assertThrows(Exception.class,
                            () -> gameService.playerDecision(gameId, playerId, new DecisionWsDTO("CALL", 0)));
                }
                if (gc.gameState.getCurrentPot() == 0) {//equivalent to checking if game has ended
                    assertEquals(GamePhase.END_AFTER_FOURTH_BETTING_ROUND, gc.gameState.getGamePhase());
                    break;
                }
            }
        }
        winnerTracker.put(gc.gameState.getRoundWinnerToken(), "winner1");
        assertEquals(GamePhase.END_AFTER_FOURTH_BETTING_ROUND, gc.gameState.getGamePhase());

        gameService.nextRound(gameId);
        assertEquals(GamePhase.FIRST_BETTING_ROUND, gc.gameState.getGamePhase());

        //second round playing
        while (gc.gameState.getGamePhase() != GamePhase.END_ALL_FOLDED) {
            for (var player : gc.playerList) {
                var playerId = player.getToken();
                if (player.isCurrentPlayer()) {
                    gameService.playerDecision(gameId, playerId, new DecisionWsDTO("FOLD", 0));
                } else {
                    assertThrows(Exception.class,
                            () -> gameService.playerDecision(gameId, playerId, new DecisionWsDTO("RAISE", 0)));
                }
                if (gc.gameState.getCurrentPot() == 0) {//equivalent to checking if game has ended
                    assertEquals(GamePhase.END_ALL_FOLDED, gc.gameState.getGamePhase());
                    break;
                }
            }
        }
        winnerTracker.put(gc.gameState.getRoundWinnerToken(), "winner2");

        //third round playing testing illegal raise
        gameService.nextRound(gameId);
        for (var player : gc.playerList) {
            var playerId = player.getToken();
            if (player.isCurrentPlayer()) {
                assertThrows(Exception.class,
                        () -> gameService.playerDecision(gameId, playerId, new DecisionWsDTO("RAISE", 100))); // can not raise since the player with the lowest score is at least at 90 points
            } else {
                assertThrows(Exception.class,
                        () -> gameService.playerDecision(gameId, playerId, new DecisionWsDTO("RAISE", 0)));
            }
            if (gc.gameState.getCurrentPot() == 0) {//equivalent to checking if game has ended
                assertEquals(GamePhase.END_ALL_FOLDED, gc.gameState.getGamePhase());
                break;
            }
        }

        //testing legal raise
        for (var player : gc.playerList) {
            var playerId = player.getToken();
            if (player.isCurrentPlayer()) {
                assertDoesNotThrow(() -> gameService.playerDecision(gameId, playerId, new DecisionWsDTO("RAISE", 70)));
                assertThrows(Exception.class,
                        () -> gameService.playerDecision(gameId, playerId, new DecisionWsDTO("RAISE", 0)));
            }
            if (gc.gameState.getCurrentPot() == 0) {//equivalent to checking if game has ended
                assertEquals(GamePhase.END_ALL_FOLDED, gc.gameState.getGamePhase());
                break;
            }
        }

        //still third round
        while (gc.gameState.getGamePhase() != GamePhase.END_AFTER_FOURTH_BETTING_ROUND) {
            for (var player : gc.playerList) {
                var playerId = player.getToken();
                if (player.isCurrentPlayer()) {
                    gameService.playerDecision(gameId, playerId, new DecisionWsDTO("CALL", 0));
                } else {
                    assertThrows(Exception.class,
                            () -> gameService.playerDecision(gameId, playerId, new DecisionWsDTO("CALL", 0)));
                }
                if (gc.gameState.getCurrentPot() == 0) {//equivalent to checking if game has ended
                    assertEquals(GamePhase.END_AFTER_FOURTH_BETTING_ROUND, gc.gameState.getGamePhase());
                    break;
                }
            }
        }
        winnerTracker.put(gc.gameState.getRoundWinnerToken(), "winner3");

        gameService.nextRound(gameId);
        //depending on chance 1-3 players are still in the game (the ones which one). Others have not enough score.
        assertEquals(winnerTracker.values().size(), gc.playerList.size());
        
        //checking whether hands have different comments
        for (var handA : gc.playerHands.values()) {
            for (var handB : gc.playerHands.values()) {
                if (handA != handB) {
                    for (var commentA : handA.getComments()) {
                        for (var commentB : handB.getComments()) {
                            assertNotEquals(commentA.getFirst().content, commentB.getFirst().content);
                        }
                    }
                }
                for (var commentA : handA.getComments()) {
                    for (var commentB : handA.getComments()) {
                        if (commentA != commentB) {
                            assertNotEquals(commentA.getFirst().content, commentB.getFirst().content);
                        }
                    }
                }
            }
        }

        //gameClosed
        gameService.closeGame(gameId);
        assertEquals(GamePhase.CLOSED, gc.gameState.getGamePhase()); //Client is informed
        assertThrows(Exception.class, () -> gameService.nextRound(gameId)); //game no longer exists
        assertThrows(Exception.class, () -> gameService.addPlayer(gameId, new Player("A"))); //game no longer exists
        assertThrows(Exception.class, () -> gameService.closeGame(gameId)); //game no longer exists
        assertThrows(Exception.class, () -> gameService.getHost(gameId)); //game no longer exists
        assertThrows(Exception.class, () -> gameService.getPlayers(gameId)); //game no longer exists
        assertThrows(Exception.class, () -> gameService.noYtApi(gameId)); //game no longer exists
        assertThrows(Exception.class, () -> gameService.playerDecision(gameId, "A", new DecisionWsDTO())); //game no longer exists
        assertThrows(Exception.class, () -> gameService.removePlayer(gameId, new Player("A"))); //game no longer exists
        assertThrows(Exception.class, () -> gameService.startGame(gameId)); //game no longer exists
    }
    
    
}

class TestGameController extends GameController{

    TestGameController() {
        super("null");
        playerHands = new HashMap<>();
    }

    GameStateWsDTO gameState;
    Collection<PlayerWsDTO> playerList;
    Collection<HandOwnerWinner> showDown;
    HashMap<String, Hand> playerHands;
    VideoDataWsDTO videoData;

    public synchronized void gameStateChanged(String gameId, GameStateWsDTO gameStateWsDTO) {
        gameState = gameStateWsDTO;
    }

    public synchronized void playerStateChanged(String gameId, Collection<PlayerWsDTO> playersDTOList) {
        playerList = playersDTOList;
    }

    public void showdown(String gameId, Collection<HandOwnerWinner> handOwnerWinners) {
        showDown = handOwnerWinners;
        playerHands = new HashMap<>();
    }

    public void newHand(String gameId, Player player, Hand hand) {
        newHand(gameId, player.getToken(), hand);
    }

    public void newHand(String gameId, String player, Hand hand) {
        playerHands.put(player, hand);
    }

    public void newVideoData(String gameId, VideoDataWsDTO videoData) {
        this.videoData = videoData;
    }


}
