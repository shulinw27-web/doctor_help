package com.doctorhelp.auth.application;

import com.doctorhelp.auth.api.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private final ConcurrentHashMap<String, TokenSession> sessions = new ConcurrentHashMap<>();
    private final Duration timeout;
    private final UserDirectoryService userDirectoryService;
    public TokenService(@Value("${doctor-help.auth.token-timeout:10m}") Duration timeout, UserDirectoryService userDirectoryService) { this.timeout = timeout; this.userDirectoryService = userDirectoryService; }
    public String createToken(LoginResponse.UserProfile user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new TokenSession(user.id(), Instant.now().plus(timeout)));
        return token;
    }
    public LoginResponse.UserProfile validateAndRefresh(String token) {
        if (token == null || token.isBlank()) return null;
        TokenSession session = sessions.get(token);
        if (session == null || !session.expiresAt().isAfter(Instant.now())) { sessions.remove(token); return null; }
        LoginResponse.UserProfile user = userDirectoryService.findById(session.userId());
        if (user == null) { sessions.remove(token); return null; }
        sessions.put(token, new TokenSession(session.userId(), Instant.now().plus(timeout)));
        return user;
    }
    public void invalidate(String token) { sessions.remove(token); }
    private record TokenSession(String userId, Instant expiresAt) { }
}
