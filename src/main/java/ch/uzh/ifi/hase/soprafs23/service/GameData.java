package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.rest.dto.GameStateWsDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerWsDTO;

import java.util.HashMap;
import java.util.List;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.game.GamePhase;

class GameData {

    
    public GameStateWsDTO gameStateWsDTO = new GameStateWsDTO(0,0,false,null,GamePhase.LOBBY);
    public HashMap<String, PlayerWsDTO> playersData = new HashMap<>();

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
