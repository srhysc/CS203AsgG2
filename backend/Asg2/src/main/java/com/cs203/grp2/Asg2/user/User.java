package com.cs203.grp2.Asg2.user;

import jakarta.validation.constraints.NotBlank;


public class User {

    @NotBlank(message = "UserID is required")
    private String id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    private String email;


    public enum Role {
        USER,
        ADMIN,
        SUPERADMIN
    }

    private Role role;

    // Default constructor
    public User() {}

    // Constructor with parameters
    public User(String id, String email, String username, Role role) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.username = username;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) { 
        this.id = id;
     }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
