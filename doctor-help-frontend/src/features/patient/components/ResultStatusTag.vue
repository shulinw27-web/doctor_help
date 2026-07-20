<script setup lang="ts">
import type { ResultStatus } from '../../../shared/types/patient'

defineProps<{ status: ResultStatus }>()

const statusLabel: Record<ResultStatus, string> = {
  NORMAL: '正常',
  HIGH: '偏高',
  LOW: '偏低',
}

const statusType: Record<ResultStatus, 'success' | 'danger' | 'warning'> = {
  NORMAL: 'success',
  HIGH: 'danger',
  LOW: 'warning',
}
</script>

<template>
  <el-tag :type="statusType[status]" effect="light">{{ statusLabel[status] }}</el-tag>
</template>

