package com.meetingminutes.meeting_minutes_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender sender; // null when no SMTP server is configured
    private final String from;

    public MailService(ObjectProvider<JavaMailSender> senderProvider,
                       @Value("${app.mail.from:}") String appFrom,
                       @Value("${spring.mail.username:}") String smtpUser) {
        this.sender = senderProvider.getIfAvailable();
        this.from = !appFrom.isBlank() ? appFrom : smtpUser;
    }

    public void sendResetLink(String to, String fullName, String link,
                              String systemName, String orgName) {
        if (sender == null) {
            log.warn("DEV MODE: no SMTP server is configured, so no email was sent. "
                    + "Password reset link for {}: {}", to, link);
            return;
        }

        String body = """
                Hello %s,

                We received a request to reset the password of your administrator account on %s.
                Open the link below to choose a new password. The link expires in 30 minutes and can be used only once.

                %s

                If you did not make this request, ignore this email. Your password will not change.

                %s
                """.formatted(fullName, systemName, link, orgName);

        SimpleMailMessage message = new SimpleMailMessage();
        if (!from.isBlank()) {
            message.setFrom(from);
        }
        message.setTo(to);
        message.setSubject("Reset your password - " + systemName);
        message.setText(body);

        // Sent in the background so the request is not delayed by the mail server
        CompletableFuture.runAsync(() -> {
            try {
                sender.send(message);
            } catch (MailException e) {
                log.error("Could not send the password reset email to {}: {}", to, e.getMessage());
            }
        });
    }
}