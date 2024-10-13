package isuru.msg_backend.services;

import isuru.msg_backend.entities.Role;
import isuru.msg_backend.entities.User;
import isuru.msg_backend.exceptions.BadRequestException;
import isuru.msg_backend.exceptions.UnauthorizedException;
import isuru.msg_backend.payload.user.NewUserDTO;
import isuru.msg_backend.payload.user.UserLoginDTO;
import isuru.msg_backend.repositories.UserRepository;
import isuru.msg_backend.security.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        List<ObjectError> errorList = new ArrayList<>();

        userRepository.findByEmail(body.email()).ifPresent(user -> {
            errorList.add(new ObjectError("email", "Email " + body.email() + " is already used!"));
        });

        userRepository.findByUsername(body.username()).ifPresent(user -> {
            errorList.add(new ObjectError("username", "Username " + body.username() + " is already used!"));
        });

        if(!body.password().equals(body.passwordConfirm())) {
            errorList.add(new ObjectError("password", "Password and Confirmation mismatch!"));
        }

        // TODO: implement password validation

        // check error list and throw errors instead of saving data
        if(!errorList.isEmpty()) {
            throw new BadRequestException(errorList);
        }

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
