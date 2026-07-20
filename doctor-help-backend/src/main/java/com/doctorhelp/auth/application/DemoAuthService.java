package com.doctorhelp.auth.application;

import com.doctorhelp.auth.api.dto.LoginRequest;
import com.doctorhelp.auth.api.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class DemoAuthService {
    private final UserDirectoryService userDirectoryService;
    private final TokenService tokenService;

    public DemoAuthService(UserDirectoryService userDirectoryService, TokenService tokenService) {
        this.userDirectoryService = userDirectoryService;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request) {
        LoginResponse.UserProfile user = userDirectoryService.findByUsername(request.username());
        if (user == null || !"demo123".equals(request.password())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        return new LoginResponse(tokenService.createToken(user), user);
    }
}
