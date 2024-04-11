package com.example.ZAuth.FirebaseClasses;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FirebaseConfig {

    private FirebaseApp firebaseApp;
    private Firestore firestore;

    @PostConstruct
    public void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            // Initialize Firebase Realtime Database
            FileInputStream serviceAccount = new FileInputStream("./google-services.json");
            FirebaseOptions databaseOptions = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://zauth-2457b-default-rtdb.firebaseio.com")
                    .build();
            firebaseApp = FirebaseApp.initializeApp(databaseOptions);

            // Initialize Firestore
            GoogleCredentials firestoreCredentials = GoogleCredentials.fromStream(new FileInputStream("./google-services.json"));
            FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                    .setCredentials(firestoreCredentials)
                    .build();
            firestore = firestoreOptions.getService();
        } else {
            firebaseApp = FirebaseApp.getInstance();
            // Retrieve Firestore instance from existing FirebaseApp
            firestore = FirestoreOptions.getDefaultInstance().getService();
        }
    }

    public FirebaseApp getFirebaseApp() {
        return firebaseApp;
    }

    public Firestore getFirestore() {
        return firestore;
    }
}
