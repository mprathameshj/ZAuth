package com.example.ZAuth.SMSServices;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioConfig {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.number}")
    private String trialNumber;

    public boolean sendSms(String to, String body) {
        Twilio.init(accountSid, authToken);

        try {
            Message message = Message.creator(
                            new PhoneNumber(to),        // to
                            new PhoneNumber(trialNumber), // from
                            body)
                    .create();

            // If message was successfully created and sent
            if (message.getStatus() != null && message.getStatus().equals(Message.Status.SENT)) {
                return true; // SMS sent successfully
            } else {
                return false; // SMS failed to send
            }
        } catch (ApiException e) {
            // Handle API exceptions (e.g., invalid credentials, Twilio service errors)
            System.out.println("Twilio API exception: " + e.getMessage());
            return false; // SMS sending failed due to an exception
        }
    }
}
