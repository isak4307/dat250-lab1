package dat250.lab1.messager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Autowired
    private ObjectMapper objectMapper;
    public void sendMessage(Integer pollId, VoteMessage message) {
        try{
            String obj = objectMapper.writeValueAsString(message);
            String exchangeName = messagerSetup.setupMessagerPoll(pollId);
            this.template.convertAndSend(exchangeName, "",obj );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
