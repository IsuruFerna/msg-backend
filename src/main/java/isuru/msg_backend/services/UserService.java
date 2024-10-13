package isuru.msg_backend.services;

import isuru.msg_backend.entities.User;
import isuru.msg_backend.exceptions.NotFoundException;
import isuru.msg_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUserId(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User: " + id + " does not exists!"));
    }

    public User findByUserEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Email: " + email + " does not exists!"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Username: " + username + " does not exists!"));
    }
}
