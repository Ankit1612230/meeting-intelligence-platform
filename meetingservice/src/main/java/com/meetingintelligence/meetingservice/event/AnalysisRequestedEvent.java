package com.meetingintelligence.meetingservice.event;

import java.util.List;

public record AnalysisRequestedEvent(
        Long meetingId,
        String ownerEmail,
        String title,
        String meetingDate,
        String transcript,
        List<Person> participants) {

    public record Person(String name, String email) {}
}