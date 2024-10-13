package isuru.msg_backend.payload.errors;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorPayloadList(
        String message,
        LocalDateTime timestamp,
        List<String> errorsList
) {
    public ErrorPayloadList(String message, LocalDateTime timestamp, List<String> errorsList) {
        this.message = message;
        this.timestamp = timestamp;
        this.errorsList = errorsList;
    }
}
