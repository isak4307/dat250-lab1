package dat250.lab1.controller;

import dat250.lab1.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


@RestController
public class PollController {
    private PollManager pollManager;

    public PollController(@Autowired PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @GetMapping("/polls")
    public ResponseEntity<HashSet<Poll>> getPolls() {
        return ResponseEntity.ok(this.pollManager.getPolls());
    }

    @GetMapping("/polls/{pollId}/result")
    public ResponseEntity<String> getResultOfPoll(@PathVariable int pollId) {
        Poll poll = this.pollManager.getPollById(pollId);
        if (poll == null) {
            return ResponseEntity.badRequest().body("Error: There is no poll with id " + pollId);
        }
        HashMap<VoteOption, Integer> result = pollManager.voteCounter(pollId);
        return ResponseEntity.ok("The following poll " + poll + "\n with the results " + result.toString());
    }

    private boolean samePollQandC(Poll poll, int cId) {
        HashSet<Poll> polls = this.pollManager.getPolls();
        User usr = this.pollManager.getUserById(cId);
        for (Poll p : polls) {
            if (p.getQuestion().equals(poll.getQuestion()) && p.getCreator().equals(usr)) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/polls/{creatorId}")
    public ResponseEntity<String> createPoll(@PathVariable int creatorId, @RequestBody Poll poll) {

        if (samePollQandC(poll, creatorId)) {
            return ResponseEntity.badRequest().body("Error: The poll already exists");
        }
        List<VoteOption> voteOptions = poll.getVoteOptions();
        if (voteOptions.size() < 2) {
            return ResponseEntity.badRequest().body("Error: Need to have at least 2 voteoptions");
        }

        // get user id for the creator and add it together later on
        User creator = this.pollManager.getUserById(creatorId);
        if (creator == null) {
            return ResponseEntity.badRequest().body("Error: user with the id " + creatorId + " doesn't exist");
        }
        //create voteOptions
        this.pollManager.createVoteOptions(poll);
        pollManager.createPoll(poll, creator);
        return ResponseEntity.ok("Created poll: " + poll.getQuestion());

    }

    @PostMapping("/polls/{pollId}/votes")
    public ResponseEntity<String> createVote(@PathVariable int pollId, @RequestBody Vote vote) {
        //Check that the voteOption and user id exists.
        if (this.pollManager.getPollById(pollId) == null) {
            return ResponseEntity.badRequest().body("Error: poll with the id " + pollId + " doesn't exist");
        }
        if (this.pollManager.getUserById(vote.getUserId()) == null) {
            return ResponseEntity.badRequest().body("Error: user with the id " + vote.getVoteId() + " can't be found");
        }
        if (this.pollManager.getVoteOptionById(pollId, vote.getVoteOptionId()) == null) {
            return ResponseEntity.badRequest().body("Error: voteOption with the id " + vote.getVoteOptionId() + " can't be found");
        }

        if (this.pollManager.userAlreadyVoted(pollId, vote.getUserId())) {
            return ResponseEntity.badRequest().body("Error: user with the id " + vote.getUserId() + " has already voted on this poll with id " + pollId);
        }
        if (this.pollManager.createVote(pollId, vote)) {
            return ResponseEntity.ok("Successfully voted for poll: " + pollId);
        } else {

            //This would mean the creatorid for the poll and userId voting is the same
            return ResponseEntity.badRequest().body("Error: userId " + vote.getUserId() + " can't vote for its own poll");
        }

    }


    @PutMapping("polls/{pollId}/changes/{userId}/newVoteOptions/{voteOption}")
    public ResponseEntity<String> changeVote(@PathVariable int pollId, @PathVariable int userId, @PathVariable int voteOption) {
        if (this.pollManager.getUserById(userId) != null && this.pollManager.getPollById(pollId) != null) {
            if (this.pollManager.changeVote(pollId, userId, voteOption)) {
                return ResponseEntity.ok("Successfully changed vote for userId " + userId +
                        " to have voteOptionId " + voteOption + " at the poll " + pollId);
            } else {
                return ResponseEntity.badRequest().body("Error: Unable to change vote to have voteOptionId " + voteOption);
            }
        } else {
            return ResponseEntity.badRequest().body("Error: userId " + userId + " doesn't exist or pollId " + pollId + " doesn't exist");
        }
    }
}
