package com.example.ZAuth.FirebaseClasses;

import com.example.ZAuth.Cache.AuthTokenCache;
import com.example.ZAuth.DatabaseHelper.TokenVerifyData;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
public class MyFirebaseOne {
    @Autowired
    FirebaseConfig firebaseConfig;

    @Autowired
    AuthTokenCache authTokenCache;


    public void updateLastLogin(String clientId,String userId){
        Firestore firestore = firebaseConfig.getFirestore();
        String specificFieldsPath = clientId + "/" + userId;
        DocumentReference docRef = firestore.document(specificFieldsPath);
        docRef.update("LastLogin",System.currentTimeMillis());
    }

    public String validateToken(TokenVerifyData data) {
        try {

            Firestore firestore = firebaseConfig.getFirestore();
            String specificFieldsPath = data.getClientId() + "/" + data.getUserId();

            // Create a FieldMask to specify the desired fields
            FieldMask fieldMask = FieldMask.of("AuthToken", "SessionTime");

            ApiFuture<DocumentSnapshot> document=firestore.document(specificFieldsPath).get(fieldMask);

            DocumentSnapshot snapshot= document.get();

            if (!snapshot.exists()) {
                return "INVALID";
            }

            String rowToken= Objects.requireNonNull(snapshot.get("AuthToken")).toString();
            String sessionTime= Objects.requireNonNull(snapshot.get("SessionTime")).toString();


            if(rowToken==null) return "UNKNOWN";

            String dataArray[]=rowToken.split(" ");
            String encryptedToken=dataArray[0];
            String timeStamp=dataArray[1].trim();

            if(!BCrypt.checkpw(data.getAuthToken(),encryptedToken)) return "UNKNOWN";
            if (timeStamp.equals("null")) {
                authTokenCache.addNewTokenCache(data.getClientId(),
                        data.getUserId(),
                        rowToken,
                        sessionTime);
                return "OK";
            }

            long minutesTillNow=getMinutesTillNow(timeStamp);
            if(minutesTillNow>Long.valueOf(sessionTime)) return "EXPIRED";

            DocumentReference docRef = firestore.document(specificFieldsPath);
            docRef.update("LastLogin",System.currentTimeMillis());

            authTokenCache.addNewTokenCache(data.getClientId(),
                                            data.getUserId(),
                                             rowToken,
                                             sessionTime);

            } catch (Exception e) {
            System.out.println(e.toString());
                return "ERROR";
            }
            return "OK";
        }

    public String validateTokenWeb(TokenVerifyData data) {
        try {

            Firestore firestore = firebaseConfig.getFirestore();
            String specificFieldsPath = data.getClientId() + "/" + data.getUserId();

            // Create a FieldMask to specify the desired fields
            FieldMask fieldMask = FieldMask.of("AuthTokenWeb", "SessionTime");

            ApiFuture<DocumentSnapshot> document=firestore.document(specificFieldsPath).get(fieldMask);

            DocumentSnapshot snapshot= document.get();

            if(snapshot==null) return "INVALID";

            String rowToken= snapshot.get("AuthTokenWeb").toString();
            String sessionTime= snapshot.get("SessionTime").toString();


            if(rowToken==null) {
                return "UNKNOWN";
            }

            String dataArray[]=rowToken.split(" ");
            String encryptedToken=dataArray[0];
            String timeStamp=dataArray[1].trim();

            if(!BCrypt.checkpw(data.getAuthToken(),encryptedToken)) return "UNKNOWN";
            if (timeStamp.equals("null")) {
                authTokenCache.addNewTokenCache(data.getClientId(),
                        data.getUserId(),
                        rowToken,
                        sessionTime);
                return "OK";
            }

            long minutesTillNow=getMinutesTillNow(timeStamp);
            if(minutesTillNow>Long.valueOf(sessionTime)) return "EXPIRED";

            DocumentReference docRef = firestore.document(specificFieldsPath);
            docRef.update("LastLogin",System.currentTimeMillis());

            authTokenCache.addNewTokenCache(data.getClientId(),
                    data.getUserId(),
                    rowToken,
                    sessionTime);

        } catch (Exception e) {
            System.out.println(e.toString());
            return "ERROR";
        }



        return "OK";
    }


        public long getMinutesTillNow(String timeStamp){
            // Convert the provided timestamp string to milliseconds
            long timestampMillis = Long.parseLong(timeStamp);

            // Get the current time in milliseconds
            long currentTimeMillis = System.currentTimeMillis();

            // Calculate the difference in milliseconds
            long differenceMillis = currentTimeMillis - timestampMillis;

            // Convert milliseconds difference to minutes
            long minutesDifference = Duration.ofMillis(differenceMillis).toMinutes();

            // Convert to int (if needed) and return the result
            return minutesDifference;
        }
    }

