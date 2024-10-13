package isuru.msg_backend.payload.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewUserDTO(
        @NotEmpty(message = "Name is required!")
        @Size(min = 3, max = 30, message = "Name must contains between 3 to 30 characters")
        String firstname,

        @NotEmpty(message = "Last name is required!")
        @Size(min = 3, max = 30, message = "Last name must contains between 3 to 30 characters")
        String lastname,

        @NotEmpty(message = "Username is required!")
        @Size(min = 3, max = 30, message = "Username must contains between 3 to 30 characters")
        String username,

        @NotEmpty(message = "Email is required!")
        @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email isn't valid")
        String email,

        @NotEmpty(message = "Password is required!")
        @Size(min = 3, max = 36, message = "Password must contains between 12 to 36 characters")
        String password,

        @NotEmpty(message = "Password confirmation is required!")
        String passwordConfirm
) {
}
