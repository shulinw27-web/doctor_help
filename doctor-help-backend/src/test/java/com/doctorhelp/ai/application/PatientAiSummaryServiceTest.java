package com.doctorhelp.ai.application;

import com.doctorhelp.patient.application.PatientDetailService;
import com.doctorhelp.patient.infrastructure.DemoPatientRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatientAiSummaryServiceTest {

    private final PatientAiSummaryService patientAiSummaryService = new PatientAiSummaryService(
            new PatientDetailService(new DemoPatientRepository())
    );

    @Test
    void shouldGenerateTraceableNonDiagnosticSummary() {
        var summary = patientAiSummaryService.generateSummary("patient-002");

        assertEquals(3, summary.abnormalFindings().size());
        assertTrue(summary.disclaimer().contains("不构成诊断"));
        assertEquals(3, summary.evidenceSources().size());
    }
}

