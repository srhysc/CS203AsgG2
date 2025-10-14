package com.cs203.grp2.Asg2.user;


import jakarta.validation.constraints.NotBlank;


public class User {

    
    private final String email;
    private final String name;
    private final String role;

    public User(String email, String name, String role){
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User [email=" + email + ", name=" + name + ", role=" + role + "]";
    }

    
    
}
