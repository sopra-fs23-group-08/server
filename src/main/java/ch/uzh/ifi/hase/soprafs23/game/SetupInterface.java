package ch.uzh.ifi.hase.soprafs23.game;

import java.io.IOException;
import java.util.List;

import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

public interface SetupInterface {

    public List<Pair<Player, Integer>> getPlayers() throws IllegalStateException;

    public void joinGame(Player p) throws IllegalStateException;

    public void leaveGame(Player p) throws IllegalStateException;

    public void setScoreForPlayer(Player p, int newInitialScore) throws IllegalStateException;

    public void setStartScoreForAll(int newInitialScore) throws IllegalStateException;

    public Pair<VideoData, List<Hand>> getYTData() throws IOException, InterruptedException, IllegalStateException;

    public int getSmallBlindAmount() throws IllegalStateException;

    public void setSmallBlindAmount(int smallBid) throws IllegalStateException;

    public int getBigBlindAmount() throws IllegalStateException;

    public void setBigBlindAmount(int bigBlind) throws IllegalStateException;

    public boolean isInfoFirstRound() throws IllegalStateException;

    public void setInfoFirstRound(boolean infoFirstRound) throws IllegalStateException;
}

