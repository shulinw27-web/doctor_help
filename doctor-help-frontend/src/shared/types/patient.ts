import type { Patient } from './workbench'

export type ResultStatus = 'NORMAL' | 'HIGH' | 'LOW'

export interface ExaminationResult {
  code: string
  category: string
  name: string
  result: string
  unit: string
  referenceRange: string
  status: ResultStatus
  measuredAt: string
}

export interface TrendPoint {
  measuredAt: string
  value: number
}

export interface ImagingRecord {
  id: string
  modality: string
  bodyPart: string
  examinationAt: string
  reportSummary: string
  reportStatus: string
}
export interface VisitHistory { visitDate: string; departmentName: string; visitType: string; summary: string }

export interface PatientDetail {
  patient: Patient
  examinationResults: ExaminationResult[]
  whiteBloodCellTrend: TrendPoint[]
  imagingRecords: ImagingRecord[]
  visitHistories: VisitHistory[]
  clinicalNotice: string
}
