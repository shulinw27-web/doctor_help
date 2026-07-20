package com.doctorhelp.ai.api.dto;

import java.util.List;

public record AiSummaryResponse(
        String generationSource,
        String overview,
        List<Finding> abnormalFindings,
        String trendSummary,
        String attentionItem,
        List<String> evidenceSources,
        String disclaimer
) {
    public record Finding(String name, String result, String referenceRange, String status) {
    }
}

