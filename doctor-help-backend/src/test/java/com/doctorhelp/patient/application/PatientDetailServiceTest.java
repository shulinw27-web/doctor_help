package com.doctorhelp.patient.application;

import com.doctorhelp.patient.api.dto.PatientDetailResponse;
import com.doctorhelp.patient.infrastructure.DemoPatientRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientDetailServiceTest {

    private final PatientDetailService patientDetailService = new PatientDetailService(new DemoPatientRepository());

    @Test
    void shouldMarkAbnormalResultsForDemoPatient() {
        PatientDetailResponse detail = patientDetailService.getPatientDetail("patient-002");

        long abnormalCount = detail.examinationResults().stream()
                .filter(result -> result.status() != PatientDetailResponse.ResultStatus.NORMAL)
                .count();

        assertEquals(3, abnormalCount);
        assertEquals(2, detail.imagingRecords().size());
    }
}

