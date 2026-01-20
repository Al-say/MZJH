<template>
  <div class="min-h-screen flex flex-col items-center py-4 overflow-hidden relative bg-slate-50 text-slate-900">
    <header class="text-center z-20 mt-2 mb-6">
        <div class="inline-flex items-center gap-3 px-4 py-2 rounded-full bg-white border border-slate-200 shadow-sm">
            <span class="text-[11px] font-semibold tracking-[0.2em] text-emerald-600">ROUNDTABLE</span>
            <span class="flex items-center gap-2 text-[10px] text-slate-500 uppercase">
                <span class="w-2 h-2 rounded-full" :class="connectionStatus === 'CONNECTED' ? 'bg-emerald-500' : 'bg-red-500'"></span>
                {{ connectionStatus }}
            </span>
        </div>
    </header>

    <div v-if="mode === 'setup'" class="w-full flex-1 flex flex-col justify-center animate-fade-in z-20">
       <AgentSetup :initialAgents="agents" @confirm="onSetupConfirm" />
    </div>

    <div v-else class="w-full flex-1 flex flex-col animate-fade-in relative z-10">
        <div class="absolute top-0 left-6 z-50">
            <button @click="mode = 'setup'" class="text-xs text-slate-500 hover:text-emerald-600 flex items-center gap-1 transition-colors">
                &lt; RECONFIGURE
            </button>
        </div>

        <RoundTableChat class="flex-1" />

        <!-- ControlBar 已移入 RoundTableChat 底部 -->
    </div>

  </div>
</template>

<script setup>
// ... 逻辑保持不变 ...
import { onMounted, ref } from 'vue';
import { useRoundtable } from './composables/useRoundtable';
import AgentSetup from './components/AgentSetup.vue';
import RoundTableChat from './components/RoundTableChat.vue';

const { agents, connect, connectionStatus, updateAgents } = useRoundtable();
const mode = ref('setup');

const onSetupConfirm = (newConfigs) => {
    updateAgents(newConfigs);
    mode.value = 'meeting';
};

onMounted(() => {
  connect();
});
</script>

<style>
/* 全局动画 */
@keyframes fadeIn { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }
.animate-fade-in { animation: fadeIn 0.35s ease-out; }
body {
  font-family: "PingFang SC", "Source Han Sans SC", "Noto Sans SC", "Microsoft YaHei", "Helvetica Neue", sans-serif;
}
</style>
