package isuru.msg_backend.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import isuru.msg_backend.entities.User;
import isuru.msg_backend.payload.user.UserResponseDTO;
import isuru.msg_backend.repositories.UserRepository;
import isuru.msg_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@JsonIgnoreProperties("")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "username") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return userRepository.findAll(pageable);
    }

    @GetMapping("/user/{username}")
    public UserResponseDTO getUser(@PathVariable String username) {

        User user = userService.findByUsername(username);
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getRole().toString()
        );
    }

    // search users by name
    @GetMapping("/{name}")
    public Page<User> searchUsersByName(@PathVariable String name,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "username") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        Criteria criteria = new Criteria().orOperator(
                Criteria.where("username").regex(name, "i"),
                Criteria.where("firstname").regex(name, "i"),
                Criteria.where("lastname").regex(name, "i")
        );

        // Group
        // MatchOperation
        MatchOperation matchOperation = Aggregation.match(criteria);
        // SortOperation
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.ASC, "username"));
        //Aggregation
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                sortOperation,
                Aggregation.skip((long) pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()));

        AggregationResults<User> output = mongoTemplate.aggregate(aggregation, "user", User.class);

        // page
        List<User> users = output.getMappedResults();

        long total = mongoTemplate.count(new Query(criteria), User.class);
        return new PageImpl<>(users, pageable, total);
    }

//    @PostMapping("/addUser")
//    public void addUser(@RequestBody User user) {
//        userRepository.save(user);
//    }
 }
