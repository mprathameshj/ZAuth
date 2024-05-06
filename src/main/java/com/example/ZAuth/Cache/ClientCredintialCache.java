package com.example.ZAuth.Cache;

import com.example.ZAuth.DataModelsForClientCred.GoogleCred;
import com.example.ZAuth.FirebaseClasses.MyFirebaseFetchClientDetails;
import com.example.ZAuth.Helper.ClientCredintialsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ClientCredintialCache {

    @Autowired
    MyFirebaseFetchClientDetails myFirebaseFetchClientDetails;

    private static HashMap<String, GoogleCred> goolgeMap=new HashMap<>();


    public GoogleCred getGoogleAuthCredintials(String clientId){
        GoogleCred cred= goolgeMap.get(clientId);

        if(cred==null) return fetchCredHelper(clientId);

       return cred;
    }



    public GoogleCred fetchCredHelper(String clientId){
        GoogleCred cred = myFirebaseFetchClientDetails.getClientsGoogleCred(clientId);
        goolgeMap.put(clientId,cred);
        return  cred;
    }
}
