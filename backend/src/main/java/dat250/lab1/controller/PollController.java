package dat250.lab1.controller;

import dat250.lab1.messager.*;
import dat250.lab1.model.*;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@CrossOrigin
@RestController
@NoArgsConstructor
public class PollController {
    @Autowired
    private PollManager pollManager;
    @Autowired
    private Producer producer;
    @Autowired
    private MessagerSetup messagerSetup;
    @Autowired
    private Consumer consumer;

    @GetMapping("/polls")
    public ResponseEntity<HashSet<Poll>> getPolls() {
        return ResponseEntity.ok(this.pollManager.getPolls());
    }

    @GetMapping("/polls/{pollId}/result")
    public ResponseEntity<HashMap<VoteOption, Integer>> getResultOfPoll(@PathVariable("pollId") Integer pollId) {
        Poll poll = this.pollManager.getPollById(pollId);
        if (poll == null) {
            return ResponseEntity.badRequest().build();
        }
        HashMap<VoteOption, Integer> result = pollManager.voteCounter(pollId);
        return ResponseEntity.ok(result);
    }

    /**
     * Check if the poll has already been created with the same question and the same creator
     *
     * @param poll the poll it attempts to create
     * @param cId  the creator id
     * @return whether the poll has been created by the same user and if the question are same
     */
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
    public ResponseEntity<Poll> createPoll(@PathVariable("creatorId") Integer creatorId, @RequestBody Poll poll) {
        List<VoteOption> voteOptions = poll.getOptions();
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
        Poll createdPoll = pollManager.createPoll(poll, creator);
        registerTopic(createdPoll);
        return ResponseEntity.ok(createdPoll);

    }

    /**
     * Register a topic that is linked to the created poll
     *
     * @param poll The poll which should be linked to that topic
     */
    private void registerTopic(Poll poll) {
        String queueName = "poll-" + poll.getId() + "-queue";
        this.messagerSetup.setupMessagerPoll(poll.getId());
        consumer.receiveMessage(queueName);
    }

    @PostMapping("/polls/{pollId}/votes")
    public ResponseEntity<Void> createVote(@PathVariable("pollId") Integer pollId, @RequestBody Vote vote) {
        //Check that the voteOption and user id exists.
        if (this.pollManager.getPollById(pollId) == null
                || this.pollManager.getUserById(vote.getUserId()) == null
                || this.pollManager.getVoteOptionById(pollId, vote.getVoteOptionId()) == null
                || this.pollManager.userAlreadyVoted(pollId, vote.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        VoteMessage msg = new VoteMessage(MessageAction.CREATEVOTE, pollId, vote);
        this.producer.sendMessage(pollId, msg);
        return ResponseEntity.accepted().build();

    }


    @PutMapping("polls/{pollId}/changes/{userId}/newVoteOptions/{voteOption}")
    public ResponseEntity<Vote> changeVote(@PathVariable("pollId") Integer pollId, @PathVariable("userId") Integer userId, @PathVariable("voteOption") Integer voteOption) {
        if (this.pollManager.getUserById(userId) != null && this.pollManager.getPollById(pollId) != null) {
            VoteMessage msg = new VoteMessage(MessageAction.CHANGEVOTE, pollId, userId, voteOption);
            this.producer.sendMessage(pollId, msg);
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
