package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.rest.dto.GameStateWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.VideoDataWsDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.game.GamePhase;
import ch.uzh.ifi.hase.soprafs23.game.Hand;

class GameData {

    
    protected GameStateWsDTO gameStateWsDTO = new GameStateWsDTO(0,0,false,"playerTokenNull",GamePhase.LOBBY);
    protected Map<String, PlayerWsDTO> playersData = new HashMap<>();
    protected Map<String, Hand> handData = new HashMap<>();
    protected VideoDataWsDTO videoData = null;

    public GameData(List<Player> players){
        for(Player player: players){
            addPlayer(player);
        }
    }
    public GameData(){}

    public void addPlayer(Player player){
        playersData.put(player.getToken(), new PlayerWsDTO(player.getToken(), player.getName(),  null ,null, false, false, false));
    }

    public void removePlayer(Player player){
        playersData.remove(player.getToken());
    }

    public void setCurrentPlayer(Player currentPlayer){
        for(PlayerWsDTO player: playersData.values()){
            player.setCurrentPlayer(false);
        }
        if (currentPlayer.getToken() != null) {
            playersData.get(currentPlayer.getToken()).setCurrentPlayer(true);
        }

    }

    public void setBigBlind(Player bigBlind){
        for(PlayerWsDTO player: playersData.values()){
            player.setBigBlind(false);
        }
        if (bigBlind.getToken() != null) {
            playersData.get(bigBlind.getToken()).setBigBlind(true);
        }

    }

    public void setSmallBlind(Player smallBlind){
        for(PlayerWsDTO player: playersData.values()){
            player.setSmallBlind(false);
        }
        if (smallBlind.getToken() != null) {
            playersData.get(smallBlind.getToken()).setSmallBlind(true);
        }

    }
    
}
