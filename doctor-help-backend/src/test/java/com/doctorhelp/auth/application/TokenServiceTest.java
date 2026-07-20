package com.doctorhelp.auth.application;

import com.doctorhelp.auth.api.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    @Test
    void validatesAndRefreshesTokenForTheCurrentUserRole() {
        UserDirectoryService users = new UserDirectoryService();
        TokenService tokens = new TokenService(Duration.ofMinutes(10), users);
        LoginResponse.UserProfile admin = users.findByUsername("admin");

        String token = tokens.createToken(admin);

        assertEquals("ADMIN", tokens.validateAndRefresh(token).role());
        users.updateRole(admin.id(), "DOCTOR");
        assertEquals("DOCTOR", tokens.validateAndRefresh(token).role());
        assertNull(tokens.validateAndRefresh("not-a-token"));
    }
}
