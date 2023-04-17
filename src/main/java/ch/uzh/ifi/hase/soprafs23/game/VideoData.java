package ch.uzh.ifi.hase.soprafs23.game;

import java.time.Duration;
import java.util.Date;

public class VideoData {
    public final Integer views;
    public final Integer likes;
    public final String title;
    public final String thumbnail; 
    public final Date releaseDate;
    public final Duration videoLength;

    public VideoData(Integer aViews, Integer aLikes, String aTitle, String aThumbnail, Date aReleaseDate,
            Duration aVideoLength) {
        views = aViews;
        likes = aLikes;
        title = aTitle;
        thumbnail = aThumbnail;
        releaseDate = aReleaseDate;
        videoLength = aVideoLength;
    }
    
    public VideoData(MutableVideoData mvd) { //copies the data of a mvd into vd
        views = mvd.views;
        likes = mvd.likes;
        title = mvd.title;
        thumbnail = mvd.thumbnail;
        releaseDate = mvd.releaseDate;
        videoLength = mvd.videoLength;
    }

    public VideoData getPartialVideoData(int gameInfoAmount) throws Exception { //game info Amount 0-4
        MutableVideoData mvd = new MutableVideoData(this);

        switch (gameInfoAmount) {
            case 0:
                mvd.likes = null;
                mvd.views = null;
            case 1:
                mvd.videoLength = null;
            case 2:
                mvd.thumbnail = null;
            case 3:
                mvd.releaseDate = null;
            case 4:
                mvd.title = null;
            case 5:
                break;
            default:
                throw new Exception("only values in [0,5] are allowed");
        }

        return new VideoData(mvd);
    }
}

class MutableVideoData { //used for mutation and copy stuff
    protected  Integer views;
    protected  Integer likes;
    protected  String title;
    protected  String thumbnail; //immutable ?
    protected  Date releaseDate;
    protected  Duration videoLength;

    protected MutableVideoData(Integer aViews, Integer aLikes, String aTitle, String aThumbnail, Date aReleaseDate,
            Duration aVideoLength) {
        views = aViews;
        likes = aLikes;
        title = aTitle;
        thumbnail = aThumbnail;
        releaseDate = aReleaseDate;
        videoLength = aVideoLength;
    }
    
    protected MutableVideoData() {
    }

    public MutableVideoData(VideoData vd) {
        views = vd.views;
        likes = vd.likes;
        title = vd.title;
        thumbnail = vd.thumbnail;
        releaseDate = vd.releaseDate;
        videoLength = vd.videoLength;
    }
}
