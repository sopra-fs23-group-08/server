package ch.uzh.ifi.hase.soprafs23.game;

import java.net.URL;

import ch.uzh.ifi.hase.soprafs23.YTAPIManager.Language;
import ch.uzh.ifi.hase.soprafs23.YTAPIManager.YTAPIManager;

public class VideoSelection {

    YTAPIManager ytAPIManager;

    public VideoSelection() {
        ytAPIManager = new YTAPIManager();
    }

    public void setPlaylist(URL url) throws Exception {
        ytAPIManager.setPlaylist(url);
    }

    public void setQuery(String query) {
        ytAPIManager.setQuery(query);
    }

    public void setLanguage(Language language) {
        ytAPIManager.setLanguage(language);
    }

    public void setMinimalViewcount(Integer viewCount) throws Exception {
        ytAPIManager.setMinimalViewcount(viewCount);
    }
}
