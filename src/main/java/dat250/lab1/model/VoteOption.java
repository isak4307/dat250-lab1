package dat250.lab1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Objects;

public class VoteOption implements Serializable {
    private int id = 0;
    private String caption;
    private int presentationOrder;
    @JsonBackReference
    private Poll poll;

    public VoteOption(String caption, int presentationOrder) {
        this.caption = caption;
        this.presentationOrder = presentationOrder;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        if (this.id == 0) {
            this.id = id;
        }
    }

    public void setPoll(Poll poll) {
        if (this.poll == null) {
            this.poll = poll;
        }
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getPresentationOrder() {
        return presentationOrder;
    }

    public void setPresentationOrder(int presentationOrder) {
        this.presentationOrder = presentationOrder;
    }

    @Override
    public String toString() {
        return "{" +
                "\n id:" + this.id +
                "\n caption:" + this.caption +
                "\n presentationOrder:" + this.presentationOrder +
                "\n}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteOption voteOption)) return false;
        return id == voteOption.id
                && presentationOrder == voteOption.presentationOrder
                && Objects.equals(caption, voteOption.caption)
                && Objects.equals(poll, voteOption.poll);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, caption, presentationOrder, poll);
    }
}
