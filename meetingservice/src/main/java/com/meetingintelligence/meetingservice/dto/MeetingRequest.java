package com.meetingintelligence.meetingservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingRequest(
        @NotBlank String title,
        @NotNull LocalDateTime meetingDate,
        String transcript,
        List<@Valid ParticipantRequest> participants) {

    public record ParticipantRequest(@NotBlank String name,
                                     @NotBlank @Email String email) {}
}