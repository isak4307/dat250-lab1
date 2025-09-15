package dat250.lab1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Vote implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer voteOptionId;
    private Instant publishedAt;

    public Vote(int userId, int voteOptionId, Instant publishedAt) {
        this.userId = userId;
        this.publishedAt = publishedAt;
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
