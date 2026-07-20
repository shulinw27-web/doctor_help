package com.doctorhelp.ai.application;

import com.doctorhelp.ai.api.dto.AiSummaryResponse;
import com.doctorhelp.patient.api.dto.PatientDetailResponse;
import com.doctorhelp.patient.application.PatientDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientAiSummaryService {

    private final PatientDetailService patientDetailService;

    public PatientAiSummaryService(PatientDetailService patientDetailService) {
        this.patientDetailService = patientDetailService;
    }

    public AiSummaryResponse generateSummary(String patientId) {
        PatientDetailResponse detail = patientDetailService.getPatientDetail(patientId);
        List<AiSummaryResponse.Finding> abnormalFindings = detail.examinationResults().stream()
                .filter(result -> result.status() != PatientDetailResponse.ResultStatus.NORMAL)
                .map(result -> new AiSummaryResponse.Finding(
                        result.name(),
                        result.result() + " " + result.unit(),
                        result.referenceRange(),
                        result.status() == PatientDetailResponse.ResultStatus.HIGH ? "偏高" : "偏低"
                ))
                .toList();

        String overview = "本次就诊已汇总 " + detail.examinationResults().size() + " 项检验结果和 "
                + detail.imagingRecords().size() + " 份影像资料。";
        String trendSummary = detail.whiteBloodCellTrend().getLast().value() > 9.5
                ? "白细胞计数较前两次结果升高，本次超过演示参考上限。"
                : "白细胞计数与前两次结果相比未见明显异常波动。";

        return new AiSummaryResponse(
                "演示规则模板（非大模型诊断）",
                overview,
                abnormalFindings,
                trendSummary,
                "请由医生结合患者主诉、体征、原始检验报告及影像原片进一步判断；本系统不提供疾病诊断、处方或治疗建议。",
                List.of("血常规报告（2026-07-18 09:16）", "炎症指标报告（2026-07-18 09:18）", "影像检查报告（2026-07-18）"),
                "本内容仅供医务人员参考，不构成诊断、处方或治疗建议。"
        );
    }
}

