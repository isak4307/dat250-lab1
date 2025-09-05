package dat250.lab1.controller;

import dat250.lab1.model.PollManager;
import dat250.lab1.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;

@RestController
public class VoteController {
    private PollManager pollManager;

    public VoteController(@Autowired PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @GetMapping("votes/polls/{pollId}")
    public ResponseEntity<String> getVotesByPoll(@PathVariable int pollId) {
        if (this.pollManager.getPollById(pollId) == null) {
            return ResponseEntity.badRequest().body("Error: There doesn't exist a poll with id " + pollId + " doesn't exist");
        }
        HashSet<Vote> votes = this.pollManager.getVotesByPollId(pollId);
        if (votes == null) {
            return ResponseEntity.badRequest().body("Error: The polls doesn't have any votes");
        } else {
            return ResponseEntity.ok("Votes for pollId " + pollId + "\n" + votes.toString());
        }
    }

    @GetMapping("votes/recent/{userId}")
    public ResponseEntity<String> getUserVotes(@PathVariable int userId) {
        if (this.pollManager.getUserById(userId) == null) {
            return ResponseEntity.badRequest().body("Error: user with id " + userId + " doesn't exist");
        }
        ArrayList<Vote> votes = this.pollManager.getVotesByUser(userId);
        Vote recentVote = this.pollManager.getRecentVote(votes);
        if (recentVote != null) {
            return ResponseEntity.ok("Recent vote for user " + userId + ": " + recentVote.toString());
        } else {
            return ResponseEntity.badRequest().body("Error: user with id " + userId + " doesn't have a vote");
        }
    }

}
