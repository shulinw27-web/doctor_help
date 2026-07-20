package com.doctorhelp.knowledge.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateKnowledgeCategoryRequest(
        List<String> parentPath,
        @NotBlank @Size(max = 30) String label
) {
}
