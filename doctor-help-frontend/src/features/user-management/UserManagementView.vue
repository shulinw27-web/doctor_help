<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { createUser, fetchUsers, syncUsersFromOa, updateUserRole, type CreateSystemUserPayload, type SystemUser, type UserRole } from './api/user-api'

const props = defineProps<{ canManage: boolean }>()
const users = ref<SystemUser[]>([])
const loading = ref(true)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = 12
const selectedUser = ref<SystemUser>()
const detailVisible = ref(false)
const addVisible = ref(false)
const syncing = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<CreateSystemUserPayload>({ username: '', name: '', gender: '男', role: 'DOCTOR', departmentName: '', title: '', mobile: '', email: '' })
const roleLabels: Record<UserRole, string> = { DOCTOR: '医生', LEADER: '院领导', ADMIN: '管理员' }
const formRules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }, { pattern: /^[a-zA-Z0-9_-]{3,30}$/, message: '账号为 3–30 位字母、数字或 _ -', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  departmentName: [{ required: true, message: '请输入部门/科室', trigger: 'blur' }],
  title: [{ required: true, message: '请输入职务', trigger: 'blur' }],
  mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  email: [{ required: true, type: 'email', message: '请输入有效邮箱', trigger: 'blur' }],
}

const filteredUsers = computed(() => {
  const search = keyword.value.trim().toLowerCase()
  if (!search) return users.value
  return users.value.filter((user) => [user.name, user.username, user.departmentName, user.employeeNo].some((value) => value.toLowerCase().includes(search)))
})
const pagedUsers = computed(() => filteredUsers.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize))

async function loadUsers() {
  loading.value = true
  try { users.value = await fetchUsers() } catch { ElMessage.error('人员数据加载失败，请重新登录或重试。') } finally { loading.value = false }
}
async function changeRole(user: SystemUser, role: UserRole) {
  try { Object.assign(user, await updateUserRole(user.id, role)); ElMessage.success('角色已更新') } catch { ElMessage.error('角色更新失败') }
}
function openDetail(user: SystemUser) { selectedUser.value = user; detailVisible.value = true }
function resetForm() { Object.assign(form, { username: '', name: '', gender: '男', role: 'DOCTOR', departmentName: '', title: '', mobile: '', email: '' }); formRef.value?.clearValidate() }
function openAdd() { resetForm(); addVisible.value = true }
async function saveUser() {
  if (!formRef.value || !(await formRef.value.validate().catch(() => false))) return
  try { users.value = [await createUser(form), ...users.value]; currentPage.value = 1; addVisible.value = false; ElMessage.success('人员已新增') } catch { ElMessage.error('新增失败，请检查账号是否已存在') }
}
async function syncFromOa() {
  syncing.value = true
  try {
    const result = await syncUsersFromOa()
    await loadUsers()
    ElMessage.success(`OA 同步完成：新增 ${result.addedCount} 人，更新 ${result.updatedCount} 人`)
  } catch { ElMessage.error('OA 同步失败，请稍后重试') } finally { syncing.value = false }
}
function resetPage() { currentPage.value = 1 }

onMounted(loadUsers)
</script>

<template>
  <main v-loading="loading" class="page">
    <header class="page-header"><div><p class="eyebrow">组织与账号</p><h1>人员管理</h1><p>演示人员目录包含院内信息与模拟 OA 同步字段；生产环境应使用医院统一身份认证与审计接口。</p></div><div v-if="props.canManage" class="header-actions"><el-button :loading="syncing" @click="syncFromOa">从 OA 同步</el-button><el-button type="primary" @click="openAdd">新增人员</el-button></div></header>
    <section class="table-panel"><p v-if="!props.canManage" class="read-only-notice">当前为只读查看权限；新增人员、OA 同步和角色调整仅管理员可执行。</p><div class="table-tools"><strong>人员总数 {{ filteredUsers.length }}</strong><el-input v-model="keyword" clearable placeholder="姓名、账号、科室或工号" @input="resetPage" /></div><el-table :data="pagedUsers" stripe><el-table-column prop="name" label="姓名" min-width="100"/><el-table-column prop="employeeNo" label="工号" min-width="140"/><el-table-column prop="departmentName" label="科室/部门" min-width="130"/><el-table-column prop="title" label="职务" min-width="120"/><el-table-column prop="mobile" label="联系电话" min-width="130"/><el-table-column label="角色" min-width="145"><template #default="scope"><el-select :model-value="scope.row.role" :disabled="!props.canManage" @update:model-value="changeRole(scope.row, $event as UserRole)"><el-option v-for="(label, value) in roleLabels" :key="value" :label="label" :value="value"/></el-select></template></el-table-column><el-table-column label="操作" width="92"><template #default="scope"><el-button link type="primary" @click="openDetail(scope.row)">详情</el-button></template></el-table-column></el-table><el-pagination v-if="filteredUsers.length > pageSize" v-model:current-page="currentPage" :page-size="pageSize" :total="filteredUsers.length" layout="total, prev, pager, next" class="pagination" /></section>
    <el-drawer v-model="detailVisible" title="人员详情" size="520px" @closed="selectedUser = undefined"><el-descriptions v-if="selectedUser" :column="1" border><el-descriptions-item label="姓名">{{ selectedUser.name }}</el-descriptions-item><el-descriptions-item label="账号">{{ selectedUser.username }}</el-descriptions-item><el-descriptions-item label="工号">{{ selectedUser.employeeNo }}</el-descriptions-item><el-descriptions-item label="性别">{{ selectedUser.gender }}</el-descriptions-item><el-descriptions-item label="科室/部门">{{ selectedUser.departmentName }}</el-descriptions-item><el-descriptions-item label="职务">{{ selectedUser.title }}</el-descriptions-item><el-descriptions-item label="联系电话">{{ selectedUser.mobile }}</el-descriptions-item><el-descriptions-item label="邮箱">{{ selectedUser.email }}</el-descriptions-item><el-descriptions-item label="入职日期">{{ selectedUser.joinedAt }}</el-descriptions-item><el-descriptions-item label="在职状态">{{ selectedUser.employmentStatus }}</el-descriptions-item></el-descriptions><section v-if="selectedUser" class="oa-section"><p class="eyebrow">OA 同步信息（模拟）</p><el-descriptions :column="1" border><el-descriptions-item label="OA 用户 ID">{{ selectedUser.oaProfile.oaUserId }}</el-descriptions-item><el-descriptions-item label="OA 部门编码">{{ selectedUser.oaProfile.oaDepartmentCode }}</el-descriptions-item><el-descriptions-item label="OA 岗位">{{ selectedUser.oaProfile.oaPositionName }}</el-descriptions-item><el-descriptions-item label="OA 账号状态">{{ selectedUser.oaProfile.oaAccountStatus }}</el-descriptions-item><el-descriptions-item label="最近同步">{{ selectedUser.oaProfile.lastSyncedAt }}</el-descriptions-item></el-descriptions></section></el-drawer>
    <el-dialog v-model="addVisible" title="新增人员" width="560px" @closed="resetForm"><el-form ref="formRef" :model="form" :rules="formRules" label-position="top"><div class="form-grid"><el-form-item label="账号" prop="username"><el-input v-model="form.username" placeholder="例如 doctor_new" /></el-form-item><el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item><el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio value="男">男</el-radio><el-radio value="女">女</el-radio></el-radio-group></el-form-item><el-form-item label="角色"><el-select v-model="form.role"><el-option v-for="(label, value) in roleLabels" :key="value" :label="label" :value="value"/></el-select></el-form-item><el-form-item label="科室/部门" prop="departmentName"><el-input v-model="form.departmentName" /></el-form-item><el-form-item label="职务" prop="title"><el-input v-model="form.title" /></el-form-item><el-form-item label="联系电话" prop="mobile"><el-input v-model="form.mobile" /></el-form-item><el-form-item label="邮箱" prop="email"><el-input v-model="form.email" /></el-form-item></div></el-form><template #footer><el-button @click="addVisible = false">取消</el-button><el-button type="primary" @click="saveUser">保存</el-button></template></el-dialog>
  </main>
</template>

<style scoped>
.page{padding:28px 32px}.page-header,.table-tools{display:flex;align-items:flex-start;justify-content:space-between;gap:18px}.eyebrow{margin:0 0 8px;color:#2f80ed;font-weight:600}h1{margin:0;color:#1d2939;font-size:28px}.page-header>div>p:not(.eyebrow){max-width:720px;margin:8px 0 28px;color:#667085;font-size:14px}.header-actions{display:flex;gap:10px;white-space:nowrap}.table-panel{padding:20px;background:#fff;border:1px solid #e7edf5;border-radius:12px}.read-only-notice{margin:0 0 18px;padding:10px 12px;color:#8a5a00;background:#fff7e6;border-radius:8px;font-size:13px}.table-tools{align-items:center;margin-bottom:18px;color:#475467}.table-tools :deep(.el-input){width:280px}.pagination{justify-content:flex-end;margin-top:18px}.oa-section{margin-top:22px}.form-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:0 16px}@media(max-width:900px){.page-header,.table-tools{flex-direction:column}.header-actions,.table-tools :deep(.el-input){width:100%}.form-grid{grid-template-columns:1fr}}
</style>
