package com.meetingintelligence.meetingservice.event;

import java.util.List;

public record AnalysisCompletedEvent(
        Long meetingId,
        String ownerEmail,
        String title,
        boolean success,
        String error,
        String summary,
        List<String> decisions,
        List<ActionItemData> actionItems) {

    public record ActionItemData(String task, String ownerEmail, String dueDate) {}
}