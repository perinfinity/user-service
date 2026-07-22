package com.perinfinity.auth_api.service;

import com.perinfinity.auth_api.exceptions.EmailSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendVerificationCode(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
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

    /** Notifie le destinataire d'un nouveau message sur une candidature. */
    public void sendNewMessageNotification(String to, String senderName, String opportunityTitle,
                                           String preview, String threadUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("Nouveau message — " + opportunityTitle);
            message.setText(senderName + " vous a envoyé un message concernant votre candidature \""
                    + opportunityTitle + "\" :\n\n« " + preview + " »\n\n"
                    + "Répondez sur la plateforme : " + threadUrl);

            mailSender.send(message);
            log.info("New-message notification sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send new-message notification to: {}", to, e);
            throw new EmailSendException(to, e);
        }
    }
}
