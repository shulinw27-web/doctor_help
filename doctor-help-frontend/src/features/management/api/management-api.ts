import { http } from '../../../shared/api/http'

export interface ManagementOverview {
  metrics: Metric[]
  knowledgeOverview: KnowledgeOverview
  dailyCaseTrend: DailyCaseTrend[]
  integrationStatuses: IntegrationStatus[]
  note: string
}
export interface Metric { label: string; value: string; description: string }
export interface IntegrationStatus { system: string; scope: string; status: string; updatedAt: string }
export interface KnowledgeOverview { totalArticles: number; weeklyAdded: number; totalCategories: number }
export interface DailyCaseTrend { date: string; cases: number }
interface ApiResponse<T> { success: boolean; data: T; message: string }

export async function fetchManagementOverview(): Promise<ManagementOverview> {
  const response = await http.get<ApiResponse<ManagementOverview>>('/management/overview')
  return response.data.data
}
