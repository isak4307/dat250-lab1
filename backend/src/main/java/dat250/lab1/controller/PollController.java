package dat250.lab1.controller;

import dat250.lab1.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@CrossOrigin
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
    public ResponseEntity<HashMap<VoteOption, Integer>> getResultOfPoll(@PathVariable Integer pollId) {
        Poll poll = this.pollManager.getPollById(pollId);
        if (poll == null) {
            return ResponseEntity.badRequest().build();
        }
        HashMap<VoteOption, Integer> result = pollManager.voteCounter(pollId);
        return ResponseEntity.ok(result);
    }

    private boolean samePollQandC(Poll poll, Integer cId) {
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
    public ResponseEntity<Poll> createPoll(@PathVariable Integer creatorId, @RequestBody Poll poll) {
        List<VoteOption> voteOptions = poll.getVoteOptions();
        if (samePollQandC(poll, creatorId) || voteOptions.size() < 2) {
            return ResponseEntity.badRequest().build();
        }

        // get user id for the creator and add it together later on
        User creator = this.pollManager.getUserById(creatorId);
        if (creator == null) {
            return ResponseEntity.badRequest().build();
        }
        //create voteOptions
        this.pollManager.createVoteOptions(poll);
        return ResponseEntity.ok(pollManager.createPoll(poll, creator));

    }

    @PostMapping("/polls/{pollId}/votes")
    public ResponseEntity<Vote> createVote(@PathVariable int pollId, @RequestBody Vote vote) {
        //Check that the voteOption and user id exists.
        if (this.pollManager.getPollById(pollId) == null
                || this.pollManager.getUserById(vote.getUserId()) == null
                || this.pollManager.getVoteOptionById(pollId, vote.getVoteOptionId()) == null
                || this.pollManager.userAlreadyVoted(pollId, vote.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        Vote createdVote = this.pollManager.createVote(pollId, vote);
        if (createdVote != null) {
            return ResponseEntity.ok(createdVote);
        } else {
            //This would mean the creatorid for the poll and userId voting is the same
            return ResponseEntity.badRequest().build();
        }

    }


    @PutMapping("polls/{pollId}/changes/{userId}/newVoteOptions/{voteOption}")
    public ResponseEntity<Vote> changeVote(@PathVariable int pollId, @PathVariable int userId, @PathVariable int voteOption) {
        if (this.pollManager.getUserById(userId) != null && this.pollManager.getPollById(pollId) != null) {
            Vote changedVote = this.pollManager.changeVote(pollId, userId, voteOption);
            if (changedVote != null) {
                return ResponseEntity.ok(changedVote);
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
