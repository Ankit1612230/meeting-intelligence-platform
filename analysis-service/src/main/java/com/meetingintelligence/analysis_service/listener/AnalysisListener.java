package com.meetingintelligence.analysis_service.listener;

import com.meetingintelligence.analysis_service.config.KafkaTopicConfig;
import com.meetingintelligence.analysis_service.event.AnalysisCompletedEvent;
import com.meetingintelligence.analysis_service.event.AnalysisRequestedEvent;
import com.meetingintelligence.analysis_service.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisListener {

    private static final Logger log = LoggerFactory.getLogger(AnalysisListener.class);

    private final AnalysisService analysisService;
    private final KafkaTemplate<String, Object> kafka;

    @KafkaListener(topics = "analysis.requested")
    void onAnalysisRequested(AnalysisRequestedEvent event) {
        AnalysisCompletedEvent outcome;
        try {
            outcome = AnalysisCompletedEvent.success(event, analysisService.analyze(event));
            log.info("Analysis done for meeting {}", event.meetingId());
        } catch (Exception ex) {
            log.error("Analysis failed for meeting {}", event.meetingId(), ex);
            outcome = AnalysisCompletedEvent.failure(event, "AI analysis failed");
        }
        kafka.send(KafkaTopicConfig.ANALYSIS_COMPLETED, String.valueOf(event.meetingId()), outcome);
    }
}