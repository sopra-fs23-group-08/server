package ch.uzh.ifi.hase.soprafs23.game;

import java.io.IOException;
import java.util.List;

import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.YTAPIManager.Language;
import ch.uzh.ifi.hase.soprafs23.YTAPIManager.YTAPIManager;

public class VideoSetup {

    YTAPIManager ytAPIManager;

    public VideoSetup() {
        ytAPIManager = new YTAPIManager();
    }

    public void setPlaylist(String url) throws IllegalStateException {
        ytAPIManager.setPlaylist(url);
    }

    public void setQuery(String query) {
        ytAPIManager.setQuery(query);
    }

    public void setLanguage(Language language) {
        ytAPIManager.setLanguage(language);
    }

    public void setMinimalViewcount(Integer viewCount) throws IllegalStateException {
        ytAPIManager.setMinimalViewcount(viewCount);
    }

    protected Pair<VideoData, List<Hand>> getVideoAndHand() throws IOException, InterruptedException {
        return ytAPIManager.getVideoAndHand();
    }

    public void useYtApi(boolean b) {
        ytAPIManager.useYtApi(b);
    }
}
