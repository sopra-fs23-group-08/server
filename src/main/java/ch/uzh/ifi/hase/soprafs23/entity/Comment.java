package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.Date;

public class Comment {
    public final Long commentId;
    public final Long videoId;
    public final String content;
    public final String author;
    public final Integer likes;
    public final Date date;

    Comment(Long aCommentId, Long aVideoId, String aContent, String aAuthor, Integer aLikes, Date aDate) {
        commentId = aCommentId;
        videoId = aVideoId;
        content = aContent;
        author = aAuthor;
        likes = aLikes;
        date = aDate;
    }
}
