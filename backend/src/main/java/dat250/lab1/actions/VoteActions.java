package dat250.lab1.actions;

import dat250.lab1.model.Vote;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VoteActions {
    private final AtomicInteger voteCounter = new AtomicInteger(0);

    public VoteActions() {
    }

    public void setVoteId(Vote vote) {
        if (vote.getId() == 0) {
            vote.setId(this.voteCounter.incrementAndGet());
        }
    }

}
