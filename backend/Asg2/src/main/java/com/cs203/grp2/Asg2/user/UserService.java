package com.cs203.grp2.Asg2.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//to read into firebase
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

@Service
public class UserService {
    @Autowired
    private DatabaseReference firebaseDatabase;
    
    public User getOrCreateUser(String clerkUserId, String email) throws ExecutionException, InterruptedException {
        //Create reference path to firestore/Users/clerkUserId
        DocumentReference ref = firebaseDatabase.child("users").child(clerkUserId);
         //Take snapshot of result
        DocumentSnapshot snap = ref.get().get();
        
        //If there IS a result - there is a user
        if (snap.exists()) {
            return snap.toObject(User.class);
        } 
        //if NO result, no user, so create a new one
        else {     
            User user = new User();
            user.setEmail(email);
            user.setUsername(email);
            user.setRole(User.Role.USER);
            //store new user in firesbase
            ref.set(user).get();
            return user;
        }
    }
    
    public User getUserById(String clerkUserId) 
            throws ExecutionException, InterruptedException {
        DataSnapshot snap = firebaseDatabase.child("users").child(clerkUserId).get().get();
        return snap.exists() ? snap.getValue(User.class) : null;
    }
    
    public void saveUser(String clerkUserId, User user) 
            throws ExecutionException, InterruptedException {
        firebaseDatabase.child("users").child(clerkUserId).setValue(user).get();
    }
    
    public List<String> getUserRoles(String clerkUserId) 
            throws ExecutionException, InterruptedException {
        User user = getUserById(clerkUserId);
        if (user != null && user.getRole() != null) {
            return List.of(user.getRole().toString());
        }
        return List.of();
    }
    
    public void updateUserRole(String clerkUserId, User.Role newRole) 
            throws ExecutionException, InterruptedException {
        User user = getUserById(clerkUserId);
        if (user != null) {
            user.setRole(newRole);
            saveUser(clerkUserId, user);
        }
    }
    
    public List<User> getAllUsers() 
            throws ExecutionException, InterruptedException {
        List<User> users = new ArrayList<>();
        DataSnapshot snap = firebaseDatabase.child("users").get().get();
        for (DataSnapshot child : snap.getChildren()) {
            User user = child.getValue(User.class);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

}
