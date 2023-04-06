package ch.uzh.ifi.hase.soprafs23.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.internal.stubbing.answers.ThrowsException;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.function.Function;

public class GameTest {
  @Test
  public void basicTest(){
    assertEquals(true,false);
  }

  @Test
  public void runThrough() {
    Game game = new Game();
    Player playerA = new Player("A",false);
    Player playerB = new Player("B",false);
    Player playerC = new Player("C",false);
    var observer = new TestGameObserver();

    assertEquals(false, game.getGameId().isEmpty());
    
    game.joinGame(playerC);
    game.joinGame(playerB);
    game.addObserver(observer);
    game.joinGame(playerA);
    game.setStartScore(1000); //maybe change call to be in game.setup.setAllPlayerScore
    game.setSmallBlind(10);
    game.setBigBlind(20);

    assertTrue(!game.getPlayers().contains(playerC));//players are added after the game has started. maybe change that?
    assertEquals(GamePhase.WAITING_FOR_PLAYERS, game.getGamePhase());

    try {
      game.startGame();
    } catch (Exception e) {
      assertEquals("Some exception occurred", e);
    }
    

    assertEquals(GamePhase.FIRST_ROUND, game.getGamePhase());
    assertTrue(game.getPlayers().contains(playerC));
    
    game.getPlayers().remove(playerC); //immutability test
    assertTrue(game.getPlayers().contains(playerC));
    
    game.leaveGame(playerC);//maybe weird change this?
    assertTrue(game.getPlayers().contains(playerC));

    executableThrowsExceptionMsg(() -> {
      game.call(playerC);
    }, "You're not the current player");

    assertEquals(1000, observer.playerScore);
    assertEquals(null, observer.currentPlayer.id);

    var smallBlind = observer.smallBlind; //rename to smallBlindPlayer ?

    game.startBettingRound();

    assertNotEquals(smallBlind, observer.smallBlind);
    smallBlind = observer.smallBlind;
    assertEquals(smallBlind.name, observer.currentPlayer.name);

    executableThrowsExceptionMsg(() -> {
      game.call(observer.bigBlind);
    }, "You're not the current player");
    
    executableThrowsExceptionMsg(() -> {
      game.call(observer.currentPlayer);
    }, "SmallBlind must raise. currentCallAmount: 0 SmallBlindAmount: 10");

    executableThrowsExceptionMsg(() -> {
      game.raise(observer.currentPlayer, 5);
    }, "SmallBlind must raise. currentCallAmount: 5 SmallBlindAmount: 10");

    assertEquals(5, observer.callAmount);

    try {
      game.raise(observer.currentPlayer, 10);
    } catch (Exception e) {
      assertEquals("Some exception occurred", e);
    }

    assertEquals(10, observer.callAmount);


  }


  static void executableThrowsExceptionMsg(Executable call, String expectedMsg) {
    Exception exception = assertThrows(Exception.class, call);

    String actualMessage = exception.getMessage();

    assertEquals(expectedMsg, actualMessage);
  }

  class TestGameObserver implements GameObserver {
    public Player player;
    public Integer playerScore;
    public Integer potScore;
    public Integer callAmount;
    public Player smallBlind;
    public Player bigBlind;
    public Hand hand;
    public Decision decision;
    public GamePhase gamePhase;
    public Player winner;
    public Player currentPlayer;

    @Override
    public void playerScoreChanged(Player player, Integer score) {
      this.player = player;
      this.playerScore = score;
    }

    @Override
    public void newHand(Player player, Hand hand) {
      this.player = player;
      this.hand = hand;
    }

    @Override
    public void playerDecisionChanged(Player player, Decision decision) {
      this.player = player;
      this.decision = decision;
    }

    @Override
    public void currentPlayerChange(Player player) {
      this.currentPlayer = player;
    }

    @Override
    public void winnerIs(Player player) {
      this.winner = player;
    }

    @Override
    public void gameGettingClosed() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'gameGettingClosed'");
    }

    @Override
    public void gamePhaseChange(GamePhase gamePhase) {
      this.gamePhase = gamePhase;
    }

    @Override
    public void potScoreChange(Integer score) {
      this.potScore = score;
    }

    @Override
    public void callAmountChanged(Integer newCallAmount) {
      this.callAmount = newCallAmount;
    }

    @Override
    public void newPlayerBigBlindNSmallBlind(Player smallBlind, Player bigBlind) {
      this.smallBlind = smallBlind;
      this.bigBlind = bigBlind;
    }
    
  }

}
