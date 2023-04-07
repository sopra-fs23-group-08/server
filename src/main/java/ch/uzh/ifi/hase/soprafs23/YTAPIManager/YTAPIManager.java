package ch.uzh.ifi.hase.soprafs23.YTAPIManager;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.data.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.uzh.ifi.hase.soprafs23.entity.Comment;
import ch.uzh.ifi.hase.soprafs23.game.Correctness;
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
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
                .indexOf("jdwp") >= 0;

        if (isDebug)
            System.out.println(
                    "Set fastDebug = true in YTAPIManager/YTAPIManager.getVideoAndHand to have faster Debugging. Attention empty VideoData and Hands will be returned");
        var fastDebug = false;
        var useYouTubeApi = false;

        if (fastDebug && isDebug) {
            return emptyVideoAndHand();
        } else if (!useYouTubeApi) {//gson is really slow in debug mode
            return APIController.readFromFile("src/main/resources/GameData1.txt");
        } else {//standard
            return APIController.getGameDataByQuery(query, language);
        }
    }

    private Pair<VideoData, List<Hand>> emptyVideoAndHand(){
            Comment comment = new Comment(null, query, query, query, null, null);
            Pair<Comment,Correctness> p = Pair.of(comment, Correctness.CORRECT);
            Hand hand = new Hand(Arrays.asList(p, p, p, p, p, p));
            return Pair.of(new VideoData(null, null, null, null, null, null), new ArrayList<>(Arrays.asList(new Hand(hand.getComments()), new Hand(hand.getComments()), new Hand(hand.getComments()), new Hand(hand.getComments()), new Hand(hand.getComments()), new Hand(hand.getComments())))); //faster debug
    }
}
