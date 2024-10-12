package isuru.msg_backend.controllers;

import isuru.msg_backend.entities.Message;
import isuru.msg_backend.services.KafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequestMapping("/chat")
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

    @PostMapping("/api/send-private")
    public ResponseEntity<?> sendPrivateMessages(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now().toString());

        String[] topics = {message.getSender(), message.getReceiver()};
        Arrays.sort(topics);
        String topic = topics[0] + "-" + topics[1];

        System.out.println("these are the ascending orderd: " + topics[0] + " : " + topics[1]);

        try {
            publisher.sendMessageToUser(message, topic);
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/group")
    public Message broadcastGroupMessage(@Payload Message message) {
        // sending this to all the subscribers
        System.out.println("im listening!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        message.setTimestamp(LocalDateTime.now().toString());
        publisher.sendMessageToTopic(message);

        return message;
    }

    @MessageMapping("/sendMessageTo")
    @SendTo("/topic/private")
    public Message broadcastPrivateMessage(@Payload Message message) {
        System.out.println("!!!!!!!!!!!!!!!!!!sending private msg!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        // creating unique topic for both receiver and sender
        // TODO: update with uuids
        // TODO: later implement a notification system to keep the performance instead of creating a topic to store all the data of all private messages
        // TODO: send topic ID to ovoid overcomplicated conflicts when receiving messages
        String[] topics = {message.getSender(), message.getReceiver()};
        Arrays.sort(topics);
        String topic = topics[0] + "-" + topics[1];

        System.out.println("these are the ascending orderd: " + topics[0] + " : " + topics[1]);

        message.setTimestamp(LocalDateTime.now().toString());
        publisher.sendMessageToUser(message, topic);

        System.out.println("this is the responese: " + message);


        return message;
    }
}
