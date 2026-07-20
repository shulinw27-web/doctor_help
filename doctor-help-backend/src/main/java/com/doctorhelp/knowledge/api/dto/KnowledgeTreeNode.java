package com.doctorhelp.knowledge.api.dto;

import java.util.List;

public record KnowledgeTreeNode(String id, String label, int articleCount, List<KnowledgeTreeNode> children) {
}
