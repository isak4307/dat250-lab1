package dat250.lab1.controller;


import dat250.lab1.model.PollManager;
import dat250.lab1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
public class UserController {
    private PollManager pollManager;

    public UserController(@Autowired PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @GetMapping("/users")
    public HashSet<User> getUsers() {
        return this.pollManager.getUsers();
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if (this.pollManager.createUser(user)) {
            return ResponseEntity.ok("Created user:" + user.getUsername());

        } else {
            return ResponseEntity.badRequest().body("Error: Username " + user.getUsername() + " or email " + user.getEmail() + " already exists");
        }
    }
}
