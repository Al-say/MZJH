<template>
  <div class="min-h-screen bg-slate-900 text-white py-8">
    <div class="max-w-7xl mx-auto px-6">
      <div class="mb-6 flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold">金字塔原理演示</h1>
          <p class="text-sm text-slate-400 mt-2">独立页面展示结构化思维与交互高亮。</p>
        </div>
        <router-link
          to="/studio"
          class="bg-cyan-600 hover:bg-cyan-500 px-5 py-2 rounded-lg text-sm font-medium transition-colors"
        >
          返回 Studio
        </router-link>
      </div>

      <PyramidPrincipleDemo />
    </div>
  </div>
</template>

<script setup lang="ts">
import PyramidPrincipleDemo from '@/components/PyramidPrincipleDemo.vue'
</script>
