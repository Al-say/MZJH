<template>
  <div class="flex flex-col h-full">
    <!-- 聊天内容 -->
    <div ref="chatBox" class="flex-1 overflow-y-auto pb-32 px-4 custom-scrollbar scroll-smooth bg-slate-50/60"
         @scroll="checkScrollPosition">
      <div v-for="msg in filteredMessages" :key="msg.id" class="my-4 animate-fade-in">
        <!-- 系统消息 / 分割线 -->
        <div v-if="msg.type === 'system'" class="text-center my-6">
          <div class="inline-block px-4 py-1 bg-white rounded-full text-xs text-slate-500 border border-slate-200 shadow-sm">
            {{ msg.content }}
          </div>
        </div>

        <!-- 用户消息（右侧） -->
        <div v-else-if="msg.type === 'user'" class="flex justify-end">
          <div class="max-w-[75%] bg-emerald-500 text-white rounded-2xl rounded-tr-none px-4 py-3 shadow-sm">
            <div class="text-xs opacity-80 mb-1">我</div>
            <div class="whitespace-pre-wrap text-sm" v-html="renderMarkdown(msg.content)"></div>
          </div>
        </div>

        <!-- Agent 消息（左侧） -->
        <div v-else class="flex justify-start">
          <div class="max-w-[75%] bg-white rounded-2xl rounded-tl-none px-4 py-3 shadow-sm border border-slate-200">
            <div class="flex items-center gap-2 text-xs opacity-80 mb-1">
              <span class="font-medium text-emerald-600">{{ getAgent(msg.agentId)?.name || 'Unknown' }}</span>
              <span class="text-slate-400">{{ formatTime(msg.timestamp) }}</span>
            </div>
            <div class="prose prose-sm max-w-none text-slate-700" v-html="renderMarkdown(msg.content)"></div>
          </div>
        </div>
      </div>

      <!-- 加载中 / 思考指示器（显示在最后） -->
      <div v-if="hasThinkingAgent" class="flex justify-center my-6">
        <div class="bg-white rounded-full px-4 py-2 text-sm text-slate-500 border border-slate-200 shadow-sm animate-pulse">
          正在思考...
        </div>
      </div>

      <!-- 滚到底部按钮 -->
      <button v-if="showScrollToBottom" @click="scrollToBottom"
              class="fixed bottom-28 right-6 z-30 bg-emerald-500 text-white rounded-full p-3 shadow-md hover:bg-emerald-600 transition">
        ↓
      </button>
    </div>

    <!-- 输入区 -->
    <div class="fixed bottom-0 left-0 right-0 z-20 bg-slate-50/90 backdrop-blur border-t border-slate-200">
      <div class="py-2">
        <ControlBar :connected="connectionStatus === 'CONNECTED'"
                    @submit="sendTopic"
                    @clear="clearHistory" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue';
import MarkdownIt from 'markdown-it';
import { useRoundtable } from '@/composables/useRoundtable';
import ControlBar from './ControlBar.vue';

const { agents, messages, connectionStatus, sendTopic, clearHistory } = useRoundtable();

const md = new MarkdownIt({ html: true, breaks: true });
const chatBox = ref(null);
const showScrollToBottom = ref(false);

const filteredMessages = computed(() => {
  return messages.value;
});

const hasThinkingAgent = computed(() => agents.value.some(a => a.status === 'thinking' || a.status === 'speaking'));

const getAgent = (id) => agents.value.find(a => a.id === id);

const formatTime = (date) => date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });

const renderMarkdown = (text) => md.render(text || '');

const scrollToBottom = async () => {
  await nextTick();
  if (chatBox.value) {
    chatBox.value.scrollTo({ top: chatBox.value.scrollHeight, behavior: 'smooth' });
  }
};

const checkScrollPosition = () => {
  if (!chatBox.value) return;
  const { scrollTop, scrollHeight, clientHeight } = chatBox.value;
  const atBottom = Math.abs(scrollHeight - scrollTop - clientHeight) < 100;
  showScrollToBottom.value = !atBottom && messages.value.length > 0;
};

// 新消息自动滚（如果已在底部）
watch(() => messages.value.length, async () => {
  await nextTick();
  if (chatBox.value) {
    const atBottom = Math.abs(chatBox.value.scrollHeight - chatBox.value.scrollTop - chatBox.value.clientHeight) < 100;
    if (atBottom) scrollToBottom();
  }
}, { deep: true });
</script>

<style scoped>
/* 自定义滚动条 */
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: rgba(15, 23, 42, 0.06); }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(16, 185, 129, 0.25); border-radius: 3px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(16, 185, 129, 0.45); }

/* 入场动画 */
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in {
    animation: fadeIn 0.3s ease-out;
}
</style>
