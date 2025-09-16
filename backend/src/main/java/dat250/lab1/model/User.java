package dat250.lab1.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
//A table can't have the name "user" thus have to rename to "users"
@Table(name = "users")
public class User implements Serializable {
    private String username;
    private String email;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // A user can have many polls, but a poll can only have one creator
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Poll> created;


    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.created = new LinkedHashSet<>();
    }

    /**
     * Creates a poll directly from the user.
     * @param question The question of the poll
     * @return The created Poll object
     */
    public Poll createPoll(String question) {
        int yearSeconds = 31556926;
        Poll newPoll = new Poll(question, Instant.now().plusSeconds( yearSeconds), Instant.now(), new ArrayList<>());
        newPoll.setCreator(this);
        this.created.add(newPoll);
        return newPoll;
    }

    /**
     * Creates a new Vote for a given VoteOption in a Poll
     * and returns the Vote as an object.
     * This way to vote creates a new Vote object that takes the option object instead of id
     */
    public Vote voteFor(VoteOption option) {
        return new Vote(this.id, option, Instant.now());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User user)) return false;
        return Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
}
