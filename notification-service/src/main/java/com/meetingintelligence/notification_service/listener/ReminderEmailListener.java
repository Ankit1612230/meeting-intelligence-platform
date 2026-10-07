package com.meetingintelligence.notification_service.listener;

import com.meetingintelligence.notification_service.event.ReminderDueEvent;
import com.meetingintelligence.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class ReminderEmailListener {

    private final EmailService emailService;

    @KafkaListener(topics = "reminder.due",
            properties = "spring.json.value.default.type=com.meetingintelligence.notification_service.event.ReminderDueEvent")
    void onReminder(ReminderDueEvent e) {
        Context ctx = new Context();
        ctx.setVariable("task", e.task());
        ctx.setVariable("meetingTitle", e.meetingTitle());
        ctx.setVariable("dueDate", e.dueDate());

        emailService.send(e.ownerEmail(), "Reminder: " + e.task(), "reminder", ctx);
    }
}