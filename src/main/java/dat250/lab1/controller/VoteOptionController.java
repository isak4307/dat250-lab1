package dat250.lab1.controller;

import dat250.lab1.model.PollManager;
import dat250.lab1.model.VoteOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;

@RestController
public class VoteOptionController {
    private PollManager pollManager;

    public VoteOptionController(@Autowired PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @GetMapping("/voteOptions")
    public ResponseEntity<HashSet<ArrayList<VoteOption>>> getAllVoteOptions() {
        return ResponseEntity.ok(pollManager.getAllVoteOptions());
    }

}
