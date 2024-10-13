package isuru.msg_backend.payload.user;

public record UserLoginDTO(
        String email,
        String password
) {
}
