package com.cs203.grp2.Asg2.user;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    @Autowired
    private FirebaseDatabase firebaseDatabase; // ✅ changed from DatabaseReference

    // Helper method to read a DataSnapshot asynchronously
    private <T> T readOnce(DatabaseReference ref, Class<T> clazz)
            throws ExecutionException, InterruptedException {
        CompletableFuture<T> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    future.complete(snapshot.getValue(clazz));
                } else {
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future.get(); // blocks until Firebase responds
    }

    public User getUserById(String userId)
            throws ExecutionException, InterruptedException {
        DatabaseReference ref = firebaseDatabase.getReference("users").child(userId); // ✅ updated
        return readOnce(ref, User.class);
    }

    public User getOrCreateUser(String userId, String email, String username)
            throws ExecutionException, InterruptedException {
        User user = getUserById(userId);

        if (user != null) {
            return user;
        } else {
            user = new User();
            user.setId(userId);
            user.setEmail(email);
            user.setRole(User.Role.USER);
            user.setUsername(username);

            firebaseDatabase.getReference("users").child(userId).setValueAsync(user).get(); 
            return user;
        }
    }

    public void saveUser(String userId, User user) {
        firebaseDatabase.getReference("users").child(userId).setValueAsync(user); 
    }

    public List<String> getUserRoles(String userId)
            throws ExecutionException, InterruptedException {
        User user = getUserById(userId);
        if (user != null && user.getRole() != null) {
            return List.of(user.getRole().toString());
        }
        return List.of();
    }

    public void updateUserRole(String userId, User.Role newRole)
            throws ExecutionException, InterruptedException {
        User user = getUserById(userId);
        if (user != null) {
            user.setRole(newRole);
            saveUser(userId, user);
        }
    }

    public List<User> getAllUsers()
            throws ExecutionException, InterruptedException {
        DatabaseReference ref = firebaseDatabase.getReference("users");
        CompletableFuture<List<User>> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) users.add(user);
                }
                future.complete(users);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future.get();
    }
}
