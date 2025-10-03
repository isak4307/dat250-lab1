package dat250.lab1.messager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    @Autowired
    private RabbitTemplate template;
    @Autowired
    private MessageSetup messageSetup;
    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(Integer pollId, VoteMessage message) {
        try {
            // Serialize the VoteMessage object before sending it in the appropriate exchange
            String json = objectMapper.writeValueAsString(message);
            String exchangeName = messageSetup.setupMessagePoll(pollId);
            this.template.convertAndSend(exchangeName, "", json);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
