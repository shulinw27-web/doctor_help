<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import MetricCard from './components/MetricCard.vue'
import { fetchWorkbench } from './api/workbench-api'
import type { WorkbenchData } from '../../shared/types/workbench'

const loading = ref(true)
const workbench = ref<WorkbenchData>()
const keyword = ref('')
const quickPrompt = ref('')
const currentPage = ref(1)
const pageSize = 12
const patients = computed(() => (workbench.value?.patients ?? []).filter((patient) => patient.name.includes(keyword.value.trim())))
const pagedPatients = computed(() => patients.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize))
const aiTasks = computed(() => (workbench.value?.patients ?? [])
  .filter((patient) => patient.status === '存在异常' || patient.status === '待查看')
  .slice(0, 4)
  .map((patient) => ({ patient, label: patient.status === '存在异常' ? '异常指标待核对' : '资料待汇总', type: patient.status === '存在异常' ? 'warning' : 'info' })))
const emit = defineEmits<{ viewPatient: [patientId: string] }>()

async function loadWorkbench() {
  loading.value = true
  try {
    workbench.value = await fetchWorkbench()
  } catch {
    ElMessage.error('无法加载演示数据，请确认后端服务已启动。')
  } finally {
    loading.value = false
  }
}

function openQuickAnalysis() {
  const value = quickPrompt.value.trim()
  if (!value) { ElMessage.info('请输入患者姓名或叫号。'); return }
  const patient = (workbench.value?.patients ?? []).find((item) => item.name.includes(value) || item.queueNumber.toLowerCase() === value.toLowerCase())
  if (!patient) { ElMessage.warning('未找到对应患者，请检查姓名或叫号。'); return }
  emit('viewPatient', patient.id)
}

onMounted(loadWorkbench)
</script>

<template>
  <main v-loading="loading" class="workbench-page">
    <template v-if="workbench">
      <header class="page-header">
        <div>
          <p class="eyebrow">门诊检测资料智能汇总</p>
          <h1>{{ workbench.doctor.name }}，您好</h1>
          <p class="subtitle">{{ workbench.doctor.departmentName }} · {{ workbench.doctor.title }}</p>
        </div>
        <el-tag type="warning" effect="light">模拟数据 · 非临床用途</el-tag>
      </header>

      <section class="metrics" aria-label="工作台统计">
        <MetricCard label="今日接诊" :value="workbench.metrics.todayVisits" accent="#2f80ed" />
        <MetricCard label="待查看资料" :value="workbench.metrics.pendingReviews" accent="#f2994a" />
        <MetricCard label="存在异常" :value="workbench.metrics.abnormalPatients" accent="#eb5757" />
        <MetricCard label="AI 汇总次数" :value="workbench.metrics.aiSummariesGenerated" accent="#27ae60" />
      </section>

      <section class="ai-command-center">
        <div class="ai-core"><div class="ai-orb"><span>AI</span><i></i><i></i></div><div class="ai-copy"><p class="ai-kicker"><span class="live-dot"></span>医生帮 · AI 协作中枢已就绪</p><h2>让医疗数据先为医生<br />提炼重点</h2><p>本地 AI 正在汇聚检验、影像与历史资料，输出可追溯的重点提示与证据索引。</p></div></div>
        <div class="ai-insight"><div class="insight-title"><span>AI 实时洞察</span><em>演示规则引擎</em></div><strong>{{ aiTasks.length }} 名患者需要优先关注</strong><div class="insight-metrics"><span><b>3</b>异常指标组</span><span><b>2</b>影像资料待核查</span><span><b>1</b>趋势变化提醒</span></div><div class="ai-flow"><span class="active">资料汇聚</span><i></i><span class="active">异常定位</span><i></i><span>证据索引</span></div></div>
        <div class="ai-prompt"><span class="prompt-icon">✦</span><el-input v-model="quickPrompt" placeholder="输入患者姓名或叫号，立即唤起 AI 分析" @keyup.enter="openQuickAnalysis" /><el-button type="primary" @click="openQuickAnalysis">唤起 AI 助手</el-button></div>
        <div class="ai-task-grid"><button v-for="task in aiTasks" :key="task.patient.id" class="ai-task" type="button" @click="emit('viewPatient', task.patient.id)"><el-tag :type="task.type" effect="light">{{ task.label }}</el-tag><strong>{{ task.patient.name }}</strong><span>{{ task.patient.queueNumber }} · {{ task.patient.visitDate }}</span><em>打开 AI 分析 →</em></button></div>
      </section>

      <section class="patient-panel">
        <div class="panel-heading">
          <div>
            <h2>待处理患者</h2>
            <p>患者与责任医生关系在真实环境中将由 HIS/挂号系统只读同步。</p>
          </div>
          <div class="panel-actions"><el-input v-model="keyword" placeholder="按患者姓名搜索" clearable @input="currentPage = 1" /><el-button type="primary" @click="loadWorkbench">刷新数据</el-button></div>
        </div>

        <el-table :data="pagedPatients" stripe>
          <el-table-column prop="name" label="患者" min-width="100" />
          <el-table-column prop="queueNumber" label="叫号" width="82" />
          <el-table-column prop="gender" label="性别" width="72" />
          <el-table-column prop="birthDate" label="出生日期" min-width="120" />
          <el-table-column prop="visitDate" label="本次就诊" min-width="150" />
          <el-table-column label="预约状态" min-width="100"><template #default="scope"><el-tag :type="scope.row.appointmentStatus === '已就诊' ? 'success' : 'warning'" effect="light">{{ scope.row.appointmentStatus }}</el-tag></template></el-table-column>
          <el-table-column label="资料状态" min-width="110">
            <template #default="scope">
              <el-tag :type="scope.row.status === '存在异常' ? 'danger' : 'info'" effect="light">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="AI 状态" min-width="138"><template #default="scope"><el-tag :type="scope.row.status === '存在异常' ? 'warning' : scope.row.status === '已汇总' ? 'success' : 'info'" effect="light">{{ scope.row.status === '存在异常' ? '需 AI 提示核对' : scope.row.status === '已汇总' ? '已生成资料汇总' : '等待 AI 汇总' }}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button link type="primary" @click="emit('viewPatient', scope.row.id)">查看资料</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-if="patients.length > pageSize" v-model:current-page="currentPage" :page-size="pageSize" :total="patients.length" layout="total, prev, pager, next" class="pagination" />
      </section>
    </template>
  </main>
</template>

<style scoped>
.workbench-page {
  padding: 28px 32px;
}

.page-header,
.panel-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2f80ed;
  font-weight: 600;
}

h1,
h2 {
  margin: 0;
  color: #1d2939;
}

h1 { font-size: 28px; }
h2 { font-size: 18px; }

.subtitle,
.panel-heading p {
  margin: 8px 0 0;
  color: #667085;
  font-size: 14px;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin: 28px 0;
}

.patient-panel {
  padding: 22px;
  background: #fff;
  border: 1px solid #e7edf5;
  border-radius: 12px;
}
.ai-command-center { display:grid; grid-template-columns:minmax(0,1.35fr) minmax(280px,.9fr); gap:22px; padding:26px; margin-bottom:16px; overflow:hidden; border:1px solid #153e71; border-radius:16px; background:radial-gradient(circle at 12% 10%,#2777c7 0,transparent 32%),linear-gradient(135deg,#082541,#103f70 70%,#145e96); color:#fff; box-shadow:0 12px 30px rgb(7 43 78 / 20%); }.ai-core{display:flex;gap:18px;align-items:flex-start}.ai-orb{position:relative;display:grid;width:58px;height:58px;flex:0 0 auto;place-items:center;border:1px solid rgb(180 225 255 / 55%);border-radius:50%;background:radial-gradient(circle at 38% 32%,#b8edff,#3e94e8 42%,#155497 78%);box-shadow:0 0 0 8px rgb(130 210 255 / 10%),0 0 28px rgb(122 205 255 / 55%);font-weight:800;letter-spacing:.5px}.ai-orb i{position:absolute;width:72px;height:72px;border:1px solid rgb(160 226 255 / 32%);border-radius:50%;animation:orbit 4s linear infinite}.ai-orb i:last-child{width:86px;height:86px;animation-direction:reverse;animation-duration:6s}.ai-copy{min-width:0}.ai-kicker{display:flex;gap:8px;align-items:center;margin:2px 0 9px;color:#c7e7ff;font-size:12px;font-weight:600;letter-spacing:.3px}.live-dot{width:7px;height:7px;border-radius:50%;background:#70f0b4;box-shadow:0 0 12px #70f0b4}.ai-copy h2{color:#fff;font-size:27px;line-height:1.23}.ai-copy>p:last-child{max-width:520px;margin:10px 0 0;color:#cfe5f8;font-size:14px;line-height:1.7}.ai-insight{padding:17px;border:1px solid rgb(192 231 255 / 24%);border-radius:12px;background:rgb(3 22 43 / 30%)}.insight-title{display:flex;justify-content:space-between;color:#c7e7ff;font-size:12px}.insight-title em{font-style:normal;color:#8bc8fb}.ai-insight>strong{display:block;margin:12px 0;color:#fff;font-size:18px}.insight-metrics{display:grid;grid-template-columns:repeat(3,1fr);gap:6px}.insight-metrics span{color:#c9e2f7;font-size:11px;line-height:1.35}.insight-metrics b{display:block;color:#fff;font-size:20px}.ai-flow{display:flex;align-items:center;gap:6px;margin-top:16px;color:#9fc9e7;font-size:11px}.ai-flow span{white-space:nowrap}.ai-flow .active{color:#74e6ba}.ai-flow i{width:18px;height:1px;background:#5b89ad}.ai-prompt{grid-column:1/-1;display:flex;gap:10px;align-items:center;padding:6px 6px 6px 15px;border:1px solid rgb(188 230 255 / 28%);border-radius:10px;background:rgb(255 255 255 / 12%)}.prompt-icon{color:#85d6ff;font-size:20px}.ai-prompt :deep(.el-input__wrapper){background:transparent!important;box-shadow:none!important}.ai-prompt :deep(.el-input__inner){color:#fff}.ai-prompt :deep(.el-input__inner::placeholder){color:#b8d9ef}.ai-prompt :deep(.el-button){height:38px;background:#4db1ff;border-color:#4db1ff}.ai-task-grid { grid-column:1/-1; display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 12px; }.ai-task { display: flex; min-width: 0; flex-direction: column; align-items: flex-start; gap: 7px; padding: 14px; cursor: pointer; border: 1px solid rgb(191 229 255 / 20%); border-radius: 10px; background: rgb(255 255 255 / 10%); text-align: left; }.ai-task:hover { background:rgb(255 255 255 / 18%); box-shadow:0 4px 16px rgb(0 0 0 / 14%); }.ai-task strong { color: #fff; }.ai-task span, .ai-task em { color: #d0e5f5; font-size: 12px; font-style: normal; }.ai-task em { color: #80cdfd; }@keyframes orbit{to{transform:rotate(360deg)}}

.patient-panel :deep(.el-table) { margin-top: 20px; }
.pagination { justify-content: flex-end; margin-top: 18px; }
.panel-actions { display:flex; gap:10px; }.panel-actions :deep(.el-input){width:200px}

@media (max-width: 900px) {
  .metrics { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .ai-command-center{grid-template-columns:1fr}.ai-task-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .ai-command-header, .ai-prompt { flex-direction: column; }.ai-prompt :deep(.el-input), .ai-prompt :deep(.el-button) { width: 100%; }
}
</style>
