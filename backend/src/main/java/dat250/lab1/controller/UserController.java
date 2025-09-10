package dat250.lab1.controller;


import dat250.lab1.model.Poll;
import dat250.lab1.model.PollManager;
import dat250.lab1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
@CrossOrigin
@RestController
public class UserController {
    private PollManager pollManager;

    public UserController(@Autowired PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @GetMapping("/users")
    public ResponseEntity<HashSet<User>> getUsers() {
        return ResponseEntity.ok(pollManager.getUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = this.pollManager.createUser(user);
        if (newUser != null) {
            return ResponseEntity.ok(newUser);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("users/{creatorId}/polls/{pollId}")
    public ResponseEntity<Poll> deletePollById(@PathVariable int creatorId, @PathVariable int pollId) {
        if (this.pollManager.getPollById(pollId) == null) {
            return ResponseEntity.badRequest().build();
        }
        if (this.pollManager.getPollById(pollId).getCreator().getId() != creatorId) {
            return ResponseEntity.badRequest().build();
        }
        Poll deletedPoll = this.pollManager.deletePollById(pollId);

        if (deletedPoll != null) {
            return ResponseEntity.ok(deletedPoll);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
