<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchManagementOverview, type ManagementOverview } from './api/management-api'

const overview = ref<ManagementOverview>()
const loadError = ref(false)

function caseBarHeight(cases: number) {
  const maximum = Math.max(...(overview.value?.dailyCaseTrend.map((item) => item.cases) ?? [1]))
  return `${Math.max(18, Math.round((cases / maximum) * 100))}%`
}

async function loadOverview() {
  loadError.value = false
  try {
    overview.value = await fetchManagementOverview()
  } catch {
    loadError.value = true
    ElMessage.error('管理驾驶舱数据加载失败，请重试或重新登录。')
  }
}

onMounted(loadOverview)
</script>

<template>
  <main v-loading="!overview && !loadError" class="page"><template v-if="overview"><header><p class="eyebrow">管理驾驶舱</p><h1>演示运行概览</h1><p>{{ overview.note }}</p></header>
    <section class="metric-grid"><article v-for="metric in overview.metrics" :key="metric.label" class="metric-card"><p>{{ metric.label }}</p><strong>{{ metric.value }}</strong><span>{{ metric.description }}</span></article></section>
    <section class="insight-grid">
      <article class="knowledge-panel"><div><p class="panel-label">知识库运营</p><h2>院内经验沉淀</h2></div><div class="knowledge-metrics"><div><strong>{{ overview.knowledgeOverview.totalArticles }}</strong><span>知识总量</span></div><div><strong>+{{ overview.knowledgeOverview.weeklyAdded }}</strong><span>本周新增</span></div><div><strong>{{ overview.knowledgeOverview.totalCategories }}</strong><span>分类层级</span></div></div><p>新增知识将在当前演示服务周期内实时计入统计。</p></article>
      <article class="trend-panel"><div><p class="panel-label">门诊工作量</p><h2>每日病例增量</h2></div><div class="bar-chart" aria-label="每日病例增量"><div v-for="item in overview.dailyCaseTrend" :key="item.date" class="bar-item"><span class="bar-value">{{ item.cases }}</span><span class="bar" :style="{ height: caseBarHeight(item.cases) }"></span><span class="bar-label">{{ item.date }}</span></div></div></article>
    </section>
    <section class="table-panel"><h2>系统接入状态</h2><el-table :data="overview.integrationStatuses" stripe><el-table-column prop="system" label="系统" min-width="170"/><el-table-column prop="scope" label="同步范围" min-width="180"/><el-table-column label="状态" width="120"><template #default="scope"><el-tag type="info" effect="light">{{ scope.row.status }}</el-tag></template></el-table-column><el-table-column prop="updatedAt" label="最近更新时间" min-width="160"/></el-table></section>
  </template><section v-else-if="loadError" class="error-panel"><h2>暂时无法加载管理驾驶舱</h2><p>请检查登录状态或后端服务，然后重试。</p><el-button type="primary" @click="loadOverview">重新加载</el-button></section></main>
</template>

<style scoped>
.page{padding:28px 32px}.eyebrow,.panel-label{margin:0 0 8px;color:#2f80ed;font-weight:600}h1,h2{margin:0;color:#1d2939}h1{font-size:28px}h2{font-size:18px}header>p:not(.eyebrow){margin:8px 0 0;color:#667085;font-size:14px}.metric-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:16px;margin:28px 0}.metric-card,.table-panel,.error-panel,.knowledge-panel,.trend-panel{background:#fff;border:1px solid #e7edf5;border-radius:12px}.metric-card{padding:20px}.metric-card p{margin:0;color:#667085;font-size:14px}.metric-card strong{display:block;margin:12px 0 8px;color:#1d2939;font-size:27px}.metric-card span{color:#98a2b3;font-size:12px;line-height:1.5}.insight-grid{display:grid;grid-template-columns:1fr 1.35fr;gap:16px;margin-bottom:16px}.knowledge-panel,.trend-panel{padding:20px}.knowledge-metrics{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin:24px 0}.knowledge-metrics div{padding:12px;border-radius:8px;background:#f6f8fb}.knowledge-metrics strong{display:block;color:#1d2939;font-size:24px}.knowledge-metrics span,.knowledge-panel>p{color:#667085;font-size:12px}.knowledge-panel>p{margin:0}.bar-chart{display:flex;height:150px;gap:12px;align-items:flex-end;margin-top:18px;padding-top:14px;border-bottom:1px solid #e7edf5}.bar-item{display:flex;flex:1;height:100%;flex-direction:column;align-items:center;justify-content:flex-end;gap:5px;min-width:0}.bar{display:block;width:100%;max-width:34px;border-radius:6px 6px 0 0;background:linear-gradient(180deg,#69afff,#2f80ed)}.bar-value{color:#475467;font-size:12px}.bar-label{color:#98a2b3;font-size:11px;white-space:nowrap}.table-panel{padding:20px}.table-panel :deep(.el-table){margin-top:18px}.error-panel{max-width:520px;margin:80px auto;padding:28px;text-align:center}.error-panel p{margin:12px 0 20px;color:#667085}@media(max-width:1000px){.metric-grid{grid-template-columns:repeat(2,minmax(0,1fr))}.insight-grid{grid-template-columns:1fr}}
</style>
