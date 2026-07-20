package com.doctorhelp.user.api;

import com.doctorhelp.auth.api.dto.LoginResponse;
import com.doctorhelp.auth.application.UserDirectoryService;
import com.doctorhelp.auth.infrastructure.AuthInterceptor;
import com.doctorhelp.common.api.ApiResponse;
import com.doctorhelp.user.api.dto.SystemUserResponse;
import com.doctorhelp.user.api.dto.CreateSystemUserRequest;
import com.doctorhelp.user.api.dto.OaSyncResultResponse;
import com.doctorhelp.user.api.dto.UpdateUserRoleRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/api/v1/users")
public class UserController {
    private final UserDirectoryService userDirectoryService;
    public UserController(UserDirectoryService userDirectoryService) { this.userDirectoryService = userDirectoryService; }
    @GetMapping public ApiResponse<List<SystemUserResponse>> list(HttpServletRequest request) { requireAuthenticated(request); return ApiResponse.success(userDirectoryService.listUsers()); }
    @PostMapping public ApiResponse<SystemUserResponse> create(@Valid @RequestBody CreateSystemUserRequest body, HttpServletRequest request) { requireAdmin(request); return ApiResponse.success(userDirectoryService.createUser(body)); }
    @PostMapping("/sync-oa") public ApiResponse<OaSyncResultResponse> syncFromOa(HttpServletRequest request) { requireAdmin(request); return ApiResponse.success(userDirectoryService.syncFromOa()); }
    @PatchMapping("/{id}/role") public ApiResponse<SystemUserResponse> updateRole(@PathVariable String id, @Valid @RequestBody UpdateUserRoleRequest body, HttpServletRequest request) { requireAdmin(request); return ApiResponse.success(userDirectoryService.updateRole(id, body.role())); }
    private void requireAdmin(HttpServletRequest request) {
        LoginResponse.UserProfile user = (LoginResponse.UserProfile) request.getAttribute(AuthInterceptor.CURRENT_USER);
        if (user == null || !"ADMIN".equals(user.role())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "仅管理员可操作用户管理");
    }
    private void requireAuthenticated(HttpServletRequest request) {
        if (request.getAttribute(AuthInterceptor.CURRENT_USER) == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已过期，请重新登录");
    }
}
