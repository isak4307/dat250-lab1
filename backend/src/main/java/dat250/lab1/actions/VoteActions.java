package dat250.lab1.actions;

import dat250.lab1.model.Vote;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Component
public class VoteActions {
    private final AtomicInteger voteCounter = new AtomicInteger(0);

    public void setVoteId(Vote vote) {
        if (vote.getId() == null) {
            vote.setId(this.voteCounter.incrementAndGet());
        }
    }

}
