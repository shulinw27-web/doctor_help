package com.doctorhelp.knowledge.api.dto;

import java.util.List;

public record KnowledgeArticleResponse(
        String id,
        List<String> categoryPath,
        String title,
        String content,
        String provider,
        int likes,
        String source,
        String updatedAt,
        boolean likedByCurrentUser
) {
}
