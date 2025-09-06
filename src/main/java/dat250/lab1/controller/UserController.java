package dat250.lab1.controller;


import dat250.lab1.model.PollManager;
import dat250.lab1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

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
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if (this.pollManager.createUser(user)) {
            return ResponseEntity.ok("Created user:" + user.getUsername());

        } else {
            return ResponseEntity.badRequest().body("Error: Username " + user.getUsername() + " or email " + user.getEmail() + " already exists");
        }
    }

    @DeleteMapping("users/{creatorId}/polls/{pollId}")
    public ResponseEntity<String> deletePollById(@PathVariable int creatorId, @PathVariable int pollId) {
        if (this.pollManager.getPollById(pollId) == null) {
            return ResponseEntity.badRequest().body("Error: poll with id " + pollId + " doesn't exist");
        }
        if (this.pollManager.getPollById(pollId).getCreator().getUserId() != creatorId) {
            return ResponseEntity.badRequest().body("Error: Poll can't be deleted if you aren't the creator");
        }
        if (this.pollManager.deletePollById(pollId)) {
            return ResponseEntity.ok("Deleted poll with id:" + pollId);
        } else {
            return ResponseEntity.badRequest().body("Error: Unable to delete poll with id " + pollId);
        }
    }
}
