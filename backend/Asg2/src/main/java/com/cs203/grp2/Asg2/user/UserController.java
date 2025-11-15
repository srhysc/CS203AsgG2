package com.cs203.grp2.Asg2.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Map;

import com.cs203.grp2.Asg2.service.UserService;
import com.cs203.grp2.Asg2.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.ExecutionException;

import com.cs203.grp2.Asg2.DTO.BookmarkRequest;
import com.cs203.grp2.Asg2.models.User.Role;
import com.cs203.grp2.Asg2.models.UserSavedRoute;



@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public User getProfile() throws ExecutionException, InterruptedException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUserById(user.getId()).join();
    }

    @GetMapping("/roles")
    public String getUserRoles() throws ExecutionException, InterruptedException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUserRoles(user.getId()).name();
    }

    @PutMapping("/role")
    public void updateRole(@RequestBody User.Role newRole) throws ExecutionException, InterruptedException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.updateUserRole(user.getId(), newRole);
    }

    @GetMapping("/bookmarks")
    public List<UserSavedRoute> getBookmarks() throws ExecutionException, InterruptedException {

    // Get the authenticated user object from clerk jwt filter
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Call the service using the actual verified user ID
    return userService.getBookmarks(user.getId());
    }


    @PostMapping("/bookmarks")
    public ResponseEntity<?> addBookmark(@RequestBody BookmarkRequest bookmarkRequest)
        throws ExecutionException, InterruptedException {

    if (bookmarkRequest.getSavedResponse() == null) {
        return ResponseEntity.badRequest().body("Missing savedResponse in request");
    }
    if (bookmarkRequest.getSavedResponse().getImportingCountry() == null) {
        return ResponseEntity.badRequest().body("Missing importingCountry in savedResponse");
    }

System.out.println("BOOKMARK RESPONSE: " + bookmarkRequest.getBookmarkName() + "IMPORTER: " + bookmarkRequest.getSavedResponse().getImportingCountry() + "EXPORTER: " + bookmarkRequest.getSavedResponse().getExportingCountry() + bookmarkRequest.getSavedResponse().getPricePerUnit() + bookmarkRequest.getSavedResponse().getTotalLandedCost()); 

    // Get the authenticated user object from clerk jwt filter
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Call the service using the actual verified user ID
    userService.addBookmark(
        bookmarkRequest.getSavedResponse(),
        user.getId(),
        bookmarkRequest.getBookmarkName()
    );

        return ResponseEntity.ok("Bookmark added successfully");

    }


    @GetMapping
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return userService.getAllUsers();
    }
}