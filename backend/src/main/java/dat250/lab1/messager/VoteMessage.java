package dat250.lab1.messager;

import dat250.lab1.model.Vote;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class VoteMessage {
    private Integer pollId;
    private Integer userId;
    private Integer voteOptionId;
    private Vote vote;
    private MessageAction action;
    public VoteMessage(MessageAction action, Integer pollId,Integer userId, Integer voteOptionId) {
        this.action = action;
        this.pollId = pollId;
        this.userId = userId;
        this.voteOptionId = voteOptionId;

    }
    // Create Poll
    public VoteMessage(MessageAction action, Integer pollId,Vote vote) {
        this.action = action;
        this.pollId = pollId;
        this.vote = vote;
    }

}
