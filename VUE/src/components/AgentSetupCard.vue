<template>
  <div class="bg-slate-800/50 backdrop-blur-xl rounded-xl border border-slate-700/50 overflow-hidden">
    <!-- Header -->
    <div class="p-4 border-b border-slate-700/50">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <img :src="agent.avatarUrl" class="w-10 h-10 rounded-lg bg-slate-700" />
          <div>
            <h3 class="font-semibold text-white">{{ agent.codename }}</h3>
            <p class="text-sm text-slate-400">{{ agent.role }}</p>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <div v-if="agent.ui.validationErrors.length > 0" class="text-xs text-red-400">
            {{ agent.ui.validationErrors.length }} errors
          </div>
          <div class="w-2 h-2 rounded-full" :class="statusColor"></div>
          <button
            @click="$emit('toggle-collapse')"
            class="p-1 text-slate-400 hover:text-white transition-colors"
          >
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              :class="{ 'rotate-180': !agent.ui.collapsed }"
              class="transition-transform"
            >
              <path d="M7 13l3 3 7-7"/>
              <path d="M12 2v10"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Content -->
    <div v-show="!agent.ui.collapsed" class="p-4 space-y-4">
      <!-- Identity Section -->
      <div class="space-y-3">
        <h4 class="text-sm font-medium text-cyan-400 uppercase tracking-wider">Identity</h4>

        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-xs text-slate-400 mb-1">Codename</label>
            <input
              v-model="localAgent.codename"
              @input="handleUpdate"
              class="w-full bg-slate-900/50 border border-slate-600 rounded px-3 py-2 text-sm text-white placeholder-slate-500 focus:border-cyan-500 focus:outline-none"
              placeholder="Agent name"
            />
          </div>

          <div>
            <label class="block text-xs text-slate-400 mb-1">Role</label>
            <input
              v-model="localAgent.role"
              @input="handleUpdate"
              class="w-full bg-slate-900/50 border border-slate-600 rounded px-3 py-2 text-sm text-white placeholder-slate-500 focus:border-cyan-500 focus:outline-none"
              placeholder="Job title"
            />
          </div>
        </div>
      </div>

      <!-- Objective Section -->
      <div class="space-y-3">
        <h4 class="text-sm font-medium text-cyan-400 uppercase tracking-wider">Objective</h4>

        <div>
          <label class="block text-xs text-slate-400 mb-1">Primary Goal</label>
          <textarea
            v-model="localAgent.goal"
            @input="handleUpdate"
            rows="3"
            class="w-full bg-slate-900/50 border border-slate-600 rounded px-3 py-2 text-sm text-white placeholder-slate-500 focus:border-cyan-500 focus:outline-none resize-none"
            placeholder="Define the agent's primary objective..."
          ></textarea>
          <div class="text-xs text-slate-500 mt-1">
            {{ localAgent.goal.length }}/500 characters
          </div>
        </div>
      </div>

      <!-- Cognition Section -->
      <div class="space-y-3">
        <h4 class="text-sm font-medium text-cyan-400 uppercase tracking-wider">Cognition</h4>

        <div class="space-y-3">
          <div>
            <label class="block text-xs text-slate-400 mb-1">Thinking Style</label>
            <input
              v-model="localAgent.thinkingStyle"
              @input="handleUpdate"
              class="w-full bg-slate-900/50 border border-slate-600 rounded px-3 py-2 text-sm text-white placeholder-slate-500 focus:border-cyan-500 focus:outline-none"
              placeholder="e.g. Analytical, Creative, Pragmatic"
            />
          </div>

          <div>
            <label class="block text-xs text-slate-400 mb-1">Reasoning Logic</label>
            <input
              v-model="localAgent.reasoningLogic"
              @input="handleUpdate"
              class="w-full bg-slate-900/50 border border-slate-600 rounded px-3 py-2 text-sm text-white placeholder-slate-500 focus:border-cyan-500 focus:outline-none"
              placeholder="e.g. First Principles, Data-Driven"
            />
          </div>
        </div>
      </div>

      <!-- Advanced Section -->
      <div class="space-y-3">
        <h4 class="text-sm font-medium text-cyan-400 uppercase tracking-wider">Advanced</h4>

        <div>
          <label class="block text-xs text-slate-400 mb-1">System Prompt Override (Optional)</label>
          <textarea
            v-model="localAgent.systemPromptOverride"
            @input="handleUpdate"
            rows="4"
            class="w-full bg-slate-900/50 border border-slate-600 rounded px-3 py-2 text-sm text-white placeholder-slate-500 focus:border-cyan-500 focus:outline-none resize-none font-mono"
            placeholder="Override the default system prompt..."
          ></textarea>
        </div>
      </div>

      <!-- Validation Errors -->
      <div v-if="agent.ui.validationErrors.length > 0" class="space-y-1">
        <div v-for="error in agent.ui.validationErrors" :key="error" class="text-xs text-red-400">
          • {{ error }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { AgentSeatDraft } from '@/stores/roundtable'

interface Props {
  agent: AgentSeatDraft
}

const props = defineProps<Props>()
const emit = defineEmits<{
  update: [agentKey: string, updates: Partial<AgentSeatDraft>]
  'toggle-collapse': [agentKey: string]
}>()

const localAgent = ref({ ...props.agent })

// 监听props变化，更新本地状态
watch(() => props.agent, (newAgent) => {
  localAgent.value = { ...newAgent }
}, { deep: true })

const statusColor = computed(() => {
  if (props.agent.ui.validationErrors.length > 0) return 'bg-red-500'
  if (props.agent.ui.dirty) return 'bg-yellow-500'
  return 'bg-green-500'
})

const handleUpdate = () => {
  emit('update', props.agent.agentKey, {
    ...localAgent.value,
    ui: {
      ...localAgent.value.ui,
      dirty: true
    }
  })
}
</script>