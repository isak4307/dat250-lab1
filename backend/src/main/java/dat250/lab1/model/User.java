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
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User implements Serializable {
    private String username;
    private String email;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    public LinkedHashSet<Poll> created;


    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.created = new LinkedHashSet<>();
    }
    public Poll createPoll(String question) {
        Poll newPoll = new Poll(question, Instant.now().plusSeconds(31556926),Instant.now(),new ArrayList<>());
        this.created.add(newPoll);
        return newPoll;
    }

    /**
     * Creates a new Vote for a given VoteOption in a Poll
     * and returns the Vote as an object.
     */
    public Vote voteFor(VoteOption option) {
        Vote newVote = new Vote(this.id,option.getId(),Instant.now());
        return newVote;
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
