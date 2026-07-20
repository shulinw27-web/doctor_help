<script setup lang="ts">
import { computed, ref } from 'vue'

const emit = defineEmits<{
  navigate: [target: 'workbench' | 'management' | 'knowledge' | 'users' | 'integration']
  viewPatient: [patientId: string]
}>()

const keyword = ref('')

const focusPatients = [
  { id: 'patient-002', queue: 'A02', name: '李明', genderAge: '男 · 53 岁', level: '高优先级', levelClass: 'high', reason: '3 项检验指标超出参考区间', tags: ['肝功能', '血脂', '血糖'], updatedAt: '09:45' },
  { id: 'patient-001', queue: 'A01', name: '陈静', genderAge: '女 · 40 岁', level: '待汇总', levelClass: 'medium', reason: '检验与影像资料已到齐，等待医生查看', tags: ['血常规', '肺部 CT'], updatedAt: '09:20' },
  { id: 'patient-004', queue: 'A04', name: '赵强', genderAge: '男 · 65 岁', level: '趋势提示', levelClass: 'medium', reason: '历史两次检查存在趋势变化，建议核对', tags: ['肾功能', '血压'], updatedAt: '10:35' },
]

const activeAnalysisId = ref(focusPatients[0].id)
const sourceNotice = ref('')

const activeAnalysisPatient = computed(() => focusPatients.find((patient) => patient.id === activeAnalysisId.value) ?? focusPatients[0])

const quickActions = [
  { icon: '⌁', title: '今日候诊', detail: '12 名患者', action: () => emit('navigate', 'workbench') },
  { icon: '✦', title: '开始 AI 预读', detail: '陈静 · A01', action: () => openPatient('patient-001') },
  { icon: '⌘', title: '院内知识速查', detail: '120 条可引用经验', action: () => emit('navigate', 'knowledge') },
]

function openPatient(patientId: string) {
  activeAnalysisId.value = patientId
}

function openFullPatient(patientId: string) {
  emit('viewPatient', patientId)
}

function showSourceNotice(source: 'HIS' | 'LIS' | 'PACS' | 'EMR') {
  const sourceMessages = {
    HIS: '演示环境：生产环境将通过 HIS 提供患者、挂号与就诊上下文的只读同步。',
    LIS: '演示环境：生产环境将跳转或联动 LIS 原始检验报告，医生帮不修改检验数据。',
    PACS: '演示环境：生产环境将跳转或联动 PACS/RIS 原始报告与 DICOM 调阅入口。',
    EMR: '演示环境：生产环境将跳转 EMR 查看原始病历；医生帮仅生成待医生确认的资料摘要。',
  }
  sourceNotice.value = sourceMessages[source]
}

const quickSearchResults = computed(() => {
  const normalizedKeyword = keyword.value.trim().toLowerCase()
  if (!normalizedKeyword) return []
  return focusPatients.filter((patient) => patient.name.includes(normalizedKeyword) || patient.queue.toLowerCase().includes(normalizedKeyword))
})
</script>

<template>
  <main class="command-center">
    <div class="ambient ambient-one"></div>
    <div class="ambient ambient-two"></div>
    <svg class="ecg ecg-top" viewBox="0 0 1440 150" preserveAspectRatio="none" aria-hidden="true">
      <path d="M0 82 H88 l20 -5 18 5 H180 l14 -10 12 10 H278 l18 -5 18 5 H375 l12 -12 10 12 H455 l16 -5 17 5 H545 l15 -10 10 10 H640 l18 -5 20 5 H735 l12 -12 12 12 H830 l15 -5 17 5 H926 l14 -10 12 10 H1020 l18 -5 18 5 H1115 l13 -12 11 12 H1210 l18 -5 18 5 H1290 l15 -10 12 10 H1440" />
    </svg>
    <svg class="ecg ecg-bottom" viewBox="0 0 1440 150" preserveAspectRatio="none" aria-hidden="true">
      <path d="M0 82 H88 l20 -5 18 5 H180 l14 -10 12 10 H278 l18 -5 18 5 H375 l12 -12 10 12 H455 l16 -5 17 5 H545 l15 -10 10 10 H640 l18 -5 20 5 H735 l12 -12 12 12 H830 l15 -5 17 5 H926 l14 -10 12 10 H1020 l18 -5 18 5 H1115 l13 -12 11 12 H1210 l18 -5 18 5 H1290 l15 -10 12 10 H1440" />
    </svg>

    <header class="center-header">
      <div class="identity">
        <span class="identity-mark">医</span>
        <div><strong>医生帮 · 智能医疗中枢</strong><small>CHANGSHA HOSPITAL / LOCAL AI COPILOT</small></div>
      </div>
      <div class="system-state"><i></i><span>院内 AI 服务运行正常</span><em>数据不出院</em></div>
    </header>

    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">DOCTOR-FIRST INTELLIGENCE</p>
        <h1>先看 AI 已为您<br /><span>筛出的重点患者</span></h1>
        <p class="hero-description">本地 AI 已汇聚今日检验、影像与历史资料。以下内容仅用于辅助核对，所有临床判断由医生确认。</p>
        <div class="summary-chips"><span><b>3</b> 名需优先关注</span><span><b>7</b> 项待核对资料</span><span><b>98.7%</b> 同步完整度</span></div>
      </div>
      <div class="ai-status-card">
        <div class="status-ring"><span>AI</span><small>LOCAL</small></div>
        <div><p>智能汇总引擎</p><strong>已完成 32 份资料预处理</strong><small>规则演示引擎 · 后续可接入医院专属模型</small></div>
      </div>
    </section>

    <section class="workspace-grid">
      <section class="focus-zone">
        <div class="section-heading"><div><p>AI PRIORITY LIST</p><h2>医生优先关注</h2></div><button type="button" class="text-link" @click="$emit('navigate', 'workbench')">查看全部患者 →</button></div>
        <div class="patient-list">
          <article v-for="patient in focusPatients" :key="patient.id" class="patient-card" :class="patient.levelClass">
            <div class="patient-index">{{ patient.queue }}</div>
            <div class="patient-main"><div class="patient-title"><strong>{{ patient.name }}</strong><span>{{ patient.genderAge }}</span></div><p>{{ patient.reason }}</p><div class="tags"><span v-for="tag in patient.tags" :key="tag">{{ tag }}</span></div></div>
            <div class="patient-side"><b>{{ patient.level }}</b><time>{{ patient.updatedAt }}</time><button type="button" @click="openPatient(patient.id)">打开 AI 分析 →</button></div>
          </article>
        </div>
        <article class="inline-ai-analysis">
          <div class="inline-analysis-heading"><div><p>AI QUICK REVIEW</p><h3>{{ activeAnalysisPatient.name }} · {{ activeAnalysisPatient.queue }} 的首屏资料摘要</h3></div><button type="button" @click="openFullPatient(activeAnalysisPatient.id)">进入完整档案 →</button></div>
          <p>AI 已定位：{{ activeAnalysisPatient.reason }}。请医生结合主诉、体征与原始报告核对，不构成诊断结论。</p>
          <div class="analysis-evidence"><span v-for="tag in activeAnalysisPatient.tags" :key="tag">已关联：{{ tag }}</span><span>历史资料可追溯</span><span>院内知识已索引</span></div>
          <div class="analysis-source-actions"><button type="button" @click="showSourceNotice('LIS')">查看 LIS 原始检验报告 ↗</button><button type="button" @click="showSourceNotice('PACS')">查看 PACS/RIS 原始影像 ↗</button></div>
        </article>
      </section>

      <aside class="quick-zone">
        <div class="section-heading"><div><p>QUICK CLINICAL ACTIONS</p><h2>常用操作</h2></div></div>
        <section class="care-context"><div><p>READ-ONLY CARE CONTEXT</p><strong>今日待诊 12 人 · 当前患者由 HIS 同步</strong><small>叫号、开单、病历、医嘱等正式业务仍在医院原有系统中完成。</small></div><button type="button" @click="$emit('navigate', 'workbench')">查看待诊队列 →</button></section>
        <section class="source-systems"><p>原系统快捷入口</p><div><button type="button" @click="showSourceNotice('HIS')">HIS / 挂号 <span>只读</span></button><button type="button" @click="showSourceNotice('LIS')">LIS / 检验 <span>原始报告</span></button><button type="button" @click="showSourceNotice('PACS')">PACS / 影像 <span>原片入口</span></button><button type="button" @click="showSourceNotice('EMR')">EMR / 病历 <span>原始病历</span></button></div><small>{{ sourceNotice || '生产环境通过单点登录、深链接或上下文联动打开医院原系统。' }}</small><button type="button" class="integration-link" @click="$emit('navigate', 'integration')">查看数据完整度与接入状态 →</button></section>
        <div class="search-box"><label for="patient-search">患者快速定位</label><div><input id="patient-search" v-model.trim="keyword" type="search" placeholder="输入姓名或叫号，如 李明 / A02" /><button type="button" @click="$emit('navigate', 'workbench')">查找全部</button></div><div v-if="keyword" class="search-results"><button v-for="patient in quickSearchResults" :key="patient.id" type="button" @click="openPatient(patient.id)">AI 优先：{{ patient.name }} · {{ patient.queue }}<span>打开资料 →</span></button><small v-if="quickSearchResults.length === 0">优先队列未命中，可进入工作台检索全部患者</small></div><small v-else>输入后显示 AI 优先患者，可直接打开资料</small></div>
        <button v-for="action in quickActions" :key="action.title" type="button" class="quick-action" @click="action.action"><i>{{ action.icon }}</i><span><strong>{{ action.title }}</strong><small>{{ action.detail }}</small></span><em>→</em></button>
        <div class="safety-note"><span>✓</span><p><strong>医生主导</strong>AI 仅展示资料重点与依据，不输出诊断结论，不自动写入病历或医嘱。</p></div>
      </aside>
    </section>

    <aside class="management-rail">
      <span>MANAGEMENT</span>
      <button type="button" @click="$emit('navigate', 'management')"><i>▦</i><b>进入管理<br />驾驶舱</b><em>→</em></button>
      <small>效率 · 知识 · 运营</small>
    </aside>

    <footer><span>仅供医务人员辅助参考 · 不替代医生临床判断</span><span>演示数据环境 · v0.1</span></footer>
  </main>
</template>

<style scoped>
.command-center{position:relative;display:flex;box-sizing:border-box;min-height:100dvh;flex-direction:column;overflow:hidden;padding:30px clamp(34px,5vw,86px) 20px;color:#dcf3ff;background:radial-gradient(ellipse at 48% 33%,#164a78 0,#082440 38%,#030e1d 100%)}.ambient{position:absolute;border-radius:50%;filter:blur(90px);opacity:.3}.ambient-one{top:-150px;left:35%;width:480px;height:390px;background:#0084d5}.ambient-two{right:12%;bottom:-180px;width:420px;height:350px;background:#00a977}.ecg{position:absolute;z-index:0;width:135%;height:180px;opacity:.45;overflow:visible}.ecg path{fill:none;stroke:#53ecaf;stroke-width:1.3;stroke-dasharray:1900;stroke-dashoffset:1900;filter:drop-shadow(0 0 6px #3cffaf);animation:ecg-flow 8s linear infinite}.ecg-top{top:95px;left:-15%}.ecg-bottom{right:-15%;bottom:15px;transform:scaleY(-1);opacity:.19}.ecg-bottom path{animation-delay:-4s}.center-header,.hero,.workspace-grid,footer{position:relative;z-index:1}.center-header{display:flex;align-items:center;justify-content:space-between}.identity{display:flex;gap:12px;align-items:center}.identity-mark{display:grid;width:38px;height:38px;place-items:center;border:1px solid #65d4ff;border-radius:11px;background:linear-gradient(145deg,#1b77b6,#094376);color:#fff;font-size:20px;font-weight:700;box-shadow:inset 0 0 17px rgb(155 231 255 / 44%),0 0 20px rgb(67 194 255 / 25%)}.identity strong{display:block;color:#fff;font-size:15px;letter-spacing:.4px}.identity small{color:#7ebee8;font-size:10px;letter-spacing:1.3px}.system-state{display:flex;gap:9px;align-items:center;color:#a6e2ff;font-size:12px}.system-state i{width:8px;height:8px;border-radius:50%;background:#6ff1b4;box-shadow:0 0 12px #6ff1b4}.system-state em{padding:4px 8px;border:1px solid rgb(109 217 255 / 35%);border-radius:5px;color:#cff0ff;font-style:normal}.hero{display:flex;align-items:center;justify-content:space-between;gap:60px;max-width:1120px;margin:clamp(30px,6vh,75px) auto 26px;width:100%}.eyebrow,.section-heading p{margin:0;color:#66d9ff;font-size:10px;font-weight:700;letter-spacing:1.6px}.hero h1{margin:10px 0 12px;color:#fff;font-size:clamp(33px,4.2vw,57px);line-height:1.1;letter-spacing:-1px}.hero h1 span{color:#64edbd;text-shadow:0 0 28px rgb(77 244 181 / 35%)}.hero-description{max-width:620px;margin:0;color:#a8d2e9;font-size:14px;line-height:1.75}.summary-chips{display:flex;flex-wrap:wrap;gap:9px;margin-top:18px}.summary-chips span{padding:8px 10px;border:1px solid rgb(123 220 255 / 18%);border-radius:6px;background:rgb(5 35 61 / 43%);color:#8cc8e6;font-size:11px}.summary-chips b{color:#fff;font-size:15px}.ai-status-card{display:flex;min-width:280px;gap:16px;align-items:center;padding:15px;border:1px solid rgb(108 229 255 / 28%);border-radius:15px;background:linear-gradient(145deg,rgb(12 77 118 / 74%),rgb(4 27 52 / 66%));box-shadow:0 18px 35px rgb(0 0 0 / 17%);backdrop-filter:blur(9px)}.status-ring{display:flex;width:66px;height:66px;flex:0 0 auto;flex-direction:column;align-items:center;justify-content:center;border:1px solid #92eaff;border-radius:50%;background:radial-gradient(circle at 35% 25%,#8ee9ff,#237fbb 52%,#0c3158);box-shadow:0 0 24px #3bbfff}.status-ring span{font-size:21px;font-weight:800}.status-ring small{font-size:8px;letter-spacing:1.5px}.ai-status-card p{margin:0 0 4px;color:#78bedf;font-size:11px}.ai-status-card strong{display:block;color:#eefaff;font-size:12px;line-height:1.5}.ai-status-card>div:last-child small{display:block;margin-top:5px;color:#80aeca;font-size:10px;line-height:1.35}.workspace-grid{display:grid;grid-template-columns:minmax(570px,1.7fr) minmax(300px,.72fr);gap:18px;max-width:1120px;width:100%;margin:0 auto}.focus-zone,.quick-zone{border:1px solid rgb(107 207 255 / 22%);border-radius:15px;background:linear-gradient(145deg,rgb(13 67 104 / 65%),rgb(2 23 43 / 63%));box-shadow:0 16px 44px rgb(0 0 0 / 14%);backdrop-filter:blur(8px)}.focus-zone{padding:19px}.quick-zone{padding:19px}.section-heading{display:flex;align-items:flex-end;justify-content:space-between;gap:15px;margin-bottom:13px}.section-heading h2{margin:6px 0 0;color:#fff;font-size:20px}.text-link{cursor:pointer;border:0;background:transparent;color:#79d7ff;font-size:11px}.patient-list{display:grid;gap:9px}.patient-card{display:grid;grid-template-columns:45px minmax(0,1fr) 108px;gap:12px;align-items:center;padding:13px;border:1px solid rgb(125 215 255 / 14%);border-radius:10px;background:rgb(3 27 51 / 53%);transition:transform .2s,border-color .2s}.patient-card:hover{transform:translateX(3px);border-color:rgb(93 223 255 / 55%)}.patient-card.high{border-left:3px solid #ffad63;background:linear-gradient(90deg,rgb(180 96 30 / 16%),rgb(3 27 51 / 53%))}.patient-index{display:grid;width:40px;height:40px;place-items:center;border:1px solid rgb(94 191 241 / 30%);border-radius:9px;color:#a9e2ff;background:#0b426d;font-size:11px;font-weight:700}.patient-title{display:flex;gap:8px;align-items:baseline}.patient-title strong{color:#fff;font-size:15px}.patient-title span{color:#75a7c5;font-size:11px}.patient-main p{margin:4px 0 7px;color:#a5cee2;font-size:12px}.tags{display:flex;flex-wrap:wrap;gap:5px}.tags span{padding:2px 5px;border-radius:3px;background:rgb(88 185 246 / 12%);color:#79c7ef;font-size:10px}.patient-side{display:flex;flex-direction:column;align-items:flex-end;gap:5px}.patient-side b{color:#90e9c0;font-size:10px}.high .patient-side b{color:#ffc178}.patient-side time{color:#719ebc;font-size:10px}.patient-side button{cursor:pointer;border:0;background:transparent;color:#86e3ff;font-size:10px}.search-box{margin-bottom:10px}.search-box label{display:block;margin-bottom:7px;color:#caedff;font-size:12px;font-weight:600}.search-box>div{display:flex}.search-box input{min-width:0;flex:1;padding:9px 10px;outline:0;border:1px solid rgb(114 207 255 / 22%);border-radius:7px 0 0 7px;background:rgb(2 26 47 / 75%);color:#fff;font-size:11px}.search-box input:focus{border-color:#5ed4ff}.search-box button{cursor:pointer;padding:0 11px;border:0;border-radius:0 7px 7px 0;background:linear-gradient(135deg,#249acf,#58d0fa);color:#fff;font-size:11px}.search-box small{display:block;min-height:15px;margin-top:6px;color:#7ca9c5;font-size:10px}.search-box .search-error{color:#ffc27c}.quick-action{display:flex;width:100%;align-items:center;gap:10px;padding:11px 0;cursor:pointer;border:0;border-top:1px solid rgb(132 210 255 / 15%);background:transparent;color:inherit;text-align:left}.quick-action i{display:grid;width:29px;height:29px;place-items:center;border-radius:7px;background:rgb(76 187 244 / 14%);color:#86e6ff;font-size:16px;font-style:normal}.quick-action span{display:flex;flex:1;flex-direction:column;gap:2px}.quick-action strong{color:#eaf8ff;font-size:12px}.quick-action small{color:#7ba9c5;font-size:10px}.quick-action em{color:#6ccff7;font-size:17px;font-style:normal}.safety-note{display:flex;gap:8px;margin-top:9px;padding:10px;border-radius:8px;background:rgb(51 205 142 / 8%);color:#a5d5bb}.safety-note span{color:#68e5ab}.safety-note p{margin:0;font-size:10px;line-height:1.6}.safety-note strong{color:#d2f9e3}.management-rail{position:absolute;z-index:2;top:50%;right:0;display:flex;align-items:center;gap:8px;transform:translateY(-50%);writing-mode:vertical-rl}.management-rail>span{color:#679aba;font-size:9px;letter-spacing:1.8px}.management-rail button{display:flex;gap:10px;align-items:center;padding:16px 13px;cursor:pointer;border:1px solid rgb(90 212 255 / 55%);border-right:0;border-radius:12px 0 0 12px;background:linear-gradient(180deg,#126ea6,#08426f);color:#e8f8ff;box-shadow:0 0 20px rgb(24 191 255 / 24%);writing-mode:horizontal-tb;text-align:left}.management-rail i{color:#7ee7ff;font-size:22px;font-style:normal}.management-rail b{font-size:12px;line-height:1.4}.management-rail em{color:#75dbff;font-style:normal}.management-rail small{color:#5c94b9;font-size:9px;letter-spacing:1px}footer{display:flex;justify-content:space-between;max-width:1120px;width:100%;margin:auto auto 0;color:#5285a5;font-size:10px}@keyframes ecg-flow{0%{stroke-dashoffset:1900;opacity:.15}12%,75%{opacity:1}100%{stroke-dashoffset:0;opacity:.15}}@media(max-width:900px){.command-center{height:auto;overflow:auto;padding:24px 25px}.hero{align-items:flex-start;flex-direction:column;gap:22px;margin:38px auto 22px}.workspace-grid{grid-template-columns:1fr}.management-rail{top:180px}.ai-status-card{min-width:0}.ecg{width:190%}}@media(max-width:610px){.center-header{align-items:flex-start;flex-direction:column;gap:12px}.workspace-grid{width:100%}.patient-card{grid-template-columns:38px 1fr}.patient-side{grid-column:2;align-items:flex-start;flex-direction:row}.patient-side button{margin-left:auto}.management-rail{display:none}.hero h1{font-size:34px}.summary-chips{gap:5px}footer{gap:6px;flex-direction:column}.system-state{font-size:10px}.patient-main p{font-size:11px}}
.search-results{display:grid;gap:5px;margin-top:6px}.search-results button{display:flex;justify-content:space-between;padding:7px 8px;cursor:pointer;border:1px solid rgb(103 218 255 / 28%);border-radius:5px;background:rgb(29 124 168 / 18%);color:#cbeeff;font-size:10px;text-align:left}.search-results button span{color:#77e4bd}.search-results small{color:#ffc27c;font-size:10px}.search-box>div>button{cursor:pointer;padding:0 11px;border:0;border-radius:0 7px 7px 0;background:linear-gradient(135deg,#249acf,#58d0fa);color:#fff;font-size:11px}.inline-ai-analysis{margin-top:12px;padding:12px;border:1px solid rgb(88 233 185 / 28%);border-radius:9px;background:linear-gradient(90deg,rgb(42 181 129 / 13%),rgb(8 45 73 / 35%))}.inline-analysis-heading{display:flex;justify-content:space-between;gap:10px;align-items:flex-start}.inline-analysis-heading p,.care-context p{margin:0;color:#65d9b4;font-size:9px;font-weight:700;letter-spacing:1.2px}.inline-analysis-heading h3{margin:5px 0 0;color:#ecfcf5;font-size:13px}.inline-analysis-heading button{cursor:pointer;border:0;background:transparent;color:#7ee5ff;font-size:10px}.inline-ai-analysis>p{margin:9px 0;color:#a9d5c3;font-size:11px;line-height:1.55}.analysis-evidence{display:flex;flex-wrap:wrap;gap:5px}.analysis-evidence span{padding:3px 6px;border-radius:4px;background:rgb(97 233 181 / 11%);color:#8be8c0;font-size:10px}.analysis-source-actions{display:flex;gap:7px;margin-top:9px}.analysis-source-actions button,.care-context button{cursor:pointer;padding:5px 7px;border:1px solid rgb(101 222 255 / 29%);border-radius:5px;background:rgb(62 173 214 / 12%);color:#9ee9ff;font-size:10px}.care-context{display:flex;justify-content:space-between;gap:12px;margin-bottom:10px;padding:11px;border:1px solid rgb(92 230 178 / 28%);border-radius:9px;background:linear-gradient(120deg,rgb(25 151 112 / 22%),rgb(7 51 80 / 30%))}.care-context strong{display:block;margin-top:4px;color:#eefff7;font-size:12px}.care-context small{display:block;margin-top:4px;color:#91c9b5;font-size:9px;line-height:1.35}.source-systems{margin-bottom:10px;padding-bottom:10px;border-bottom:1px solid rgb(132 210 255 / 15%)}.source-systems>p{margin:0 0 7px;color:#caedff;font-size:12px;font-weight:600}.source-systems>div{display:grid;grid-template-columns:repeat(2,1fr);gap:5px}.source-systems button{display:flex;justify-content:space-between;padding:7px;cursor:pointer;border:1px solid rgb(105 199 247 / 17%);border-radius:5px;background:rgb(3 34 59 / 42%);color:#cceaff;font-size:10px;text-align:left}.source-systems button span{color:#6edab0;font-size:9px}.source-systems>small{display:block;min-height:27px;margin-top:7px;color:#82b3cf;font-size:9px;line-height:1.45}.integration-link{width:100%;margin-top:7px;cursor:pointer;border:0;background:transparent;color:#75dcff;font-size:10px;text-align:right}@media(max-width:610px){.care-context{flex-direction:column}.analysis-source-actions{flex-wrap:wrap}}
</style>
