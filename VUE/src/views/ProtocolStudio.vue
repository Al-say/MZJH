<template>
  <div class="min-h-screen bg-slate-900 text-white overflow-hidden">
    <!-- Header -->
    <header class="bg-slate-800/50 backdrop-blur-xl border-b border-slate-700/50">
      <div class="max-w-7xl mx-auto px-6 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="w-8 h-8 bg-cyan-500 rounded-lg flex items-center justify-center">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 12l2 2 4-4"/>
                <path d="M21 12c.552 0 1-.448 1-1V5c0-.552-.448-1-1-1H3c-.552 0-1 .448-1 1v6c0 .552.448 1 1 1"/>
                <path d="M3 12v6c0 .552.448 1 1 1h16c.552 0 1-.448 1-1v-6"/>
              </svg>
            </div>
            <div>
              <h1 class="text-xl font-bold">Protocol Studio</h1>
              <p class="text-sm text-slate-400">Configure your AI Roundtable</p>
            </div>
          </div>

          <div class="flex items-center gap-4">
            <div class="text-sm text-slate-400">
              {{ validAgentsCount }}/5 Agents Configured
            </div>
            <router-link
              to="/live"
              class="bg-cyan-600 hover:bg-cyan-500 px-6 py-2 rounded-lg font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              :class="{ 'opacity-50 cursor-not-allowed': validAgentsCount < 5 }"
              :disabled="validAgentsCount < 5"
            >
              Launch Roundtable
            </router-link>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <div class="flex-1 overflow-hidden">
      <div class="max-w-7xl mx-auto p-6 h-full">
        <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6 h-full overflow-y-auto">
          <AgentSetupCard
            v-for="agent in agentDrafts"
            :key="agent.agentKey"
            :agent="agent"
            @update="updateAgent"
            @toggle-collapse="toggleAgentCollapse"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoundtableStore } from '@/stores/roundtable'
import type { AgentSeatDraft } from '@/stores/roundtable'
import AgentSetupCard from '@/components/AgentSetupCard.vue'

const store = useRoundtableStore()

const agentDrafts = computed(() => store.agentDrafts)

const validAgentsCount = computed(() => {
  return agentDrafts.value.filter(agent => {
    return agent.codename.trim() &&
           agent.role.trim() &&
           agent.goal.trim() &&
           agent.ui.validationErrors.length === 0
  }).length
})

const updateAgent = (agentKey: string, updates: Partial<AgentSeatDraft>) => {
  store.updateAgentDraft(agentKey, updates)
  store.validateAgentDraft(agentKey)
}

const toggleAgentCollapse = (agentKey: string) => {
  store.toggleAgentCollapsed(agentKey)
}
</script>