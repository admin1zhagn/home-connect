<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2 class="title">家庭智联服务系统</h2>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="密码"
            :prefix-icon="Lock"
            size="large"
            @keyup.enter="onLogin"
          />
        </el-form-item>
        <el-button type="primary" :loading="loading" size="large" style="width: 100%" @click="onLogin">
          登 录
        </el-button>
      </el-form>
      <p class="tip">默认账号：admin / admin123（首次使用请尽快修改密码）</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const form = reactive({ username: 'admin', password: 'admin123' })
const loading = ref(false)

async function onLogin() {
  loading.value = true
  try {
    const res = await login(form)
    auth.setAuth(res.data.token, res.data.username)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    // 错误信息由拦截器统一提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #001529;
}
.login-card {
  width: 340px;
}
.title {
  text-align: center;
  margin: 0 0 20px;
}
.tip {
  text-align: center;
  color: #909399;
  font-size: 12px;
  margin-top: 12px;
}
</style>
