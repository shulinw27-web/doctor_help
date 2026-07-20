package com.doctorhelp.management.application;

import com.doctorhelp.management.api.dto.ManagementOverviewResponse;
import com.doctorhelp.knowledge.application.KnowledgeArticleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagementOverviewService {

    private final KnowledgeArticleService knowledgeArticleService;

    public ManagementOverviewService(KnowledgeArticleService knowledgeArticleService) {
        this.knowledgeArticleService = knowledgeArticleService;
    }

    public ManagementOverviewResponse getOverview() {
        KnowledgeArticleService.KnowledgeStatistics knowledgeStatistics = knowledgeArticleService.getStatistics();
        return new ManagementOverviewResponse(
                List.of(
                        new ManagementOverviewResponse.Metric("今日查看病例", "32", "演示环境累计资料查看量"),
                        new ManagementOverviewResponse.Metric("AI 汇总生成", "18", "演示规则模板调用次数"),
                        new ManagementOverviewResponse.Metric("知识库检索", "26", "演示知识条目查看次数"),
                        new ManagementOverviewResponse.Metric("模拟平均处理时长", "4分12秒", "仅为演示数据，不代表真实效率")
                ),
                new ManagementOverviewResponse.KnowledgeOverview(
                        knowledgeStatistics.totalArticles(), knowledgeStatistics.weeklyAdded(), knowledgeStatistics.totalCategories()
                ),
                List.of(
                        new ManagementOverviewResponse.DailyCaseTrend("07-13", 18),
                        new ManagementOverviewResponse.DailyCaseTrend("07-14", 23),
                        new ManagementOverviewResponse.DailyCaseTrend("07-15", 21),
                        new ManagementOverviewResponse.DailyCaseTrend("07-16", 27),
                        new ManagementOverviewResponse.DailyCaseTrend("07-17", 25),
                        new ManagementOverviewResponse.DailyCaseTrend("07-18", 32),
                        new ManagementOverviewResponse.DailyCaseTrend("07-19", 16)
                ),
                List.of(
                        new ManagementOverviewResponse.IntegrationStatus("OA / 统一身份认证", "医生、科室、角色", "演示模拟", "2026-07-18 10:30"),
                        new ManagementOverviewResponse.IntegrationStatus("HIS / 挂号系统", "患者、就诊、责任医生", "演示模拟", "2026-07-18 10:30"),
                        new ManagementOverviewResponse.IntegrationStatus("LIS", "检验与其他检查结果", "演示模拟", "2026-07-18 10:30"),
                        new ManagementOverviewResponse.IntegrationStatus("PACS / RIS", "影像报告与原片入口", "演示模拟", "2026-07-18 10:30")
                ),
                "当前所有统计与同步状态均来自模拟数据。真实试点需以医院只读接口、审计和数据安全流程为准。"
        );
    }
}
