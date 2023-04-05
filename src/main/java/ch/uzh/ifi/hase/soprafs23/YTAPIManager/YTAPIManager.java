package ch.uzh.ifi.hase.soprafs23.YTAPIManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.game.Hand;
import ch.uzh.ifi.hase.soprafs23.game.VideoData;

public class YTAPIManager {
    private String query;
    private Language language;

    public void setPlaylist(URL url) throws Exception {
        throw new Exception("method not implemented yet");
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setMinimalViewcount(Integer viewCount) throws Exception {
        throw new Exception("method not implemented yet");
    }
    
    public Pair<VideoData, List<Hand>> getVideoAndHand() throws IOException, InterruptedException, Exception {
        return APIController.getGameDataByQuery(query, language);
    } 
}
