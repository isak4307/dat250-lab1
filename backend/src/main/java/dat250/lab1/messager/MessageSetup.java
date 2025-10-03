package dat250.lab1.messager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Getter
@Setter
@NoArgsConstructor
@Component
public class MessageSetup {
    private final String EXCHANGENAME = "poll-";
    private final String QUEUENAME = "-queue";
    @Autowired
    private AmqpAdmin amqpAdmin;

    public String setupMessagePoll(Integer pollId) {
        String exchangeName = EXCHANGENAME + pollId;
        String queueName = EXCHANGENAME + pollId + QUEUENAME;

        TopicExchange exchange = new TopicExchange(exchangeName);
        Queue queue = new Queue(queueName);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with("");

        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
        return exchangeName;
    }
}
