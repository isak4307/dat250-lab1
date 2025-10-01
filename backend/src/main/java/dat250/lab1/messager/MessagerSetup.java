package dat250.lab1.messager;

import dat250.lab1.model.Poll;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagerSetup {
    @Autowired
    private AmqpAdmin amqpAdmin;

    public String setupMessagerPoll(Integer pollId) {
        String exchangeName = "poll-" + pollId;
        String queueName = "poll-" + pollId + "-queue";

        TopicExchange exchange = new TopicExchange(exchangeName);
        Queue queue = new Queue(queueName);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with("");

        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
        return exchangeName;
    }
}
