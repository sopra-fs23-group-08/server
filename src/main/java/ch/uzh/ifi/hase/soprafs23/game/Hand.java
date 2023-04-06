package ch.uzh.ifi.hase.soprafs23.game;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.util.Pair;
import ch.uzh.ifi.hase.soprafs23.entity.Comment;

public class Hand {
    private List<Pair<Comment, Correctness>> comments;
    
    public Hand(ArrayList<Pair<Comment, Correctness>> cards) {
        comments = new ArrayList<>(cards); //shallow copy
    }
    
    public List<Pair<Comment, Correctness>> getComments() {
        return new ArrayList<>(comments); //shallow copy
    }

    public int getCountCorrect(){
        int sum = 0;
        for(Pair<Comment, Correctness> pair : comments){
            if(pair.getSecond() == Correctness.CORRECT){
                sum += 1;
            }
        }
        return sum;
    }
}

