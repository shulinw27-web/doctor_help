package com.doctorhelp.knowledge.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateKnowledgeArticleRequest(
        @NotEmpty List<@NotBlank @Size(max = 30) String> categoryPath,
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 5000) String content
) {
}
