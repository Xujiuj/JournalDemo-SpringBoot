<template>
  <header class="sticky top-0 z-50 bg-white dark:bg-slate-900 shadow-sm border-b border-slate-200 dark:border-slate-700">
    <!-- Main Header Bar -->
    <div class="bg-white dark:bg-slate-900 py-3 min-h-[64px]">
      <div class="container mx-auto px-4">
        <div class="flex justify-between items-center gap-8">
          <!-- Left: Journal Info -->
          <div class="flex items-center gap-4 flex-shrink-0 justify-center">
            <div class="w-12 h-12 bg-slate-800 dark:bg-slate-700 rounded-md flex items-center justify-center flex-shrink-0 overflow-hidden">
              <img
                v-if="logoUrl"
                :src="logoUrl"
                alt="Journal Logo"
                class="w-10 h-10 object-contain"
              />
              <span v-else class="text-white text-xl font-bold font-serif">{{ brandInitial }}</span>
            </div>
            <div class="flex flex-col justify-center">
              <div class="text-xl font-bold text-slate-800 dark:text-slate-200 leading-tight font-serif">
                {{ displayPublisher }}
              </div>
              <div class="text-sm text-slate-500 dark:text-slate-400 font-normal leading-tight -mt-0">
                ISSN: {{ displayIssn }}
              </div>
            </div>
          </div>
          
          <!-- Right: Search and Auth --> 
          <div class="flex items-center gap-4">
            <!-- Search Box -->
            <div class="transform transition-transform duration-300 hover:scale-110 origin-center">
            <GlowingSearchBox
              v-model="searchQuery"
              placeholder="Search Articles"
              @search="performSearch"
            />
            </div>
            
            <!-- Auth Section -->
            <div class="flex items-center gap-3">
              <!-- User Avatar & Menu (when logged in) -->
              <div v-if="currentUser" ref="userMenuRef" class="relative group" @mouseenter="toggleUserMenu" @mouseleave="toggleUserMenu">
                <button
                  class="flex items-center gap-3 p-2 rounded-full hover:bg-slate-100 dark:hover:bg-slate-800 transition-all duration-300 transform hover:scale-110"
                >
                  <!-- User Avatar -->
                  <div class="w-8 h-8 bg-gradient-to-r from-cyan-500 to-blue-600 rounded-full flex items-center justify-center text-white font-semibold text-sm">
                    {{ currentUser.userRealName ? currentUser.userRealName.charAt(0).toUpperCase() : 'U' }}
                  </div>
                  <svg
                    class="w-4 h-4 text-slate-600 dark:text-slate-400 transition-transform duration-200"
                    :class="{ 'rotate-180': isUserMenuOpen }"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                  </svg>
                </button>

                <!-- User Dropdown Menu -->
                <div
                  v-show="isUserMenuOpen"
                  class="absolute left-1/2 -translate-x-1/2 top-full mt-2 w-64 bg-white dark:bg-slate-800 rounded-xl shadow-xl border border-slate-200 dark:border-slate-700 py-2 z-99"
                  style="animation: dropdownSlideDown 0.3s ease-out forwards;"
                >
                  <!-- User Info Header -->
                  <div class="px-4 py-3 border-b border-slate-200 dark:border-slate-700">
                    <div class="flex items-center gap-3">
                      <div class="w-10 h-10 bg-gradient-to-r from-cyan-500 to-blue-600 rounded-full flex items-center justify-center text-white font-semibold">
                        {{ currentUser.userRealName ? currentUser.userRealName.charAt(0).toUpperCase() : 'U' }}
                      </div>
                      <div class="flex-1 min-w-0">
                        <p class="text-sm font-semibold text-slate-900 dark:text-white truncate">
                          {{ currentUser.userRealName || 'User' }}
                        </p>
                        <p class="text-xs text-slate-500 dark:text-slate-400 truncate">
                          {{ currentUser.userEmail || '' }}
                        </p>
                      </div>
                    </div>
                  </div>

                  <!-- Menu Items -->
                  <div class="py-2">
                    <router-link
                      v-for="menu in userMenus"
                      :key="menu.menuId"
                      :to="menu.menuPath"
                      @click="isUserMenuOpen = false"
                      class="flex items-center gap-3 px-4 py-2 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 hover:text-slate-900 dark:hover:text-white transition-colors"
                    >
                      <svg v-if="menu.menuIcon" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="menu.menuIcon"></path>
                      </svg>
                      <span v-else class="w-4 h-4 flex items-center justify-center">
                        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                        </svg>
                      </span>
                      {{ menu.menuName }}
                    </router-link>

                    <!-- Logout -->
                    <div class="border-t border-slate-200 dark:border-slate-700 mt-2 pt-2">
                      <button
                        @click="handleLogout"
                        class="flex items-center gap-3 px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 w-full text-left transition-colors"
                      >
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
                        </svg>
                        Logout
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Login/Register Buttons (when not logged in) -->
              <div v-else class="flex items-center gap-3">
              <router-link 
                  :to="branding.registerRoute"
                  class="inline-flex items-center gap-2 px-5 py-2 bg-transparent text-cyan-500 dark:text-cyan-400 border border-cyan-500/40 dark:border-cyan-400/40 rounded-full text-sm font-semibold hover:bg-cyan-500/10 dark:hover:bg-cyan-400/10 hover:border-cyan-500 dark:hover:border-cyan-400 transition-all duration-300 transform hover:scale-110 origin-center"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
                </svg>
                Register
              </router-link>
              <router-link 
                  :to="branding.loginRoute"
                  class="inline-flex items-center gap-2 px-5 py-2 bg-gradient-to-r from-cyan-500 via-blue-600 to-indigo-600 text-white rounded-full text-sm font-semibold hover:from-cyan-600 hover:via-blue-700 hover:to-indigo-700 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:scale-110 origin-center"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"></path>
                </svg>
                Login
              </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Navigation Bar -->
    <nav class="bg-white dark:bg-slate-900 border-t border-slate-200 dark:border-slate-700 relative z-50">
      <div class="container mx-auto px-4">
        <div class="flex items-center justify-center gap-0">
          <router-link 
            v-for="item in navItems" 
            :key="item.menuName"
            :to="item.menuPath"
            class="nav-link-gemini group relative px-5 py-3 text-slate-700 dark:text-slate-300 text-sm font-medium transition duration-300 transform origin-center hover:scale-105 hover:text-slate-900 dark:hover:text-slate-100"
            :class="{ 'text-slate-900 dark:text-slate-100': $route.path === item.menuPath }"
          >
            <span class="relative z-10">{{ item.menuName }}</span>
            
            <!-- Hover Background (lowest layer) -->
            <div class="absolute inset-0 bg-gradient-to-r from-cyan-50/80 to-blue-50/80 dark:from-cyan-900/20 dark:to-blue-900/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300 z-0"></div>
            
            <!-- Bottom Gradient Effect - appears only on hover/active, spreads from center -->
            <div class="absolute bottom-0 left-0 right-0 h-1 overflow-hidden z-20">
              <div
                class="w-full h-full bg-gradient-to-r from-cyan-600 via-blue-600 to-indigo-700 transform origin-center transition duration-500 ease-out scale-x-0 group-hover:scale-x-100 opacity-0 group-hover:opacity-100"
                :class="{ 'scale-x-100 opacity-100': $route.path === item.menuPath }"
              ></div>
            </div>
          </router-link>
        </div>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import GlowingSearchBox from '@/components/ui/GlowingSearchBox.vue'

const router = useRouter()
const store = useStore()
const searchQuery = ref('')
const ROUTES = Object.freeze({
  articles: '/articles',
  register: '/register',
  login: '/login'
})

// 从 Vuex 获取系统信息和菜单
const branding = computed(() => store.getters['sysInfo/brandingInfo'])
const navItems = computed(() => {
  const menus = store.getters['sysInfo/getMenuList'] || []
  return menus
    .filter((item) => item?.menuParentId === 0 && item?.menuPath && item.menuType === 'M')
    .sort((a, b) => (a.menuOrderNum ?? 0) - (b.menuOrderNum ?? 0))
})
const displayPublisher = computed(() => branding.value.publisher)
const displayIssn = computed(() => branding.value.issn)
const logoUrl = computed(() => branding.value.logo)
const brandInitial = computed(() => {
  const source = branding.value.shortName || branding.value.name
  return source.charAt(0).toUpperCase()
})

// 从 Vuex 获取用户信息
const currentUser = computed(() => store.getters['user/currentUser'])
const userMenus = computed(() => {
  const menus = store.getters['sysInfo/getMenuList'] || []
  return menus
    .filter((item) => item?.menuParentId === 8 && item?.menuType === 'M')
    .sort((a, b) => (a.menuOrderNum ?? 0) - (b.menuOrderNum ?? 0))
})

// 用户下拉菜单状态
const isUserMenuOpen = ref(false)
const userMenuRef = ref(null)

// Methods
const performSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: branding.value.articlesRoute, query: { search: searchQuery.value } })
  }
}

const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

const handleLogout = async () => {
  try {
    await store.dispatch('user/logout')
    isUserMenuOpen.value = false
    router.push(branding.value.homeRoute)
  } catch (error) {
    console.error('Logout failed:', error)
  }
}

// 点击外部关闭菜单
const closeUserMenu = (event) => {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
    isUserMenuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', closeUserMenu)
})

onUnmounted(() => {
  document.removeEventListener('click', closeUserMenu)
})
</script>

<style>
@import '../assets/css/header.css';

@keyframes dropdownSlideDown {
  from {
    opacity: 0;
    transform: translate(-50%, -10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0) scale(1);
  }
}
</style>