import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '首页工作台' } },
      { path: 'customers', component: () => import('../views/CustomerList.vue'), meta: { title: '客户列表' } },
      { path: 'customers/edit', component: () => import('../views/CustomerEdit.vue'), meta: { title: '客户编辑' } },
      { path: 'customers/:id', component: () => import('../views/CustomerDetail.vue'), meta: { title: '客户详情' } },
      { path: 'products', component: () => import('../views/ProductLibrary.vue'), meta: { title: '运营商套餐库' } },
      { path: 'analysis', component: () => import('../views/PlanAnalysis.vue'), meta: { title: '家庭套餐分析' } },
      { path: 'analysis/history', component: () => import('../views/AnalysisHistory.vue'), meta: { title: '推荐历史' } },
      { path: 'follow-ups', component: () => import('../views/FollowUpList.vue'), meta: { title: '待跟进' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
