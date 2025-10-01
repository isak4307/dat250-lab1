package dat250.lab1.messager;

import dat250.lab1.model.Poll;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    @Autowired
    private RabbitTemplate template;
    @Autowired
    private MessagerSetup messagerSetup;

    public void sendMessage(Integer pollId, VoteMessage message) {
        String exchangeName = messagerSetup.setupMessagerPoll(pollId);
        this.template.convertAndSend(exchangeName, "", message);

    }
}
