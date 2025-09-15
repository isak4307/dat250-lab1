package dat250.lab1.controller;

import dat250.lab1.model.PollManager;
import dat250.lab1.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
@CrossOrigin
@RestController
public class VoteController {
    private PollManager pollManager;

    public VoteController(@Autowired PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @GetMapping("votes/polls/{pollId}")
    public ResponseEntity<HashSet<Vote>> getVotesByPoll(@PathVariable Integer pollId) {
        if (this.pollManager.getPollById(pollId) == null) {
            return ResponseEntity.badRequest().build();
        }
        HashSet<Vote> votes = this.pollManager.getVotesByPollId(pollId);
        if (votes != null) {
            return ResponseEntity.ok(votes);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("votes/recent/{userId}")
    public ResponseEntity<Vote> getUserVotes(@PathVariable Integer userId) {
        if (this.pollManager.getUserById(userId) == null) {
            return ResponseEntity.badRequest().build();
        }
        ArrayList<Vote> votes = this.pollManager.getVotesByUser(userId);
        Vote recentVote = this.pollManager.getRecentVote(votes);
        if (recentVote != null) {
            return ResponseEntity.ok(recentVote);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/votes")
    public ResponseEntity<ArrayList<Vote>> getAllVotes() {
        return ResponseEntity.ok(this.pollManager.getAllVotes());
    }

}
