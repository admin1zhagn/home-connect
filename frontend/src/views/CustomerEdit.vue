<template>
  <div class="page-container">
    <div class="card-block" style="max-width: 640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="客户称呼" required>
          <el-input v-model="form.customerName" placeholder="如：张先生 / 张总" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="选填" />
        </el-form-item>
        <el-form-item label="所在小区">
          <el-input v-model="form.community" />
        </el-form-item>
        <el-form-item label="客户来源">
          <el-select v-model="form.source" placeholder="请选择" style="width: 100%">
            <el-option v-for="s in sourceOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户状态">
          <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
            <el-option v-for="s in statusOptions" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="下次联系">
          <el-date-picker
            v-model="form.nextContactAt"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="设置下次联系时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">保存</el-button>
          <el-button @click="back">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCustomer, saveCustomer } from '@/api/customer'

const route = useRoute()
const router = useRouter()
const sourceOptions = ['公司工作接触', '客户主动咨询', '熟人介绍', '自主拓展', '其他']
const statusOptions = ['新客户', '已沟通', '待跟进', '已成交', '暂无需求']
const saving = ref(false)
const form = reactive<any>({
  id: undefined,
  customerName: '',
  phone: '',
  community: '',
  source: '',
  status: '新客户',
  nextContactAt: '',
  remark: ''
})

function resetForm() {
  Object.assign(form, {
    id: undefined,
    customerName: '',
    phone: '',
    community: '',
    source: '',
    status: '新客户',
    nextContactAt: '',
    remark: ''
  })
}

async function loadForm() {
  const id = route.query.id
  if (id) {
    const res: any = await getCustomer(id)
    if (res && res.data) {
      Object.assign(form, res.data)
      form.id = id
    }
  } else {
    resetForm()
  }
}

onMounted(loadForm)
// 路由复用（同组件切换 id / add↔edit）时重新加载，避免表单不回显或残留旧数据
watch(
  () => route.fullPath,
  () => {
    if (route.path === '/customers/edit') loadForm()
  }
)

async function save() {
  if (!form.customerName) {
    ElMessage.warning('请填写客户称呼')
    return
  }
  saving.value = true
  try {
    await saveCustomer(form)
    ElMessage.success('保存成功')
    back()
  } finally {
    saving.value = false
  }
}
function back() {
  router.push('/customers')
}
</script>
