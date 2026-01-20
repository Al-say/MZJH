<template>
  <div
    class="relative w-[320px] group perspective"
  >
    <div
      class="relative bg-white border border-slate-200 rounded-2xl overflow-hidden
             flex flex-col h-[240px] transition-all duration-500
             hover:shadow-md hover:translate-y-[-4px]"
      :class="statusBorderClass"
    >
      <div class="absolute inset-0 bg-gradient-to-br from-emerald-50 to-transparent opacity-70 pointer-events-none"></div>

      <div class="relative flex items-center gap-4 p-4 border-b border-slate-100 bg-slate-50">

        <div class="relative shrink-0">
          <div class="w-12 h-12 rounded-full p-[2px] bg-gradient-to-b from-emerald-200 to-transparent overflow-hidden relative z-10">
              <img :src="agent?.avatar" class="w-full h-full rounded-full bg-slate-200 object-cover" />
          </div>

          <div class="absolute -inset-1 rounded-full z-0 transition-all duration-500" :class="statusRingClass"></div>
        </div>

        <div class="flex-1 min-w-0 flex flex-col justify-center">
          <div class="flex justify-between items-baseline">
            <h3 class="text-slate-900 font-semibold text-[15px] tracking-wide truncate">
              {{ agent?.name }}
            </h3>
            <span class="text-[10px] uppercase tracking-wider px-1.5 py-0.5 rounded-md bg-emerald-50 text-emerald-700 border border-emerald-100 truncate ml-2">
              {{ agent?.role }}
            </span>
          </div>
          <p class="text-xs text-slate-500 truncate mt-1">
            {{ agent?.function_title || '席位专家' }}
          </p>
        </div>
      </div>

      <div
        ref="box"
        class="flex-1 p-4 overflow-y-auto custom-scrollbar relative"
      >
        <div v-if="agent?.status === 'thinking' && !agent?.content" class="flex items-center gap-2 text-emerald-600 text-xs mt-4 animate-pulse">
           <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
             <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
             <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
           </svg>
           <span>Analyzing input stream...</span>
        </div>

        <div v-else class="relative">
            <div
                v-html="renderedMarkdown"
                class="prose prose-sm max-w-none
                       prose-p:my-2 prose-p:leading-relaxed prose-p:text-slate-700
                       prose-headings:text-slate-900 prose-headings:font-semibold prose-headings:my-3
                       prose-strong:text-slate-900 prose-strong:font-semibold
                       prose-code:text-emerald-700 prose-code:bg-emerald-50 prose-code:px-1 prose-code:py-0.5 prose-code:rounded prose-code:before:content-none prose-code:after:content-none
                       text-[13px]"
            ></div>
            <span v-if="agent?.status === 'speaking'" class="inline-block w-2 h-5 bg-emerald-500 ml-1 animate-blink align-sub"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, watch, ref, nextTick } from 'vue';
import MarkdownIt from 'markdown-it';

const props = defineProps(['agent']);
// 配置 markdown-it 不自动转换换行，由 CSS 控制
const md = new MarkdownIt({ html: true, breaks: false });
const box = ref(null);

const renderedMarkdown = computed(() => {
    if (!props.agent || !props.agent.content) return '';
    return md.render(props.agent.content);
});

// 动态边框与辉光
const statusBorderClass = computed(() => {
    if (!props.agent) return 'border-slate-200';
    switch (props.agent.status) {
        case 'speaking': return 'border-emerald-300 shadow-[0_0_12px_rgba(16,185,129,0.2)]';
        case 'thinking': return 'border-amber-300 shadow-[0_0_10px_rgba(251,191,36,0.15)]';
        case 'error': return 'border-red-300 bg-red-50';
        default: return 'border-slate-200'; // IDLE
    }
});

// 头像状态光环 (替代文字)
const statusRingClass = computed(() => {
    if (!props.agent) return 'bg-slate-200/60 blur-sm';
    switch (props.agent.status) {
        case 'speaking': return 'bg-emerald-300/60 animate-pulse blur-md';
        case 'thinking': return 'bg-amber-300/50 animate-pulse-slow blur-md';
        case 'error': return 'bg-red-300/60 blur-md';
        default: return 'bg-slate-200/60 blur-sm';
    }
});

// 自动滚动
watch(() => props.agent?.content, async () => {
    await nextTick();
    if (box.value) {
        // 只有在用户没手动滚上去的时候才自动滚底（体验更好）
        const atBottom = Math.abs(box.value.scrollHeight - box.value.scrollTop - box.value.clientHeight) < 100;
        if (atBottom) {
            box.value.scrollTo({ top: box.value.scrollHeight, behavior: 'smooth' });
        }
    }
});
</script>

<style scoped>
/* 定制浅色模式滚动条 */
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: rgba(15, 23, 42, 0.05); }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(16, 185, 129, 0.3); border-radius: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(16, 185, 129, 0.5); }

@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }
.animate-blink { animation: blink 0.8s infinite steps(1); }

/* blockquote 样式强化 */
.prose blockquote {
  border-left-color: theme('colors.emerald.500');
  background: rgba(16, 185, 129, 0.08);
  margin: 1.25rem 0;
  padding: 0.75rem 1rem;
  font-size: 0.875rem;
  color: theme('colors.emerald.800');
}

.prose blockquote strong {
  color: theme('colors.emerald.700');
}

/* 针对插入的 <span style="color:#22d3ee"> */
.prose blockquote span[style*="color:#22d3ee"] {
  color: #10b981 !important;
  letter-spacing: 0.5px;
}
</style>
