package com.doctorhelp.knowledge.application;

import com.doctorhelp.knowledge.api.dto.CreateKnowledgeArticleRequest;
import com.doctorhelp.knowledge.api.dto.CreateKnowledgeCategoryRequest;
import com.doctorhelp.knowledge.api.dto.KnowledgeArticleResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KnowledgeArticleServiceTest {

    @Test
    void shouldAllowOneLikePerAccountAndExposeTheCurrentUsersLikeState() {
        KnowledgeArticleService service = new KnowledgeArticleService();
        KnowledgeArticleResponse original = service.getArticle("knowledge-001");

        KnowledgeArticleResponse firstLike = service.likeArticle(original.id(), "doctor-001");
        KnowledgeArticleResponse repeatedLike = service.likeArticle(original.id(), "doctor-001");

        assertEquals(original.likes() + 1, firstLike.likes());
        assertEquals(firstLike.likes(), repeatedLike.likes());
        assertTrue(repeatedLike.likedByCurrentUser());
        assertFalse(service.listArticles("doctor-002").stream()
                .filter(article -> article.id().equals(original.id()))
                .findFirst().orElseThrow().likedByCurrentUser());
    }

    @Test
    void shouldCreateACategoryLevelAndArticleUsingTheSelectedTreePath() {
        KnowledgeArticleService service = new KnowledgeArticleService();
        service.createCategoryLevel(new CreateKnowledgeCategoryRequest(List.of("操作类", "影像类"), "CT"));

        KnowledgeArticleResponse created = service.createArticle(
                new CreateKnowledgeArticleRequest(List.of("操作类", "影像类", "CT"), "CT 调阅规范", "正文内容"),
                "doctor-001"
        );

        assertEquals(List.of("操作类", "影像类", "CT"), created.categoryPath());
        assertEquals("张医生", created.provider());
        assertTrue(service.getKnowledgeTree().getFirst().children().getFirst().children().stream()
                .anyMatch(node -> node.label().equals("CT") && node.articleCount() == 1));
    }
}
