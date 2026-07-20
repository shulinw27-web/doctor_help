package com.doctorhelp.knowledge.application;

import com.doctorhelp.knowledge.api.dto.CreateKnowledgeArticleRequest;
import com.doctorhelp.knowledge.api.dto.CreateKnowledgeCategoryRequest;
import com.doctorhelp.knowledge.api.dto.KnowledgeArticleResponse;
import com.doctorhelp.knowledge.api.dto.KnowledgeTreeNode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KnowledgeArticleService {

    private final CopyOnWriteArrayList<KnowledgeArticleResponse> articles = new CopyOnWriteArrayList<>(createSeedArticles());
    private final CopyOnWriteArrayList<KnowledgeTreeNode> categoryTree = new CopyOnWriteArrayList<>(List.of(
            new KnowledgeTreeNode("operation", "操作类", 0, List.of(new KnowledgeTreeNode("operation-image", "影像类", 0, List.of(new KnowledgeTreeNode("operation-image-xray", "X 光", 0, List.of()))))),
            new KnowledgeTreeNode("indicator", "指标类", 0, List.of(new KnowledgeTreeNode("indicator-blood", "血液指标", 0, List.of()))),
            new KnowledgeTreeNode("general", "通用类", 0, List.of())
    ));
    private final Map<String, Set<String>> likesByArticleId = new ConcurrentHashMap<>();
    private static final Map<String, String> PROVIDERS = Map.of(
            "doctor-001", "张医生",
            "admin-001", "系统管理员"
    );

    public List<KnowledgeArticleResponse> listArticles(String userId) {
        return listArticles(userId, null);
    }

    public List<KnowledgeArticleResponse> listArticles(String userId, String titleKeyword) {
        String normalizedKeyword = titleKeyword == null ? "" : titleKeyword.trim().toLowerCase();
        return articles.stream()
                .filter(article -> normalizedKeyword.isEmpty() || article.title().toLowerCase().contains(normalizedKeyword))
                .map(article -> withLikedState(article, userId))
                .toList();
    }

    public KnowledgeArticleResponse getArticle(String id) {
        return articles.stream().filter(article -> article.id().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到知识条目"));
    }

    public KnowledgeArticleResponse likeArticle(String id, String userId) {
        KnowledgeArticleResponse article = getArticle(id);
        Set<String> userIds = likesByArticleId.computeIfAbsent(id, ignored -> ConcurrentHashMap.newKeySet());
        if (userIds.add(userId)) {
            KnowledgeArticleResponse liked = copyOf(article, article.likes() + 1, false);
            articles.set(articles.indexOf(article), liked);
            article = liked;
        }
        return withLikedState(article, userId);
    }

    public List<KnowledgeTreeNode> getKnowledgeTree() {
        return categoryTree.stream().map(node -> withArticleCounts(node, List.of())).toList();
    }

    public KnowledgeStatistics getStatistics() {
        LocalDate startOfWeek = LocalDate.now().minusDays(6);
        int weeklyAdded = (int) articles.stream()
                .map(KnowledgeArticleResponse::updatedAt)
                .map(LocalDate::parse)
                .filter(updatedAt -> !updatedAt.isBefore(startOfWeek))
                .count();
        return new KnowledgeStatistics(articles.size(), weeklyAdded, countCategories(categoryTree));
    }

    public synchronized KnowledgeTreeNode createCategoryLevel(CreateKnowledgeCategoryRequest request) {
        List<String> parentPath = request.parentPath() == null ? List.of() : request.parentPath();
        String label = request.label().trim();
        if (findNode(categoryTree, parentPath) == null && !parentPath.isEmpty()) {
            throw new IllegalArgumentException("未找到所选父级分类");
        }
        if (hasChild(categoryTree, parentPath, label)) {
            throw new IllegalArgumentException("该层级下已存在同名分类");
        }
        KnowledgeTreeNode created = new KnowledgeTreeNode("category-" + UUID.randomUUID(), label, 0, List.of());
        replaceBranch(categoryTree, parentPath, created);
        return created;
    }

    public synchronized KnowledgeArticleResponse createArticle(CreateKnowledgeArticleRequest request, String userId) {
        List<String> categoryPath = request.categoryPath().stream().map(String::trim).toList();
        if (findNode(categoryTree, categoryPath) == null) {
            throw new IllegalArgumentException("请选择知识分类树中的有效层级");
        }
        KnowledgeArticleResponse created = article(
                "knowledge-" + UUID.randomUUID(), categoryPath, request.title().trim(), request.content().trim(),
                PROVIDERS.getOrDefault(userId, "当前用户"), 0, "医生手动新增（演示）"
        );
        articles.add(created);
        return withLikedState(created, userId);
    }

    private static KnowledgeArticleResponse article(String id, List<String> categoryPath, String title, String content, String provider, int likes, String source) {
        return new KnowledgeArticleResponse(id, List.copyOf(categoryPath), title, content, provider, likes, source, LocalDate.now().toString(), false);
    }

    private static List<KnowledgeArticleResponse> createSeedArticles() {
        List<KnowledgeArticleResponse> seeded = new ArrayList<>(List.of(
                article("knowledge-001", List.of("指标类", "血液指标"), "白细胞计数（WBC）", "白细胞计数结果应结合实验室参考区间、患者主诉、体征和其他检查结果综合判断。", "检验科王医生", 24, "演示知识库：检验项目说明"),
                article("knowledge-002", List.of("指标类", "血液指标"), "C 反应蛋白（CRP）", "单项 CRP 结果不用于疾病诊断；需要结合动态变化与临床资料分析。", "呼吸内科李医生", 18, "演示知识库：检验项目说明"),
                article("knowledge-003", List.of("操作类", "影像类", "X 光"), "影像资料调阅原则", "在医生帮中查看内容后，应通过 PACS 调阅 DICOM 原片；系统不替代影像科判读。", "影像科陈医生", 31, "演示知识库：影像流程说明"),
                article("knowledge-004", List.of("通用类"), "门诊检测资料核对流程", "建议按患者身份、检验时间、异常项目、影像报告、原始证据的顺序进行核对。", "门诊部张医生", 12, "演示知识库：门诊工作流程")
        ));
        List<List<String>> paths = List.of(List.of("指标类", "血液指标"), List.of("操作类", "影像类", "X 光"), List.of("通用类"));
        String[] providers = {"检验科王医生", "影像科陈医生", "门诊部张医生", "呼吸内科李医生"};
        for (int index = 5; index <= 120; index++) {
            List<String> path = paths.get(index % paths.size());
            seeded.add(article(
                    "knowledge-" + String.format("%03d", index), path, "模拟知识条目 " + String.format("%03d", index),
                    "这是用于演示层级检索、知识沉淀和院内审核流程的模拟知识内容。实际使用时应由对应专科专家确认并保留版本记录。",
                    providers[index % providers.length], index % 36, "演示批量知识库数据"
            ));
        }
        return List.copyOf(seeded);
    }

    private KnowledgeArticleResponse withLikedState(KnowledgeArticleResponse article, String userId) {
        boolean liked = userId != null && likesByArticleId.getOrDefault(article.id(), Set.of()).contains(userId);
        return copyOf(article, article.likes(), liked);
    }

    private static KnowledgeArticleResponse copyOf(KnowledgeArticleResponse article, int likes, boolean likedByCurrentUser) {
        return new KnowledgeArticleResponse(article.id(), article.categoryPath(), article.title(), article.content(), article.provider(), likes, article.source(), article.updatedAt(), likedByCurrentUser);
    }

    private KnowledgeTreeNode withArticleCounts(KnowledgeTreeNode node, List<String> ancestors) {
        List<String> path = new ArrayList<>(ancestors);
        path.add(node.label());
        int count = (int) articles.stream().filter(article -> startsWith(article.categoryPath(), path)).count();
        return new KnowledgeTreeNode(node.id(), node.label(), count, node.children().stream().map(child -> withArticleCounts(child, path)).toList());
    }

    private static boolean startsWith(List<String> path, List<String> prefix) {
        return path.size() >= prefix.size() && path.subList(0, prefix.size()).equals(prefix);
    }

    private static int countCategories(List<KnowledgeTreeNode> nodes) {
        return nodes.stream().mapToInt(node -> 1 + countCategories(node.children())).sum();
    }

    private static KnowledgeTreeNode findNode(List<KnowledgeTreeNode> nodes, List<String> path) {
        if (path.isEmpty()) return null;
        KnowledgeTreeNode current = null;
        List<KnowledgeTreeNode> currentLevel = nodes;
        for (String segment : path) {
            current = currentLevel.stream().filter(node -> node.label().equals(segment)).findFirst().orElse(null);
            if (current == null) return null;
            currentLevel = current.children();
        }
        return current;
    }

    private static boolean hasChild(List<KnowledgeTreeNode> nodes, List<String> parentPath, String label) {
        if (parentPath.isEmpty()) return nodes.stream().anyMatch(node -> node.label().equals(label));
        KnowledgeTreeNode parent = findNode(nodes, parentPath);
        return parent != null && parent.children().stream().anyMatch(node -> node.label().equals(label));
    }

    private static void replaceBranch(CopyOnWriteArrayList<KnowledgeTreeNode> nodes, List<String> parentPath, KnowledgeTreeNode child) {
        if (parentPath.isEmpty()) {
            nodes.add(child);
            return;
        }
        KnowledgeTreeNode root = nodes.stream().filter(node -> node.label().equals(parentPath.getFirst())).findFirst().orElseThrow();
        KnowledgeTreeNode updated = addChild(root, parentPath, 1, child);
        nodes.set(nodes.indexOf(root), updated);
    }

    private static KnowledgeTreeNode addChild(KnowledgeTreeNode node, List<String> path, int index, KnowledgeTreeNode child) {
        if (index == path.size()) {
            List<KnowledgeTreeNode> children = new ArrayList<>(node.children());
            children.add(child);
            return new KnowledgeTreeNode(node.id(), node.label(), node.articleCount(), children);
        }
        KnowledgeTreeNode next = node.children().stream().filter(candidate -> candidate.label().equals(path.get(index))).findFirst().orElseThrow();
        List<KnowledgeTreeNode> children = new ArrayList<>(node.children());
        children.set(children.indexOf(next), addChild(next, path, index + 1, child));
        return new KnowledgeTreeNode(node.id(), node.label(), node.articleCount(), children);
    }

    public record KnowledgeStatistics(int totalArticles, int weeklyAdded, int totalCategories) {
    }
}
