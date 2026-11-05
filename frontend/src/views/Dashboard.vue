<template>
  <div class="page-container">
    <el-row :gutter="16">
      <el-col v-for="s in statCards" :key="s.label" :xs="12" :sm="8" :md="6" :lg="3">
        <div class="stat-card">
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :md="12">
        <div class="card-block">
          <div class="block-title">经营指标概览</div>
          <div ref="chartRef" style="height: 280px"></div>
        </div>
      </el-col>
      <el-col :xs="24" :md="12">
        <div class="card-block">
          <div class="block-title">最近新增客户</div>
          <el-table :data="recentCustomers" size="small" max-height="280">
            <el-table-column prop="customerName" label="称呼" />
            <el-table-column prop="community" label="小区" />
            <el-table-column prop="status" label="状态" />
            <el-table-column prop="createdAt" label="创建时间" width="160" />
          </el-table>
        </div>
      </el-col>
    </el-row>

    <div class="card-block">
      <div class="block-title">待跟进提醒</div>
      <el-table :data="recentFollowUps" size="small" empty-text="暂无待跟进">
        <el-table-column prop="customerName" label="客户" />
        <el-table-column prop="title" label="事项" />
        <el-table-column label="到期/约定">
          <template #default="{ row }">{{ row.dueDate || '—' }}</template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.overdue ? 'danger' : 'warning'" size="small">
              {{ row.overdue ? '已逾期' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref, shallowRef } from 'vue'
import * as echarts from 'echarts'
import { dashboardStats } from '@/api/dashboard'

const statCards = ref<any[]>([])
const recentCustomers = ref<any[]>([])
const recentFollowUps = ref<any[]>([])
const chartRef = ref<HTMLDivElement>()
const chart = shallowRef<echarts.ECharts>()

async function load() {
  const res = await dashboardStats()
  const d = res.data
  statCards.value = [
    { label: '客户总数', value: d.customerTotal },
    { label: '本月新增', value: d.customerNewThisMonth },
    { label: '待跟进', value: d.followUpPending },
    { label: '已成交', value: d.dealCount },
    { label: '本月推荐次数', value: d.recommendCountThisMonth },
    { label: '推荐成功率', value: d.recommendSuccessRate },
    { label: '客户转化率', value: d.conversionRate }
  ]
  recentCustomers.value = d.recentCustomers || []
  recentFollowUps.value = d.recentFollowUps || []
  renderChart(d)
}

function renderChart(d: any) {
  if (!chartRef.value) return
  if (!chart.value) chart.value = echarts.init(chartRef.value)
  chart.value.setOption({
    tooltip: {},
    xAxis: {
      type: 'category',
      data: ['客户总数', '本月新增', '待跟进', '已成交', '本月推荐', '推荐成功']
    },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: [
          d.customerTotal, d.customerNewThisMonth, d.followUpPending,
          d.dealCount, d.recommendCountThisMonth, d.recommendSuccessCount
        ],
        itemStyle: { color: '#409eff' },
        label: { show: true, position: 'top' }
      }
    ]
  })
}

onMounted(() => {
  load()
  window.addEventListener('resize', () => chart.value?.resize())
})
onUnmounted(() => chart.value?.dispose())
</script>

<style scoped>
.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  text-align: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  color: #909399;
  margin-top: 4px;
  font-size: 13px;
}
.block-title {
  font-weight: bold;
  margin-bottom: 12px;
}
</style>
