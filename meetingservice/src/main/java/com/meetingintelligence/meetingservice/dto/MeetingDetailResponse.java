package com.meetingintelligence.meetingservice.dto;

import com.meetingintelligence.meetingservice.entity.ActionItem;
import com.meetingintelligence.meetingservice.entity.ActionItemStatus;
import com.meetingintelligence.meetingservice.entity.Meeting;
import com.meetingintelligence.meetingservice.entity.MeetingStatus;
import com.meetingintelligence.meetingservice.entity.Participant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MeetingDetailResponse(
        Long id, String title, LocalDateTime meetingDate, MeetingStatus status,
        String transcript, List<Participant> participants,
        String summary, List<String> decisions,
        List<ActionItemResponse> actionItems, LocalDateTime createdAt) {

    public record ActionItemResponse(Long id, String task, String ownerEmail,
                                     LocalDate dueDate, ActionItemStatus status,
                                     LocalDateTime completedAt) {
        public static ActionItemResponse from(ActionItem a) {
            return new ActionItemResponse(a.getId(), a.getTask(), a.getOwnerEmail(),
                    a.getDueDate(), a.getStatus(), a.getCompletedAt());
        }
    }

    public static MeetingDetailResponse from(Meeting m) {
        return new MeetingDetailResponse(m.getId(), m.getTitle(), m.getMeetingDate(),
                m.getStatus(), m.getTranscript(), List.copyOf(m.getParticipants()),
                m.getSummary(), List.copyOf(m.getDecisions()),
                m.getActionItems().stream().map(ActionItemResponse::from).toList(),
                m.getCreatedAt());
    }
}