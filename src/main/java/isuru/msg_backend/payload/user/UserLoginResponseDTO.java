package isuru.msg_backend.payload.user;

public record UserLoginResponseDTO(
        String token,
        String id
) {
}
