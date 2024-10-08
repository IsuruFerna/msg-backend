package isuru.msg_backend.entities;

import lombok.Data;

@Data
public class Message {
    private String sender;
    private String receiver;
    private String content;
    private String timestamp;
}
