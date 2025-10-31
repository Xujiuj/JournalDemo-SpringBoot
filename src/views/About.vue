<template>
  <PageScaffold :show-progress="true" :meteor-count="25">
    <div class="about min-h-screen relative overflow-hidden">
      <!-- Hero Section -->
      <section class="hero-section relative z-10 flex items-center justify-center bg-transparent" style="height: 100vh;">
        <div class="container mx-auto px-4 w-full h-full flex items-center justify-center">
          <div class="max-w-6xl mx-auto w-full">
            <div class="text-center mb-16">
              <h1 class="text-4xl lg:text-5xl xl:text-6xl font-bold text-white leading-tight font-serif mb-6 animate-fade-in drop-shadow-2xl">
                {{ displayName }}
              </h1>
              <p class="text-lg lg:text-xl text-slate-100 leading-relaxed mb-8 animate-slide-up drop-shadow-lg">
                ISSN: {{ displayIssn }}
              </p>
            </div>

            <!-- Journal Info Cards -->
            <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
              <div class="smooth-card smooth-card-cyan animate-slide-up">
                <div class="text-center">
                  <h3 class="text-lg font-semibold text-white mb-2">Impact Factor</h3>
                  <p class="text-3xl font-bold text-cyan-400">{{ displayImpactFactor }}</p>
                </div>
              </div>
              <div class="smooth-card smooth-card-blue animate-slide-up">
                <div class="text-center">
                  <h3 class="text-lg font-semibold text-white mb-2">Founded</h3>
                  <p class="text-2xl font-bold text-blue-400">{{ displayFoundedYear }}</p>
                </div>
              </div>
              <div class="smooth-card smooth-card-green animate-slide-up">
                <div class="text-center">
                  <h3 class="text-lg font-semibold text-white mb-2">Publisher</h3>
                  <p class="text-lg text-slate-200">{{ displayPublisher }}</p>
                </div>
              </div>
              <div class="smooth-card smooth-card-purple animate-slide-up">
                <div class="text-center">
                  <h3 class="text-lg font-semibold text-white mb-2">Website</h3>
                  <a :href="displayWebsite" target="_blank" class="text-cyan-400 hover:text-cyan-300 transition-colors duration-300">
                    Visit Website
                  </a>
                </div>
              </div>
            </div>

            <!-- Journal Description -->
            <div class="smooth-card smooth-card-blue animate-slide-up">
              <h2 class="text-2xl font-bold text-white mb-6 text-center">About the Journal</h2>
              <p class="text-lg text-slate-200 leading-relaxed mb-6 text-center">
                {{ displayDescription }}
              </p>
              <p class="text-base text-slate-300 leading-relaxed text-center">
                {{ displayMission }}
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- Editorial Team Section -->
      <section class="relative z-10 py-20">
        <div class="container mx-auto px-4">
          <div class="text-center mb-16">
            <h2 class="text-3xl lg:text-4xl font-bold text-white mb-4 font-serif drop-shadow-2xl">Editorial Team</h2>
            <p class="text-lg text-slate-200 drop-shadow-lg">Meet our distinguished editorial board</p>
          </div>

          <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            <div v-for="member in editorialTeam" :key="member.teamId" class="smooth-card smooth-card-cyan animate-slide-up">
              <div class="text-center">
                <div class="w-20 h-20 mx-auto mb-4 bg-gradient-to-br from-cyan-500 to-blue-500 rounded-full flex items-center justify-center transition-all duration-300">
                  <i class="text-2xl text-white">👤</i>
                </div>
                <h3 class="text-xl font-bold text-white mb-2 transition-colors duration-300">{{ member.teamUser.userRealName }}</h3>
                <p class="text-lg font-semibold text-cyan-400 mb-2">{{ member.teamTitle }}</p>
                <p class="text-slate-200 mb-2">{{ member.teamUser.userAffiliation }}</p>
                <p v-if="member.teamUser.userEmail" class="text-sm text-cyan-400 mb-4 transition-colors duration-300">{{ member.teamUser.userEmail }}</p>
              </div>
              <p v-if="member.teamUser.userBiography" class="text-slate-300 text-sm mb-4 text-center">{{ member.teamUser.userBiography }}</p>
              <div v-if="member.teamUser.userKeywords" class="text-center">
                <h4 class="text-sm font-semibold text-white mb-2">Keywords</h4>
                <p class="text-slate-300 text-sm">{{ member.teamUser.userKeywords }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Contact Information Section -->
      <section class="relative z-10 py-20">
        <div class="container mx-auto px-4">
          <div class="text-center mb-16">
            <h2 class="text-3xl lg:text-4xl font-bold text-white mb-4 font-serif drop-shadow-2xl">Contact Information</h2>
            <p class="text-lg text-slate-200 drop-shadow-lg">Get in touch with our editorial team</p>
          </div>

          <div class="grid md:grid-cols-3 gap-8">
            <div class="smooth-card smooth-card-green animate-slide-up text-center">
              <div class="w-16 h-16 mx-auto mb-4 bg-gradient-to-br from-cyan-500 to-blue-500 rounded-full flex items-center justify-center transition-all duration-300">
                <i class="text-2xl text-white">📍</i>
              </div>
              <h3 class="text-xl font-bold text-white mb-3">Editorial Office</h3>
              <p class="text-slate-200">{{ displayAddress }}</p>
            </div>

            <div class="smooth-card smooth-card-blue animate-slide-up text-center">
              <div class="w-16 h-16 mx-auto mb-4 bg-gradient-to-br from-cyan-500 to-blue-500 rounded-full flex items-center justify-center transition-all duration-300">
                <i class="text-2xl text-white">✉️</i>
              </div>
              <h3 class="text-xl font-bold text-white mb-3">Email</h3>
              <a :href="`mailto:${displayEmail}`" class="text-cyan-400 hover:text-cyan-300 transition-colors duration-300">
                {{ displayEmail }}
              </a>
            </div>

            <div class="smooth-card smooth-card-purple animate-slide-up text-center">
              <div class="w-16 h-16 mx-auto mb-4 bg-gradient-to-br from-cyan-500 to-blue-500 rounded-full flex items-center justify-center transition-all duration-300">
                <i class="text-2xl text-white">📞</i>
              </div>
              <h3 class="text-xl font-bold text-white mb-3">Phone</h3>
              <a :href="`tel:${displayPhone}`" class="text-cyan-400 hover:text-cyan-300 transition-colors duration-300">
                {{ displayPhone }}
              </a>
            </div>
          </div>
        </div>
      </section>
    </div>
  </PageScaffold>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { editorialApi } from '../api'
import { useStore } from 'vuex'
import PageScaffold from '@/components/layout/PageScaffold.vue'

const store = useStore()

// 从 Vuex 获取系统信息
const branding = computed(() => store.getters['sysInfo/brandingInfo'])
const displayName = computed(() => branding.value.name)
const displayIssn = computed(() => branding.value.issn)
const displayImpactFactor = computed(() => branding.value.impactFactor)
const displayFoundedYear = computed(() => branding.value.foundedYear)
const displayPublisher = computed(() => branding.value.publisher)
const displayWebsite = computed(() => branding.value.website)
const displayMission = computed(() => branding.value.mission)
const displayDescription = computed(() => branding.value.description)
const displayEmail = computed(() => branding.value.email)
const displayPhone = computed(() => branding.value.phone)
const displayAddress = computed(() => branding.value.address)

const editorialTeam = ref([])
const loading = ref(false)

const extractList = (payload) => {
  if (!payload) {
    return []
  }

  if (Array.isArray(payload)) {
    return payload
  }

  if (Array.isArray(payload.data)) {
    return payload.data
  }

  if (Array.isArray(payload.rows)) {
    return payload.rows
  }

  if (payload.data && Array.isArray(payload.data.data)) {
    return payload.data.data
  }

  return []
}

const loadData = async () => {
  loading.value = true
  try {
    const teamResponse = await editorialApi.getEditorialTeam({
      pageNum: 1,
      pageSize: 100,
      teamStatus: 1
    })
    console.log(teamResponse)
    editorialTeam.value = extractList(teamResponse)
  } catch (error) {
    console.error('Failed to load data:', error)
    // 设置默认数据，确保页面正常显示
    editorialTeam.value = [
      {
        teamId: 1,
        teamName: 'Dr. Sarah Johnson',
        teamTitle: 'Editor-in-Chief',
        teamAffiliation: 'Harvard University',
        teamEmail: 'sarah.johnson@harvard.edu',
        teamBio: 'Leading researcher in computer science with over 20 years of experience.',
        teamResearchArea: 'Artificial Intelligence, Machine Learning'
      },
      {
        teamId: 2,
        teamName: 'Prof. Michael Chen',
        teamTitle: 'Associate Editor',
        teamAffiliation: 'MIT',
        teamEmail: 'michael.chen@mit.edu',
        teamBio: 'Expert in data science and statistical analysis.',
        teamResearchArea: 'Data Science, Statistics'
      },
      {
        teamId: 3,
        teamName: 'Dr. Emily Rodriguez',
        teamTitle: 'Review Editor',
        teamAffiliation: 'Stanford University',
        teamEmail: 'emily.rodriguez@stanford.edu',
        teamBio: 'Specialist in software engineering and system design.',
        teamResearchArea: 'Software Engineering, System Design'
      }
    ]
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
