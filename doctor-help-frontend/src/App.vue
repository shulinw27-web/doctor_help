<script setup lang="ts">
import { ref } from 'vue'
import LoginView from './features/auth/LoginView.vue'
import type { CurrentUser, LoginResponse } from './features/auth/api/auth-api'
import DoctorWorkbenchView from './features/doctor-workbench/DoctorWorkbenchView.vue'
import PatientDetailView from './features/patient/PatientDetailView.vue'
import KnowledgeBaseView from './features/knowledge/KnowledgeBaseView.vue'
import ManagementDashboardView from './features/management/ManagementDashboardView.vue'
import UserManagementView from './features/user-management/UserManagementView.vue'
import AiCommandCenterView from './features/command-center/AiCommandCenterView.vue'
import DataIntegrationView from './features/integration/DataIntegrationView.vue'

type View = 'command' | 'workbench' | 'patient' | 'knowledge' | 'management' | 'users' | 'integration'

const activeView = ref<View>('command')
const selectedPatientId = ref('patient-002')
const profileVisible = ref(false)
const savedUser = window.localStorage.getItem('doctor-help-current-user')
const currentUser = ref<CurrentUser | undefined>(savedUser ? JSON.parse(savedUser) : undefined)

function openPatientDetail(patientId: string) { selectedPatientId.value = patientId; activeView.value = 'patient' }
function selectMenu(index: string) { if (['command', 'workbench', 'knowledge', 'management', 'users', 'integration'].includes(index)) activeView.value = index as View }
function roleLabel(role: CurrentUser['role']) { return { DOCTOR: '医生', LEADER: '管理者', ADMIN: '系统管理员' }[role] }
function logout() { window.localStorage.removeItem('doctor-help-access-token'); window.localStorage.removeItem('doctor-help-current-user'); currentUser.value = undefined; activeView.value = 'command' }
function handleLoggedIn(result: LoginResponse) { currentUser.value = result.user; window.localStorage.setItem('doctor-help-access-token', result.accessToken); window.localStorage.setItem('doctor-help-current-user', JSON.stringify(result.user)) }

window.addEventListener('doctor-help-session-expired', logout)
</script>

<template>
  <LoginView v-if="!currentUser" @logged-in="handleLoggedIn" />
  <AiCommandCenterView v-else-if="activeView === 'command'" @navigate="activeView = $event" @view-patient="openPatientDetail" />
  <el-container v-else class="app-shell">
    <el-aside width="224px" class="sidebar">
      <div class="brand">
        <span class="brand__mark">医</span>
        <span class="brand__name">医生帮</span>
      </div>
      <el-menu :default-active="activeView" class="menu" @select="selectMenu">
        <el-menu-item index="command"><span class="menu__icon" aria-hidden="true">✦</span><span class="menu__label">AI 数据中枢</span></el-menu-item>
        <el-menu-item index="integration"><span class="menu__icon" aria-hidden="true">⌁</span><span class="menu__label">数据接入状态</span></el-menu-item>
        <el-menu-item index="workbench"><span class="menu__icon" aria-hidden="true">⌂</span><span class="menu__label">医生工作台</span></el-menu-item>
        <el-menu-item index="knowledge"><span class="menu__icon" aria-hidden="true">⌘</span><span class="menu__label">知识库</span></el-menu-item>
        <el-menu-item index="management"><span class="menu__icon" aria-hidden="true">▦</span><span class="menu__label">管理驾驶舱</span></el-menu-item>
        <el-menu-item index="users"><span class="menu__icon" aria-hidden="true">◎</span><span class="menu__label">人员管理</span></el-menu-item>
      </el-menu>
      <p class="sidebar__notice">演示版 v0.1<br />数据不出本地</p>
    </el-aside>
    <el-container class="workspace">
      <el-header class="topbar">
        <span>院内门诊检测资料智能汇总助手</span>
        <div class="user"><el-button link type="primary" @click="profileVisible = true">{{ currentUser.name }} · {{ currentUser.departmentName }}</el-button><el-button link type="primary" @click="logout">退出登录</el-button></div>
      </el-header>
      <el-main class="content-area">
        <DoctorWorkbenchView v-if="activeView === 'workbench'" @view-patient="openPatientDetail" />
        <PatientDetailView v-else-if="activeView === 'patient'" :patient-id="selectedPatientId" @back="activeView = 'workbench'" />
        <KnowledgeBaseView v-else-if="activeView === 'knowledge'" :current-user="currentUser" />
        <ManagementDashboardView v-else-if="activeView === 'management'" />
        <UserManagementView v-else-if="activeView === 'users'" :can-manage="currentUser.role === 'ADMIN'" />
        <DataIntegrationView v-else />
      </el-main>
    </el-container>
    <el-dialog v-model="profileVisible" title="个人信息" width="460px">
      <el-descriptions v-if="currentUser" :column="1" border>
        <el-descriptions-item label="姓名">{{ currentUser.name }}</el-descriptions-item>
        <el-descriptions-item label="账号">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleLabel(currentUser.role) }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ currentUser.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentUser.title }}</el-descriptions-item>
        <el-descriptions-item label="身份来源">本地演示账号</el-descriptions-item>
      </el-descriptions>
      <p class="profile-hint">生产环境将通过医院 OA / 统一身份认证同步账号、科室与角色；当前演示版不连接外网。</p>
      <template #footer><el-button type="primary" @click="profileVisible = false">知道了</el-button></template>
    </el-dialog>
  </el-container>
</template>

<style scoped>
.app-shell { height: 100dvh; min-height: 0; overflow: hidden; }
.workspace { min-width: 0; min-height: 0; overflow: hidden; }
.sidebar { display: flex; flex: 0 0 auto; flex-direction: column; overflow: hidden; background: #12365a; color: #fff; }
.brand { display: flex; align-items: center; gap: 10px; min-height: 76px; padding: 20px; font-size: 20px; font-weight: 700; white-space: nowrap; }
.brand__mark { display: grid; width: 28px; height: 28px; place-items: center; border-radius: 8px; background: #4ea5ff; }
.brand__name { overflow: hidden; }
.menu { border-right: 0; background: transparent; }
.menu :deep(.el-menu-item) { color: #c8d7e7; }
.menu :deep(.el-menu-item.is-active), .menu :deep(.el-menu-item:hover) { color: #fff; background: #1c4b79; }
.menu__icon { display: inline-grid; width: 24px; margin-right: 10px; place-items: center; font-size: 18px; }
.menu__label { white-space: nowrap; }
.sidebar__notice { margin: auto 20px 20px; color: #9fbad5; font-size: 12px; line-height: 1.7; }
.topbar { display: flex; flex: 0 0 64px; align-items: center; justify-content: space-between; height: 64px; color: #475467; background: #fff; border-bottom: 1px solid #e7edf5; }
.user { display: flex; gap: 14px; align-items: center; font-size: 14px; }
.content-area { min-width: 0; min-height: 0; padding: 0; overflow-y: auto; overscroll-behavior: contain; background: #f6f8fb; }
.profile-hint { margin: 18px 0 0; color: #667085; font-size: 13px; line-height: 1.7; }
</style>
