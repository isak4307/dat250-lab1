package dat250.lab1.actions;

import dat250.lab1.model.Poll;
import dat250.lab1.model.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PollActions {
    private final AtomicInteger pollIdCounter = new AtomicInteger(0);

    public PollActions() {
    }

    public void setPollId(Poll poll, User creator) {
        if (poll.getId() == null) {
            poll.setId(this.pollIdCounter.incrementAndGet());
            poll.setCreator(creator);
        }
    }

}
