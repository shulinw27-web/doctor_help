package com.doctorhelp.user.api.dto;

import com.doctorhelp.auth.api.dto.LoginResponse;

public record SystemUserResponse(
        String id,
        String username,
        String name,
        String role,
        String departmentName,
        String title,
        String employeeNo,
        String gender,
        String mobile,
        String email,
        String employmentStatus,
        String joinedAt,
        OaProfile oaProfile
) {
    public static SystemUserResponse from(LoginResponse.UserProfile user, PersonnelProfile profile) {
        return new SystemUserResponse(
                user.id(), user.username(), user.name(), user.role(), user.departmentName(), user.title(),
                profile.employeeNo(), profile.gender(), profile.mobile(), profile.email(), profile.employmentStatus(), profile.joinedAt(),
                new OaProfile(profile.oaUserId(), profile.oaDepartmentCode(), profile.oaPositionName(), profile.oaAccountStatus(), profile.oaLastSyncedAt())
        );
    }

    public record OaProfile(String oaUserId, String oaDepartmentCode, String oaPositionName, String oaAccountStatus, String lastSyncedAt) {
    }

    public record PersonnelProfile(
            String employeeNo,
            String gender,
            String mobile,
            String email,
            String employmentStatus,
            String joinedAt,
            String oaUserId,
            String oaDepartmentCode,
            String oaPositionName,
            String oaAccountStatus,
            String oaLastSyncedAt
    ) {
    }
}
