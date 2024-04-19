package com.example.ZAuth.TimeFunctions;

import com.example.ZAuth.Helper.EmailOTPCred;
import com.example.ZAuth.Helper.SMSCrediantials;
import com.example.ZAuth.VerificationCache.EmailOtpVerifySatus;
import com.example.ZAuth.VerificationCache.SMSVerificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
public class DeleteOTPLogs {

    @Autowired
    private SMSVerificationStatus smsVerificationStatus;

    @Scheduled(fixedDelay = 540000) // Run every 540 seconds (540000 milliseconds)
    public void cleanupExpiredEntries() {

        Map<String, SMSCrediantials> SMSmap = smsVerificationStatus.getSMSmap();
        long currentTimeMillis = System.currentTimeMillis();

        Iterator<Map.Entry<String, SMSCrediantials>> iterator = SMSmap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SMSCrediantials> entry = iterator.next();
            SMSCrediantials cred = entry.getValue();
            long entryTimestamp = Long.parseLong(cred.timeStamp); // Assuming timeStamp is stored as String of System.currentTimeMillis()

            // Check if the entry is older than 540 seconds (9 minutes)
            if (currentTimeMillis - entryTimestamp > 540000) {
                iterator.remove(); // Remove the entry from the map
                System.out.println("the entry removed "+ entry.toString()+" "+cred.generatedOtp);
            }
        }
    }


    @Scheduled(fixedDelay = 540000) // Run every 120 seconds (120000 milliseconds)
    public void cleanupExpiredEntriesEmail() {

        Map<String, EmailOTPCred> SMSmap = EmailOtpVerifySatus.getEmailmap();
        long currentTimeMillis = System.currentTimeMillis();

        Iterator<Map.Entry<String, EmailOTPCred>> iterator = SMSmap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, EmailOTPCred> entry = iterator.next();
            EmailOTPCred cred = entry.getValue();
            long entryTimestamp = Long.parseLong(cred.timeStamp); // Assuming timeStamp is stored as String of System.currentTimeMillis()

            // Check if the entry is older than 120 seconds (2 minutes)
            if (currentTimeMillis - entryTimestamp > 540000) {
                iterator.remove(); // Remove the entry from the map
                System.out.println("the entry removed "+ entry.toString());
            }
        }
    }

}
