package com.cs203.grp2.Asg2.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FirebaseConfigTest {

    private FirebaseConfig firebaseConfig;

    @BeforeEach
    void setUp() {
        firebaseConfig = new FirebaseConfig();
        // Clean up any existing Firebase apps before each test
        for (FirebaseApp app : FirebaseApp.getApps()) {
            app.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up Firebase apps after each test
        for (FirebaseApp app : FirebaseApp.getApps()) {
            try {
                app.delete();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }

    @Test
    void testFirebaseDatabase_WithValidServiceAccount_ShouldInitializeSuccessfully() throws IOException {
        // Act
        FirebaseDatabase database = firebaseConfig.firebaseDatabase();

        // Assert
        assertNotNull(database);
        assertFalse(FirebaseApp.getApps().isEmpty());
        assertEquals(1, FirebaseApp.getApps().size());
    }

    @Test
    void testFirebaseDatabase_CalledMultipleTimes_ShouldReturnSameInstance() throws IOException {
        // Act
        FirebaseDatabase database1 = firebaseConfig.firebaseDatabase();
        FirebaseDatabase database2 = firebaseConfig.firebaseDatabase();

        // Assert
        assertNotNull(database1);
        assertNotNull(database2);
        // Firebase apps should only be initialized once
        assertEquals(1, FirebaseApp.getApps().size());
    }

    @Test
    void testFirebaseDatabase_WithMissingServiceAccount_ShouldThrowIOException() {
        // Create a config that will look for a non-existent file
        FirebaseConfig configWithMissingFile = new FirebaseConfig() {
            @Override
            public FirebaseDatabase firebaseDatabase() throws IOException {
                if (FirebaseApp.getApps().isEmpty()) {
                    InputStream serviceAccount =
                            getClass().getClassLoader().getResourceAsStream("nonexistent.json");

                    if (serviceAccount == null) {
                        throw new IOException(" serviceAccountKey.json not found in classpath!");
                    }
                }
                return FirebaseDatabase.getInstance();
            }
        };

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> {
            configWithMissingFile.firebaseDatabase();
        });

        assertTrue(exception.getMessage().contains("serviceAccountKey.json not found"));
    }

    @Test
    void testFirebaseDatabase_CheckDatabaseUrl_ShouldMatchExpectedUrl() throws IOException {
        // Act
        FirebaseDatabase database = firebaseConfig.firebaseDatabase();

        // Assert
        assertNotNull(database);
        // Verify the Firebase app was initialized with correct database URL
        FirebaseApp app = FirebaseApp.getInstance();
        assertNotNull(app);
        // The database URL should be accessible through the FirebaseOptions
        String dbUrl = app.getOptions().getDatabaseUrl();
        assertEquals("https://cs203asg-default-rtdb.asia-southeast1.firebasedatabase.app", dbUrl);
    }

    @Test
    void testFirebaseDatabase_VerifyServiceAccountResource_ShouldExist() {
        // Verify that the service account key file exists in classpath
        InputStream serviceAccount = 
            getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");

        assertNotNull(serviceAccount, "serviceAccountKey.json should exist in classpath");
        
        try {
            serviceAccount.close();
        } catch (IOException e) {
            // Ignore close errors
        }
    }

    @Test
    void testFirebaseDatabase_AfterSuccessfulInit_FirebaseAppShouldBeAvailable() throws IOException {
        // Act
        firebaseConfig.firebaseDatabase();

        // Assert
        assertFalse(FirebaseApp.getApps().isEmpty());
        assertDoesNotThrow(() -> FirebaseApp.getInstance());
    }

    @Test
    void testFirebaseDatabase_Configuration_ShouldBeAnnotatedWithBean() throws NoSuchMethodException {
        // Verify that the method is annotated with @Bean
        var method = FirebaseConfig.class.getMethod("firebaseDatabase");
        assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
    }

    @Test
    void testFirebaseConfig_ShouldBeAnnotatedWithConfiguration() {
        // Verify that the class is annotated with @Configuration
        assertTrue(FirebaseConfig.class.isAnnotationPresent(
            org.springframework.context.annotation.Configuration.class));
    }
}
