<template>
  <el-container class="layout">
    <!-- 桌面端侧边栏 -->
    <el-aside v-if="!isMobile" width="210px" class="aside">
      <div class="logo">家庭智联服务</div>
      <el-menu :default-active="activeMenu" router class="menu">
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">
          <el-icon><component :is="m.icon" /></el-icon>
          <span>{{ m.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 移动端抽屉菜单 -->
    <el-drawer v-model="drawer" title="菜单" direction="ltr" size="60%">
      <el-menu :default-active="activeMenu" router @select="drawer = false">
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">
          <el-icon><component :is="m.icon" /></el-icon>
          <span>{{ m.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-drawer>

    <el-container>
      <el-header class="header">
        <el-icon class="hamburger" v-if="isMobile" @click="drawer = true"><Menu /></el-icon>
        <span class="title">{{ currentTitle }}</span>
        <div class="user">
          <el-dropdown @command="onCommand">
            <span class="username">{{ username }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view :key="$route.fullPath" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const username = computed(() => auth.username || localStorage.getItem('username') || '用户')

const menus = [
  { path: '/dashboard', title: '首页工作台', icon: 'HomeFilled' },
  { path: '/customers', title: '客户列表', icon: 'User' },
  { path: '/analysis', title: '家庭套餐分析', icon: 'DataAnalysis' },
  { path: '/analysis/history', title: '推荐历史', icon: 'List' },
  { path: '/products', title: '运营商套餐库', icon: 'Goods' },
  { path: '/follow-ups', title: '待跟进', icon: 'Bell' }
]

const activeMenu = computed(() => {
  const p = route.path
  if (p.startsWith('/analysis/history')) return '/analysis/history'
  if (p.startsWith('/analysis')) return '/analysis'
  return p
})

const currentTitle = computed(() => (route.meta.title as string) || '家庭智联服务系统')

const isMobile = ref(false)
const drawer = ref(false)

function checkMobile() {
  isMobile.value = window.innerWidth <= 768
}
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})
onUnmounted(() => window.removeEventListener('resize', checkMobile))

function onCommand(cmd: string) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
      .then(() => {
        auth.logout()
        router.push('/login')
      })
      .catch(() => {})
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}
.aside {
  background: #001529;
  color: #fff;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  background: #000c17;
}
.menu {
  border-right: none;
  background: #001529;
}
.menu :deep(.el-menu-item) {
  color: #bfcbd9;
}
.menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: #1890ff;
}
.header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}
.hamburger {
  font-size: 22px;
  margin-right: 12px;
  cursor: pointer;
}
.title {
  font-size: 16px;
  font-weight: bold;
  flex: 1;
}
.user .username {
  cursor: pointer;
}
.main {
  background: #f5f7fa;
  padding: 0;
}
</style>
