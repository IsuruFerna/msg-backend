package isuru.msg_backend.payload.user;

public record UserResponseDTO(
        String id,
        String username,
        String firstname,
        String lastname,
        String email,
        String role
) {
}
