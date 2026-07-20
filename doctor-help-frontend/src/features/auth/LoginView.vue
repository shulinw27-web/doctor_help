<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { login, type LoginResponse } from './api/auth-api'
const emit = defineEmits<{ loggedIn: [result: LoginResponse] }>()
const form = ref({ username: 'zhangyisheng', password: 'demo123' })
const loading = ref(false)
async function submit() { loading.value = true; try { emit('loggedIn', await login(form.value.username, form.value.password)) } catch { ElMessage.error('账号或密码错误') } finally { loading.value = false } }
</script>
<template><main class="login-page"><section class="login-card"><div class="brand"><span>医</span><h1>医生帮</h1></div><p>院内门诊检测资料智能汇总助手</p><el-form @submit.prevent="submit"><el-form-item label="账号"><el-input v-model="form.username"/></el-form-item><el-form-item label="密码"><el-input v-model="form.password" type="password" show-password/></el-form-item><el-button type="primary" native-type="submit" :loading="loading" class="login-button">登录</el-button></el-form><small>演示账号：zhangyisheng / demo123；admin / demo123</small></section></main></template>
<style scoped>.login-page{display:grid;min-height:100vh;place-items:center;background:linear-gradient(135deg,#12365a,#2f80ed)}.login-card{width:400px;padding:38px;background:#fff;border-radius:16px;box-shadow:0 20px 60px rgba(0,0,0,.2)}.brand{display:flex;align-items:center;gap:10px}.brand span{display:grid;width:34px;height:34px;place-items:center;border-radius:10px;background:#2f80ed;color:#fff;font-weight:bold}.brand h1{margin:0;color:#1d2939}.login-card>p{margin:12px 0 24px;color:#667085}.login-button{width:100%}small{display:block;margin-top:18px;color:#98a2b3;line-height:1.6}</style>
