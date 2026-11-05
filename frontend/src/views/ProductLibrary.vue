<template>
  <div class="page-container">
    <div class="card-block">
      <div class="toolbar">
        <el-input v-model="kw" placeholder="搜索套餐名称/运营商" style="width: 220px" @keyup.enter="search" />
        <el-select v-model="typeFilter" placeholder="类型" clearable style="width: 140px" @change="search">
          <el-option v-for="t in types" :key="t" :label="t" :value="t" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px" @change="search">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button type="success" @click="openDialog(null)">+ 新增套餐</el-button>
      </div>

      <el-table :data="rows" border size="small" v-loading="loading">
        <el-table-column prop="planName" label="套餐名称" min-width="140" />
        <el-table-column prop="operator" label="运营商" width="90" />
        <el-table-column prop="planType" label="类型" width="100" />
        <el-table-column label="标准/优惠/结束后月费" width="200">
          <template #default="{ row }">
            {{ row.standardFee }} ｜ <b class="red">{{ row.discountFee || '-' }}</b> ｜ {{ row.afterDiscountFee || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="discountMonths" label="优惠月数" width="80" />
        <el-table-column prop="contractMonths" label="合约月" width="80" />
        <el-table-column prop="installationFee" label="安装费" width="80" />
        <el-table-column prop="equipmentFee" label="设备费" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="warning" size="small" @click="toggle(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
            <el-button link type="danger" size="small" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top: 12px" background layout="prev, pager, next, total"
        :total="total" :page-size="size" :current-page="page" @current-change="(p: number) => { page = p; load() }" />
    </div>

    <el-dialog v-model="visible" :title="form.id ? '编辑套餐' : '新增套餐'" width="720px">
      <el-form :model="form" label-width="120px">
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="套餐名称"><el-input v-model="form.planName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="运营商"><el-input v-model="form.operator" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="套餐类型">
            <el-select v-model="form.planType" style="width: 100%"><el-option v-for="t in types" :key="t" :label="t" :value="t" /></el-select>
          </el-form-item></el-col>
          <el-col :span="12"><el-form-item label="副卡数量"><el-input-number v-model="form.subCardCount" :min="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="标准月费"><el-input-number v-model="form.standardFee" :min="0" :precision="2" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="优惠月费"><el-input-number v-model="form.discountFee" :min="0" :precision="2" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="结束后月费"><el-input-number v-model="form.afterDiscountFee" :min="0" :precision="2" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="优惠月数"><el-input-number v-model="form.discountMonths" :min="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="合约期限(月)"><el-input-number v-model="form.contractMonths" :min="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="宽带速率"><el-input-number v-model="form.broadbandSpeed" :min="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="流量(GB)"><el-input-number v-model="form.dataGb" :min="0" :precision="2" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="通话(分)"><el-input-number v-model="form.callMinutes" :min="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="安装费"><el-input-number v-model="form.installationFee" :min="0" :precision="2" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="设备费"><el-input-number v-model="form.equipmentFee" :min="0" :precision="2" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="核实日期"><el-date-picker v-model="form.verifiedAt" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="数据来源"><el-input v-model="form.sourceNote" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="办理条件"><el-input v-model="form.conditions" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="套餐说明"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productPage, saveProduct, deleteProduct } from '@/api/product'

const types = ['手机套餐', '家庭宽带', '电视业务', '家庭副卡', '流量包', '其他增值业务']
const loading = ref(false)
const saving = ref(false)
const visible = ref(false)
const rows: any = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const kw = ref('')
const typeFilter = ref('')
const statusFilter = ref<number | ''>('')

const form: any = reactive({
  id: null, planName: '', operator: '', planType: '手机套餐', standardFee: 0, discountFee: null,
  discountMonths: 0, afterDiscountFee: null, dataGb: null, callMinutes: null, broadbandSpeed: null,
  subCardCount: 0, contractMonths: 0, installationFee: 0, equipmentFee: 0, conditions: '',
  description: '', sourceNote: '', verifiedAt: '', status: 1
})

async function load() {
  loading.value = true
  try {
    const r: any = (await productPage({ page: page.value, size: size.value, keyword: kw.value, planType: typeFilter.value, status: statusFilter.value || undefined })).data
    rows.value = r.records
    total.value = Number(r.total)
  } finally { loading.value = false }
}
function search() { page.value = 1; load() }
function openDialog(row: any) {
  Object.assign(form, {
    id: null, planName: '', operator: '', planType: '手机套餐', standardFee: 0, discountFee: null,
    discountMonths: 0, afterDiscountFee: null, dataGb: null, callMinutes: null, broadbandSpeed: null,
    subCardCount: 0, contractMonths: 0, installationFee: 0, equipmentFee: 0, conditions: '',
    description: '', sourceNote: '', verifiedAt: '', status: 1
  }, row || {})
  visible.value = true
}
async function save() {
  if (!form.planName) return ElMessage.warning('请填写套餐名称')
  saving.value = true
  try { await saveProduct(form); ElMessage.success('已保存'); visible.value = false; load() }
  finally { saving.value = false }
}
async function toggle(row: any) {
  row.status = row.status === 1 ? 0 : 1
  await saveProduct(row); ElMessage.success('已更新')
}
async function remove(row: any) {
  await ElMessageBox.confirm('确认删除该套餐？', '提示', { type: 'warning' })
  await deleteProduct(row.id); ElMessage.success('已删除'); load()
}
onMounted(load)
</script>

<style scoped>
.toolbar { margin-bottom: 12px; display: flex; gap: 10px; flex-wrap: wrap; }
.red { color: #e63946; }
</style>
