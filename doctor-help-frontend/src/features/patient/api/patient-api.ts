import { http } from '../../../shared/api/http'
import type { PatientDetail } from '../../../shared/types/patient'

interface ApiResponse<T> {
  success: boolean
  data: T
  message: string
}

export async function fetchPatientDetail(patientId: string): Promise<PatientDetail> {
  const response = await http.get<ApiResponse<PatientDetail>>(`/patients/${patientId}`)
  return response.data.data
}

