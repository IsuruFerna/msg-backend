package isuru.msg_backend.payload;

public record UserLoginDTO(
        String email,
        String password
) {
}
