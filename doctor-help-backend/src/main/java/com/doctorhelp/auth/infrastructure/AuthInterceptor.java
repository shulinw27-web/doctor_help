package com.doctorhelp.auth.infrastructure;

import com.doctorhelp.auth.api.dto.LoginResponse;
import com.doctorhelp.auth.application.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public static final String CURRENT_USER = "currentUser";
    private final TokenService tokenService;
    public AuthInterceptor(TokenService tokenService) { this.tokenService = tokenService; }
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorization != null && authorization.startsWith("Bearer ") ? authorization.substring(7) : null;
        LoginResponse.UserProfile user = tokenService.validateAndRefresh(token);
        if (user == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); response.setContentType("application/json;charset=UTF-8");
            try { response.getWriter().write("{\"success\":false,\"data\":null,\"message\":\"登录已过期，请重新登录\"}"); } catch (Exception ignored) { }
            return false;
        }
        request.setAttribute(CURRENT_USER, user); return true;
    }
}
