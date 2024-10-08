package isuru.msg_backend.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @Autowired
    private SimpMessagingTemplate template;

    @KafkaListener(topics = "topic-message", groupId = "group-message")
    public void listen(Message message) {
        System.out.println("sending via kafka listener..");

        // convert the message and send that to WebSocket topic
        template.convertAndSend("/topic/group", message.getPayload());
    }
}
