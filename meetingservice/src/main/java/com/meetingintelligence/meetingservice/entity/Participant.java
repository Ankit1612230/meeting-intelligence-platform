package com.meetingintelligence.meetingservice.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record Participant(String name, String email) {}