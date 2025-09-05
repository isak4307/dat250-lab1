package dat250.lab1.actions;

import dat250.lab1.model.Poll;
import dat250.lab1.model.VoteOption;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VoteOptionActions {
    private final AtomicInteger voteOptionIdCounter = new AtomicInteger(0);

    public VoteOptionActions() {
    }

    public void setVoteOptionId(VoteOption vo, Poll poll) {
        if (vo.getId() == 0) {
            vo.setId(this.voteOptionIdCounter.incrementAndGet());
            vo.setPoll(poll);
        }

    }

}
