package com.example.ZAuth.FirebaseClasses;

import com.example.ZAuth.DataModelsForClientCred.GoogleCred;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldMask;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MyFirebaseFetchClientDetails {

    @Autowired
    FirebaseConfig firebaseConfig;


    public GoogleCred getClientsGoogleCred(String clientId){
        try{
            Firestore firestore = firebaseConfig.getFirestore();
            String specificFieldsPath = clientId+ "/" +clientId;

            // Create a FieldMask to specify the desired fields
            FieldMask fieldMask = FieldMask.of("GoogleId", "GoogleIdSecrete");

            ApiFuture<DocumentSnapshot> document=firestore.document(specificFieldsPath).get(fieldMask);

            DocumentSnapshot snapshot= document.get();

            if(snapshot==null) return null;

            String googleId= Objects.requireNonNull(snapshot.get("GoogleId")).toString();
            String googleKey= Objects.requireNonNull(snapshot.get("GoogleIdSecrete")).toString();

            if(googleId==null||googleKey==null) return null;

            GoogleCred cred=new GoogleCred();
            cred.setGoogleId(googleId);
            cred.setGoogleIdSecrete(googleKey);


            return cred;
        }catch(Exception e){
            return null;
        }
    }



}
