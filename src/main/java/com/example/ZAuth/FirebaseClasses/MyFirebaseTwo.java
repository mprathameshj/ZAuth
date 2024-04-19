package com.example.ZAuth.FirebaseClasses;

import com.example.ZAuth.DataEncryptor.BcryptEncrypt;
import com.example.ZAuth.DatabaseHelper.AddUserWithEmailOtp;
import com.example.ZAuth.DatabaseHelper.AddUserWithEmailPassData;
import com.example.ZAuth.DatabaseHelper.AddUserWithGoogleData;
import com.example.ZAuth.DatabaseHelper.AddUserWithMobNumData;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class MyFirebaseTwo {

    @Autowired
    FirebaseConfig firebaseConfig;


    public String findUserByEmailAndUpdateAuthToken(String email, String clientId, String authToken, String platform) {
        try {
            // Get Firestore instance from FirebaseConfig
            Firestore firestore = firebaseConfig.getFirestore();

            // Reference to the Firestore collection (assuming 'clientId' is the collection name)
            CollectionReference collectionRef = firestore.collection(clientId);

            // Create a query to find documents where 'mobNum' field matches the provided mobile number
            Query query = collectionRef.whereEqualTo("Email", email);

            // Asynchronously query Firestore and fetch only the document IDs
            ApiFuture<QuerySnapshot> querySnapshotFuture = query.select(String.valueOf(FieldPath.documentId()), "Blocked", "SessionTime").get();

            // Get the result of the query (blocking operation)
            QuerySnapshot querySnapshot = querySnapshotFuture.get();

            // Check if any documents match the query
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Document ID with matching mobile number found
                String docId = document.getId();
                String sessionTime = (String) document.get("SessionTime");
                if (sessionTime == null) sessionTime = "null";
                boolean isBlocked = Boolean.TRUE.equals(document.getBoolean("Blocked"));

                if (isBlocked) return "BLOCKED";

                // Asynchronously update the 'AuthToken' field of the document with the provided token
                if (platform.equals("null")) {
                    if (sessionTime.equals("null")) {
                        ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                                .update("AuthToken", authToken + " " + "null"); // Update only the 'AuthToken' field
                    } else {
                        int sessionTimeMinutes = Integer.parseInt(sessionTime);
                        String AuthValidTimeStamp = generateFutureTimestamp(sessionTimeMinutes);
                        ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                                .update("AuthToken", authToken + " " + AuthValidTimeStamp);
                    }
                } else {
                    if (sessionTime.equals("null")) {
                        ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                                .update("AuthTokenWeb", authToken + " " + "null"); // Update only the 'AuthToken' field
                    } else {
                        int sessionTimeMinutes = Integer.parseInt(sessionTime);
                        String AuthValidTimeStamp = generateFutureTimestamp(sessionTimeMinutes);
                        ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                                .update("AuthTokenWeb", authToken + " " + AuthValidTimeStamp);
                    }
                }
                // Return true to indicate a user with the specified mobile number was found and updated
                return docId;
            }

            // No document found with the specified mobile number
            return "NEWUSER";

        } catch (Exception e) {
            // Handle any potential exceptions (e.g., Firestore initialization, query execution)
            e.printStackTrace();
            return "ERROR"+ e; // Return false in case of error
        }
    }


    public String createUserWithEmailCredintials(AddUserWithGoogleData data, String encryptedToken) {
        String userId = String.valueOf(UUID.randomUUID());
        String clientId = data.getClientId();
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("Email", data.getEmail());
        userInfo.put("AuthMethod", data.getAuthMethod());

        if (data.getSessionTime().equals("null")) {
            if (!data.getPlatform().equals("WEB"))
                userInfo.put("AuthToken", encryptedToken + " " + "null");
            else
                userInfo.put("AuthTokenWeb", encryptedToken + " " + "null");
        } else {
            int sessionTimeMinutes = Integer.parseInt(data.getSessionTime());
            String AuthValidTimeStamp = generateFutureTimestamp(sessionTimeMinutes);

            if (!data.getPlatform().equals("WEB"))
                userInfo.put("AuthToken", encryptedToken + " " + AuthValidTimeStamp);
            else
                userInfo.put("AuthTokenWeb", encryptedToken + " " + AuthValidTimeStamp);
        }

        if (!data.getAvailableSessions().equals("null"))
            userInfo.put("AvailableSessions", Integer.valueOf(data.getAvailableSessions()));
        if (!data.getRole().equals("null"))
            userInfo.put("Role", data.getRole());
        userInfo.put("CurrLogin", 1);

        if (data.getSessionTime().equals("null"))
            userInfo.put("SessionTime", "null");
        else
            userInfo.put("SessionTime", data.getSessionTime());

        String deviceInfo = data.getIpAdd() + " " + data.getDeviceInfo() + " " + data.getTimeStamp();//Add in LoginHistory Array
        // Add device info to LoginHistory array
        List<String> loginHistory = new ArrayList<>();
        loginHistory.add(deviceInfo);
        userInfo.put("LoginHistory", loginHistory);

        userInfo.put("Blocked", false);
        userInfo.put("LastLogin", System.currentTimeMillis());


        Firestore firestore = firebaseConfig.getFirestore();


        // Define the path to the document in Firestore (e.g., "users/clientId/newDocumentId")
        String documentPath = clientId + "/" + userId;

        // Create or overwrite the document with the specified ID
        DocumentReference docRef = firestore.document(documentPath);

        // Set the document data with custom options to merge with existing data
        docRef.set(userInfo, SetOptions.merge());

        return userId;
    }

    public String createUserWithEmailCredintials(AddUserWithEmailOtp data, String encryptedToken) {
        String userId = String.valueOf(UUID.randomUUID());
        String clientId = data.getClientId();
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("Email", data.getEmail());
        userInfo.put("AuthMethod", data.getAuthMethod());

        if (data.getSessionTime().equals("null")) {
            if (!data.getPlatform().equals("WEB"))
                userInfo.put("AuthToken", encryptedToken + " " + "null");
            else
                userInfo.put("AuthTokenWeb", encryptedToken + " " + "null");
        } else {
            int sessionTimeMinutes = Integer.parseInt(data.getSessionTime());
            String AuthValidTimeStamp = generateFutureTimestamp(sessionTimeMinutes);

            if (!data.getPlatform().equals("WEB"))
                userInfo.put("AuthToken", encryptedToken + " " + AuthValidTimeStamp);
            else
                userInfo.put("AuthTokenWeb", encryptedToken + " " + AuthValidTimeStamp);
        }

        if (!data.getAvailableSessions().equals("null"))
            userInfo.put("AvailableSessions", Integer.valueOf(data.getAvailableSessions()));
        if (!data.getRole().equals("null"))
            userInfo.put("Role", data.getRole());
        userInfo.put("CurrLogin", 1);

        if (data.getSessionTime().equals("null"))
            userInfo.put("SessionTime", "null");
        else
            userInfo.put("SessionTime", data.getSessionTime());

        String deviceInfo = data.getIpAdd() + " " + data.getDeviceInfo() + " " + data.getTimeStamp();//Add in LoginHistory Array
        // Add device info to LoginHistory array
        List<String> loginHistory = new ArrayList<>();
        loginHistory.add(deviceInfo);
        userInfo.put("LoginHistory", loginHistory);

        userInfo.put("Blocked", false);
        userInfo.put("LastLogin", System.currentTimeMillis());


        Firestore firestore = firebaseConfig.getFirestore();


        // Define the path to the document in Firestore (e.g., "users/clientId/newDocumentId")
        String documentPath = clientId + "/" + userId;

        // Create or overwrite the document with the specified ID
        DocumentReference docRef = firestore.document(documentPath);

        // Set the document data with custom options to merge with existing data
        docRef.set(userInfo, SetOptions.merge());

        return userId;
    }



    public String findUserByEmailPassAndUpdateAuthToken(String email, String clientId, String authToken, String platform, String password) {
        try {
            // Get Firestore instance from FirebaseConfig
            Firestore firestore = firebaseConfig.getFirestore();

            // Reference to the Firestore collection (assuming 'clientId' is the collection name)
            CollectionReference collectionRef = firestore.collection(clientId);

            // Create a query to find documents where 'mobNum' field matches the provided mobile number
            Query query = collectionRef.whereEqualTo("Email", email);

            // Asynchronously query Firestore and fetch only the document IDs
            ApiFuture<QuerySnapshot> querySnapshotFuture = query.select(String.valueOf(FieldPath.documentId()), "Blocked", "SessionTime", "Password").get();

            // Get the result of the query (blocking operation)
            QuerySnapshot querySnapshot = querySnapshotFuture.get();

            // Check if any documents match the query
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Document ID with matching mobile number found
                String docId = document.getId();
                String sessionTime = (String) document.get("SessionTime");
                if (sessionTime == null) sessionTime = "null";
                boolean isBlocked = Boolean.TRUE.equals(document.getBoolean("Blocked"));
                String encryptedPass = String.valueOf(document.get("Password"));

                if (isBlocked) return "BLOCKED";

                if (!BCrypt.checkpw(password, encryptedPass)) return "WRONG";

                // Asynchronously update the 'AuthToken' field of the document with the provided token
                if (platform.equals("null")) {
                    if (sessionTime.equals("null")) {
                        ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                                .update("AuthToken", authToken + " " + "null"); // Update only the 'AuthToken' field
                    } else {
                        int sessionTimeMinutes = Integer.parseInt(sessionTime);
                        String AuthValidTimeStamp = generateFutureTimestamp(sessionTimeMinutes);
                        ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                                .update("AuthToken", authToken + " " + AuthValidTimeStamp);
                    }
                } else {
                    if (sessionTime.equals("null")) {
                        ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                                .update("AuthTokenWeb", authToken + " " + "null"); // Update only the 'AuthToken' field
                    } else {
                        int sessionTimeMinutes = Integer.parseInt(sessionTime);
                        String AuthValidTimeStamp = generateFutureTimestamp(sessionTimeMinutes);
                        ApiFuture<WriteResult> updateFuture = collectionRef.document(docId)
                                .update("AuthTokenWeb", authToken + " " + AuthValidTimeStamp);
                    }
                }
                // Return true to indicate a user with the specified mobile number was found and updated
                return docId;
            }

            // No document found with the specified mobile number
            return "NEWUSER";

        } catch (Exception e) {
            // Handle any potential exceptions (e.g., Firestore initialization, query execution)
            e.printStackTrace();
            return "ERROR" + e.toString(); // Return false in case of error
        }

    }

    public String createUserWithEmailPassCredintials(AddUserWithEmailPassData data, String encryptedToken) {
        String userId = String.valueOf(UUID.randomUUID());
        String clientId = data.getClientId();
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("Email", data.getEmail());
        userInfo.put("Password", BcryptEncrypt.encrypt(data.getPassword()));
        userInfo.put("AuthMethod", data.getAuthMethod());

        if (data.getSessionTime().equals("null")) {
            if (!data.getPlatform().equals("WEB"))
                userInfo.put("AuthToken", encryptedToken + " " + "null");
            else
                userInfo.put("AuthTokenWeb", encryptedToken + " " + "null");
        } else {
            int sessionTimeMinutes = Integer.parseInt(data.getSessionTime());
            String AuthValidTimeStamp = generateFutureTimestamp(sessionTimeMinutes);

            if (!data.getPlatform().equals("WEB"))
                userInfo.put("AuthToken", encryptedToken + " " + AuthValidTimeStamp);
            else
                userInfo.put("AuthTokenWeb", encryptedToken + " " + AuthValidTimeStamp);
        }

        if (!data.getAvailableSessions().equals("null"))
            userInfo.put("AvailableSessions", Integer.valueOf(data.getAvailableSessions()));
        if (!data.getRole().equals("null"))
            userInfo.put("Role", data.getRole());
        userInfo.put("CurrLogin", 1);

        if (data.getSessionTime().equals("null"))
            userInfo.put("SessionTime", "null");
        else
            userInfo.put("SessionTime", data.getSessionTime());

        String deviceInfo = data.getIpAdd() + " " + data.getDeviceInfo() + " " + data.getTimeStamp();//Add in LoginHistory Array
        // Add device info to LoginHistory array
        List<String> loginHistory = new ArrayList<>();
        loginHistory.add(deviceInfo);
        userInfo.put("LoginHistory", loginHistory);

        userInfo.put("Blocked", false);
        userInfo.put("LastLogin", System.currentTimeMillis());


        Firestore firestore = firebaseConfig.getFirestore();


        // Define the path to the document in Firestore (e.g., "users/clientId/newDocumentId")
        String documentPath = clientId + "/" + userId;

        // Create or overwrite the document with the specified ID
        DocumentReference docRef = firestore.document(documentPath);

        // Set the document data with custom options to merge with existing data
        docRef.set(userInfo, SetOptions.merge());

        return userId;
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

