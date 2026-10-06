package com.meetingintelligence.meetingservice.repository;

import com.meetingintelligence.meetingservice.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByOwnerEmailOrderByMeetingDateDesc(String ownerEmail);

    Optional<Meeting> findByIdAndOwnerEmail(Long id, String ownerEmail);
}