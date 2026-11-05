<template>
  <div class="page-container">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="称呼/电话/小区" clearable style="width: 200px" @keyup.enter="load" />
      <el-select v-model="query.status" placeholder="客户状态" clearable style="width: 140px">
        <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
      </el-select>
      <el-select v-model="query.source" placeholder="客户来源" clearable style="width: 140px">
        <el-option v-for="s in sourceOptions" :key="s" :label="s" :value="s" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="goAdd">新增客户</el-button>
    </div>

    <!-- 手机卡片视图 -->
    <div v-if="isMobile" class="m-list">
      <div v-for="row in rows" :key="row.id" class="m-card" @click="goDetail(row)">
        <div class="m-card-head">
          <span class="m-name">{{ row.customerName }}</span>
          <el-tag size="small">{{ row.status || '新客户' }}</el-tag>
        </div>
        <div class="m-line">电话：{{ row.phone }}</div>
        <div class="m-line">小区：{{ row.community }}</div>
        <div class="m-meta">来源：{{ row.source || '—' }} ｜ {{ row.createdAt }}</div>
        <div class="m-actions" @click.stop>
          <el-button size="small" @click="goDetail(row)">详情</el-button>
          <el-button size="small" type="primary" @click="goEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </div>
      </div>
      <el-empty v-if="!rows.length" description="暂无客户" />
    </div>

    <!-- 桌面表格视图 -->
    <div v-else class="card-block">
      <el-table :data="rows" size="small" stripe>
        <el-table-column prop="customerName" label="称呼" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="community" label="小区" />
        <el-table-column prop="source" label="来源" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag size="small">{{ row.status || '新客户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="goDetail(row)">详情</el-button>
            <el-button size="small" type="primary" @click="goEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pager"
        background
        layout="prev, pager, next, total"
        :total="total"
        :current-page="query.page"
        :page-size="query.size"
        @current-change="onPage"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerPage, deleteCustomer } from '@/api/customer'
import { useScreen } from '@/composables/useScreen'

const router = useRouter()
const { isMobile } = useScreen()
const statusOptions = ['新客户', '已沟通', '待跟进', '已成交', '暂无需求']
const sourceOptions = ['公司工作接触', '客户主动咨询', '熟人介绍', '自主拓展', '其他']
const rows = ref<any[]>([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, keyword: '', status: '', source: '' })

async function load() {
  const res = await customerPage(query)
  rows.value = res.data.records
  total.value = Number(res.data.total)
}
function onPage(p: number) {
  query.page = p
  load()
}
function goAdd() {
  router.push('/customers/edit')
}
function goEdit(row: any) {
  router.push(`/customers/edit?id=${row.id}`)
}
function goDetail(row: any) {
  router.push(`/customers/${row.id}`)
}
async function remove(row: any) {
  await ElMessageBox.confirm(`确认删除客户「${row.customerName}」？`, '提示', { type: 'warning' })
  await deleteCustomer(row.id)
  ElMessage.success('已删除')
  load()
}
onMounted(load)
</script>

<style scoped>
.pager {
  margin-top: 12px;
  justify-content: flex-end;
}

/* 手机卡片列表 */
.m-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.m-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.m-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}
.m-name {
  font-size: 16px;
  font-weight: bold;
}
.m-line {
  font-size: 13px;
  color: #303133;
  line-height: 1.6;
}
.m-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.m-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
.m-actions .el-button {
  flex: 1;
}
</style>
