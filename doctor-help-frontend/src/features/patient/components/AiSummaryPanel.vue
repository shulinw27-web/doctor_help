<script setup lang="ts">
import type { AiSummary } from '../../../shared/types/ai-summary'

defineProps<{ summary: AiSummary }>()
</script>

<template>
  <section class="ai-summary-panel">
    <div class="source"><span>生成方式</span><el-tag type="info" effect="plain">{{ summary.generationSource }}</el-tag></div>
    <p class="overview">{{ summary.overview }}</p>
    <div class="summary-grid">
      <article><h3>异常项目</h3><ul><li v-for="finding in summary.abnormalFindings" :key="finding.name"><strong>{{ finding.name }}</strong><span>{{ finding.result }}（{{ finding.status }}，参考 {{ finding.referenceRange }}）</span></li><li v-if="summary.abnormalFindings.length === 0">未发现演示参考区间外项目。</li></ul></article>
      <article><h3>趋势与关注项</h3><p>{{ summary.trendSummary }}</p><p class="attention">{{ summary.attentionItem }}</p></article>
    </div>
    <div class="evidence"><strong>资料依据</strong><span v-for="source in summary.evidenceSources" :key="source">{{ source }}</span></div>
    <p class="disclaimer">{{ summary.disclaimer }}</p>
  </section>
</template>

<style scoped>
.ai-summary-panel { padding: 18px; background: #f6faff; border: 1px solid #cfe4ff; border-radius: 10px; }
.source { display: flex; align-items: center; gap: 10px; color: #667085; font-size: 13px; }
.overview { margin: 14px 0; color: #344054; line-height: 1.6; }
.summary-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.summary-grid article { padding: 14px; background: #fff; border-radius: 8px; }
h3 { margin: 0 0 10px; color: #1d2939; font-size: 14px; }
ul { padding-left: 18px; margin: 0; color: #475467; font-size: 13px; line-height: 1.65; }
li span { display: block; }
.summary-grid p { margin: 0; color: #475467; font-size: 13px; line-height: 1.65; }
.attention { margin-top: 10px !important; color: #9a6700 !important; }
.evidence { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin-top: 14px; color: #475467; font-size: 12px; }
.evidence span { padding: 4px 8px; background: #eaf3ff; border-radius: 999px; }
.disclaimer { margin: 14px 0 0; color: #c93d3d; font-size: 12px; line-height: 1.55; }
@media (max-width: 960px) { .summary-grid { grid-template-columns: 1fr; } }
</style>

