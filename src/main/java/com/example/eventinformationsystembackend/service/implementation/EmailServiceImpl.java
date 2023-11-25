package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.EventDtoResponse;
import com.example.eventinformationsystembackend.dto.OrderItemDtoResponse;
import com.example.eventinformationsystembackend.dto.TicketDtoResponse;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.example.eventinformationsystembackend.common.EmailTexts.*;
import static com.example.eventinformationsystembackend.common.FilePaths.EVENTS_FOLDER_PATH;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
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

    @Async
    @Override
    public void sendOrderEmail(User user, String text,
                               List<OrderItemDtoResponse> orderItems) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "utf-8");

            helper.setText(text, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Order received!");
            helper.setFrom("event-information@gmail.com");

            for (OrderItemDtoResponse orderItem : orderItems) {
                EventDtoResponse event = orderItem.getTickets().get(0).getEvent();
                String imagePath = EVENTS_FOLDER_PATH + event.getName() + "\\resized.jpg";
                FileSystemResource inline = new FileSystemResource(new File(imagePath));
                helper.addInline(String.valueOf(event.getId()), inline);
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Async
    @Override
    public void sendTicketsEmail(User user, String text, List<String> ticketsPdfPaths,
                                 List<TicketDtoResponse> tickets) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "utf-8");

            helper.setText(text, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Tickets received!");
            helper.setFrom("event-information@gmail.com");

            for (int i = 0; i < ticketsPdfPaths.size(); i++) {
                String attachmentName = "Ticket-" + (i + 1) + "-" + tickets.get(i).getEvent().getName();
                helper.addAttachment(attachmentName, new File(ticketsPdfPaths.get(i)));
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Async
    @Override
    public void sendSupportTicketReceivedEmail(String receiverEmail,
                                               String text) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(text, true);
            helper.setTo(receiverEmail);
            helper.setSubject("Support Ticket Received!");
            helper.setFrom("event-information@gmail.com");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Async
    @Override
    public void sendSupportTicketResponseEmail(String receiverEmail,
                                               String text) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(text, true);
            helper.setTo(receiverEmail);
            helper.setSubject("Support Ticket Response!");
            helper.setFrom("event-information@gmail.com");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    private String buildEmail(String name, String link) {
        return String.format(ACCOUNT_CONFIRMATION_EMAIL_TEMPLATE, name, link, link);
    }
}
