package com.devmode.superdev.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;

public class SmsSender {
    static Dotenv dotenv = Dotenv.load();
    // Twilio credentials (get them from your Twilio dashboard)
    public static final String ACCOUNT_SID = dotenv.get("ACCOUNT_SID");
    public static final String AUTH_TOKEN = dotenv.get("AUTH_TOKEN");

    public static void sendSms(String to, String body) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String send_to;
        if (to.startsWith("+961")) {
            send_to = to;
        } else {
            send_to = "+961" + to;
        }

        Message message = Message.creator(
                new PhoneNumber(send_to),     // To phone number
                new PhoneNumber("+12076871493"),   // From Twilio number
                body                     // SMS body
        ).create();
        System.out.println("Message sent with SID: " + message.getSid());
    }
}




