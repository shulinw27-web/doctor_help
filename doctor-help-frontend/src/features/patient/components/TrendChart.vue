<script setup lang="ts">
import * as echarts from 'echarts'
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { TrendPoint } from '../../../shared/types/patient'

const props = defineProps<{ points: TrendPoint[] }>()
const chartElement = ref<HTMLDivElement>()
let chart: echarts.ECharts | undefined

function renderChart() {
  if (!chartElement.value) return

  chart ??= echarts.init(chartElement.value)
  chart.setOption({
    grid: { top: 26, right: 24, bottom: 28, left: 46 },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: props.points.map((point) => point.measuredAt), axisTick: { show: false } },
    yAxis: { type: 'value', name: '×10⁹/L', min: 0, max: 15, splitLine: { lineStyle: { color: '#edf1f6' } } },
    series: [{
      type: 'line',
      data: props.points.map((point) => point.value),
      smooth: true,
      symbolSize: 8,
      lineStyle: { color: '#2f80ed', width: 3 },
      itemStyle: { color: '#2f80ed' },
      areaStyle: { color: 'rgba(47, 128, 237, 0.12)' },
      markLine: { symbol: 'none', lineStyle: { color: '#eb5757', type: 'dashed' }, data: [{ yAxis: 9.5, label: { formatter: '参考上限' } }] },
    }],
  })
}

onMounted(renderChart)
watch(() => props.points, renderChart, { deep: true })
onBeforeUnmount(() => chart?.dispose())
</script>

<template><div ref="chartElement" class="trend-chart"></div></template>

<style scoped>
.trend-chart { width: 100%; height: 260px; }
</style>

