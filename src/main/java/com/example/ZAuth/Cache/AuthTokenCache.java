package com.example.ZAuth.Cache;

import com.example.ZAuth.DatabaseHelper.TokenVerifyData;

import com.example.ZAuth.Helper.UserTokenSessionData;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;

@Component
public class AuthTokenCache {

    private static  HashMap<String,HashMap<String, UserTokenSessionData>> authMap=new HashMap<>();


    public  void addNewTokenCache(String clientId,
                                        String userId,
                                        String authToken,
                                        String sessionTime){
        UserTokenSessionData data=new UserTokenSessionData();
        data.setAuthToken(authToken);
        data.setSessionTime(sessionTime);

        System.out.println("Add new token hit for "+userId);

        if(authMap.get(clientId)==null) {
            authMap.put(clientId,new HashMap<>());
        }
        authMap.get(clientId).put(userId,data);
    }

    public  String validateToken(TokenVerifyData data){
        if(authMap.get(data.getClientId())==null) {
            System.out.println("validate token hit - not found");
            return "NOTFOUND";
        }
        UserTokenSessionData tokenData=authMap.get(data.getClientId()).get(data.getUserId());

        if (tokenData==null) {
            System.out.println("Validate token hit not found");
            return "NOTFOUND";
        }

        String rowToken=tokenData.getAuthToken();
        String sessionTime=tokenData.getSessionTime();


        String dataArray[]=rowToken.split(" ");
        String encryptedToken=dataArray[0];
        String timeStamp=dataArray[1].trim();

        if(!BCrypt.checkpw(data.getAuthToken(),encryptedToken)) {
            System.out.println("Validate token hit wrong token");
            return "NO";
        }
        if (timeStamp.equals("null")){
            System.out.println("Validate token hit for "+data.getUserId());
            return "OK";
        }

        long minutesTillNow=getMinutesTillNow(timeStamp);
        if(minutesTillNow>Long.valueOf(sessionTime)) return "NO";

        System.out.println("Validate token hit for "+data.getUserId());
        return "OK";
    }

    public  long getMinutesTillNow(String timeStamp){
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
