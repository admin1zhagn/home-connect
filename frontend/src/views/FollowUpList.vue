<template>
  <div class="page-container">
    <div class="card-block">
      <el-tabs v-model="scope" @tab-change="load">
        <el-tab-pane label="今日待跟进" name="today" />
        <el-tab-pane label="逾期未跟进" name="overdue" />
        <el-tab-pane label="全部" name="all" />
      </el-tabs>

      <el-table :data="rows" border size="small" v-loading="loading" empty-text="暂无待跟进事项">
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.sourceType === 'CUSTOMER' ? 'primary' : 'warning'">
              {{ row.sourceType === 'CUSTOMER' ? '客户' : '服务' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户" width="120" />
        <el-table-column prop="title" label="事项" />
        <el-table-column label="到期/约定时间" width="170">
          <template #default="{ row }">{{ row.dueDate || '未设置' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag size="small">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="逾期" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.overdue" size="small" type="danger">逾期</el-tag>
            <span v-else class="muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="go(row)">去处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { followUpList } from '@/api/followup'

const router = useRouter()
const loading = ref(false)
const scope = ref('today')
const rows: any = ref([])

async function load() {
  loading.value = true
  try { rows.value = (await followUpList(scope.value)).data || [] } finally { loading.value = false }
}
function go(row: any) {
  if (row.sourceType === 'CUSTOMER') router.push(`/customers/${row.customerId}`)
  else router.push(`/customers/${row.customerId}`)
}
onMounted(load)
</script>

<style scoped>
.muted { color: #909399; }
</style>
