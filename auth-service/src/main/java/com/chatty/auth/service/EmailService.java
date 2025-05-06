
package com.chatty.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String to, String token) {
        try {
            log.info("Sending verification email to: {}", to);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            
            helper.setTo(to);
            helper.setSubject("Verify your email address");
            helper.setFrom("noreply@chatty.com");
            
            // Create verification link
            String verificationLink = "https://chatty.com/verify?email=" + to + "&token=" + token;
            
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
            htmlContent.append("<h1 style='color: #4285F4;'>Welcome to Chatty!</h1>");
            htmlContent.append("<p>Thank you for registering with us. Please click the button below to verify your email address:</p>");
            htmlContent.append("<div style='text-align: center;'>");
            htmlContent.append("<a href='").append(verificationLink).append("' style='display: inline-block; background-color: #4285F4; color: white; font-weight: bold; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Verify Email</a>");
            htmlContent.append("</div>");
            htmlContent.append("<p style='margin-top: 30px;'>If the button doesn't work, please copy and paste the following link into your browser:</p>");
            htmlContent.append("<p>").append(verificationLink).append("</p>");
            htmlContent.append("<p>This link will expire in 24 hours.</p>");
            htmlContent.append("<p>If you did not create an account, you can safely ignore this email.</p>");
            htmlContent.append("<p>Best regards,<br>The Chatty Team</p>");
            htmlContent.append("</div>");
            
            helper.setText(htmlContent.toString(), true);
            mailSender.send(message);
            
            log.info("Verification email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send verification email", e);
        }
    }

    @Async
    public void sendApiKeyCreationNotification(String to, String apiKey) {
        try {
            log.info("Sending API key notification to: {}", to);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            
            helper.setTo(to);
            helper.setSubject("Your API Key has been created");
            helper.setFrom("noreply@chatty.com");
            
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
            htmlContent.append("<h1 style='color: #4285F4;'>Your API Key is Ready!</h1>");
            htmlContent.append("<p>Your API key has been successfully created. Here are the details:</p>");
            htmlContent.append("<p><strong>API Key:</strong> ").append(apiKey).append("</p>");
            htmlContent.append("<p>Please note that for security reasons, we only show the API Secret once during creation. Make sure to store it securely.</p>");
            htmlContent.append("<p>You can manage your API keys from your dashboard at any time.</p>");
            htmlContent.append("<p>Best regards,<br>The Chatty Team</p>");
            htmlContent.append("</div>");
            
            helper.setText(htmlContent.toString(), true);
            mailSender.send(message);
            
            log.info("API key notification email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send API key notification email", e);
        }
    }
}
