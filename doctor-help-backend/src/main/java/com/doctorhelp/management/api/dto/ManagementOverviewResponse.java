package com.doctorhelp.management.api.dto;

import java.util.List;

public record ManagementOverviewResponse(
        List<Metric> metrics,
        KnowledgeOverview knowledgeOverview,
        List<DailyCaseTrend> dailyCaseTrend,
        List<IntegrationStatus> integrationStatuses,
        String note
) {
    public record Metric(String label, String value, String description) {
    }

    public record IntegrationStatus(String system, String scope, String status, String updatedAt) {
    }

    public record KnowledgeOverview(int totalArticles, int weeklyAdded, int totalCategories) {
    }

    public record DailyCaseTrend(String date, int cases) {
    }
}
