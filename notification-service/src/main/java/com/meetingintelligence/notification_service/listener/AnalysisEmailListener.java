package com.meetingintelligence.notification_service.listener;

import com.meetingintelligence.notification_service.event.AnalysisCompletedEvent;
import com.meetingintelligence.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class AnalysisEmailListener {

    private final EmailService emailService;

    @KafkaListener(topics = "analysis.completed")
    void onCompleted(AnalysisCompletedEvent e) {
        Context ctx = new Context();
        ctx.setVariable("title", e.title());
        ctx.setVariable("success", e.success());
        ctx.setVariable("summary", e.summary());
        ctx.setVariable("decisions", e.decisions());
        ctx.setVariable("actionItems", e.actionItems());

        String subject = e.success()
                ? "Meeting analysis ready: " + e.title()
                : "Meeting analysis failed: " + e.title();
        emailService.send(e.ownerEmail(), subject, "analysis-ready", ctx);
    }
}