package ch.uzh.ifi.hase.soprafs23.YTAPIManager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class YtApiManagerTest {
    
    static YTAPIManager apiManager;
    @BeforeAll
    static public void setup() {
        apiManager = new YTAPIManager();
    }

    @Test
    public void checkPlaylistTest() {
        assertThrows(Exception.class, () -> YTAPIManager.checkPlaylistUrl("")); //not valid url
        assertThrows(Exception.class, () -> YTAPIManager.checkPlaylistUrl(
                "https://www.youtube.com/watch?v=8RTR1Ag0rhQ&list=PLS9WZcsKGOeCVp6uMq3gROdIBTQFrR6ma")); //private playlist
        assertDoesNotThrow(() -> YTAPIManager.checkPlaylistUrl(
                "https://www.youtube.com/watch?v=hT_nvWreIhg&list=PLbZIPy20-1pN7mqjckepWF78ndb6ci_qi")); //valid playlist
    }
    
    @Test
    public void getVideosAndHandTest() throws IOException, InterruptedException {
        apiManager.setPlaylist("https://www.youtube.com/watch?v=hT_nvWreIhg&list=PLbZIPy20-1pN7mqjckepWF78ndb6ci_qi");
        var videoAndHand = apiManager.getVideoAndHand();
        assertNotEquals(null, videoAndHand.getFirst());
        assertNotEquals(null, videoAndHand.getSecond());
        assertEquals(7, videoAndHand.getSecond().size());
    }
}
