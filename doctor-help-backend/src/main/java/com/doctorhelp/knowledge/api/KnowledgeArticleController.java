package com.doctorhelp.knowledge.api;

import com.doctorhelp.auth.api.dto.LoginResponse;
import com.doctorhelp.auth.infrastructure.AuthInterceptor;
import com.doctorhelp.common.api.ApiResponse;
import com.doctorhelp.knowledge.api.dto.CreateKnowledgeArticleRequest;
import com.doctorhelp.knowledge.api.dto.CreateKnowledgeCategoryRequest;
import com.doctorhelp.knowledge.api.dto.KnowledgeArticleResponse;
import com.doctorhelp.knowledge.application.KnowledgeArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge/articles")
public class KnowledgeArticleController {
    private final KnowledgeArticleService knowledgeArticleService;
    public KnowledgeArticleController(KnowledgeArticleService knowledgeArticleService) { this.knowledgeArticleService = knowledgeArticleService; }
    @GetMapping public ApiResponse<List<KnowledgeArticleResponse>> listArticles(@RequestParam(required = false) String title, HttpServletRequest request) { return ApiResponse.success(knowledgeArticleService.listArticles(currentUser(request).id(), title)); }
    @GetMapping("/{id}") public ApiResponse<KnowledgeArticleResponse> getArticle(@PathVariable String id) { return ApiResponse.success(knowledgeArticleService.getArticle(id)); }
    @PostMapping("/{id}/like") public ApiResponse<KnowledgeArticleResponse> likeArticle(@PathVariable String id, HttpServletRequest request) { return ApiResponse.success(knowledgeArticleService.likeArticle(id, currentUser(request).id())); }
    @GetMapping("/tree") public ApiResponse<List<com.doctorhelp.knowledge.api.dto.KnowledgeTreeNode>> getTree() { return ApiResponse.success(knowledgeArticleService.getKnowledgeTree()); }
    @PostMapping public ApiResponse<KnowledgeArticleResponse> createArticle(@Valid @RequestBody CreateKnowledgeArticleRequest request, HttpServletRequest servletRequest) { return ApiResponse.success(knowledgeArticleService.createArticle(request, currentUser(servletRequest).id())); }
    @PostMapping("/tree") public ApiResponse<com.doctorhelp.knowledge.api.dto.KnowledgeTreeNode> createCategoryLevel(@Valid @RequestBody CreateKnowledgeCategoryRequest request) { return ApiResponse.success(knowledgeArticleService.createCategoryLevel(request)); }
    private LoginResponse.UserProfile currentUser(HttpServletRequest request) { return (LoginResponse.UserProfile) request.getAttribute(AuthInterceptor.CURRENT_USER); }
}
