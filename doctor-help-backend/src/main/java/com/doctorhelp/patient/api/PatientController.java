package com.doctorhelp.patient.api;

import com.doctorhelp.common.api.ApiResponse;
import com.doctorhelp.patient.api.dto.PatientDetailResponse;
import com.doctorhelp.patient.application.PatientDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientDetailService patientDetailService;

    public PatientController(PatientDetailService patientDetailService) {
        this.patientDetailService = patientDetailService;
    }

    @GetMapping("/{patientId}")
    public ApiResponse<PatientDetailResponse> getPatientDetail(@PathVariable String patientId) {
        return ApiResponse.success(patientDetailService.getPatientDetail(patientId));
    }
}
