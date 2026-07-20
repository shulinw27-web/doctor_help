import { http } from '../../../shared/api/http'

export type UserRole = 'DOCTOR' | 'LEADER' | 'ADMIN'

export interface OaProfile {
  oaUserId: string
  oaDepartmentCode: string
  oaPositionName: string
  oaAccountStatus: string
  lastSyncedAt: string
}

export interface SystemUser {
  id: string
  username: string
  name: string
  role: UserRole
  departmentName: string
  title: string
  employeeNo: string
  gender: string
  mobile: string
  email: string
  employmentStatus: string
  joinedAt: string
  oaProfile: OaProfile
}

export interface CreateSystemUserPayload {
  username: string
  name: string
  gender: string
  role: UserRole
  departmentName: string
  title: string
  mobile: string
  email: string
}

export interface OaSyncResult { addedCount: number; updatedCount: number; totalCount: number; syncedAt: string }

interface ApiResponse<T> { success: boolean; data: T; message: string }

export async function fetchUsers(): Promise<SystemUser[]> { return (await http.get<ApiResponse<SystemUser[]>>('/users')).data.data }
export async function updateUserRole(id: string, role: UserRole): Promise<SystemUser> { return (await http.patch<ApiResponse<SystemUser>>(`/users/${id}/role`, { role })).data.data }
export async function createUser(payload: CreateSystemUserPayload): Promise<SystemUser> { return (await http.post<ApiResponse<SystemUser>>('/users', payload)).data.data }
export async function syncUsersFromOa(): Promise<OaSyncResult> { return (await http.post<ApiResponse<OaSyncResult>>('/users/sync-oa')).data.data }
