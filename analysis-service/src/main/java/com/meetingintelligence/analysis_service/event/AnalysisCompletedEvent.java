package com.meetingintelligence.analysis_service.event;

import com.meetingintelligence.analysis_service.model.AnalysisResult;

import java.util.List;

public record AnalysisCompletedEvent(
        Long meetingId,
        String ownerEmail,
        String title,
        boolean success,
        String error,
        String summary,
        List<String> decisions,
        List<AnalysisResult.ActionItem> actionItems) {

    public static AnalysisCompletedEvent success(AnalysisRequestedEvent e, AnalysisResult r) {
        return new AnalysisCompletedEvent(e.meetingId(), e.ownerEmail(), e.title(),
                true, null, r.summary(), r.decisions(), r.actionItems());
    }

    public static AnalysisCompletedEvent failure(AnalysisRequestedEvent e, String error) {
        return new AnalysisCompletedEvent(e.meetingId(), e.ownerEmail(), e.title(),
                false, error, null, List.of(), List.of());
    }
}