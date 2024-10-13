package isuru.msg_backend.controllers;

import isuru.msg_backend.entities.User;
import isuru.msg_backend.exceptions.BadRequestException;
import isuru.msg_backend.payload.user.NewUserDTO;
import isuru.msg_backend.payload.user.NewUserResponseDTO;
import isuru.msg_backend.payload.user.UserLoginDTO;
import isuru.msg_backend.payload.user.UserLoginResponseDTO;
import isuru.msg_backend.services.AuthService;
import isuru.msg_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserLoginResponseDTO login(@RequestBody UserLoginDTO body) {
        String accessToken = authService.authenticateUser(body);
        User user = userService.findByUserEmail(body.email());
        return new UserLoginResponseDTO(accessToken, user.getId());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserResponseDTO creteUser(@RequestBody @Validated NewUserDTO newUserPayload, BindingResult validation) throws IOException {
        if(validation.hasErrors()) {
            System.out.println("validation errors: " + validation.getAllErrors());
            throw new BadRequestException(validation.getAllErrors());
        } else {
            User newUser = authService.save(newUserPayload);
            return new NewUserResponseDTO(newUser.getId());
        }
    }

}
