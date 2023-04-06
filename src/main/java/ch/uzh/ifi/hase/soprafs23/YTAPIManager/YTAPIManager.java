package ch.uzh.ifi.hase.soprafs23.YTAPIManager;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.List;

import org.springframework.data.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.uzh.ifi.hase.soprafs23.game.Hand;
import ch.uzh.ifi.hase.soprafs23.game.VideoData;

public class YTAPIManager {
    private String query = "LoFi HipHop";
    private Language language = Language.GERMAN;

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
        return APIController.readFromFile("src/main/resources/GameData1.txt");


        // return APIController.getGameDataByQuery(query, language);
    } 
}
