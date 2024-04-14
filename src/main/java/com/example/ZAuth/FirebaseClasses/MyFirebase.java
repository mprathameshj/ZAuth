package com.example.ZAuth.FirebaseClasses;

import com.example.ZAuth.DatabaseHelper.AddUserWithMobNumData;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.api.core.ApiFuture;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Component
public class MyFirebase {

    @Autowired
    FirebaseConfig firebaseConfig;

    public boolean findUserByMobileNumber(String mobNum, String clientId) {
        try {
            // Get Firestore instance from FirebaseConfig
            Firestore firestore = firebaseConfig.getFirestore();

            // Reference to the Firestore collection (assuming 'clientId' is the collection name)
            CollectionReference collectionRef = firestore.collection(clientId);

            // Create a query to find documents where 'mobNum' field matches the provided mobile number
            Query query = collectionRef.whereEqualTo("MobNumber", mobNum);

            // Asynchronously query Firestore
            ApiFuture<QuerySnapshot> querySnapshotFuture = query.get();

            // Get the result of the query (blocking operation)
            QuerySnapshot querySnapshot = querySnapshotFuture.get();

            // Check if any documents match the query
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Document with matching mobile number found
                return true;
            }

            // No document found with the specified mobile number
            return false;

        } catch (Exception e) {
            // Handle any potential exceptions (e.g., Firestore initialization, query execution)
            e.printStackTrace();
            return false; // Return false in case of error
        }
    }

    public boolean findUserByMobileAndUpdateAuthToken(String mobNum, String clientId,String authToken){
        try {
            // Get Firestore instance from FirebaseConfig
            Firestore firestore = firebaseConfig.getFirestore();

            // Reference to the Firestore collection (assuming 'clientId' is the collection name)
            CollectionReference collectionRef = firestore.collection(clientId);

            // Create a query to find documents where 'mobNum' field matches the provided mobile number
            Query query = collectionRef.whereEqualTo("MobNumber", mobNum);

            // Asynchronously query Firestore and fetch only the document IDs
            ApiFuture<QuerySnapshot> querySnapshotFuture = query.select(FieldPath.documentId()).get();

            // Get the result of the query (blocking operation)
            QuerySnapshot querySnapshot = querySnapshotFuture.get();

            // Check if any documents match the query
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Document ID with matching mobile number found
                String docId = document.getId();

                // Asynchronously update the 'AuthToken' field of the document with the provided token
                ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                        .update("AuthToken", authToken); // Update only the 'AuthToken' field

                // Wait for the update operation to complete (blocking operation)
                updateFuture.get();

                // Return true to indicate a user with the specified mobile number was found and updated
                return true;
            }

            // No document found with the specified mobile number
            return false;

        } catch (Exception e) {
            // Handle any potential exceptions (e.g., Firestore initialization, query execution)
            e.printStackTrace();
            return false; // Return false in case of error
        }
    }


    public void createUserWithMobCredintials(AddUserWithMobNumData data,String encryptedToken){
        String userId= String.valueOf(UUID.randomUUID());
        String clientId= data.getClientId();
        Map<String,Object> userInfo=new HashMap<>();

        userInfo.put("AuthMethod",data.getAuthMethod());
        userInfo.put("MobNumber",data.getMobNumber());

        if(data.getSessionTime().equals("null")){
            userInfo.put("AuthToken",encryptedToken+" "+"null");
        }else{
            int sessionTimeMinutes= Integer.parseInt(data.getSessionTime());
            String AuthValidTimeStamp= generateFutureTimestamp(sessionTimeMinutes);
            userInfo.put("AuthToken",encryptedToken+" "+AuthValidTimeStamp);
        }

        if(!data.getAvailableSessions().equals("null"))
            userInfo.put("AvailableSessions",Integer.valueOf(data.getAvailableSessions()));
        if(!data.getRole().equals("null"))
            userInfo.put("Role",data.getRole());
        userInfo.put("CurrLogin",1);

        if(data.getSessionTime().equals("null"))
            userInfo.put("SessionTime","null");
        else
            userInfo.put("SessionTime",data.getSessionTime());

        String deviceInfo=data.getIpAdd()+" "+data.getDeviceInfo()+" "+data.getTimeStamp();//Add in LoginHistory Array
        // Add device info to LoginHistory array
        List<String> loginHistory = new ArrayList<>();
        loginHistory.add(deviceInfo);
        userInfo.put("LoginHistory", loginHistory);

        userInfo.put("Blocked",false);

        Firestore firestore=firebaseConfig.getFirestore();


        // Define the path to the document in Firestore (e.g., "users/clientId/newDocumentId")
        String documentPath =clientId + "/" + userId;

        // Create or overwrite the document with the specified ID
        DocumentReference docRef = firestore.document(documentPath);

        // Set the document data with custom options to merge with existing data
        docRef.set(userInfo, SetOptions.merge());

    }


    //Generate the token validation timestamp
    public static String generateFutureTimestamp(int minutes) {
        // Get current timestamp in milliseconds
        String currTimeStamp = String.valueOf(System.currentTimeMillis());

        // Convert current timestamp to Instant
        Instant currentInstant = Instant.ofEpochMilli(Long.parseLong(currTimeStamp));

        // Generate timestamp ahead of current time by the specified minutes
        Instant futureInstant = currentInstant.plus(minutes, ChronoUnit.MINUTES);

        // Convert futureInstant back to milliseconds timestamp
        long futureTimestampMillis = futureInstant.toEpochMilli();

        // Convert future timestamp to String and return
        return String.valueOf(futureTimestampMillis);
    }
}
