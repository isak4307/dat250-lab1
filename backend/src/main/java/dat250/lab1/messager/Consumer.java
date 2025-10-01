package dat250.lab1.messager;


import com.fasterxml.jackson.databind.ObjectMapper;
import dat250.lab1.model.PollManager;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Consumer {
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PollManager pollManager;

    public void receiveMessage(String queueName) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(message -> {
            try {
                VoteMessage msg = objectMapper.readValue(message.getBody(), VoteMessage.class);
                performAction(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        container.start();
    }

    private void performAction(VoteMessage msg) {
        if (msg.getPollId() != null) {
            switch (msg.getAction()) {
                case CREATEVOTE:
                    if(msg.getVote()!=null)
                        this.pollManager.createVote(msg.getPollId(), msg.getVote());
                    break;
                case CHANGEVOTE:
                    if (msg.getUserId() != null && msg.getVoteOptionId() != null)
                        this.pollManager.changeVote(msg.getPollId(), msg.getUserId(), msg.getVoteOptionId());
                    break;
                case DELETEPOLL:
                    this.pollManager.deletePollById(msg.getPollId());
                    break;
                default:
                    throw new UnsupportedOperationException(msg.getAction().toString());
            }
        }
    }
}
