package ch.uzh.ifi.hase.soprafs23.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.entity.Player;



class SetupData extends Setup {

    private static final int MAXPLAYER = 6;
    private List<Pair<Player, Integer>> players;
    private int smallBlind;
    private int bigBlind;
    private boolean infoFirstRound;
    private int initialScore;

    public SetupData() {
        players = new ArrayList<>();
        initialScore = 0;
        video = new VideoSetup();
    }

    public List<Pair<Player, Integer>> getPlayers() {
        return players;
    }

    public void joinGame(Player p) {
        synchronized (players) {
            if (players.size() >= MAXPLAYER) {
                throw new IllegalStateException("Lobby already full");
            } else {
                this.players.add(Pair.of(p, initialScore));
            }
        }
    }

    public void leaveGame(Player p) {
        synchronized (players) {
            Pair<Player, Integer> remove = null;
            for (Pair<Player, Integer> pair : players) {
                if (pair.getFirst().getToken() == p.getToken()) {
                    remove = pair;
                    break;
                }
            }
            players.remove(remove);
        }
    }

    public void setScoreForPlayer(Player p, int newInitialScore) {
        synchronized (players) {
            leaveGame(p);//chanky implementation ok interface
            this.players.add(Pair.of(p, newInitialScore));
        }
    }

    public void setStartScoreForAll(int newInitialScore) {
        synchronized (players) {
            initialScore = newInitialScore;
            List<Pair<Player, Integer>> l = new ArrayList<>();
            for (Pair<Player, Integer> pair : players) {
                l.add(Pair.of(pair.getFirst(), newInitialScore));
            }
            players.clear();
            players.addAll(l);
        }
    }

    public Pair<VideoData, List<Hand>> getYTData() throws IOException, InterruptedException {
        return video.getVideoAndHand();
    }
    
    public int getSmallBlindAmount() {
        return smallBlind;
    }
    public void setSmallBlindAmount(int smallBid) {
        this.smallBlind = smallBid;
    }
    public int getBigBlindAmount() {
        return bigBlind;
    }
    public void setBigBlindAmount(int bigBlind) {
        this.bigBlind = bigBlind;
    }
    public boolean isInfoFirstRound() {
        return infoFirstRound;
    }
    public void setInfoFirstRound(boolean infoFirstRound) {
        this.infoFirstRound = infoFirstRound;
    }
    
}
