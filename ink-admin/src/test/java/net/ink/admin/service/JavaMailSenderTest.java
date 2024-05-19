package net.ink.admin.service;

import net.ink.push.service.FcmLikePushServiceImpl;
import net.ink.push.service.FcmService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@Disabled("테스트를 하게 되면 실제 메일이 발송되므로 실제 테스트 할때는 Disabled 주석 처리할것")
@SpringBootTest
@ActiveProfiles("dev")
@Import({FcmLikePushServiceImpl.class, FcmService.class})
public class JavaMailSenderTest {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.from}")
    private String fromEmail;

    @Test
    @DisplayName("이메일을 보낸다.")
    void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        String toEmail = "naruhodo627@gmail.com";
        message.setTo(toEmail);
        String subject = "Test Email";
        message.setSubject(subject);
        String text = "This is a test email.";
        message.setText(text);
        javaMailSender.send(message);
    }
}
