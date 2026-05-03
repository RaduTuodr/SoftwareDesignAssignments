package com.andrei.demo.service;

import io.pingram.Pingram;
import io.pingram.model.SenderPostBody;
import io.pingram.model.SenderPostBodySms;
import io.pingram.model.SenderPostBodyTo;
import io.pingram.model.SenderPostResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Value("${PINGRAM_API_KEY}")
    private String apiKey;

    @Value("${PINGRAM_API_URL}")
    private String apiUrl;

    private Pingram pingram;

    @PostConstruct
    public void init() {
        pingram = new Pingram(apiKey, apiUrl);
    }

    public void sendMessage(String phoneNumber, String code) {
        SenderPostBodyTo to = new SenderPostBodyTo().number(phoneNumber);
        SenderPostBody body = new SenderPostBody()
                .type("sms_compose_preview")
                .to(to)
                .sms(new SenderPostBodySms()
                        .message("Your verification code is: " + code + "."));

        try {
            SenderPostResponse response = pingram.send(body);
            System.out.println("Message sent successfully: " + response);
            response.getMessages();
            if (!response.getMessages().isEmpty()) {
                System.out.println("Messages: " + response.getMessages());
            }
        } catch (Exception e) {
            System.err.println("API Error: " + e.getMessage());
        }
    }
}
