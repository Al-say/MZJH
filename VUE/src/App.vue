<template>
  <div id="app" class="min-h-screen bg-slate-900 text-white">
    <header class="border-b border-slate-800 bg-slate-950/80 backdrop-blur-xl">
      <div class="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-3 px-6 py-4 text-sm text-slate-200">
        <div class="flex flex-wrap items-center gap-3">
          <router-link to="/studio" class="rounded-full bg-slate-800 px-4 py-2 transition hover:bg-slate-700">Studio</router-link>
          <router-link to="/live" class="rounded-full bg-slate-800 px-4 py-2 transition hover:bg-slate-700">Live</router-link>
          <router-link to="/pyramid-demo" class="rounded-full bg-slate-800 px-4 py-2 transition hover:bg-slate-700">Pyramid Demo</router-link>
          <router-link to="/ppt" class="rounded-full bg-cyan-600 px-4 py-2 text-white transition hover:bg-cyan-500">PPT Web</router-link>
        </div>
        <div class="text-slate-400">AI Roundtable</div>
      </div>
    </header>

    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('AI Roundtable App initialized')
})
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100vh;
}

/* 自定义滚动条样式 */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(15, 23, 42, 0.1);
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(16, 185, 129, 0.3);
  border-radius: 3px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(16, 185, 129, 0.5);
}

/* 动画类 */
@keyframes pulse-slow {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.animate-pulse-slow {
  animation: pulse-slow 3s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.animate-blink {
  animation: blink 0.8s infinite steps(1);
}
</style>
