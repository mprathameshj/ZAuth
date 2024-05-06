package com.example.ZAuth;
import com.example.ZAuth.Cache.ClientIdCache;
import com.example.ZAuth.DatabaseHelper.AddUserWithMobNumData;
import com.example.ZAuth.FirebaseClasses.FirebaseConfig;
import com.example.ZAuth.FirebaseClasses.MyFirebase;
import com.example.ZAuth.FirebaseClasses.MyFirebaseRealtime;
import com.example.ZAuth.SMSServices.EmailSender;
import com.example.ZAuth.VerificationCache.SMSVerificationStatus;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class Controller {

    @Autowired
    private FirebaseConfig firebaseConfig;

    @Autowired
    EmailSender emailSender;

    @Autowired
    SMSVerificationStatus smsVerificationStatus;

    @GetMapping("/test")
    public  String poke() throws IOException {
        final String[] message = {"Error"};
        FirebaseApp firebaseApp=firebaseConfig.getFirebaseApp();
        FirebaseDatabase database=FirebaseDatabase.getInstance(firebaseApp);

        DatabaseReference ref= database.getReference("Test");
        ref.setValue("DOne", new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError==null){
                    message[0] ="Success";
                }else{
                    message[0]= "fail";
                }
            }
        });

        return message[0];
    }

    @GetMapping("/testFirestore")
    public String testFirebase() throws ExecutionException, InterruptedException {
        Firestore firestore = firebaseConfig.getFirestore();

        // Get a reference to the Firestore collection
        CollectionReference collection = firestore.collection("users");

        // Create a new document with a custom ID
        String documentId = "user1"; // Replace with your desired document ID
        DocumentReference documentRef = collection.document(documentId);

        // Create a Map object representing the document data
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", 30);
        data.put("email", "john@example.com");

        // Add the document to the Firestore collection
        ApiFuture<WriteResult> result = documentRef.set(data);

        // Wait for the operation to complete (optional)
        result.get(); // This blocks until the operation is complete


        return "Firebase services (Realtime Database and Firestore) initialized successfully!";
    }


    @PostMapping("/testMethod")
    public String test(){
        return  "This is received from server";
    }

}
