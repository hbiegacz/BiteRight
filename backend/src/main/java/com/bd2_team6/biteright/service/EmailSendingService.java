package com.bd2_team6.biteright.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailSendingService {
    @Autowired
    private final JavaMailSender javaMailSender;

    @Autowired
    private final ResourceLoader resourceLoader;

    @Value("${spring.mail.username}")
    private String emailSender;

    public void sendVerificationEmail(String username, String email, String verificationCode) {
        String subject = "BiteRight - Email Verification";
        String path = "http://localhost:80/verifyuser/" + email + "/" + verificationCode; 
        String body = "Hello, " + username + "!\nThank you for registering in BiteRight!\nPlease click the link below to verify your email address.";

        sendEmail(email, verificationCode, subject, path, body);
    }

    public void sendForgotPasswordEmail(String username, String email, String forgottenPasswordCode){
        String subject = "BiteRight - Forgotten password";
        String path = "http://localhost:80/passwordreset/" + email + "/" + forgottenPasswordCode; 
        String body = "Hello, " + username + 
                    "!\nIf you requested to reset your password, please click the link below to complete the process.\n"+
                    "If it wasn't you then ignore this email.";
        sendEmail(email, forgottenPasswordCode, subject, path, body);
    }

    private void sendEmail (String email, String verificationCode, String subject, String path, String body) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/verification-email-template.html");
            String htmlTemplate = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String formattedBody = htmlTemplate
                    .replace("{{SUBJECT}}", subject)
                    .replace("{{BODY}}", body)
                    .replace("{{CODE}}", verificationCode)
                    .replace("{{LINK}}", path);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setFrom(emailSender); 
            helper.setSubject(subject);
            helper.setText(formattedBody, true); 
            javaMailSender.send(message);

        } catch (IOException e) {
            throw new RuntimeException("Error loading email template: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    public String generateVeryficationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(100_000_000); 
        return String.format("%08d", code);   
    }
}