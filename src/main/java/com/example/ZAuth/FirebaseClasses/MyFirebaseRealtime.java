package com.example.ZAuth.FirebaseClasses;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class MyFirebaseRealtime {

    @Autowired
    FirebaseConfig firebaseConfig;

    public boolean checkClientDetails(String clientId,String clientApikey,String clientApiPass){
        try {
            FirebaseApp firebaseApp = firebaseConfig.getFirebaseApp(); // Get FirebaseApp instance

            // Get reference to the Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);

            // Reference to the client node in the database
            DatabaseReference clientRef = database.getReference("Clients").child(clientId);

            // Use CompletableFuture to perform asynchronous validation synchronously
            CompletableFuture<Boolean> validationFuture = new CompletableFuture<>();

            clientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Client node exists, now retrieve API key and API password
                        String apiKey = dataSnapshot.child("ApiKey").getValue(String.class);
                        String apiPass = dataSnapshot.child("ApiPass").getValue(String.class);

                        // Validate API key and API password
                        if (apiKey != null && apiPass != null && apiKey.equals(clientApikey) && apiPass.equals(clientApiPass)) {
                            // Client details are valid
                            validationFuture.complete(true);
                        } else {
                            // Invalid client details
                            validationFuture.complete(false);
                        }
                    } else {
                        // Client not found
                        validationFuture.complete(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle onCancelled event (optional)
                    validationFuture.completeExceptionally(databaseError.toException());
                }
            });

            // Wait for the validation result synchronously
            return validationFuture.get();

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false in case of any error
        }
    }
}
