package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.exceptions.EmailSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Code de vérification - PerInfinity");
            message.setText("Votre code de vérification est : " + code + "\n\n" +
                    "Ce code expire dans 10 minutes.\n" +
                    "Si vous n'avez pas demandé ce code, ignorez cet email.");

            mailSender.send(message);
            log.info("Verification code sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification code to: {}", to, e);
            throw new EmailSendException(to, e);
        }
    }
}
