import { onMounted, onUnmounted, ref } from 'vue'

const MOBILE_MAX_WIDTH = 768

/**
 * 响应式屏幕判断：窗口宽度 <= 768px 视为移动端。
 * 在 setup 中调用即可，自动监听 resize。
 */
export function useScreen() {
  const isMobile = ref(
    typeof window !== 'undefined' && window.innerWidth <= MOBILE_MAX_WIDTH
  )

  function onResize() {
    isMobile.value = window.innerWidth <= MOBILE_MAX_WIDTH
  }

  onMounted(() => window.addEventListener('resize', onResize))
  onUnmounted(() => window.removeEventListener('resize', onResize))

  return { isMobile }
}
