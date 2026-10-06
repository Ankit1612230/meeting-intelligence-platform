package com.meetingintelligence.meetingservice.repository;

import com.meetingintelligence.meetingservice.entity.ActionItem;
import com.meetingintelligence.meetingservice.entity.ActionItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findByMeetingId(Long meetingId);
    Optional<ActionItem> findByIdAndMeetingId(Long id, Long meetingId);
    void deleteByMeetingIdAndStatus(Long meetingId, ActionItemStatus status);
}