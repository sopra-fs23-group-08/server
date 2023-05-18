package ch.uzh.ifi.hase.soprafs23.YTAPIManager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.util.Pair;
import ch.uzh.ifi.hase.soprafs23.entity.Comment;
import ch.uzh.ifi.hase.soprafs23.game.Correctness;

class HandCreator {
    private ArrayList<Pair<Comment, Correctness>> comments;
    
    public HandCreator() {
        comments = new ArrayList<>();
    }
    
    public void addComment(Comment comment, Correctness correctness){
        comments.add(Pair.of(comment, correctness));
    }

    public List<Pair<Comment, Correctness>> getComments() {
        return new ArrayList<>(comments); //shallow copy
    }
}

