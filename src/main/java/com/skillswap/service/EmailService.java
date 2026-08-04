package com.skillswap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String token) {
        String resetLink = "https://skillswap-frontend-zeta.vercel.app/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("SkillSwap - Reset Your Password");
        message.setText("Hello,\n\n" +
                "We received a request to reset your password for your SkillSwap account.\n\n" +
                "Click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Team SkillSwap");

        mailSender.send(message);
    }

    public void sendWelcomeEmail(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to SkillSwap!");
        message.setText("Hello " + name + ",\n\n" +
                "Welcome to SkillSwap! 🎉\n\n" +
                "You can now start learning and teaching skills with our community.\n\n" +
                "Get started: https://skillswap-frontend-zeta.vercel.app\n\n" +
                "Team SkillSwap");

        mailSender.send(message);
    }

    public void sendOTPEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("SkillSwap - Your OTP Code");
        message.setText("Hello,\n\n" +
                "Your OTP for SkillSwap login is:\n\n" +
                otp + "\n\n" +
                "This OTP will expire in 5 minutes.\n\n" +
                "Team SkillSwap");

        mailSender.send(message);
    }
}