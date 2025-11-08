package com.cs203.grp2.Asg2.service;

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

import com.cs203.grp2.Asg2.models.User.Role;
import com.cs203.grp2.Asg2.models.UserSavedRoute;
import com.cs203.grp2.Asg2.models.User;
import com.cs203.grp2.Asg2.DTO.LandedCostResponse;
import com.cs203.grp2.Asg2.exceptions.UserNotFoundException;

@Service
public class UserService {

    @Autowired
    private FirebaseDatabase firebaseDatabase;

    public CompletableFuture<User> getUserById(String userId)
            throws ExecutionException, InterruptedException {
        // System.out.println("Fetching user: " + userId);
        // DatabaseReference ref = firebaseDatabase.getReference("users").child(userId);
        // return readOnce(ref, User.class);
        DatabaseReference ref = firebaseDatabase.getReference("users").child(userId);
        CompletableFuture<User> future = readOnce(ref, User.class);
        // Exception handling: if user is null, throw UserNotFoundException
        future.thenAccept(user -> {
            if (user == null) {
                throw new UserNotFoundException("User not found: " + userId);
            }
        });
        return future;
    }

    public User getOrCreateUser(String userId, String email, String username)
            throws ExecutionException, InterruptedException {
        User user = getUserById(userId).join(); // blocks until ready

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

    public Role getUserRoles(String userId)
            throws ExecutionException, InterruptedException {
        // User user = getUserById(userId).join();
        // ;
        // if (user != null && user.getRole() != null) {
        //     return user.getRole();
        // }
        // return null;
        User user = getUserById(userId).join();
        // Exception handling: if user or role is null, throw UserNotFoundException
        if (user == null || user.getRole() == null) {
            throw new UserNotFoundException("User or role not found for user: " + userId);
        }
        return user.getRole();
    }

    public void updateUserRole(String userId, User.Role newRole)
            throws ExecutionException, InterruptedException {
        // User user = getUserById(userId).join();
        // ;
        // if (user != null) {
        //     user.setRole(newRole);
        //     saveUser(userId, user);
        // }
        User user = getUserById(userId).join();
        // Exception handling: if user is null, throw UserNotFoundException
        if (user == null) {
            throw new UserNotFoundException("User not found for update: " + userId);
        }
        user.setRole(newRole);
        saveUser(userId, user);
    }

    public void addBookmark(LandedCostResponse response, String userId, String bookmarkName)
            throws ExecutionException, InterruptedException {
        // get user object from firebase, including bookmarks
        User u = getUserById(userId).join();
        
        if (u == null) {
            throw new UserNotFoundException("User not found for adding bookmark: " + userId);
        }

        DatabaseReference ref = firebaseDatabase.getReference("users").child(userId);

        // if user has no bookmarks, create an empty list
        if (u.getBookmarks() == null) {
            u.setBookmarks(new ArrayList<>());
        }
        System.out.println(
                "ADDING BOOKMARK: " + bookmarkName + "IMPORTER: " + response.getImportingCountry() + "EXPORTER: "
                        + response.getExportingCountry() + response.getPricePerUnit() + response.getTotalLandedCost());

        u.getBookmarks().add(new UserSavedRoute(response, bookmarkName));
        // Write updated user back to Firebase
        ref.setValueAsync(u);
    }

    public List<UserSavedRoute> getBookmarks(String userId) throws ExecutionException, InterruptedException {
        DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId);
        DataSnapshot snapshot = getSnapshot(userRef);

        System.out.println("USER BOOKMARK SNAPSHOT: " + snapshot.getValue(User.class));

        // User user = snapshot.getValue(User.class);
        // return user != null && user.getBookmarks() != null
        //         ? user.getBookmarks()
        //         : List.of();
        User user = snapshot.getValue(User.class);
        // Exception handling: if user is null, throw UserNotFoundException
        if (user == null) {
            throw new UserNotFoundException("User not found for bookmarks: " + userId);
        }
        return user.getBookmarks() != null ? user.getBookmarks() : List.of();
    }

    public List<User> getAllUsers()
            throws ExecutionException, InterruptedException {
        // List<User> users = new ArrayList<User>();
        // DatabaseReference ref = firebaseDatabase.getReference("users");
        // DataSnapshot snapshot = getSnapshot(ref);

        // for (DataSnapshot child : snapshot.getChildren()) {
        //     User user = child.getValue(User.class);
        //     if (user != null)
        //         users.add(user);
        // }

        // return users;
        List<User> users = new ArrayList<User>();
        DatabaseReference ref = firebaseDatabase.getReference("users");
        DataSnapshot snapshot = getSnapshot(ref);

        for (DataSnapshot child : snapshot.getChildren()) {
            User user = child.getValue(User.class);
            if (user != null) users.add(user);
        }
        // Exception handling: if users list is empty, throw UserNotFoundException
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found.");
        }
        return users;
    }

    // =========FIREBASE FUNCTION============
    // Helper to synchronously get a snapshot
    private DataSnapshot getSnapshot(DatabaseReference ref)
            throws ExecutionException, InterruptedException {

        // promise to have a Datasnapshot in the future
        CompletableFuture<DataSnapshot> future = new CompletableFuture<>();

        // contact database for that path, run ONCE.
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            // call this when data received
            public void onDataChange(DataSnapshot snapshot) {
                // return a snapshot
                future.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });

        return future.get();
    }

    // Helper method to read a single object
    private <T> CompletableFuture<T> readOnce(DatabaseReference ref, Class<T> clazz) {
        CompletableFuture<T> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot.exists() ? snapshot.getValue(clazz) : null);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }
}
