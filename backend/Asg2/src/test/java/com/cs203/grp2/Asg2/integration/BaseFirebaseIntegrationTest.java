package com.cs203.grp2.Asg2.integration;

import com.google.firebase.database.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Base class for integration tests that interact with Firebase.
 * 
 * This class provides:
 * - Full Spring Boot context loading
 * - Firebase connection management
 * - Test data cleanup utilities
 * - Common test utilities for async operations
 * 
 * Usage:
 * Extend this class for integration tests that need to interact with Firebase services.
 * 
 * Note: These tests require a valid serviceAccountKey.json file in src/test/resources
 * and will connect to the actual Firebase instance. Consider using a separate test
 * database or Firebase emulator for true isolation.
 */
@SpringBootTest
@ActiveProfiles("integration-test")
public abstract class BaseFirebaseIntegrationTest {

    @Autowired
    protected FirebaseDatabase firebaseDatabase;

    protected static final String TEST_DATA_PREFIX = "test_";
    protected static final int TIMEOUT_SECONDS = 10;

    /**
     * Override this method to set up test-specific data before each test.
     * Called after the base setup is complete.
     */
    protected void setupTestData() throws Exception {
        // Override in subclasses if needed
    }

    /**
     * Override this method to clean up test-specific data after each test.
     * Called before the base cleanup.
     */
    protected void cleanupTestData() throws Exception {
        // Override in subclasses if needed
    }

    @BeforeEach
    public void baseSetup() throws Exception {
        setupTestData();
    }

    @AfterEach
    public void baseCleanup() throws Exception {
        cleanupTestData();
    }

    /**
     * Utility method to write data to Firebase synchronously
     */
    protected <T> void writeToFirebase(String path, T data) throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference(path);
        ref.setValueAsync(data).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Utility method to read data from Firebase synchronously
     */
    protected <T> T readFromFirebase(String path, Class<T> clazz) throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference(path);
        CompletableFuture<T> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    T value = snapshot.getValue(clazz);
                    future.complete(value);
                } else {
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Utility method to delete data from Firebase synchronously
     */
    protected void deleteFromFirebase(String path) throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference(path);
        ref.removeValueAsync().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Utility method to check if a path exists in Firebase
     */
    protected boolean existsInFirebase(String path) throws Exception {
        DatabaseReference ref = firebaseDatabase.getReference(path);
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                future.complete(snapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Generate a unique test ID to avoid conflicts between test runs
     */
    protected String generateTestId() {
        return TEST_DATA_PREFIX + System.currentTimeMillis() + "_" + 
               Thread.currentThread().threadId();
    }
}
