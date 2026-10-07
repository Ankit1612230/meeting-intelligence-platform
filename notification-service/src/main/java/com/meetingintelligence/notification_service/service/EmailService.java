package com.meetingintelligence.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templates;

    public void send(String to, String subject, String template, Context context) {
        try {
            String html = templates.process(template, context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject.replaceAll("[\\r\\n]", " "));
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Could not send email to " + to, e);
        }
    }
}