package ch.uzh.ifi.hase.soprafs23.game;

import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    assertEquals(false, game.getGameId().isEmpty());
    
  }



}
