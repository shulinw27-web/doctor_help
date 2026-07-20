import { http } from '../../../shared/api/http'

export interface KnowledgeArticle {
  id: string
  categoryPath: string[]
  title: string
  content: string
  provider: string
  likes: number
  source: string
  updatedAt: string
  likedByCurrentUser: boolean
}
export interface KnowledgeTreeNode { id: string; label: string; articleCount: number; children: KnowledgeTreeNode[] }
export interface CreateKnowledgeArticlePayload { categoryPath: string[]; title: string; content: string }
export interface CreateKnowledgeCategoryPayload { parentPath: string[]; label: string }

interface ApiResponse<T> { success: boolean; data: T; message: string }

export async function fetchKnowledgeArticles(title?: string): Promise<KnowledgeArticle[]> {
  const response = await http.get<ApiResponse<KnowledgeArticle[]>>('/knowledge/articles', { params: title?.trim() ? { title: title.trim() } : undefined })
  return response.data.data
}

export async function likeKnowledgeArticle(id: string): Promise<KnowledgeArticle> {
  const response = await http.post<ApiResponse<KnowledgeArticle>>(`/knowledge/articles/${id}/like`)
  return response.data.data
}

export async function createKnowledgeArticle(article: CreateKnowledgeArticlePayload): Promise<KnowledgeArticle> {
  const response = await http.post<ApiResponse<KnowledgeArticle>>('/knowledge/articles', article)
  return response.data.data
}
export async function fetchKnowledgeTree(): Promise<KnowledgeTreeNode[]> { const response = await http.get<ApiResponse<KnowledgeTreeNode[]>>('/knowledge/articles/tree'); return response.data.data }
export async function createKnowledgeCategory(payload: CreateKnowledgeCategoryPayload): Promise<KnowledgeTreeNode> { const response = await http.post<ApiResponse<KnowledgeTreeNode>>('/knowledge/articles/tree', payload); return response.data.data }
