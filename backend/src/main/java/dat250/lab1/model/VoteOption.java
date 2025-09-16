package dat250.lab1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class VoteOption implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String caption;
    private int presentationOrder;
    //A poll can have many voteoptions, but a voteoption belongs to only one poll object
    @ManyToOne
    @JoinColumn(name = "poll_id")
    @JsonBackReference
    private Poll poll;

    public VoteOption(String caption, int presentationOrder) {
        this.caption = caption;
        this.presentationOrder = presentationOrder;
    }
    public void setId(Integer id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public void setPoll(Poll poll) {
        if (this.poll == null) {
            this.poll = poll;
        }
    }
    @Override
    public String toString() {
        return "{" +
                "id:" + this.id +
                "; caption:" + this.caption + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteOption voteOption)) return false;
        return Objects.equals(id, voteOption.id)
                && presentationOrder == voteOption.presentationOrder
                && Objects.equals(caption, voteOption.caption)
                && Objects.equals(poll, voteOption.poll);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, caption, presentationOrder, poll);
    }
}
