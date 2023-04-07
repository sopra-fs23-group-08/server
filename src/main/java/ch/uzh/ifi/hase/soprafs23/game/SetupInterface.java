package ch.uzh.ifi.hase.soprafs23.game;

import java.io.IOException;
import java.util.List;

import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

public interface SetupInterface {

    public List<Pair<Player, Integer>> getPlayers() throws Exception;

    public void joinGame(Player p) throws Exception;

    public void leaveGame(Player p) throws Exception;

    public void setScoreForPlayer(Player p, int newInitialScore) throws Exception;

    public void setStartScoreForAll(int newInitialScore) throws Exception;

    public Pair<VideoData, List<Hand>> getYTData() throws IOException, InterruptedException, Exception;

    public int getSmallBlindAmount() throws Exception;

    public void setSmallBlindAmount(int smallBid) throws Exception;

    public int getBigBlindAmount() throws Exception;

    public void setBigBlindAmount(int bigBlind) throws Exception;

    public boolean isInfoFirstRound() throws Exception;

    public void setInfoFirstRound(boolean infoFirstRound) throws Exception;
}

