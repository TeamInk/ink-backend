package net.ink.admin.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
public class AdminEmailService {

    private final JavaMailSender emailSender;
    private final String fromEmail;

    public AdminEmailService(JavaMailSender emailSender, @Value("${EMAIL_FROM:noreply@example.com}") String fromEmail) {
        this.emailSender = emailSender;
        this.fromEmail = fromEmail;
    }

    public void sendPromotionEmail(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}