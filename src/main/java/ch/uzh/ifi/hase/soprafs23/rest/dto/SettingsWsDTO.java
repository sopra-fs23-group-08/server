package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.YTAPIManager.Language;

public class SettingsWsDTO {
    
    private Integer bigBlind;
    private Integer smallBlind;
    private Integer initialBalance; //not sure if it's called like this
    private String playlistUrl;
    private Language language;

    public Integer getBigBlind(){
        return bigBlind;
    }

    public void setBigBlind(Integer bigBlind) {
        this.bigBlind = bigBlind;
    }

    public Integer getSmallBlind(){
        return smallBlind;
    }

    public void setSmallBlind(Integer smallBlind) {
        this.smallBlind = smallBlind;
    }

    public Integer getInitialBalance(){
        return initialBalance;
    }

    public void setInitialBalance(Integer initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getPlaylistUrl(){
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }
    
    public Language getLanguage(){
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
