package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.common.EmailTexts;
import com.example.eventinformationsystembackend.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.example.eventinformationsystembackend.common.EmailTexts.*;
import java.util.logging.Logger;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendConfirmationEmail(User user, String link) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(buildEmail(user.getFirstName(), link), true);
            helper.setTo(user.getEmail());
            helper.setSubject("Confirm your email");
            helper.setFrom("event-information@gmail.com");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }


    private String buildEmail(String name, String link) {
        return String.format(ACCOUNT_CONFIRMATION_TEXT, name, link, link);
    }
}
