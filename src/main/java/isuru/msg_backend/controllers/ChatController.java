package isuru.msg_backend.controllers;

import isuru.msg_backend.entities.Message;
import isuru.msg_backend.services.KafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ChatController {

    @Autowired
    private KafkaMessagePublisher publisher;

    @PostMapping("/api/send")
    public ResponseEntity<?> sendMessages(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now().toString());
        try {
            publisher.sendMessageToTopic(message);
            return ResponseEntity.ok("Message published successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/group")
    public Message broadcastGroupMessage(@Payload Message message) {
        // sending this to all the subscribers
        return message;
    }
}
