package com.cs203.grp2.Asg2.user;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import com.google.firebase.database.FirebaseDatabase;
import com.cs203.grp2.Asg2.petroleum.Petroleum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final FirebaseDatabase firebaseDatabase;

    public UserService(FirebaseDatabase firebaseDatabase){
        this.firebaseDatabase = firebaseDatabase;
    }

    // public List<User> getAllUsers() {
    //     return repo.findAll();
    // }

    // public User getUserById(Long id) {
    //     return repo.findById(id)
    //         .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    // }

    // public User getUserByUsername(String username) {
    //     return repo.findByUsername(username)
    //         .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));
    // }

    // public User createUser(User user) {
    //     return repo.save(user);
    // }

    // public void deleteUser(Long id) {
    //     if (!repo.existsById(id)) {
    //         throw new UserNotFoundException("User not found with id " + id);
    //     }
    //     repo.deleteById(id);
    // }
}
