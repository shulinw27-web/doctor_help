<script setup lang="ts">
defineProps<{
  label: string
  value: number
  accent: string
}>()
</script>

<template>
  <article class="metric-card">
    <span class="metric-card__accent" :style="{ background: accent }"></span>
    <p>{{ label }}</p>
    <strong>{{ value }}</strong>
  </article>
</template>

<style scoped>
.metric-card {
  position: relative;
  min-width: 0;
  padding: 18px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e7edf5;
  border-radius: 12px;
}

.metric-card__accent {
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

p {
  margin: 0;
  color: #667085;
  font-size: 14px;
}

strong {
  display: block;
  margin-top: 10px;
  color: #1d2939;
  font-size: 28px;
}
</style>

