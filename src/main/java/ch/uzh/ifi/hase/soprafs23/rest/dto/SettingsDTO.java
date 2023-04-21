package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class SettingsDTO {
    
    private Long bigblind;
    private Long smallbling;
    private Long initialbalance; //not sure if it's called like this
    private String playlisturl;
    private String language;

    public Long getBigblind(){
        return bigblind;
    }

    public void setBigblind(Long bigblind) {
        this.bigblind = bigblind;
    }

    public Long getSmallblind(){
        return smallbling;
    }

    public void setSmallblind(Long smallbling) {
        this.smallbling = smallbling;
    }

    public Long getInitialbalance(){
        return initialbalance;
    }

    public void setInitialbalance(Long initialbalance) {
        this.initialbalance = initialbalance;
    }

    public String getPlaylisturl(){
        return playlisturl;
    }

    public void setPlaylisturl(String playlisturl) {
        this.playlisturl = playlisturl;
    }
    
    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
