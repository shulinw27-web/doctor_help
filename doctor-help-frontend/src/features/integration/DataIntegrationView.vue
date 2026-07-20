<script setup lang="ts">
const sources = [
  { name: 'HIS / 挂号', scope: '患者主索引、预约、就诊上下文', mode: '只读同步', status: '已接入', coverage: '100%', freshness: '最近同步 10:30', level: 'ready' },
  { name: 'LIS / 检验', scope: '检验结果、参考区间、检测时间', mode: '只读同步', status: '已接入', coverage: '100%', freshness: '最近同步 10:28', level: 'ready' },
  { name: 'PACS / RIS', scope: '检查信息、影像报告、原片入口', mode: '报告已联动', status: '部分接入', coverage: '92%', freshness: '最近同步 10:25', level: 'partial' },
  { name: 'EMR / 病历', scope: '病历摘要、历史就诊索引', mode: '摘要联动', status: '试点范围', coverage: '68%', freshness: '最近同步 10:20', level: 'partial' },
  { name: '病理系统', scope: '病理报告与切片调阅入口', mode: '待确认接口', status: '待接入', coverage: '—', freshness: '尚未纳入本期试点', level: 'pending' },
  { name: 'OA / 统一身份', scope: '账号、科室、角色、单点登录', mode: '模拟联动', status: '演示模拟', coverage: '100%', freshness: '演示数据', level: 'demo' },
]

const integrationPrinciples = [
  ['只读优先', '首期不写回源系统，不改变业务主数据。'],
  ['按场景接入', '优先满足门诊资料预读所需的数据链路。'],
  ['统一视图', '在医生帮内统一展示，保留数据来源与更新时间。'],
  ['原系统为准', '叫号、开单、病历、医嘱等正式操作仍在原系统完成。'],
]
</script>

<template>
  <main class="integration-page">
    <section class="integration-hero">
      <div><p>DATA COLLABORATION LAYER</p><h1>院内数据协作与接入状态</h1><span>统一患者数据视图，不替代医院已有业务系统。</span></div>
      <div class="coverage-card"><strong>4 / 6</strong><span>演示数据源可用</span><small>当前患者资料完整度 86%</small></div>
    </section>

    <section class="flow-panel" aria-label="数据协作流程"><div>医院源系统<br /><strong>HIS · LIS · PACS · EMR</strong></div><i>→</i><div>医生帮连接层<br /><strong>适配 · 标准化 · 权限 · 审计</strong></div><i>→</i><div>AI 协作应用<br /><strong>预读 · 溯源 · 提示 · 统计</strong></div></section>

    <section class="source-section"><div class="section-heading"><div><p>SOURCE INVENTORY</p><h2>数据源接入清单</h2></div><small>演示环境状态 · 生产环境以医院接口授权为准</small></div><div class="source-grid"><article v-for="source in sources" :key="source.name" class="source-card" :class="source.level"><div class="source-title"><div><strong>{{ source.name }}</strong><span>{{ source.mode }}</span></div><b>{{ source.status }}</b></div><p>{{ source.scope }}</p><div class="source-meta"><span>覆盖度 <strong>{{ source.coverage }}</strong></span><span>{{ source.freshness }}</span></div></article></div></section>

    <section class="principle-section"><div class="section-heading"><div><p>IMPLEMENTATION BOUNDARY</p><h2>接入原则与边界</h2></div></div><div class="principle-grid"><article v-for="principle in integrationPrinciples" :key="principle[0]"><strong>{{ principle[0] }}</strong><p>{{ principle[1] }}</p></article></div></section>
  </main>
</template>

<style scoped>
.integration-page{min-height:100%;padding:30px clamp(24px,4vw,54px);background:#f6f8fb;color:#172b4d}.integration-hero{display:flex;align-items:center;justify-content:space-between;gap:28px;padding:28px 30px;border-radius:18px;background:radial-gradient(circle at 80% 25%,#2479c5 0,#123f6b 39%,#0b2949 100%);color:#fff}.integration-hero p,.section-heading p{margin:0;color:#69d7ff;font-size:11px;font-weight:700;letter-spacing:1.4px}.integration-hero h1{margin:9px 0;font-size:28px}.integration-hero>div>span{color:#c2e1f5;font-size:14px}.coverage-card{display:flex;min-width:168px;flex-direction:column;padding:17px;border:1px solid rgb(165 231 255 / 35%);border-radius:12px;background:rgb(5 28 52 / 25%);text-align:right}.coverage-card strong{font-size:29px}.coverage-card span{margin-top:2px;color:#cdf3ff;font-size:12px}.coverage-card small{margin-top:8px;color:#8dcae5;font-size:10px}.flow-panel{display:grid;grid-template-columns:1fr 40px 1fr 40px 1fr;align-items:center;gap:8px;margin:22px 0;padding:17px;border:1px solid #dce7f1;border-radius:13px;background:#fff}.flow-panel div{padding:12px;border-radius:8px;background:#f1f7fc;color:#54718d;font-size:12px;line-height:1.65}.flow-panel strong{color:#1f527d;font-size:12px}.flow-panel i{color:#2c96dc;font-size:23px;font-style:normal;text-align:center}.section-heading{display:flex;align-items:flex-end;justify-content:space-between;gap:20px;margin-bottom:14px}.section-heading h2{margin:6px 0 0;color:#1a3858;font-size:21px}.section-heading small{color:#8093a8;font-size:11px}.source-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:13px}.source-card{padding:16px;border:1px solid #dbe6f0;border-radius:11px;background:#fff}.source-card.ready{border-top:3px solid #2fba82}.source-card.partial{border-top:3px solid #e5ab3d}.source-card.pending{border-top:3px solid #a5b4c4}.source-card.demo{border-top:3px solid #659fd5}.source-title{display:flex;align-items:flex-start;justify-content:space-between;gap:8px}.source-title strong{display:block;color:#203d5c;font-size:15px}.source-title span{display:block;margin-top:4px;color:#6e849b;font-size:10px}.source-title b{padding:4px 6px;border-radius:4px;background:#e9f9f0;color:#269564;font-size:10px;white-space:nowrap}.partial .source-title b{background:#fff6e3;color:#b57a0e}.pending .source-title b{background:#f0f3f6;color:#718196}.demo .source-title b{background:#edf5ff;color:#477fae}.source-card>p{min-height:34px;margin:13px 0;color:#60788f;font-size:12px;line-height:1.55}.source-meta{display:flex;justify-content:space-between;gap:8px;padding-top:10px;border-top:1px solid #edf1f5;color:#8294a7;font-size:10px}.source-meta strong{color:#2b6c99}.principle-section{margin-top:25px}.principle-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:12px}.principle-grid article{padding:15px;border-left:3px solid #5aaee5;border-radius:0 8px 8px 0;background:#fff}.principle-grid strong{color:#295476;font-size:13px}.principle-grid p{margin:7px 0 0;color:#70859b;font-size:11px;line-height:1.65}@media(max-width:950px){.source-grid{grid-template-columns:repeat(2,1fr)}.principle-grid{grid-template-columns:repeat(2,1fr)}}@media(max-width:620px){.integration-page{padding:20px}.integration-hero{align-items:flex-start;flex-direction:column}.coverage-card{text-align:left}.flow-panel{grid-template-columns:1fr;gap:4px}.flow-panel i{transform:rotate(90deg)}.source-grid,.principle-grid{grid-template-columns:1fr}.section-heading{align-items:flex-start;flex-direction:column;gap:7px}}
</style>
