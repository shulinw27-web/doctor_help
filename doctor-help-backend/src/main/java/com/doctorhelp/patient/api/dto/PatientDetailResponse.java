package com.doctorhelp.patient.api.dto;

import com.doctorhelp.patient.domain.Patient;

import java.util.List;

public record PatientDetailResponse(
        Patient patient,
        List<ExaminationResult> examinationResults,
        List<TrendPoint> whiteBloodCellTrend,
        List<ImagingRecord> imagingRecords,
        List<VisitHistory> visitHistories,
        String clinicalNotice
) {
    public record ExaminationResult(
            String code,
            String category,
            String name,
            String result,
            String unit,
            String referenceRange,
            ResultStatus status,
            String measuredAt
    ) {
    }

    public record TrendPoint(String measuredAt, double value) {
    }

    public record ImagingRecord(
            String id,
            String modality,
            String bodyPart,
            String examinationAt,
            String reportSummary,
            String reportStatus
    ) {
    }

    public record VisitHistory(String visitDate, String departmentName, String visitType, String summary) {
    }

    public enum ResultStatus {
        NORMAL,
        HIGH,
        LOW
    }
}
