package com.doctorhelp.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateSystemUserRequest(
        @NotBlank @Pattern(regexp = "[a-zA-Z0-9_-]{3,30}") String username,
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "男|女") String gender,
        @NotBlank @Pattern(regexp = "DOCTOR|LEADER|ADMIN") String role,
        @NotBlank String departmentName,
        @NotBlank String title,
        @NotBlank String mobile,
        @NotBlank String email
) {
}
