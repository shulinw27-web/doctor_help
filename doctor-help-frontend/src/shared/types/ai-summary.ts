export interface AiFinding {
  name: string
  result: string
  referenceRange: string
  status: string
}

export interface AiSummary {
  generationSource: string
  overview: string
  abnormalFindings: AiFinding[]
  trendSummary: string
  attentionItem: string
  evidenceSources: string[]
  disclaimer: string
}

