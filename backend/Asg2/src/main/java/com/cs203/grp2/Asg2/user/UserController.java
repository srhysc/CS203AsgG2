package com.cs203.grp2.Asg2.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService svc;

    public UserController(UserService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return svc.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return svc.getUserById(id);
    }

    @GetMapping("/by-username")
    public User getUserByUsername(@RequestParam String username) {
        return svc.getUserByUsername(username);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return svc.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        svc.deleteUser(id);
    }
}
