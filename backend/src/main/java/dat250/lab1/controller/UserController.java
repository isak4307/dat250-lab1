package dat250.lab1.controller;


import dat250.lab1.messager.MessageAction;
import dat250.lab1.messager.MessagerSetup;
import dat250.lab1.messager.Producer;
import dat250.lab1.messager.VoteMessage;
import dat250.lab1.model.PollManager;
import dat250.lab1.model.User;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Objects;

@CrossOrigin
@RestController
@NoArgsConstructor
public class UserController {
    @Autowired
    private MessagerSetup messagerSetup;
    @Autowired
    private Producer producer;
    @Autowired
    private PollManager pollManager;


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

    @PostMapping("/users/signInn")
    public ResponseEntity<User> signInn(@RequestBody User user) {
        HashSet<User> users = this.pollManager.getUsers();
        if (users.contains(user)) {
            for (User u : users) {
                if (u.equals(user)) {
                    return ResponseEntity.ok(u);
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("users/{creatorId}/polls/{pollId}")
    public ResponseEntity<Void> deletePollById(@PathVariable("creatorId") Integer creatorId, @PathVariable("pollId") Integer pollId) {
        if (this.pollManager.getPollById(pollId) == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!Objects.equals(this.pollManager.getPollById(pollId).getCreator().getId(), creatorId)) {
            return ResponseEntity.badRequest().build();
        }
        VoteMessage msg = new VoteMessage(MessageAction.DELETEPOLL, pollId);
        this.producer.sendMessage(pollId, msg);
        return ResponseEntity.accepted().build();

    }
}
