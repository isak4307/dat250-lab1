package dat250.lab1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;

public class Poll {
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


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getPollId() {
        return this.id;
    }

    public void setPollId(int id) {
        this.id = id;
    }

    public User getCreator() {
        return this.creator;
    }

    public void setCreator(User creator) {
        if (this.creator == null) {
            this.creator = creator;
        }
    }

    public ArrayList<VoteOption> getVoteOptions() {
        return this.voteOptions;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
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


}
