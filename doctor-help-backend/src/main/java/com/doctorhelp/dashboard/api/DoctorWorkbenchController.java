package com.doctorhelp.dashboard.api;

import com.doctorhelp.common.api.ApiResponse;
import com.doctorhelp.dashboard.api.dto.DoctorWorkbenchResponse;
import com.doctorhelp.dashboard.application.DoctorWorkbenchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctor-workbench")
public class DoctorWorkbenchController {

    private final DoctorWorkbenchService doctorWorkbenchService;

    public DoctorWorkbenchController(DoctorWorkbenchService doctorWorkbenchService) {
        this.doctorWorkbenchService = doctorWorkbenchService;
    }

    @GetMapping
    public ApiResponse<DoctorWorkbenchResponse> getWorkbench() {
        return ApiResponse.success(doctorWorkbenchService.getWorkbench("doctor-001"));
    }
}

