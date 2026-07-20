import { http } from '../../../shared/api/http'

export interface CurrentUser { id: string; username: string; name: string; role: 'DOCTOR' | 'LEADER' | 'ADMIN'; departmentName: string; title: string }
export interface LoginResponse { accessToken: string; user: CurrentUser }
interface ApiResponse<T> { success: boolean; data: T; message: string }

export async function login(username: string, password: string): Promise<LoginResponse> {
  const response = await http.post<ApiResponse<LoginResponse>>('/auth/login', { username, password })
  return response.data.data
}
