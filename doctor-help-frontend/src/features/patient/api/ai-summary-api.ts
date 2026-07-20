import { http } from '../../../shared/api/http'
import type { AiSummary } from '../../../shared/types/ai-summary'

interface ApiResponse<T> {
  success: boolean
  data: T
  message: string
}

export async function fetchAiSummary(patientId: string): Promise<AiSummary> {
  const response = await http.get<ApiResponse<AiSummary>>(`/patients/${patientId}/ai-summary`)
  return response.data.data
}

