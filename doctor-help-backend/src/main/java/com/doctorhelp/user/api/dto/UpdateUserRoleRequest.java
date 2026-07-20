package com.doctorhelp.user.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record UpdateUserRoleRequest(@NotBlank @Pattern(regexp = "DOCTOR|LEADER|ADMIN") String role) { }
