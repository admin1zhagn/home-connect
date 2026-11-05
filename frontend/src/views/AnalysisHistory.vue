<template>
  <div class="page-container">
    <div class="card-block">
      <div class="toolbar">
        <el-select v-model="customerFilter" filterable clearable placeholder="按客户筛选" style="width: 240px" @change="search">
          <el-option v-for="c in customerOptions" :key="c.id" :label="c.customerName" :value="c.id" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
      </div>

      <el-table :data="rows" border size="small" v-loading="loading" @row-click="openDetail">
        <el-table-column label="分析编号" prop="analysisNo" width="170" />
        <el-table-column label="客户">
          <template #default="{ row }">{{ nameMap[row.customerId] || row.customerId }}</template>
        </el-table-column>
        <el-table-column label="当前月费" prop="currentMonthlyFee" width="100" />
        <el-table-column label="推荐月费" prop="recommendedMonthlyFee" width="100" />
        <el-table-column label="对比周期" width="90">
          <template #default="{ row }">{{ row.comparisonMonths }}月</template>
        </el-table-column>
        <el-table-column label="预计差额" width="100">
          <template #default="{ row }"><span :class="row.estimatedSaving >= 0 ? 'green' : 'red'">{{ row.estimatedSaving }}</span></template>
        </el-table-column>
        <el-table-column label="结论" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="{ SAVING: 'success', NO_SAVING: 'warning', VERIFY: 'info' }[row.conclusion]">
              {{ { SAVING: '可节省', NO_SAVING: '不降反增', VERIFY: '待核实' }[row.conclusion] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="客户反馈" width="110">
          <template #default="{ row }">{{ row.customerFeedback || '未记录' }}</template>
        </el-table-column>
        <el-table-column label="分析时间" prop="createdAt" width="160" />
      </el-table>

      <el-pagination style="margin-top: 12px" background layout="prev, pager, next, total"
        :total="total" :page-size="size" :current-page="page" @current-change="(p: number) => { page = p; load() }" />
    </div>

    <el-dialog v-model="detailVisible" title="推荐分析详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="分析编号">{{ detail.analysis.analysisNo }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ nameMap[detail.analysis.customerId] }}</el-descriptions-item>
          <el-descriptions-item label="当前月费">{{ detail.analysis.currentMonthlyFee }}</el-descriptions-item>
          <el-descriptions-item label="优惠期月费">{{ detail.analysis.recommendedMonthlyFee }}</el-descriptions-item>
          <el-descriptions-item label="结束后月费">{{ detail.analysis.afterDiscountMonthlyFee }}</el-descriptions-item>
          <el-descriptions-item label="一次性费用">{{ detail.analysis.oneTimeFee }}</el-descriptions-item>
          <el-descriptions-item label="对比周期">{{ detail.analysis.comparisonMonths }} 月</el-descriptions-item>
          <el-descriptions-item label="预计差额">{{ detail.analysis.estimatedSaving }}</el-descriptions-item>
        </el-descriptions>
        <el-divider content-position="left">推荐明细</el-divider>
        <el-table :data="detail.items" border size="small">
          <el-table-column prop="productName" label="套餐" min-width="140" />
          <el-table-column prop="operator" label="运营商" width="90" />
          <el-table-column prop="planType" label="类型" width="100" />
          <el-table-column label="标准/优惠" width="140">
            <template #default="{ row }">{{ row.monthlyFee }} ｜ {{ row.discountFee || '-' }}</template>
          </el-table-column>
          <el-table-column prop="afterDiscountFee" label="结束后" width="90" />
        </el-table>
        <el-divider content-position="left">推荐理由</el-divider>
        <pre class="reason">{{ detail.analysis.recommendationReason }}</pre>

        <el-divider content-position="left">客户反馈与成交</el-divider>
        <el-form :inline="true">
          <el-form-item label="客户反馈">
            <el-select v-model="feedback" style="width: 180px">
              <el-option v-for="f in feedbackOptions" :key="f" :label="f" :value="f" />
            </el-select>
          </el-form-item>
          <el-form-item label="成交状态">
            <el-select v-model="dealStatus" style="width: 160px">
              <el-option v-for="d in dealOptions" :key="d" :label="d" :value="d" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="saveFeedback">保存反馈</el-button>
          </el-form-item>
        </el-form>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { analysisPage, getAnalysis, updateFeedback } from '@/api/analysis'
import { customerPage } from '@/api/customer'

const loading = ref(false)
const saving = ref(false)
const rows: any = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const customerFilter = ref<number | null>(null)
const customerOptions: any = ref([])
const nameMap: any = reactive({})
const detailVisible = ref(false)
const detail: any = ref(null)
const feedback = ref('')
const dealStatus = ref('')

const feedbackOptions = ['接受推荐', '暂不考虑', '需要进一步沟通', '价格不合适', '套餐不符合需求', '其他']
const dealOptions = ['未成交', '已成交', '跟进中']

async function load() {
  loading.value = true
  try {
    const r: any = (await analysisPage({ page: page.value, size: size.value, customerId: customerFilter.value || undefined })).data
    rows.value = r.records; total.value = Number(r.total)
  } finally { loading.value = false }
}
function search() { page.value = 1; load() }
async function openDetail(row: any) {
  const r: any = (await getAnalysis(row.id)).data
  detail.value = r
  feedback.value = r.analysis.customerFeedback || ''
  dealStatus.value = r.analysis.dealStatus || '未成交'
  detailVisible.value = true
}
async function saveFeedback() {
  saving.value = true
  try {
    await updateFeedback(detail.value.analysis.id, { feedback: feedback.value, dealStatus: dealStatus.value })
    ElMessage.success('反馈已保存'); detailVisible.value = false; load()
  } finally { saving.value = false }
}
async function loadCustomers() {
  const c: any = (await customerPage({ page: 1, size: 1000 })).data
  customerOptions.value = c.records
  c.records.forEach((x: any) => (nameMap[x.id] = x.customerName))
}
onMounted(async () => { await loadCustomers(); await load() })
</script>

<style scoped>
.toolbar { margin-bottom: 12px; display: flex; gap: 10px; }
.reason { white-space: pre-wrap; background: #f7f8fa; padding: 12px; border-radius: 6px; line-height: 1.7; }
.green { color: #2e7d32; }
.red { color: #e63946; }
</style>
