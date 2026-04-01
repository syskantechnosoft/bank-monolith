package com.bank.monolith.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Async
    public void sendEmail(String to, String subject, String body) {
        // In a real application, this would use JavaMailSender to send real emails.
        log.info("--- START EMAIL ---");
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Body:\n{}", body);
        log.info("--- END EMAIL ---");
    }

    public void sendWelcomeEmail(String to, String name) {
        String subject = "Welcome to Bank Monolith!";
        String body = String.format(
                "Hello %s,\n\nWelcome to our online banking application. We are thrilled to have you with us.", name);
        sendEmail(to, subject, body);
    }

    public void sendTransactionAlert(String to, String transactionType, String amount, String account) {
        String subject = "Transaction Alert: " + transactionType;
        String body = String.format("A new transaction of type %s for amount %s has occurred on your account %s.",
                transactionType, amount, account);
        sendEmail(to, subject, body);
    }
}
