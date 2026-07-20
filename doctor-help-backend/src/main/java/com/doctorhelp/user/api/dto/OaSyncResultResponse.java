package com.doctorhelp.user.api.dto;

public record OaSyncResultResponse(int addedCount, int updatedCount, int totalCount, String syncedAt) {
}
