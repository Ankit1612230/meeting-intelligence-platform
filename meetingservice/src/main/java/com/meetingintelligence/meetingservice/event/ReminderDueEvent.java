package com.meetingintelligence.meetingservice.event;

public record ReminderDueEvent(Long actionItemId, String ownerEmail,
                               String meetingTitle, String task, String dueDate) {}