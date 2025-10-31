<template>
  <div id="app">
    <Header />
    <main>
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <Footer />
    
    <!-- 回到顶部按钮 -->
    <button 
      v-show="showBackToTop" 
      @click="scrollToTop"
      class="back-to-top"
      :class="{ 'show': showBackToTop }"
      title="回到顶部"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path>
      </svg>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'

import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import { scrollToTop, onScroll } from './utils/scroll.js'

const store = useStore()
const showBackToTop = ref(false)


// 滚动监听
let removeScrollListener = null

const handleScroll = (position) => {
  // 当滚动超过300px时显示回到顶部按钮
  showBackToTop.value = position.y > 300
}

onMounted(async () => {
  try {
    await Promise.all([
      store.dispatch('sysInfo/fetchSysInfo'),
      store.dispatch('sysInfo/fetchMenuList')
    ])
  } catch (error) {
    console.error('Failed to initialize app data:', error)
  }
  
  // 开始监听滚动事件
  removeScrollListener = onScroll(handleScroll)
})

onUnmounted(() => {
  // 清理滚动监听
  if (removeScrollListener) {
    removeScrollListener()
  }
})
</script>

<style>
@import './assets/css/global.css';
@import './assets/css/forms.css';

/* 回到顶部按钮样式 */
.back-to-top {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  width: 3rem;
  height: 3rem;
  background: linear-gradient(135deg, #06b6d4, #3b82f6, #6366f1);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  z-index: 1000;
  opacity: 0;
  transform: translateY(20px);
}

.back-to-top:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  background: linear-gradient(135deg, #0891b2, #2563eb, #4f46e5);
}

.back-to-top.show {
  opacity: 1;
  transform: translateY(0);
}

.back-to-top:active {
  transform: translateY(0);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .back-to-top {
    bottom: 1rem;
    right: 1rem;
    width: 2.5rem;
    height: 2.5rem;
  }
}
</style>
