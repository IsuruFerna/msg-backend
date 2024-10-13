package isuru.msg_backend.payload.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

public record ErrorPayload(
        String message,
        LocalDateTime timestamp
) {
    public ErrorPayload(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
