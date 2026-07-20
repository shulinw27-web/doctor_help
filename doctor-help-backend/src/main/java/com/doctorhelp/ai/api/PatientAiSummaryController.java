package com.doctorhelp.ai.api;

import com.doctorhelp.ai.api.dto.AiSummaryResponse;
import com.doctorhelp.ai.application.PatientAiSummaryService;
import com.doctorhelp.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/ai-summary")
public class PatientAiSummaryController {

    private final PatientAiSummaryService patientAiSummaryService;

    public PatientAiSummaryController(PatientAiSummaryService patientAiSummaryService) {
        this.patientAiSummaryService = patientAiSummaryService;
    }

    @GetMapping
    public ApiResponse<AiSummaryResponse> generateSummary(@PathVariable String patientId) {
        return ApiResponse.success(patientAiSummaryService.generateSummary(patientId));
    }
}

