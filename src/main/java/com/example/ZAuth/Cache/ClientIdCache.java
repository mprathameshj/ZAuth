package com.example.ZAuth.Cache;

import com.example.ZAuth.FirebaseClasses.MyFirebaseRealtime;
import com.example.ZAuth.Helper.ClientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ClientIdCache {
    private HashMap<String, ClientData> clientIdmap=new HashMap<>();

    @Autowired
    MyFirebaseRealtime firebaseRealtime;

    public  boolean validateClient(String clientId,String clientApi,String clientApiKey){
        if(!clientIdmap.containsKey(clientId)){
            if(firebaseRealtime.checkClientDetails(clientId,clientApi,clientApiKey)){
                addClientCache(clientId,clientApi,clientApiKey);
                return true;
            }else{
                return false;
            }
        };

        return  clientIdmap.get(clientId).getApiKey().equals(clientApi)&&
                clientIdmap.get(clientId).getApiPassword().equals(clientApiKey);
    }

    public void addClientCache(String clientId,String clientApi,String clientApiKey){
        ClientData clientData=new ClientData();
        clientData.setApiKey(clientApi);
        clientData.setApiPassword(clientApiKey);

        clientIdmap.put(clientId,clientData);
    }

}
