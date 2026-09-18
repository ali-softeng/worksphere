package com.ali.worksphere.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("WorkSphere - Password Reset OTP");

        message.setText(
                "Your password reset OTP is: " + otp +
                        "\n\nThis OTP will expire in 2 minutes."
        );

        mailSender.send(message);
    }
}
