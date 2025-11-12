package com.cs203.grp2.Asg2.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseDatabase firebaseDatabase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            //added test
            // ✅ Works both locally and in Docker (reads from classpath)
            InputStream serviceAccount =
                    getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");

            if (serviceAccount == null) {
                throw new IOException(" serviceAccountKey.json not found in classpath!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://cs203asg-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .build();

            FirebaseApp.initializeApp(options);
        }

        return FirebaseDatabase.getInstance();
    }
}


// package com.cs203.grp2.Asg2.config;

// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.FirebaseOptions;
// import com.google.firebase.database.FirebaseDatabase;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import java.io.IOException;
// import java.io.InputStream;
// import java.io.FileInputStream;


// @Configuration
// public class FirebaseConfig {

//     @Bean
//     public FirebaseDatabase firebaseDatabase() throws IOException {
//         if (FirebaseApp.getApps().isEmpty()) {

//             // ✅ Works both locally and in Docker (reads from classpath)
//             InputStream serviceAccount = new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));

//             if (serviceAccount == null) {
//                 throw new IOException(" serviceAccountKey.json not found in classpath!");
//             }

//             FirebaseOptions options = FirebaseOptions.builder()
//                     .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                     .setDatabaseUrl("https://cs203asg-default-rtdb.asia-southeast1.firebasedatabase.app")
//                     .build();

//             FirebaseApp.initializeApp(options);
//         }

//         return FirebaseDatabase.getInstance();
//     }
// }
