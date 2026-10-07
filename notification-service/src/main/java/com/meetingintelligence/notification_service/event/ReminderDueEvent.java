package com.meetingintelligence.notification_service.event;

public record ReminderDueEvent(Long actionItemId, String ownerEmail,
                               String meetingTitle, String task, String dueDate) {}