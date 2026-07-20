export interface Doctor {
  id: string
  name: string
  title: string
  departmentId: string
  departmentName: string
}

export interface Patient {
  id: string
  name: string
  gender: string
  birthDate: string
  visitId: string
  attendingDoctorId: string
  visitDate: string
  status: string
  queueNumber: string
  appointmentStatus: string
}

export interface WorkbenchMetrics {
  todayVisits: number
  pendingReviews: number
  abnormalPatients: number
  aiSummariesGenerated: number
}

export interface WorkbenchData {
  doctor: Doctor
  patients: Patient[]
  metrics: WorkbenchMetrics
}
