package com.doctorhelp.auth.api;

import com.doctorhelp.auth.api.dto.LoginRequest;
import com.doctorhelp.auth.api.dto.LoginResponse;
import com.doctorhelp.auth.application.DemoAuthService;
import com.doctorhelp.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final DemoAuthService demoAuthService;
    public AuthController(DemoAuthService demoAuthService) { this.demoAuthService = demoAuthService; }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(demoAuthService.login(request));
    }
}
