<template>
  <div class="min-h-screen bg-slate-50 text-slate-800 flex flex-col items-center justify-between py-6 overflow-hidden font-sans">

    <header class="text-center z-20 mt-4">
      <div class="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-white border border-slate-200 shadow-sm">
        <span class="text-xs font-bold tracking-widest text-slate-500">ROUNDTABLE OS</span>
        <span class="w-px h-3 bg-slate-200"></span>
        <span class="flex items-center gap-1.5 text-[10px] font-mono font-bold" :class="statusColor">
          <span class="relative flex h-2 w-2">
            <span v-if="connectionStatus === 'CONNECTED'" class="animate-ping absolute inline-flex h-full w-full rounded-full opacity-75 bg-current"></span>
            <span class="relative inline-flex rounded-full h-2 w-2 bg-current"></span>
          </span>
          {{ connectionStatus }}
        </span>
      </div>
    </header>

    <MeetingTable :agents="agents" class="flex-1" />

    <ControlBar :connected="connectionStatus === 'CONNECTED'" @submit="sendTopic" class="mb-6" />
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue';
import { useRoundtable } from './composables/useRoundtable';
import MeetingTable from './components/MeetingTable.vue';
import ControlBar from './components/ControlBar.vue';

const { agents, connect, sendTopic, connectionStatus } = useRoundtable();

const statusColor = computed(() => {
    switch (connectionStatus.value) {
        case 'CONNECTED': return 'text-emerald-600';
        case 'CONNECTING': return 'text-amber-500';
        default: return 'text-rose-600';
    }
});

onMounted(() => {
  connect();
});
</script>

<style>
body {
  background-color: #f8fafc; /* slate-50 */
  /* 浅色网格背景 */
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 40px 40px;
}
</style>
