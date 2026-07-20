package com.doctorhelp.patient.application;

import com.doctorhelp.patient.api.dto.PatientDetailResponse;
import com.doctorhelp.patient.domain.Patient;
import com.doctorhelp.patient.infrastructure.DemoPatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientDetailService {

    private final DemoPatientRepository patientRepository;

    public PatientDetailService(DemoPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientDetailResponse getPatientDetail(String patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("未找到模拟患者"));

        boolean hasAbnormalResult = "patient-002".equals(patientId);
        return new PatientDetailResponse(
                patient,
                createExaminationResults(hasAbnormalResult),
                createWhiteBloodCellTrend(hasAbnormalResult),
                createImagingRecords(hasAbnormalResult),
                createVisitHistories(),
                "本页为模拟检测资料演示。异常状态仅根据参考区间自动标注，需由医生结合主诉、体征和原始报告判断。"
        );
    }

    private List<PatientDetailResponse.ExaminationResult> createExaminationResults(boolean hasAbnormalResult) {
        return List.of(
                new PatientDetailResponse.ExaminationResult("WBC", "血常规", "白细胞计数", hasAbnormalResult ? "12.8" : "6.2", "×10⁹/L", "3.5–9.5", hasAbnormalResult ? PatientDetailResponse.ResultStatus.HIGH : PatientDetailResponse.ResultStatus.NORMAL, "2026-07-18 09:16"),
                new PatientDetailResponse.ExaminationResult("NEUT", "血常规", "中性粒细胞百分比", hasAbnormalResult ? "78.4" : "60.1", "%", "40.0–75.0", hasAbnormalResult ? PatientDetailResponse.ResultStatus.HIGH : PatientDetailResponse.ResultStatus.NORMAL, "2026-07-18 09:16"),
                new PatientDetailResponse.ExaminationResult("HGB", "血常规", "血红蛋白", "143", "g/L", "130–175", PatientDetailResponse.ResultStatus.NORMAL, "2026-07-18 09:16"),
                new PatientDetailResponse.ExaminationResult("CRP", "炎症指标", "C 反应蛋白", hasAbnormalResult ? "18.6" : "2.1", "mg/L", "0–8", hasAbnormalResult ? PatientDetailResponse.ResultStatus.HIGH : PatientDetailResponse.ResultStatus.NORMAL, "2026-07-18 09:18"),
                new PatientDetailResponse.ExaminationResult("GLU", "生化", "葡萄糖", "5.4", "mmol/L", "3.9–6.1", PatientDetailResponse.ResultStatus.NORMAL, "2026-07-18 09:18")
        );
    }

    private List<PatientDetailResponse.TrendPoint> createWhiteBloodCellTrend(boolean hasAbnormalResult) {
        double latestValue = hasAbnormalResult ? 12.8 : 6.2;
        return List.of(
                new PatientDetailResponse.TrendPoint("2026-05-12", 6.4),
                new PatientDetailResponse.TrendPoint("2026-06-20", 7.1),
                new PatientDetailResponse.TrendPoint("2026-07-18", latestValue)
        );
    }

    private List<PatientDetailResponse.ImagingRecord> createImagingRecords(boolean hasAbnormalResult) {
        String chestReport = hasAbnormalResult
                ? "双肺纹理增多，右下肺野见片状密度增高影；建议结合临床及原始影像进一步评估。"
                : "双肺纹理清晰，未见明显实变影。";

        return List.of(
                new PatientDetailResponse.ImagingRecord("image-001", "DR", "胸部正位", "2026-07-18 09:30", chestReport, hasAbnormalResult ? "需关注" : "已出报告"),
                new PatientDetailResponse.ImagingRecord("image-002", "超声", "肝胆胰脾", "2026-07-18 09:40", "肝胆胰脾超声检查报告已归档；原始图像仅供医生在 PACS 中调阅。", "已出报告")
        );
    }

    private List<PatientDetailResponse.VisitHistory> createVisitHistories() {
        return List.of(
                new PatientDetailResponse.VisitHistory("2026-07-18 09:45", "普通门诊", "当日预约", "本次检验、影像资料已归档，待医生查看。"),
                new PatientDetailResponse.VisitHistory("2026-06-20 14:10", "普通门诊", "复诊", "已完成血常规及生化检查。"),
                new PatientDetailResponse.VisitHistory("2026-05-12 10:30", "普通门诊", "初诊", "建立演示患者档案。")
        );
    }
}
