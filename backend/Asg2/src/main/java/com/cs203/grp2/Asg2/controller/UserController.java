package com.cs203.grp2.Asg2.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.cs203.grp2.Asg2.service.UserService;
import com.cs203.grp2.Asg2.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public User getProfile() throws ExecutionException, InterruptedException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUserById(user.getId());
    }

    @GetMapping("/roles")
    public List<String> getUserRoles() throws ExecutionException, InterruptedException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUserRoles(user.getId());
    }

    @PutMapping("/role")
    public void updateRole(@RequestBody User.Role newRole) throws ExecutionException, InterruptedException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateUserRole(user.getId(), newRole);
    }

    @GetMapping
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return userService.getAllUsers();
    }
}