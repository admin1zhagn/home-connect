<template>
  <div class="page-container" v-loading="loading">
    <div class="card-block">
      <h3 style="margin-top: 0">家庭套餐分析</h3>
      <el-form label-width="90px">
        <el-form-item label="选择客户">
          <el-select v-model="customerId" filterable placeholder="请选择客户" style="width: 100%" @change="onCustomerChange">
            <el-option v-for="c in customerOptions" :key="c.id" :label="`${c.customerName}（${c.community || '未填小区'}）`" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-alert v-if="!customerId" type="info" :closable="false" title="请先选择客户，系统将自动载入其家庭成员、当前套餐与每月通信支出。" />

      <template v-if="customerId">
        <!-- 客户现状 -->
        <el-divider content-position="left">客户现状</el-divider>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="家庭成员">{{ members.length }} 人</el-descriptions-item>
          <el-descriptions-item label="当前月支出">{{ currentMonthly }} 元<span v-if="isEstimated" class="warn">（含估算）</span></el-descriptions-item>
          <el-descriptions-item label="家庭流量需求">{{ familyData }} GB</el-descriptions-item>
          <el-descriptions-item label="宽带情况">{{ broadbandText }}</el-descriptions-item>
          <el-descriptions-item label="当前合约限制">{{ contractText }}</el-descriptions-item>
        </el-descriptions>

        <!-- 推荐套餐组合 -->
        <el-divider content-position="left">选择推荐套餐组合</el-divider>
        <el-table :data="productOptions" border size="small" @selection-change="onSelect">
          <el-table-column type="selection" width="45" />
          <el-table-column prop="planName" label="套餐名称" min-width="140" />
          <el-table-column prop="operator" label="运营商" width="90" />
          <el-table-column prop="planType" label="类型" width="100" />
          <el-table-column label="标准/优惠/结束后" width="180">
            <template #default="{ row }">{{ row.standardFee }} ｜ <b class="red">{{ row.discountFee || '-' }}</b> ｜ {{ row.afterDiscountFee || '-' }}</template>
          </el-table-column>
          <el-table-column prop="discountMonths" label="优惠月" width="70" />
          <el-table-column label="一次性费" width="100">
            <template #default="{ row }">{{ Number(row.installationFee||0) + Number(row.equipmentFee||0) }}</template>
          </el-table-column>
        </el-table>

        <el-form label-width="110px" style="margin-top: 12px">
          <el-form-item label="对比周期(月)">
            <el-input-number v-model="comparisonMonths" :min="1" :max="120" />
            <span class="muted" style="margin-left: 8px">留空或默认：取所选手套餐的最大优惠/合约月数，不足 12 取 12</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="calcLoading" @click="preview">预览测算</el-button>
            <el-button type="success" :loading="calcLoading" :disabled="!canSave" @click="save">保存并生成推荐</el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>

    <!-- 测算结果 -->
    <div class="card-block" style="margin-top: 16px" v-if="result">
      <h3 style="margin-top: 0">测算结果 <el-tag :type="result.estimated ? 'warning' : 'success'" size="small">{{ result.estimated ? '含估算数据' : '数据精确' }}</el-tag></h3>
      <el-row :gutter="12">
        <el-col :span="6"><div class="stat"><div class="num">{{ result.currentMonthlyFee }}</div><div class="lab">当前月费(元)</div></div></el-col>
        <el-col :span="6"><div class="stat green"><div class="num">{{ result.recommendedMonthlyFee }}</div><div class="lab">优惠期月费(元)</div></div></el-col>
        <el-col :span="6"><div class="stat"><div class="num">{{ result.afterDiscountMonthlyFee }}</div><div class="lab">结束后月费(元)</div></div></el-col>
        <el-col :span="6"><div class="stat"><div class="num">{{ result.oneTimeFee }}</div><div class="lab">一次性费用(元)</div></div></el-col>
        <el-col :span="6"><div class="stat"><div class="num">{{ result.comparisonMonths }}</div><div class="lab">对比周期(月)</div></div></el-col>
        <el-col :span="6"><div class="stat"><div class="num">{{ result.currentTotalFee }}</div><div class="lab">当前周期总费(元)</div></div></el-col>
        <el-col :span="6"><div class="stat"><div class="num">{{ result.recommendedTotalFee }}</div><div class="lab">推荐周期总费(元)</div></div></el-col>
        <el-col :span="6"><div class="stat" :class="conclusionClass"><div class="num">{{ result.estimatedSaving }}</div><div class="lab">预计差额(元)</div></div></el-col>
      </el-row>

      <el-alert :type="conclusionType" :closable="false" style="margin: 12px 0">
        <template #title>
          <span v-if="result.conclusion === 'SAVING'">预计在对比周期内可节省约 {{ result.estimatedSaving }} 元。</span>
          <span v-else-if="result.conclusion === 'NO_SAVING'">按当前选择，预计对比周期内费用不降反增 {{ Math.abs(Number(result.estimatedSaving)) }} 元，请向客户说明。</span>
          <span v-else>部分套餐缺少优惠结束后价格或合约信息，无法给出确定节省结论，需补充资费后再次测算。</span>
        </template>
      </el-alert>

      <el-divider content-position="left">推荐套餐明细</el-divider>
      <el-table :data="result.items" border size="small">
        <el-table-column prop="productName" label="套餐名称" min-width="140" />
        <el-table-column prop="operator" label="运营商" width="90" />
        <el-table-column prop="planType" label="类型" width="100" />
        <el-table-column label="标准/优惠月费" width="150">
          <template #default="{ row }">{{ row.monthlyFee }} ｜ <b class="red">{{ row.discountFee || '-' }}</b></template>
        </el-table-column>
        <el-table-column prop="discountMonths" label="优惠月" width="70" />
        <el-table-column prop="afterDiscountFee" label="结束后月费" width="100" />
        <el-table-column label="一次性费" width="100">
          <template #default="{ row }">{{ Number(row.installationFee||0) + Number(row.equipmentFee||0) }}</template>
        </el-table-column>
      </el-table>

      <el-divider content-position="left">推荐理由</el-divider>
      <pre class="reason">{{ result.recommendationReason }}</pre>
      <div v-if="savedId" class="saved-tip">
        已保存为分析编号记录（ID {{ savedId }}），可在「推荐历史」中查看与补充客户反馈。
        <el-button link type="primary" @click="$router.push('/analysis/history')">前往推荐历史</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { customerPage } from '@/api/customer'
import { memberList } from '@/api/member'
import { planList } from '@/api/plan'
import { productPage } from '@/api/product'
import { previewAnalysis, saveAnalysis } from '@/api/analysis'

const route = useRoute()
const loading = ref(false)
const calcLoading = ref(false)
const customerId = ref<number | null>(null)
const customerOptions: any = ref([])
const productOptions: any = ref([])
const members: any = ref([])
const plans: any = ref([])
const selectedIds: any = ref([])
const comparisonMonths = ref<number | null>(null)
const result: any = ref(null)
const savedId = ref<number | null>(null)
const isEstimated = ref(false)

const canSave = computed(() => result.value && selectedIds.value.length > 0)

const currentMonthly = computed(() =>
  plans.value.filter((p: any) => p.effectiveStatus === 'active')
    .reduce((s: number, p: any) => s + Number(p.monthlyFee || 0) + Number(p.extraMonthlyFee || 0), 0)
    .toFixed(2))
const familyData = computed(() => members.value.reduce((s: number, m: any) => s + Number(m.monthlyDataGb || 0), 0))
const broadbandText = computed(() => {
  const b = plans.value.find((p: any) => p.planType === '家庭宽带')
  return b ? `${b.broadbandSpeed || '?'}Mbps（${b.operator}）` : '未录入宽带'
})
const contractText = computed(() => {
  const c = plans.value.filter((p: any) => p.contractEndDate)
  return c.length ? c.map((p: any) => `${p.planName}:${p.contractEndDate}`).join('；') : '无明确合约限制'
})
const conclusionClass = computed(() => {
  if (!result.value) return ''
  if (result.value.conclusion === 'SAVING') return 'green'
  if (result.value.conclusion === 'NO_SAVING') return 'red'
  return 'warn'
})
const conclusionType = computed(() => {
  if (!result.value) return 'info'
  return { SAVING: 'success', NO_SAVING: 'warning', VERIFY: 'info' }[result.value.conclusion]
})

async function loadOptions() {
  const c: any = (await customerPage({ page: 1, size: 1000 })).data
  customerOptions.value = c.records
  const p: any = (await productPage({ page: 1, size: 1000, status: 1 })).data
  productOptions.value = p.records
}
async function onCustomerChange(id: number) {
  result.value = null; savedId.value = null; selectedIds.value = []
  members.value = (await memberList(id)).data || []
  plans.value = (await planList(id)).data || []
  isEstimated.value = plans.value.some((p: any) => p.feeAccuracy === 'estimated')
}
function onSelect(rows: any[]) { selectedIds.value = rows.map((r) => r.id) }

async function preview() {
  if (!customerId.value) return ElMessage.warning('请先选择客户')
  if (selectedIds.value.length === 0) return ElMessage.warning('请至少选择一个推荐套餐')
  calcLoading.value = true; savedId.value = null
  try {
    const r: any = (await previewAnalysis({
      customerId: customerId.value,
      selectedProductIds: selectedIds.value,
      comparisonMonths: comparisonMonths.value || null
    })).data
    result.value = r
  } finally { calcLoading.value = false }
}
async function save() {
  calcLoading.value = true
  try {
    const r: any = (await saveAnalysis({
      customerId: customerId.value,
      selectedProductIds: selectedIds.value,
      comparisonMonths: comparisonMonths.value || null
    })).data
    result.value = r
    savedId.value = r.analysisId
    ElMessage.success('推荐方案已保存')
  } finally { calcLoading.value = false }
}

onMounted(async () => {
  loading.value = true
  try { await loadOptions() } finally { loading.value = false }
  const preset = Number(route.query.customerId)
  if (preset) { customerId.value = preset; await onCustomerChange(preset) }
})
</script>

<style scoped>
.stat { border: 1px solid #ebeef5; border-radius: 8px; padding: 12px; text-align: center; margin-bottom: 12px; background: #fafafa; }
.stat .num { font-size: 22px; font-weight: 700; }
.stat .lab { font-size: 12px; color: #909399; margin-top: 4px; }
.stat.green .num { color: #2e7d32; }
.stat.red .num { color: #e63946; }
.stat.warn .num { color: #e6a23c; }
.reason { white-space: pre-wrap; background: #f7f8fa; padding: 12px; border-radius: 6px; line-height: 1.7; }
.red { color: #e63946; }
.warn { color: #e6a23c; }
.muted { color: #909399; }
.saved-tip { margin-top: 10px; color: #2e7d32; }
</style>
