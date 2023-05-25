package ch.uzh.ifi.hase.soprafs23.YTAPIManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.util.Pair;

import ch.uzh.ifi.hase.soprafs23.entity.Comment;
import ch.uzh.ifi.hase.soprafs23.game.Correctness;
import ch.uzh.ifi.hase.soprafs23.game.Hand;
import ch.uzh.ifi.hase.soprafs23.game.VideoData;

//todo add difference to video selection cosine similarity & yt recommendations?
//todo add login for 2auth to increase the contingent. At the time around 80 games per day. 
//todo Or getting rid of search call. 1000 games per day

public class YTAPIManager {
    private String query = "LoFi HipHop";
    private String playlistId = "PLbZIPy20-1pN7mqjckepWF78ndb6ci_qi";
    private Language language = Language.GERMAN;
    private boolean useYouTubeApi = true;

    public static boolean checkPlaylistUrl(String Url) throws IllegalStateException, IOException, InterruptedException {
        var listId = urlToPlaylistId(Url);
        var videoCount = APIController.getVideoCountForPlaylist(listId);
        if (videoCount > 5) {
            return true;
        } else {
            throw new IllegalStateException("There must be at least 6 videos in the Playlist (Playlist contains " + videoCount + ")");
        }
    }
    
    public void setPlaylist(String URL) throws IllegalStateException {
        playlistId = urlToPlaylistId(URL);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setMinimalViewcount(Integer viewCount) throws IllegalStateException {
        throw new IllegalStateException("method not implemented yet");
    }
    
    public Pair<VideoData, List<Hand>> getVideoAndHand() throws IOException, InterruptedException  {
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
                .indexOf("jdwp") >= 0;

        if (isDebug)
            System.out.println(
                    "Set fastDebug = true in YTAPIManager/YTAPIManager.getVideoAndHand to have faster Debugging. Attention empty VideoData and Hands will be returned");

        var fastDebug = false;
        

        //NOSONAR
        if (fastDebug && isDebug) {
            return emptyVideoAndHand();
        } else if (!useYouTubeApi) {//gson is really slow in debug mode
            return APIController.readFromFile("src/main/resources/GameData1.txt"); //reads local file
        } else {//standard uses YT API
            return APIController.getGameDataByPlaylist(playlistId, language);
        }
    }

    static private String urlToPlaylistId(String URL) throws IllegalStateException {
        Pattern pattern = Pattern.compile("list=");
        String[] s1 = pattern.split(URL);
        String[] s2;
        if (s1.length < 2) {
            throw new IllegalStateException("There is no Playlist in this URL: " + URL);
        } else {
            s2 = s1[1].split("&");
        }
        return s2[0];
    }
    
    private Pair<VideoData, List<Hand>> emptyVideoAndHand() {
        Comment comment = new Comment(null, query, query, query, null, null);
        Pair<Comment, Correctness> p = Pair.of(comment, Correctness.CORRECT);
        Hand hand = new Hand(Arrays.asList(p, p, p, p, p, p));
        return Pair.of(new VideoData(null, null, null, null, null, null),
                new ArrayList<>(Arrays.asList(new Hand(hand.getComments()), new Hand(hand.getComments()),
                        new Hand(hand.getComments()), new Hand(hand.getComments()), new Hand(hand.getComments()),
                        new Hand(hand.getComments())))); //faster debug
    }
    
    public void useYtApi(boolean use){
        useYouTubeApi = use;
    }
}
