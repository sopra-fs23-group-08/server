package ch.uzh.ifi.hase.soprafs23.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.YTAPIManager.YTAPIManager;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;



public class SetupData {

    private List<Pair<Player, Integer>> players;
    private int smallBlind;
    private int bigBlind;
    private boolean infoFirstRound;
    private YTAPIManager yt;

    public SetupData() {
        yt = new YTAPIManager();
        players = new ArrayList<>();
    }

    public List<Pair<Player, Integer>> getPlayers() {
        return players;
    }

    public void addPlayer(Player p, int initialScore) {
        this.players.add(Pair.of(p, initialScore));
    }

    public void removePlayer(Player p) {
        Pair<Player, Integer> remove = null;
        for (Pair<Player, Integer> pair : players) {
            if (pair.getFirst().id == p.id) {
                remove = pair;
                break;
            }
        }
        players.remove(remove);
    }

    public void changeInitialScore(Player p, int newInitialScore) {
        removePlayer(p);
        addPlayer(p, newInitialScore);
    }

    public void changeInitialScoreForAll(int newInitialScore) {
        List<Pair<Player, Integer>> l = new ArrayList<>();
        for (Pair<Player, Integer> pair : players) {
            l.add(Pair.of(pair.getFirst(), newInitialScore));
        }
        players.clear();
        players.addAll(l);
    }

    public Pair<VideoData, List<Hand>> getYTData() throws IOException, InterruptedException, Exception {
        return yt.getVideoAndHand();
    }
    
    public int getSmallBlind() {
        return smallBlind;
    }
    public void setSmallBlind(int smallBid) {
        this.smallBlind = smallBid;
    }
    public int getBigBlind() {
        return bigBlind;
    }
    public void setBigBlind(int bigBlind) {
        this.bigBlind = bigBlind;
    }
    public boolean isInfoFirstRound() {
        return infoFirstRound;
    }
    public void setInfoFirstRound(boolean infoFirstRound) {
        this.infoFirstRound = infoFirstRound;
    }
    
}
