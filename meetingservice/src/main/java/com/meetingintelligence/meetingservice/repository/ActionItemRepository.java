package com.meetingintelligence.meetingservice.repository;

import com.meetingintelligence.meetingservice.entity.ActionItem;
import com.meetingintelligence.meetingservice.entity.ActionItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findByMeetingId(Long meetingId);
    Optional<ActionItem> findByIdAndMeetingId(Long id, Long meetingId);
    void deleteByMeetingIdAndStatus(Long meetingId, ActionItemStatus status);
    @Query("""
            select a from ActionItem a join fetch a.meeting
            where a.dueDate = :date and a.status = :status
              and a.reminderSentAt is null and a.ownerEmail is not null
            """)
    List<ActionItem> findDueForReminder(@Param("date") LocalDate date,
                                        @Param("status") ActionItemStatus status);
}