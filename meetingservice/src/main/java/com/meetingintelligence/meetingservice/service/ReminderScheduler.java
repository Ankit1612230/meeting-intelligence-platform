package com.meetingintelligence.meetingservice.service;

import com.meetingintelligence.meetingservice.config.KafkaTopicConfig;
import com.meetingintelligence.meetingservice.entity.ActionItem;
import com.meetingintelligence.meetingservice.entity.ActionItemStatus;
import com.meetingintelligence.meetingservice.event.ReminderDueEvent;
import com.meetingintelligence.meetingservice.repository.ActionItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    private final ActionItemRepository actionItems;
    private final KafkaTemplate<String, Object> kafka;

    @Scheduled(cron = "${reminder.cron:0 0 9 * * *}", zone = "Asia/Kolkata")
    @Transactional
    public void sendReminders() {
        LocalDate tomorrow = LocalDate.now(ZONE).plusDays(1);
        List<ActionItem> due = actionItems.findDueForReminder(tomorrow, ActionItemStatus.PENDING);

        for (ActionItem a : due) {
            kafka.send(KafkaTopicConfig.REMINDER_DUE, String.valueOf(a.getId()),
                    new ReminderDueEvent(a.getId(), a.getOwnerEmail(), a.getMeeting().getTitle(),
                            a.getTask(), a.getDueDate().toString()));
            a.setReminderSentAt(LocalDateTime.now());
        }
        if (!due.isEmpty()) {
            log.info("Published {} reminders for {}", due.size(), tomorrow);
        }
    }
}