package dat250.lab1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
@Getter
@Setter
@NoArgsConstructor
public class Poll implements Serializable {
    private int id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    @JsonManagedReference
    private ArrayList<VoteOption> voteOptions;
    @JsonBackReference
    private User creator;

    public Poll(String question, Instant validUntil, Instant publishedAt, ArrayList<VoteOption> voteOptions) {
        this.question = question;
        this.validUntil = validUntil;
        this.publishedAt = publishedAt;
        this.voteOptions = voteOptions;
    }

    public void setCreator(User creator) {
        if (this.creator == null) {
            this.creator = creator;
        }
    }

    public void sortVoteOptions() {
        this.voteOptions = new ArrayList<>(this.voteOptions.stream().sorted(Comparator.comparing(VoteOption::getPresentationOrder)).toList());
    }

    @Override
    public String toString() {
        return "{" +
                "\n id:" + this.id +
                "\n question:" + question +
                ",\n validUntil:" + validUntil +
                ",\n publishedAt:" + publishedAt +
                ",\n voteOptions:" + voteOptions +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Poll poll)) return false;
        return Objects.equals(question, poll.question)
                && Objects.equals(validUntil, poll.validUntil)
                && Objects.equals(publishedAt, poll.publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, validUntil, publishedAt);
    }
}
