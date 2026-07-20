<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchPatientDetail } from './api/patient-api'
import { fetchAiSummary } from './api/ai-summary-api'
import AiSummaryPanel from './components/AiSummaryPanel.vue'
import ResultStatusTag from './components/ResultStatusTag.vue'
import TrendChart from './components/TrendChart.vue'
import type { PatientDetail } from '../../shared/types/patient'
import type { AiSummary } from '../../shared/types/ai-summary'

const props = defineProps<{ patientId: string }>()
const emit = defineEmits<{ back: [] }>()
const loading = ref(true)
const detail = ref<PatientDetail>()
const generatingSummary = ref(false)
const aiSummary = ref<AiSummary>()
const aiFocus = ref('综合检测资料')
const aiFeedback = ref('')
const trendDateRange = ref<string[]>([])
const abnormalCount = computed(() => detail.value?.examinationResults.filter((item) => item.status !== 'NORMAL').length ?? 0)
const filteredTrendPoints = computed(() => {
  const points = detail.value?.whiteBloodCellTrend ?? []
  if (trendDateRange.value.length !== 2) return points
  return points.filter((point) => point.measuredAt >= trendDateRange.value[0] && point.measuredAt <= trendDateRange.value[1])
})

async function loadPatientDetail() {
  loading.value = true
  try {
    detail.value = await fetchPatientDetail(props.patientId)
  } catch {
    ElMessage.error('无法加载患者模拟资料。')
  } finally {
    loading.value = false
  }
}

async function generateAiSummary() {
  generatingSummary.value = true
  try {
    aiSummary.value = await fetchAiSummary(props.patientId)
  } catch {
    ElMessage.error('无法生成演示汇总。')
  } finally {
    generatingSummary.value = false
  }
}

function recordAiFeedback(feedback: string) {
  aiFeedback.value = feedback
  ElMessage.success(feedback === 'helpful' ? '已记录“有帮助”反馈。' : '已记录待改进反馈，后续将用于规则与知识库优化。')
}

onMounted(loadPatientDetail)
watch(() => props.patientId, loadPatientDetail)
</script>

<template>
  <main v-loading="loading" class="patient-detail-page">
    <template v-if="detail">
      <div class="page-actions">
        <el-button text type="primary" @click="emit('back')">← 返回医生工作台</el-button>
        <el-tag type="warning" effect="light">模拟数据 · 非临床用途</el-tag>
      </div>

      <section class="patient-header card">
        <div>
          <p class="eyebrow">门诊患者资料</p>
          <h1>{{ detail.patient.name }}</h1>
          <p>{{ detail.patient.gender }} · {{ detail.patient.birthDate }} · 本次就诊 {{ detail.patient.visitDate }}</p>
        </div>
        <div class="summary-chip"><strong>{{ abnormalCount }}</strong><span>项异常指标</span></div>
      </section>

      <section class="card notice"><strong>资料使用提示</strong><span>{{ detail.clinicalNotice }}</span></section>

      <section class="patient-workspace">
      <div class="patient-data-column">
      <section class="content-grid">
        <article class="card">
          <div class="section-heading"><div><h2>检测指标</h2><p>异常状态根据演示参考区间自动计算。</p></div></div>
          <el-table :data="detail.examinationResults" stripe>
            <el-table-column prop="category" label="类别" min-width="88" />
            <el-table-column prop="name" label="项目" min-width="132" />
            <el-table-column label="结果" min-width="100"><template #default="scope"><strong :class="{ 'abnormal-result': scope.row.status !== 'NORMAL' }">{{ scope.row.result }} {{ scope.row.unit }}</strong></template></el-table-column>
            <el-table-column prop="referenceRange" label="参考区间" min-width="100" />
            <el-table-column label="状态" min-width="82"><template #default="scope"><ResultStatusTag :status="scope.row.status" /></template></el-table-column>
            <el-table-column prop="measuredAt" label="检测时间" min-width="144" />
          </el-table>
        </article>

        <article class="card trend-panel">
          <div class="section-heading"><div><h2>白细胞计数趋势</h2><p>可选择历史时间范围比较指标变化。</p></div><el-date-picker v-model="trendDateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" /></div>
          <TrendChart :points="filteredTrendPoints" />
        </article>
      </section>

      <section class="card imaging-panel">
        <div class="section-heading">
          <div><h2>影像资料</h2><p>演示版展示影像报告摘要；真实环境将通过 PACS/RIS 提供原始 DICOM 调阅入口。</p></div>
          <el-tag type="info" effect="light">不进行原始影像 AI 判读</el-tag>
        </div>
        <div class="imaging-list">
          <article v-for="record in detail.imagingRecords" :key="record.id" class="imaging-record">
            <div class="modality"><strong>{{ record.modality }}</strong><span>{{ record.bodyPart }}</span></div>
            <div class="imaging-report"><p>{{ record.reportSummary }}</p><span>{{ record.examinationAt }}</span></div>
            <el-tag :type="record.reportStatus === '需关注' ? 'warning' : 'success'" effect="light">{{ record.reportStatus }}</el-tag>
          </article>
        </div>
      </section>

      <section class="card history-panel">
        <div class="section-heading"><div><h2>历史就诊档案</h2><p>按时间查看患者历次门诊与已归档检测资料。</p></div></div>
        <el-timeline><el-timeline-item v-for="visit in detail.visitHistories" :key="visit.visitDate" :timestamp="visit.visitDate" placement="top"><strong>{{ visit.departmentName }} · {{ visit.visitType }}</strong><p>{{ visit.summary }}</p></el-timeline-item></el-timeline>
      </section>

      </div>
      <aside class="card ai-panel">
        <div class="section-heading">
          <div><p class="eyebrow">医生帮 AI 助手</p><h2>当前患者资料分析</h2><p>AI 提供重点提示与资料依据，需由医生确认。</p></div>
        </div>
        <div class="ai-status"><span><strong>{{ abnormalCount }}</strong> 项异常待核对</span><span><strong>{{ detail.imagingRecords.length }}</strong> 份影像资料</span></div>
        <el-select v-model="aiFocus" class="ai-focus"><el-option label="综合检测资料" value="综合检测资料" /><el-option label="优先关注异常指标" value="优先关注异常指标" /><el-option label="优先关注影像资料" value="优先关注影像资料" /></el-select>
        <el-button class="ai-generate" type="primary" :loading="generatingSummary" @click="generateAiSummary">生成 {{ aiFocus }} AI 分析</el-button>
        <AiSummaryPanel v-if="aiSummary" :summary="aiSummary" class="summary-result" />
        <el-empty v-else description="生成后可查看资料摘要、异常项目、趋势与证据来源。" :image-size="62" />
        <div class="ai-knowledge"><strong>AI 关联知识</strong><el-tag effect="plain">异常指标核对流程</el-tag><el-tag effect="plain">影像资料调阅原则</el-tag></div>
        <div v-if="aiSummary" class="ai-feedback"><span>此分析是否有帮助？</span><el-button size="small" :type="aiFeedback === 'helpful' ? 'primary' : 'default'" @click="recordAiFeedback('helpful')">有帮助</el-button><el-button size="small" :type="aiFeedback === 'inaccurate' ? 'warning' : 'default'" @click="recordAiFeedback('inaccurate')">需要改进</el-button></div>
      </aside>
      </section>
    </template>
  </main>
</template>

<style scoped>
.patient-detail-page { padding: 24px 32px; }
.page-actions, .patient-header, .section-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 18px; }
.card { background: #fff; border: 1px solid #e7edf5; border-radius: 12px; }
.patient-header { padding: 24px; margin-top: 12px; }
.eyebrow { margin: 0 0 8px; color: #2f80ed; font-weight: 600; }
h1, h2 { margin: 0; color: #1d2939; }
h1 { font-size: 28px; }
h2 { font-size: 18px; }
.patient-header p, .section-heading p { margin: 8px 0 0; color: #667085; font-size: 14px; }
.summary-chip { display: flex; flex-direction: column; align-items: center; min-width: 112px; padding: 12px 16px; background: #fff3f3; border-radius: 10px; color: #c93d3d; }
.summary-chip strong { font-size: 28px; }
.summary-chip span { margin-top: 3px; font-size: 12px; }
.notice { display: flex; flex-direction: column; gap: 8px; padding: 16px 20px; margin-top: 16px; color: #475467; line-height: 1.6; }
.notice strong { color: #1d2939; }
.patient-workspace { display: grid; grid-template-columns: minmax(0, 1fr) minmax(360px, .48fr); gap: 16px; align-items: start; margin-top: 16px; }.patient-data-column { min-width: 0; }.content-grid { display: grid; grid-template-columns: minmax(0, 1.5fr) minmax(320px, 1fr); gap: 16px; }
.content-grid > article { padding: 20px; }
.content-grid :deep(.el-table) { margin-top: 20px; }
.abnormal-result { color: #d14343; }
.imaging-panel { padding: 20px; margin-top: 16px; }
.history-panel { padding: 20px; margin-top: 16px; }.history-panel :deep(.el-timeline){margin:22px 0 0}.history-panel :deep(.el-timeline-item__content p){margin:6px 0;color:#667085}
.ai-panel { position: sticky; top: 16px; padding: 20px; border-color: #cfe4ff; background: linear-gradient(180deg, #f9fcff, #fff); }.ai-status { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin-top: 18px; }.ai-status span { padding: 10px; color: #475467; border-radius: 8px; background: #eef6ff; font-size: 12px; }.ai-status strong { color: #2f80ed; font-size: 18px; }.ai-focus, .ai-generate { width: 100%; margin-top: 14px; }.ai-knowledge { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin-top: 16px; color: #475467; font-size: 12px; }.ai-feedback { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin-top: 16px; padding-top: 14px; border-top: 1px solid #e7edf5; color: #667085; font-size: 12px; }
.summary-result { margin-top: 18px; }
.imaging-list { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; margin-top: 18px; }
.imaging-record { display: grid; grid-template-columns: 92px 1fr auto; gap: 14px; align-items: center; padding: 16px; background: #f8fafc; border: 1px solid #edf1f6; border-radius: 10px; }
.modality { display: flex; flex-direction: column; gap: 4px; color: #475467; font-size: 13px; }
.modality strong { color: #2f80ed; font-size: 20px; }
.imaging-report p { margin: 0; color: #344054; font-size: 14px; line-height: 1.55; }
.imaging-report span { display: block; margin-top: 6px; color: #98a2b3; font-size: 12px; }
@media (max-width: 960px) { .imaging-list { grid-template-columns: 1fr; } }
@media (max-width: 1200px) { .patient-workspace, .content-grid { grid-template-columns: 1fr; }.ai-panel { position: static; } }
</style>
