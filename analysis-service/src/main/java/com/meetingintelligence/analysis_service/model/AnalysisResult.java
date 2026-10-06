package com.meetingintelligence.analysis_service.model;

import java.util.List;

public record AnalysisResult(String summary,
                             List<String> decisions,
                             List<ActionItem> actionItems) {

    public record ActionItem(String task, String ownerEmail, String dueDate) {}
}