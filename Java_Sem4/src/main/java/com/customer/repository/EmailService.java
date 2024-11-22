package com.customer.repository;

import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(String toEmail, String confirmationLink) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Email Confirmation");
        
        // Đặt tên người gửi là "Electro Sphere" và địa chỉ email là "anhbon148@gmail.com"
        helper.setFrom("Electro Sphere <anhbon148@gmail.com>");

        helper.setText(
        	    "<html>" +
        	    "<body style=\"font-family: Arial, sans-serif;\">" +
        	    "<div style=\"max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9;\">" +
        	    "<h2 style=\"color: #333; text-align: center;\">Registration Confirmation</h2>" +
        	    "<p style=\"font-size: 16px; color: #555; text-align: center;\">Thank you for registering! Please click the button below to confirm your registration:</p>" +
        	    "<div style=\"text-align: center;\">" +
        	    "<a href=\"" + confirmationLink + "\" style=\"display: inline-block; margin: 20px 0; padding: 10px 20px; background-color: #007BFF; color: white; text-decoration: none; border-radius: 5px; font-size: 16px;\">" +
        	    "Confirm Registration" +
        	    "</a>" +
        	    "</div>" +
        	    "<p style=\"font-size: 14px; color: #777; text-align: center;\">If you did not request this, please ignore this email.</p>" +
        	    "<footer style=\"text-align: center; margin-top: 20px; font-size: 12px; color: #aaa;\">" +
        	    "<p>Electro Sphere<br>Your trusted partner for electronic components</p>" +
        	    "</footer>" +
        	    "</div>" +
        	    "</body>" +
        	    "</html>",
        	    true
        	);

        mailSender.send(message);
        System.out.println("Confirmation email sent successfully!");
    }

    
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    public void sendcontact(String name, String email, String Message) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("anhbon1488@gmail.com");  
        helper.setSubject("Contact from ES");

        helper.setFrom(email);

        String htmlContent = "<html><body>" +
                "<h2 style='color: #4CAF50;'>Contact Information</h2>" +
                "<p><strong>Name:</strong> " + name + "</p>" +
                "<p><strong>Email:</strong> " + email + "</p>" +
                "<p><strong>Message:</strong></p>" +
                "<p style='border: 1px solid #ddd; padding: 10px; background-color: #f9f9f9;'>" + Message + "</p>" +
                "</body></html>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
