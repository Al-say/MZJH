<template>
  <div class="w-full max-w-3xl">
    <div class="relative bg-slate-800/70 backdrop-blur-xl p-4 rounded-xl border border-slate-700/50 shadow-lg group focus-within:border-cyan-400/60 transition-all duration-300">
      
      <div class="flex gap-4">
        <div class="flex-1 relative">
            <span class="absolute top-4 left-4 text-cyan-400 text-lg">></span>
            <textarea 
                v-model="text"
                @keydown.enter.prevent="emitSend"
                placeholder="输入你的问题..." 
                class="w-full bg-transparent text-slate-100 p-4 pl-10 outline-none resize-none h-[60px] placeholder-slate-400 text-sm leading-relaxed custom-scrollbar"
            ></textarea>
        </div>

        <div class="flex gap-2">
          <button 
              @click="emitClear"
              class="p-3 text-slate-400 hover:text-rose-400 hover:bg-rose-500/20 rounded-lg transition-colors"
              title="清除全部历史对话">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/><path d="M10 11v6"/><path d="M14 11v6"/>
            </svg>
          </button>

          <button 
              @click="emitSend"
              :disabled="!connected || !text.trim()"
              class="bg-cyan-600 hover:bg-cyan-500 border border-cyan-500 hover:border-cyan-400 text-white 
                     disabled:opacity-50 disabled:cursor-not-allowed
                     rounded-lg px-6 transition-all duration-200 flex flex-col items-center justify-center min-w-[100px] group/btn">
              <span class="text-xs font-semibold tracking-widest group-hover/btn:translate-y-[-2px] transition-transform">SEND</span>
              <span class="text-[10px] opacity-70 scale-75 mt-0.5">ENTER</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
defineProps(['connected']);
const emit = defineEmits(['submit', 'clear']);
const text = ref('');

const emitSend = () => {
    if (!text.value.trim()) return;
    emit('submit', text.value);
    text.value = '';
};

const emitClear = () => {
    emit('clear');
};
</script>
