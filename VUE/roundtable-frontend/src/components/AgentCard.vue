<template>
  <div
    class="relative w-[340px] flex flex-col transition-all duration-500 ease-out"
    :class="{
      'scale-105 z-50': agent.status === 'speaking',
      'opacity-90 scale-100 z-10': agent.status !== 'speaking'
    }"
  >
    <div
      class="bg-white border rounded-xl overflow-hidden shadow-xl flex flex-col h-[260px] transition-colors duration-300"
      :class="statusBorderClass"
    >

      <div class="flex items-center gap-3 p-3 border-b border-slate-100 bg-slate-50/50">
        <div class="relative">
          <img :src="agent.avatar" class="w-10 h-10 rounded-full bg-slate-200 object-cover ring-2 ring-white shadow-sm" />

          <span v-if="agent.status === 'thinking'" class="absolute -top-1 -right-1 flex h-3 w-3">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-amber-400 opacity-75"></span>
            <span class="relative inline-flex rounded-full h-3 w-3 bg-amber-500"></span>
          </span>
          <span v-if="agent.status === 'speaking'" class="absolute -top-1 -right-1 flex h-3 w-3">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
            <span class="relative inline-flex rounded-full h-3 w-3 bg-blue-600"></span>
          </span>
        </div>

        <div class="flex-1 min-w-0">
          <div class="flex justify-between items-center">
            <h3 class="text-slate-800 font-bold text-sm truncate">{{ agent.name }}</h3>
            <span class="text-[10px] uppercase font-mono tracking-wider font-bold" :class="statusTextClass">
              {{ agent.status }}
            </span>
          </div>
          <p class="text-xs text-slate-500 truncate">{{ agent.role }}</p>
        </div>
      </div>

      <div
        ref="box"
        class="flex-1 p-4 overflow-y-auto custom-scrollbar bg-white relative group"
      >
        <div v-if="agent.status === 'thinking' && !agent.content" class="flex items-center gap-2 text-slate-400 text-xs mt-2">
           <span class="w-2 h-2 bg-slate-300 rounded-full animate-bounce"></span>
           <span class="w-2 h-2 bg-slate-300 rounded-full animate-bounce delay-100"></span>
           <span class="w-2 h-2 bg-slate-300 rounded-full animate-bounce delay-200"></span>
           <span>思考中...</span>
        </div>

        <div v-else class="relative">
            <div
                v-html="renderedMarkdown"
                class="prose prose-slate prose-sm max-w-none prose-p:my-1 prose-headings:my-2 prose-ul:my-1 text-slate-700 leading-relaxed text-[13px] prose-a:text-blue-600 prose-code:bg-slate-100 prose-code:text-slate-800 prose-code:px-1 prose-code:rounded"
            ></div>
            <span v-if="agent.status === 'speaking'" class="inline-block w-1.5 h-4 bg-blue-600 ml-0.5 animate-pulse align-sub"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, watch, ref, nextTick } from 'vue';
import MarkdownIt from 'markdown-it';

const props = defineProps(['agent']);
const md = new MarkdownIt({ html: true, breaks: true });
const box = ref(null);

const renderedMarkdown = computed(() => md.render(props.agent.content || ''));

const statusBorderClass = computed(() => {
    switch (props.agent.status) {
        case 'speaking': return 'border-blue-500 ring-2 ring-blue-500/10 shadow-blue-200/50';
        case 'thinking': return 'border-amber-400 ring-2 ring-amber-400/10';
        case 'error': return 'border-red-300 bg-red-50';
        default: return 'border-slate-200 hover:border-slate-300';
    }
});

const statusTextClass = computed(() => {
     switch (props.agent.status) {
        case 'speaking': return 'text-blue-600 animate-pulse';
        case 'thinking': return 'text-amber-500';
        default: return 'text-slate-400';
    }
});

watch(() => props.agent.content, async () => {
    await nextTick();
    if (box.value) {
        box.value.scrollTo({ top: box.value.scrollHeight, behavior: 'smooth' });
    }
});
</script>

<style>
/* 浅色滚动条 */
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; } /* slate-300 */
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; } /* slate-400 */
</style>