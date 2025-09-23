package com.cs203.grp2.Asg2.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    public User getUserByUsername(String username) {
        return repo.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));
    }

    public User createUser(User user) {
        return repo.save(user);
    }

    public void deleteUser(Long id) {
        if (!repo.existsById(id)) {
            throw new UserNotFoundException("User not found with id " + id);
        }
        repo.deleteById(id);
    }
}
