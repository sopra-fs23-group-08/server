package ch.uzh.ifi.hase.soprafs23.game;

import java.io.IOException;
import java.util.List;

import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

class SetupClosed extends Setup {
    
    public SetupClosed() {
        video = null; //maybe nicer possibilities
    }

    @Override
    public List<Pair<Player, Integer>> getPlayers() throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed");
    }

    @Override
    public void joinGame(Player p) throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'joinGame'");
    }

    @Override
    public void leaveGame(Player p) throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'leaveGame'");
    }

    @Override
    public void setScoreForPlayer(Player p, int newInitialScore) throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'setScoreForPlayer'");
    }

    @Override
    public void setStartScoreForAll(int newInitialScore) throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'setStartScoreForAll'");
    }

    @Override
    public Pair<VideoData, List<Hand>> getYTData() throws IOException, InterruptedException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'getYTData'");
    }

    @Override
    public int getSmallBlindAmount() throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'getSmallBlindAmount'");
    }

    @Override
    public void setSmallBlindAmount(int smallBid) throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'setSmallBlindAmount'");
    }

    @Override
    public int getBigBlindAmount() throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'getBigBlindAmount'");
    }

    @Override
    public void setBigBlindAmount(int bigBlind) throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'setBigBlindAmount'");
    }

    @Override
    public boolean isInfoFirstRound() throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'isInfoFirstRound'");
    }

    @Override
    public void setInfoFirstRound(boolean infoFirstRound) throws IllegalStateException {
        throw new IllegalStateException("During the game the Setup can't be changed. 'setInfoFirstRound'");
    }
    
}
