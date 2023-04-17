package ch.uzh.ifi.hase.soprafs23.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {
  Game game;
  Player playerA;
  Player playerB;
  Player playerC;
  TestGameObserver observer;

  @BeforeEach
  public void setUpGame() throws Exception {

    game = new Game();
    playerA = new Player("A",false);
    playerB = new Player("B",false);
    playerC = new Player("C",false);
    observer = new TestGameObserver();

    assertEquals(false, game.getGameId().isEmpty());
    
    game.setup.joinGame(playerC);
    game.setup.joinGame(playerB);
    game.addObserver(observer);
    game.setup.joinGame(playerA);
    game.setup.setStartScoreForAll(1000); //maybe change call to be in game.setup.setAllPlayerScore
    game.setup.setSmallBlindAmount(10);
    game.setup.setBigBlindAmount(20);

    assertTrue(!game.getPlayers().contains(playerC));//players are added after the game has started. maybe change that?
    assertEquals(GamePhase.LOBBY, game.getGamePhase());

    try {
      game.startGame();
    } catch (Exception e) {
      assertEquals("Some exception occurred", e);
    }
    
    
  }

  @Test
  public void runThrough() throws Exception { //this is like a sample game
    executableThrowsExceptionMsg(() -> {
      game.setup.setBigBlindAmount(0);
    }, "During the game the Setup can't be changed. 'setBigBlindAmount'");

    assertEquals(GamePhase.FIRST_BETTING_ROUND, game.getGamePhase());
    assertTrue(game.getPlayers().contains(playerC));

    game.getPlayers().remove(playerC); //immutability test
    assertTrue(game.getPlayers().contains(playerC));

    executableThrowsExceptionMsg(() -> {
      game.setup.leaveGame(playerC);
    }, "During the game the Setup can't be changed. 'leaveGame'");

    executableThrowsExceptionMsg(() -> {
      game.call(playerC);
    }, "You're not the current player");

    assertEquals(1000, observer.playerScore);
    assertEquals(null, observer.currentPlayer.id);

    game.startBettingRound();

    var smallBlind = observer.smallBlind; //rename to smallBlindPlayer ?
    var currentPlayer = observer.currentPlayer;
    assertEquals(smallBlind.name, currentPlayer.name);

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

    assertNotEquals(currentPlayer, observer.currentPlayer); //since a new player is playing
    currentPlayer = observer.currentPlayer;
    assertEquals(10, observer.callAmount);

    executableThrowsExceptionMsg(() -> {
      game.call(observer.currentPlayer);
    }, "BigBlind must raise. currentCallAmount: 10 BigBlindAmount: 20");

    assertEquals(null, observer.winner);
    try {
      game.raise(observer.currentPlayer, 20);
      game.call(observer.currentPlayer);
      assertEquals(GamePhase.FIRST_BETTING_ROUND, observer.gamePhase);
      game.call(observer.currentPlayer);
      assertEquals(GamePhase.SECOND_BETTING_ROUND, observer.gamePhase);
      game.call(observer.currentPlayer);
      game.call(observer.currentPlayer);
      game.call(observer.currentPlayer);
      assertEquals(GamePhase.THIRD_BETTING_ROUND, observer.gamePhase);
      game.call(observer.currentPlayer);
      game.raise(observer.currentPlayer, 40);
      game.fold(observer.currentPlayer);
      game.call(observer.currentPlayer);
      assertEquals(GamePhase.FOURTH_BETTING_ROUND, observer.gamePhase);
      var winner = observer.currentPlayer;
      game.raise(observer.currentPlayer, 80);
      game.fold(observer.currentPlayer);
      assertEquals(GamePhase.END_ALL_FOLDED, observer.gamePhase);
      assertEquals(winner, observer.winner);
      
    } catch (Exception e) {
      assertEquals("Some exception occurred", e);
    }

    
  }

  @Test
  public void runThrough2() {

    game.startBettingRound();
    assertEquals(null, observer.winner);
    
    try {
      game.raise(observer.currentPlayer, 10);
      assertEquals(10, observer.potScore);
      game.raise(observer.currentPlayer, 20);
      assertEquals(30, observer.potScore);
      game.call(observer.currentPlayer);
      assertEquals(50, observer.potScore);
      assertEquals(GamePhase.FIRST_BETTING_ROUND, observer.gamePhase);
      game.call(observer.currentPlayer);
      assertEquals(70, observer.potScore);
      assertEquals(GamePhase.SECOND_BETTING_ROUND, observer.gamePhase);

      game.call(observer.currentPlayer);
      assertEquals(70, observer.potScore);
      game.call(observer.currentPlayer);
      assertEquals(70, observer.potScore);
      game.call(observer.currentPlayer);
      assertEquals(70, observer.potScore);
      assertEquals(GamePhase.THIRD_BETTING_ROUND, observer.gamePhase);

      game.call(observer.currentPlayer);
      game.raise(observer.currentPlayer, 40);
      assertEquals(110, observer.potScore);
      game.call(observer.currentPlayer);
      assertEquals(150, observer.potScore);
      executableThrowsExceptionMsg(() -> {
        game.raise(observer.currentPlayer, 20);
      }, "The CallAmount must be higher after a raise. CallAmountBefore: 40 NewCallAmount: 20");
      game.call(observer.currentPlayer);
      assertEquals(GamePhase.FOURTH_BETTING_ROUND, observer.gamePhase);

      executableThrowsExceptionMsg(() -> {
        game.raise(observer.currentPlayer, 1000);
      }, "Player score(930) is not high enough to raise(1000).");
      game.call(observer.currentPlayer);
      game.call(observer.currentPlayer);
      game.call(observer.currentPlayer);
      assertEquals(GamePhase.END_AFTER_FOURTH_BETTING_ROUND, observer.gamePhase);

    } catch (Exception e) {
      assertEquals("Some exception occurred", e);
    }
    assertNotEquals(null, observer.winner);
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
