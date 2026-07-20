import { http } from '../../../shared/api/http'
import type { WorkbenchData } from '../../../shared/types/workbench'

interface ApiResponse<T> {
  success: boolean
  data: T
  message: string
}

export async function fetchWorkbench(): Promise<WorkbenchData> {
  const response = await http.get<ApiResponse<WorkbenchData>>('/doctor-workbench')
  return response.data.data
}

