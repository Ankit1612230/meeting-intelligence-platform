package com.meetingintelligence.meetingservice.service;

import com.meetingintelligence.meetingservice.dto.MeetingDetailResponse;
import com.meetingintelligence.meetingservice.dto.MeetingRequest;
import com.meetingintelligence.meetingservice.dto.MeetingResponse;
import com.meetingintelligence.meetingservice.entity.Meeting;
import com.meetingintelligence.meetingservice.entity.Participant;
import com.meetingintelligence.meetingservice.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.meetingintelligence.meetingservice.config.KafkaTopicConfig;
import com.meetingintelligence.meetingservice.event.AnalysisRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import com.meetingintelligence.meetingservice.entity.ActionItem;
import com.meetingintelligence.meetingservice.entity.ActionItemStatus;
import com.meetingintelligence.meetingservice.entity.MeetingStatus;
import com.meetingintelligence.meetingservice.event.AnalysisCompletedEvent;
import com.meetingintelligence.meetingservice.repository.ActionItemRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetings;
    private final KafkaTemplate<String, Object> kafka;
    private final ActionItemRepository actionItems;

    @Transactional
    public MeetingResponse create(MeetingRequest r, String ownerEmail) {
        Meeting m = new Meeting();
        m.setOwnerEmail(ownerEmail);
        m.setTitle(r.title().trim());
        m.setMeetingDate(r.meetingDate());
        m.setTranscript(r.transcript());
        if (r.participants() != null) {
            r.participants().forEach(p -> m.getParticipants()
                    .add(new Participant(p.name().trim(), p.email().trim().toLowerCase())));
        }
        return MeetingResponse.from(meetings.save(m));
    }

    @Transactional(readOnly = true)
    public List<MeetingResponse> list(String ownerEmail) {
        return meetings.findByOwnerEmailOrderByMeetingDateDesc(ownerEmail).stream()
                .map(MeetingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeetingDetailResponse get(Long id, String ownerEmail) {
        return MeetingDetailResponse.from(find(id, ownerEmail));
    }

    @Transactional
    public void delete(Long id, String ownerEmail) {
        meetings.delete(find(id, ownerEmail));
    }
    @Transactional(readOnly = true)
    public void requestAnalysis(Long id, String ownerEmail) {
        Meeting m = find(id, ownerEmail);
        if (m.getTranscript() == null || m.getTranscript().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting has no transcript");
        }
        var people = m.getParticipants().stream()
                .map(p -> new AnalysisRequestedEvent.Person(p.name(), p.email()))
                .toList();
        kafka.send(KafkaTopicConfig.ANALYSIS_REQUESTED, String.valueOf(m.getId()),
                new AnalysisRequestedEvent(m.getId(), m.getOwnerEmail(), m.getTitle(),
                        m.getMeetingDate().toString(), m.getTranscript(), people));
    }
    @Transactional
    public void applyAnalysis(AnalysisCompletedEvent e) {
        Meeting m = meetings.findById(e.meetingId()).orElse(null);
        if (m == null || !m.getOwnerEmail().equals(e.ownerEmail())) {
            log.warn("Ignoring analysis result for unknown meeting {}", e.meetingId());
            return;
        }
        if (!e.success()) {
            m.setStatus(MeetingStatus.FAILED);
            return;
        }

        m.setSummary(e.summary());
        m.getDecisions().clear();
        if (e.decisions() != null) m.getDecisions().addAll(e.decisions());

        // replace open tasks, keep finished ones
        actionItems.deleteByMeetingIdAndStatus(m.getId(), ActionItemStatus.PENDING);
        if (e.actionItems() != null) {
            for (var a : e.actionItems()) {
                ActionItem item = new ActionItem();
                item.setMeeting(m);
                item.setTask(a.task());
                item.setOwnerEmail(a.ownerEmail());
                item.setDueDate(a.dueDate() == null ? null : LocalDate.parse(a.dueDate()));
                actionItems.save(item);
            }
        }
        m.setStatus(MeetingStatus.ANALYZED);
    }
    @Transactional
    public MeetingDetailResponse.ActionItemResponse completeItem(Long meetingId, Long itemId,
                                                                 String ownerEmail) {
        Meeting m = find(meetingId, ownerEmail);
        ActionItem item = actionItems.findByIdAndMeetingId(itemId, m.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Action item not found"));
        if (item.getStatus() != ActionItemStatus.DONE) {
            item.setStatus(ActionItemStatus.DONE);
            item.setCompletedAt(LocalDateTime.now());
        }
        return MeetingDetailResponse.ActionItemResponse.from(item);
    }
    private Meeting find(Long id, String ownerEmail) {
        return meetings.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));
    }
}