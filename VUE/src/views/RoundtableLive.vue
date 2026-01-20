<template>
  <div class="min-h-screen bg-slate-900 text-white flex flex-col overflow-hidden">
    <!-- Header -->
    <header class="bg-slate-800/50 backdrop-blur-xl border-b border-slate-700/50 flex-shrink-0">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <router-link to="/studio" class="text-slate-400 hover:text-white transition-colors">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M15 18l-6-6 6-6"/>
              </svg>
            </router-link>
            <div>
              <h1 class="text-xl font-bold">Roundtable Live</h1>
              <p class="text-sm text-slate-400">
                {{ currentSession?.topicText || 'No active session' }}
              </p>
            </div>
          </div>

          <div class="flex items-center gap-4">
            <div class="flex items-center gap-2 text-sm">
              <div class="w-2 h-2 rounded-full" :class="connectionStatusColor"></div>
              {{ connectionStatus }}
            </div>

            <div class="flex items-center gap-2">
              <button
                @click="toggleLeftPanel"
                class="p-2 text-slate-400 hover:text-white transition-colors"
                :class="{ 'text-cyan-400': !ui.leftPanelCollapsed }"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 6h16M4 12h16M4 18h16"/>
                </svg>
              </button>

              <button
                @click="toggleRightPanel"
                class="p-2 text-slate-400 hover:text-white transition-colors"
                :class="{ 'text-cyan-400': !ui.rightPanelCollapsed }"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content Area: 唯一滚动容器，底部留出空间 -->
    <main class="flex-1 overflow-y-auto">
      <div class="px-4 py-4 pb-28">
        <!-- 三列布局容器 -->
        <div class="flex h-full">
          <!-- Left Panel -->
          <div
            class="bg-slate-800/30 backdrop-blur-xl border-r border-slate-700/50 flex-shrink-0 transition-all duration-300"
            :class="leftPanelClasses"
          >
            <div class="p-4">
              <h3 class="font-semibold mb-4 text-cyan-400">Protocol Summary</h3>
              <div class="space-y-3">
                <div v-for="agent in agentDrafts" :key="agent.agentKey" class="bg-slate-700/50 rounded-lg p-3">
                  <div class="flex items-center gap-3 mb-2">
                    <img :src="agent.avatarUrl" class="w-8 h-8 rounded" />
                    <div>
                      <div class="font-medium text-sm">{{ agent.codename }}</div>
                      <div class="text-xs text-slate-400">{{ agent.role }}</div>
                    </div>
                  </div>
                  <div class="text-xs text-slate-300 line-clamp-2">{{ agent.goal }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Center Content -->
          <div class="flex-1 flex flex-col min-w-0">
            <!-- Meeting Table -->
            <div class="flex-1 relative">
              <MeetingTable
                :agents="agentDrafts"
                :runtime-states="currentSession?.agents || {}"
              />
            </div>
          </div>

          <!-- Right Panel -->
          <div
            class="bg-slate-800/30 backdrop-blur-xl border-l border-slate-700/50 flex-shrink-0 transition-all duration-300"
            :class="rightPanelClasses"
          >
            <div class="p-4">
              <h3 class="font-semibold mb-4 text-cyan-400">Event Log</h3>
              <div class="space-y-2 max-h-96 overflow-y-auto">
                <div v-for="event in eventLog.slice(-20)" :key="event.id" class="text-xs bg-slate-700/30 rounded p-2">
                  <div class="text-slate-300">{{ event.timestamp }}</div>
                  <div class="text-slate-400">{{ event.message }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Control Bar: Fixed 贴底 -->
    <footer class="fixed left-0 right-0 bottom-0 z-50 border-t border-slate-700/50 bg-slate-900/90 backdrop-blur-xl pb-[env(safe-area-inset-bottom)]">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <ControlBar
          :connected="connectionStatus === 'CONNECTED'"
          :session-active="!!currentSession"
          @submit="handleSendMessage"
          @cancel="handleCancel"
          @clear="handleClear"
        />
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { useRoundtableStore } from '@/stores/roundtable'
import MeetingTable from '@/components/MeetingTable.vue'
import ControlBar from '@/components/ControlBar.vue'

const store = useRoundtableStore()

const currentSession = computed(() => store.currentSession)
const agentDrafts = computed(() => store.agentDrafts)
const ui = computed(() => store.ui)
const connectionStatus = computed(() => store.connectionStatus)

const eventLog = ref<Array<{id: string, timestamp: string, message: string}>>([])

const leftPanelClasses = computed(() => {
  return ui.value.leftPanelCollapsed ? 'w-0 opacity-0' : 'w-80 opacity-100'
})

const rightPanelClasses = computed(() => {
  return ui.value.rightPanelCollapsed ? 'w-0 opacity-0' : 'w-80 opacity-100'
})

const connectionStatusColor = computed(() => {
  switch (connectionStatus.value) {
    case 'CONNECTED': return 'bg-green-500'
    case 'CONNECTING': return 'bg-yellow-500'
    default: return 'bg-red-500'
  }
})

const toggleLeftPanel = () => {
  store.ui.leftPanelCollapsed = !store.ui.leftPanelCollapsed
}

const toggleRightPanel = () => {
  store.ui.rightPanelCollapsed = !store.ui.rightPanelCollapsed
}

const handleSendMessage = (message: string) => {
  if (!message.trim()) return

  // 创建新会话
  const session = store.createSession(message)

  // 添加到事件日志
  addEventLog(`Started session: ${session.topicId}`)

  // 这里可以触发WebSocket连接和消息发送
  console.log('Starting roundtable session:', session)
}

const handleCancel = () => {
  if (currentSession.value) {
    store.resetSession()
    addEventLog('Session cancelled')
  }
}

const handleClear = () => {
  store.resetSession()
  eventLog.value = []
  addEventLog('Session cleared')
}

const addEventLog = (message: string) => {
  eventLog.value.push({
    id: Date.now().toString(),
    timestamp: new Date().toLocaleTimeString(),
    message
  })
}

onMounted(() => {
  // 检查是否是移动设备
  const checkMobile = () => {
    store.ui.isMobile = window.innerWidth < 1024
    if (store.ui.isMobile) {
      store.ui.leftPanelCollapsed = true
      store.ui.rightPanelCollapsed = true
    }
  }

  checkMobile()
  window.addEventListener('resize', checkMobile)
})
</script>