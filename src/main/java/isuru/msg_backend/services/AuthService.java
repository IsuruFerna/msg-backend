package isuru.msg_backend.services;

import isuru.msg_backend.entities.Role;
import isuru.msg_backend.entities.User;
import isuru.msg_backend.exceptions.BadRequestException;
import isuru.msg_backend.exceptions.UnauthorizedException;
import isuru.msg_backend.payload.NewUserDTO;
import isuru.msg_backend.payload.UserLoginDTO;
import isuru.msg_backend.repositories.UserRepository;
import isuru.msg_backend.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private PasswordEncoder bcrypt;

    public String authenticateUser(UserLoginDTO body) {
        User user = userService.findByUserEmail(body.email());

        if(bcrypt.matches(body.password(), user.getPassword())) {
            return jwtTools.createToken(user);
        } else {
            throw new UnauthorizedException("Not valid credentials!");
        }
    }

    public User save(NewUserDTO body) throws IOException {
        userRepository.findByEmail(body.email()).ifPresent(user -> {
            throw new BadRequestException("Email " + body.email() + " is already used!");
        });

        userRepository.findByUsername(body.username()).ifPresent(user -> {
            throw new BadRequestException("Email " + body.username() + " is already used!");
        });

        if(!body.password().equals(body.passwordConfirm())) {
            throw new org.apache.coyote.BadRequestException("Password and Confirmation mismatch!");
        }

        // TODO: implement password validation

        User newUser = new User();
        newUser.setFirstname(body.firstname());
        newUser.setLastname(body.lastname());
        newUser.setUsername(body.username());
        newUser.setEmail(body.email());
        newUser.setRole(Role.USER);
        newUser.setPassword(bcrypt.encode(body.password()));

        return userRepository.save(newUser);
    }
}
