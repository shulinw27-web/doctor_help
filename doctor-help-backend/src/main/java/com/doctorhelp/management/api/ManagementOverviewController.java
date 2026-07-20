package com.doctorhelp.management.api;

import com.doctorhelp.common.api.ApiResponse;
import com.doctorhelp.management.api.dto.ManagementOverviewResponse;
import com.doctorhelp.management.application.ManagementOverviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/management/overview")
public class ManagementOverviewController {

    private final ManagementOverviewService managementOverviewService;

    public ManagementOverviewController(ManagementOverviewService managementOverviewService) {
        this.managementOverviewService = managementOverviewService;
    }

    @GetMapping
    public ApiResponse<ManagementOverviewResponse> getOverview() {
        return ApiResponse.success(managementOverviewService.getOverview());
    }
}
