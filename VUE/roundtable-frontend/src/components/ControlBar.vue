<template>
  <div class="w-full max-w-2xl px-4 z-50">
    <div class="bg-white p-2 rounded-2xl shadow-xl border border-slate-100 flex gap-3 ring-1 ring-slate-200">
      <textarea
        v-model="text"
        @keydown.enter.prevent="emitSend"
        placeholder="请输入议题..."
        class="w-full bg-transparent text-slate-800 p-3 outline-none resize-none h-[60px] placeholder-slate-400"
      ></textarea>
      <button
        @click="emitSend"
        :disabled="!connected || !text.trim()"
        class="bg-blue-600 hover:bg-blue-700 disabled:bg-slate-200 disabled:text-slate-400 text-white font-bold px-6 rounded-xl transition-all w-24 flex flex-col items-center justify-center shadow-md">
        <span>发起</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
defineProps(['connected']);
const emit = defineEmits(['submit']);
const text = ref('');

const emitSend = () => {
    if (!text.value.trim()) return;
    emit('submit', text.value);
    text.value = '';
};
</script>