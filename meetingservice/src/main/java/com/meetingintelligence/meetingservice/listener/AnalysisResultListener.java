package com.meetingintelligence.meetingservice.listener;

import com.meetingintelligence.meetingservice.event.AnalysisCompletedEvent;
import com.meetingintelligence.meetingservice.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisResultListener {

    private final MeetingService meetingService;

    @KafkaListener(topics = "analysis.completed")
    void onCompleted(AnalysisCompletedEvent event) {
        meetingService.applyAnalysis(event);
    }
}