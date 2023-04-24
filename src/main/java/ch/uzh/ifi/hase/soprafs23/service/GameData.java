package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.game.Hand;
import ch.uzh.ifi.hase.soprafs23.game.Decision;
import ch.uzh.ifi.hase.soprafs23.game.GamePhase;

import java.util.HashMap;
import java.util.List;

import ch.uzh.ifi.hase.soprafs23.entity.Player;

class GameData {

    public Player currentPlayer;
    public Player bigBlindPlayer;
    public Player smallBlindPlayer;
    public Integer pot;
    public GamePhase gamePhase;
    public Integer callAmount;
    public HashMap<String, PlayerData> playersData = new HashMap<>();

    public GameData(List<Player> players){
        for(Player player: players){
            addPlayer(player);
        }
    }
    public GameData(){}

    public void addPlayer(Player player){
        playersData.put(player.getToken(), new PlayerData(player.getToken(), player.getName()));
    }

    public void removePlayer(Player player){
        playersData.remove(player.getToken());
    }

    public class PlayerData{
        public String token;
        public String username;
        public Integer score;
        public Hand hand;
        public Decision decision;

        public PlayerData(String token, String name){
            this.token = token;
            this.token = name;
        }
    }
    
}
