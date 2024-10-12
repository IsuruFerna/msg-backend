package isuru.msg_backend.payload;

public record UserLoginResponseDTO(
        String token,
        String id
) {
}
