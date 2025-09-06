package dat250.lab1.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class Vote implements Serializable {
    private int id = 0;
    private int userId;
    private int voteOptionId;
    private Instant publishedAt;

    public Vote(int userId, int voteOptionId, Instant publishedAt) {
        this.userId = userId;
        this.publishedAt = publishedAt;
        this.voteOptionId = voteOptionId;
    }

    public int getVoteId() {
        return this.id;
    }

    public void setVoteId(int id) {
        this.id = id;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getVoteOptionId() {
        return this.voteOptionId;
    }

    public void setVoteOptionId(int voteOptionId) {
        this.voteOptionId = voteOptionId;
    }

    @Override
    public String toString() {
        return "{" +
                "\n userId:" + this.userId +
                "\n voteOptionId:" + this.voteOptionId +
                "\n}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vote vote)) return false;
        return id == vote.id
                && userId == vote.userId
                && voteOptionId == vote.voteOptionId
                && Objects.equals(publishedAt, vote.publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, voteOptionId, publishedAt);
    }
}
