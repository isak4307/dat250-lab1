package dat250.lab1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Poll implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    // A poll can have many voteOptions, but a voteoption belong to only one poll
    //OrphanRemoval removes a voteOption if the corresponding Poll object doesn't exist
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<VoteOption> options;
    // A user can have many Polls, but a poll can only belong to one creator
    @ManyToOne
    @JoinColumn(name="creator_id")
    @JsonBackReference
    private User creator;

    public Poll(String question, Instant validUntil, Instant publishedAt, ArrayList<VoteOption> options) {
        this.question = question;
        this.validUntil = validUntil;
        this.publishedAt = publishedAt;
        this.options = options;
    }

    public void setCreator(User creator) {
        if (this.creator == null) {
            this.creator = creator;
        }
    }

    /**
     * Sorts the voteOptions on by the presentationOrder
     */
    public void sortVoteOptions() {
        this.options = new ArrayList<>(this.options.stream().sorted(Comparator.comparing(VoteOption::getPresentationOrder)).toList());
    }

    @Override
    public String toString() {
        return "{" +
                "\n id:" + this.id +
                "\n question:" + question +
                ",\n validUntil:" + validUntil +
                ",\n publishedAt:" + publishedAt +
                ",\n options:" + options +
                '}';
    }
    public VoteOption addVoteOption(String caption) {
       VoteOption vo = new VoteOption(caption,this.options.size());
       vo.setPoll(this);
       this.options.add(vo);
       return vo;
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
