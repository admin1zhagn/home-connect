<template>
  <div class="page-container" v-loading="loading">
    <div class="card-block">
      <div class="detail-head">
        <div>
          <h2 style="margin: 0">{{ customer?.customerName }}</h2>
          <div class="muted" style="margin-top: 4px">
            {{ customer?.community || '未填小区' }} ｜ 来源：{{ customer?.source || '未填' }} ｜
            状态：<el-tag size="small">{{ customer?.status }}</el-tag>
          </div>
        </div>
        <div>
          <el-button @click="goEdit">编辑资料</el-button>
          <el-button type="primary" @click="goAnalysis">做套餐分析</el-button>
        </div>
      </div>
      <el-descriptions :column="isMobile ? 1 : 2" border size="small" style="margin-top: 12px">
        <el-descriptions-item label="联系电话">{{ customer?.phone || '未填' }}</el-descriptions-item>
        <el-descriptions-item label="下次联系">{{ customer?.nextContactAt || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ customer?.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="card-block" style="margin-top: 16px">
      <el-tabs v-model="activeTab">
        <!-- 家庭成员 -->
        <el-tab-pane label="家庭成员" name="member">
          <div class="toolbar">
            <el-button type="primary" size="small" @click="openMember(null)">+ 添加成员</el-button>
          </div>
          <el-table :data="members" size="small" border>
            <el-table-column prop="memberName" label="称呼" />
            <el-table-column prop="relation" label="关系" />
            <el-table-column prop="operator" label="运营商" />
            <el-table-column prop="fee" label="套餐费用(元)" />
            <el-table-column prop="monthlyDataGb" label="月流量(GB)" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openMember(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="removeMember(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 当前套餐 -->
        <el-tab-pane label="当前套餐" name="plan">
          <div class="toolbar">
            <el-button type="primary" size="small" @click="openPlan(null)">+ 添加套餐</el-button>
            <span class="muted">家庭当前每月通信支出：<b class="money">{{ familyMonthly }} 元</b></span>
          </div>
          <el-table :data="plans" size="small" border>
            <el-table-column prop="planName" label="套餐名称" />
            <el-table-column prop="operator" label="运营商" />
            <el-table-column prop="planType" label="类型" />
            <el-table-column label="月费" width="110">
              <template #default="{ row }">{{ row.monthlyFee }}<span v-if="row.extraMonthlyFee" class="muted"> +{{ row.extraMonthlyFee }}</span></template>
            </el-table-column>
            <el-table-column label="精度" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="row.feeAccuracy === 'estimated' ? 'warning' : 'success'">
                  {{ row.feeAccuracy === 'estimated' ? '估算' : '精确' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="row.effectiveStatus === 'active' ? '' : 'info'">
                  {{ row.effectiveStatus === 'active' ? '生效中' : '已停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openPlan(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="removePlan(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 服务记录 -->
        <el-tab-pane label="服务记录" name="record">
          <div class="toolbar">
            <el-button type="primary" size="small" @click="openRecord(null)">+ 添加记录</el-button>
          </div>
          <el-table :data="records" size="small" border>
            <el-table-column prop="serviceType" label="类型" width="110" />
            <el-table-column prop="serviceTime" label="时间" width="160" />
            <el-table-column prop="serviceContent" label="内容" show-overflow-tooltip />
            <el-table-column prop="serviceResult" label="结果" show-overflow-tooltip />
            <el-table-column prop="followUpDate" label="下次跟进" width="160" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openRecord(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="removeRecord(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 授权记录 -->
        <el-tab-pane label="授权记录" name="consent">
          <div class="toolbar">
            <el-button type="primary" size="small" @click="openConsent()">+ 新增授权</el-button>
          </div>
          <el-table :data="consents" size="small" border>
            <el-table-column prop="consentType" label="授权类型" width="140" />
            <el-table-column label="是否同意" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="row.consentStatus ? 'success' : 'danger'">{{ row.consentStatus ? '已同意' : '已撤回' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="consentScope" label="授权范围" show-overflow-tooltip />
            <el-table-column prop="consentTime" label="授权时间" width="160" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button v-if="row.consentStatus" link type="warning" size="small" @click="revoke(row)">撤回</el-button>
                <el-button link type="danger" size="small" @click="removeConsent(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 成员弹窗 -->
    <el-dialog v-model="memberVisible" :title="memberForm.id ? '编辑成员' : '添加成员'" width="520px">
      <el-form :model="memberForm" label-width="90px">
        <el-form-item label="称呼"><el-input v-model="memberForm.memberName" /></el-form-item>
        <el-form-item label="关系">
          <el-select v-model="memberForm.relation" style="width: 100%">
            <el-option v-for="r in relationOptions" :key="r" :label="r" :value="r" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="memberForm.phone" /></el-form-item>
        <el-form-item label="运营商">
          <el-select v-model="memberForm.operator" style="width: 100%" placeholder="请选择运营商">
            <el-option v-for="o in operatorOptions" :key="o" :label="o" :value="o" />
          </el-select>
        </el-form-item>
        <el-form-item label="套餐费用(元)"><el-input-number v-model="memberForm.fee" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="月流量(GB)"><el-input-number v-model="memberForm.monthlyDataGb" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="月通话(分)"><el-input-number v-model="memberForm.monthlyCallMinutes" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="memberForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveMember">保存</el-button>
      </template>
    </el-dialog>

    <!-- 套餐弹窗 -->
    <el-dialog v-model="planVisible" :title="planForm.id ? '编辑套餐' : '添加套餐'" width="600px">
      <el-form :model="planForm" label-width="100px">
        <el-form-item label="关联成员">
          <el-select v-model="planForm.memberId" clearable placeholder="可不选" style="width: 100%">
            <el-option v-for="m in members" :key="m.id" :label="m.memberName" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="套餐名称"><el-input v-model="planForm.planName" /></el-form-item>
        <el-form-item label="运营商"><el-input v-model="planForm.operator" /></el-form-item>
        <el-form-item label="套餐类型">
          <el-select v-model="planForm.planType" style="width: 100%">
            <el-option v-for="t in planTypeOptions" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="月费(元)"><el-input-number v-model="planForm.monthlyFee" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="额外月费(元)"><el-input-number v-model="planForm.extraMonthlyFee" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="流量(GB)"><el-input-number v-model="planForm.dataGb" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="宽带速率"><el-input-number v-model="planForm.broadbandSpeed" :min="0" /></el-form-item>
        <el-form-item label="合约期限(月)"><el-input-number v-model="planForm.contractMonths" :min="0" /></el-form-item>
        <el-form-item label="设备费用"><el-input-number v-model="planForm.equipmentFee" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="合约到期日"><el-date-picker v-model="planForm.contractEndDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="费用精度">
          <el-radio-group v-model="planForm.feeAccuracy">
            <el-radio value="confirmed">精确</el-radio>
            <el-radio value="estimated">估算</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="planForm.effectiveStatus">
            <el-radio value="active">生效中</el-radio>
            <el-radio value="inactive">已停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="planForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="savePlan">保存</el-button>
      </template>
    </el-dialog>

    <!-- 服务记录弹窗 -->
    <el-dialog v-model="recordVisible" :title="recordForm.id ? '编辑记录' : '添加记录'" width="600px">
      <el-form :model="recordForm" label-width="90px">
        <el-form-item label="服务类型">
          <el-select v-model="recordForm.serviceType" style="width: 100%">
            <el-option v-for="s in serviceOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务时间"><el-date-picker v-model="recordForm.serviceTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item>
        <el-form-item label="服务内容"><el-input v-model="recordForm.serviceContent" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="处理结果"><el-input v-model="recordForm.serviceResult" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="下次跟进"><el-date-picker v-model="recordForm.followUpDate" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="recordForm.status" style="width: 100%">
            <el-option v-for="s in recordStatusOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="recordForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveRecord">保存</el-button>
      </template>
    </el-dialog>

    <!-- 授权弹窗 -->
    <el-dialog v-model="consentVisible" title="新增授权记录" width="520px">
      <el-form :model="consentForm" label-width="90px">
        <el-form-item label="授权类型">
          <el-select v-model="consentForm.consentType" style="width: 100%">
            <el-option v-for="c in consentTypeOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否同意"><el-switch v-model="consentForm.consentStatus" /></el-form-item>
        <el-form-item label="授权范围"><el-input v-model="consentForm.consentScope" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="consentForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="consentVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveConsent">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomer } from '@/api/customer'
import { memberList, saveMember, deleteMember } from '@/api/member'
import { planList, savePlan, deletePlan } from '@/api/plan'
import { recordList, saveRecord, deleteRecord } from '@/api/record'
import { consentList, saveConsent as addConsent, revokeConsent, deleteConsent } from '@/api/consent'
import { useScreen } from '@/composables/useScreen'

const route = useRoute()
const router = useRouter()
const { isMobile } = useScreen()
const cid = route.params.id
const loading = ref(false)
const saving = ref(false)
const activeTab = ref('member')
const customer: any = ref(null)

const members: any = ref([])
const plans: any = ref([])
const records: any = ref([])
const consents: any = ref([])

const familyMonthly = computed(() => {
  return plans.value
    .filter((p: any) => p.effectiveStatus === 'active')
    .reduce((s: number, p: any) => s + Number(p.monthlyFee || 0) + Number(p.extraMonthlyFee || 0), 0)
    .toFixed(2)
})

const relationOptions = ['本人', '配偶', '父亲', '母亲', '儿子', '女儿', '其他']
const operatorOptions = ['电信', '移动', '联通', '其他']
const planTypeOptions = ['手机套餐', '家庭宽带', '电视业务', '家庭副卡', '流量包', '其他增值业务']
const serviceOptions = ['套餐咨询', '宽带安装', '宽带维修', '网络优化', 'FTTR', '智能门锁', '监控安装', '智能家居咨询', '其他']
const recordStatusOptions = ['已完成', '处理中', '待跟进']
const consentTypeOptions = ['信息收集', '套餐分析', '服务记录', '数据使用']

const memberVisible = ref(false)
const planVisible = ref(false)
const recordVisible = ref(false)
const consentVisible = ref(false)

const blank = () => ({ id: null, customerId: cid, fee: undefined })
const memberForm: any = reactive(blank())
const planForm: any = reactive(blank())
const recordForm: any = reactive(blank())
const consentForm: any = reactive({ customerId: cid, consentType: '信息收集', consentStatus: true, consentScope: '', remark: '' })

async function loadAll() {
  loading.value = true
  try {
    customer.value = (await getCustomer(cid)).data
    members.value = (await memberList(cid)).data || []
    plans.value = (await planList(cid)).data || []
    records.value = (await recordList(cid)).data || []
    consents.value = (await consentList(cid)).data || []
  } finally {
    loading.value = false
  }
}

function openMember(row: any) {
  Object.assign(memberForm, blank(), row || {})
  memberVisible.value = true
}
async function saveMember() {
  if (!memberForm.memberName) return ElMessage.warning('请填写成员称呼')
  saving.value = true
  try {
    await saveMember({ ...memberForm, customerId: cid })
    ElMessage.success('已保存')
    memberVisible.value = false
    await loadAll()
  } finally { saving.value = false }
}
async function removeMember(row: any) {
  await ElMessageBox.confirm('确认删除该成员？', '提示', { type: 'warning' })
  await deleteMember(row.id); ElMessage.success('已删除'); await loadAll()
}

function openPlan(row: any) {
  Object.assign(planForm, blank(), row || {}, { monthlyFee: 0, extraMonthlyFee: 0, feeAccuracy: 'confirmed', effectiveStatus: 'active' })
  planVisible.value = true
}
async function savePlan() {
  if (!planForm.planName) return ElMessage.warning('请填写套餐名称')
  saving.value = true
  try {
    await savePlan({ ...planForm, customerId: cid })
    ElMessage.success('已保存')
    planVisible.value = false
    await loadAll()
  } finally { saving.value = false }
}
async function removePlan(row: any) {
  await ElMessageBox.confirm('确认删除该套餐？', '提示', { type: 'warning' })
  await deletePlan(row.id); ElMessage.success('已删除'); await loadAll()
}

function openRecord(row: any) {
  Object.assign(recordForm, blank(), row || {}, { status: '已完成' })
  recordVisible.value = true
}
async function saveRecord() {
  if (!recordForm.serviceType) return ElMessage.warning('请选择服务类型')
  saving.value = true
  try {
    await saveRecord({ ...recordForm, customerId: cid })
    ElMessage.success('已保存')
    recordVisible.value = false
    await loadAll()
  } finally { saving.value = false }
}
async function removeRecord(row: any) {
  await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
  await deleteRecord(row.id); ElMessage.success('已删除'); await loadAll()
}

function openConsent() {
  Object.assign(consentForm, { customerId: cid, consentType: '信息收集', consentStatus: true, consentScope: '', remark: '' })
  consentVisible.value = true
}
async function saveConsent() {
  saving.value = true
  try {
    await addConsent({ ...consentForm })
    ElMessage.success('已保存')
    consentVisible.value = false
    await loadAll()
  } finally { saving.value = false }
}
async function revoke(row: any) {
  await revokeConsent(row.id); ElMessage.success('已撤回'); await loadAll()
}
async function removeConsent(row: any) {
  await ElMessageBox.confirm('确认删除该授权记录？', '提示', { type: 'warning' })
  await deleteConsent(row.id); ElMessage.success('已删除'); await loadAll()
}

function goEdit() { router.push(`/customers/edit?id=${cid}`) }
function goAnalysis() { router.push(`/analysis?customerId=${cid}`) }

onMounted(loadAll)
</script>

<style scoped>
.detail-head { display: flex; justify-content: space-between; align-items: flex-start; }
.toolbar { margin-bottom: 10px; display: flex; align-items: center; gap: 12px; }
.money { color: #e63946; }
.muted { color: #909399; font-size: 13px; }
</style>
