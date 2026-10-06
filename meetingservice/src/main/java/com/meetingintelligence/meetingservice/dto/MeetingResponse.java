package com.meetingintelligence.meetingservice.dto;

import com.meetingintelligence.meetingservice.entity.Meeting;
import com.meetingintelligence.meetingservice.entity.MeetingStatus;
import com.meetingintelligence.meetingservice.entity.Participant;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingResponse(Long id, String title, LocalDateTime meetingDate,
                              MeetingStatus status, List<Participant> participants,
                              LocalDateTime createdAt) {

    public static MeetingResponse from(Meeting m) {
        return new MeetingResponse(m.getId(), m.getTitle(), m.getMeetingDate(),
                m.getStatus(), List.copyOf(m.getParticipants()), m.getCreatedAt());
    }
}