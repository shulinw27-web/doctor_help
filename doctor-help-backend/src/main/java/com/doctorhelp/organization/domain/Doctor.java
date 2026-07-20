package com.doctorhelp.organization.domain;

public record Doctor(
        String id,
        String name,
        String title,
        String departmentId,
        String departmentName
) {
}

