package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class SettingsWsDTO {
    
    private Long bigBlind;
    private Long smallBlind;
    private Long initialBalance; //not sure if it's called like this
    private String playlistUrl;
    private String language;

    public Long getBigBlind(){
        return bigBlind;
    }

    public void setBigBlind(Long bigBlind) {
        this.bigBlind = bigBlind;
    }

    public Long getSmallBlind(){
        return smallBlind;
    }

    public void setSmallBlind(Long smallBlind) {
        this.smallBlind = smallBlind;
    }

    public Long getInitialBalance(){
        return initialBalance;
    }

    public void setInitialBalance(Long initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getPlaylistUrl(){
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }
    
    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
