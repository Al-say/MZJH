<template>
  <div class="min-h-screen bg-slate-900 text-white py-8">
    <div class="max-w-7xl mx-auto px-6">
      <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h1 class="text-3xl font-bold">PPT 演示网页</h1>
          <p class="text-sm text-slate-400 mt-2">用网页形式展示金字塔原理演示页。</p>
        </div>
        <div class="flex flex-wrap gap-3">
          <router-link
            to="/studio"
            class="rounded-lg border border-slate-700 bg-slate-800 px-4 py-2 text-sm text-slate-200 transition hover:border-cyan-400 hover:text-white"
          >Studio</router-link>
          <router-link
            to="/live"
            class="rounded-lg border border-slate-700 bg-slate-800 px-4 py-2 text-sm text-slate-200 transition hover:border-cyan-400 hover:text-white"
          >Live</router-link>
          <router-link
            to="/pyramid-demo"
            class="rounded-lg border border-slate-700 bg-slate-800 px-4 py-2 text-sm text-slate-200 transition hover:border-cyan-400 hover:text-white"
          >Pyramid Demo</router-link>
        </div>
      </div>

      <div class="grid gap-6 lg:grid-cols-[1.25fr_0.75fr]">
        <section class="rounded-3xl border border-slate-700/50 bg-slate-950/80 p-8 shadow-xl">
          <div class="flex flex-col gap-4">
            <div class="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
              <div>
                <p class="text-sm uppercase tracking-[0.35em] text-cyan-400">Slide</p>
                <h2 class="text-3xl font-bold">{{ currentSlide.title }}</h2>
              </div>
              <div class="rounded-3xl bg-slate-900/90 px-4 py-3 text-sm text-slate-300">
                {{ currentIndex + 1 }} / {{ slides.length }}
              </div>
            </div>

            <div class="rounded-3xl bg-slate-900/90 p-6 shadow-inner">
              <p class="text-slate-400 mb-4">{{ currentSlide.subtitle }}</p>
              <ul class="space-y-3 list-disc pl-5 text-slate-200">
                <li v-for="point in currentSlide.points" :key="point">{{ point }}</li>
              </ul>
              <p v-if="currentSlide.footer" class="mt-6 text-sm text-slate-500">{{ currentSlide.footer }}</p>
            </div>

            <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <button
                @click="prevSlide"
                :disabled="currentIndex === 0"
                class="w-full rounded-2xl border border-slate-700 bg-slate-800 px-6 py-3 text-sm text-white transition hover:border-cyan-400 disabled:cursor-not-allowed disabled:opacity-50 sm:w-auto"
              >
                上一页
              </button>
              <button
                @click="nextSlide"
                :disabled="currentIndex === slides.length - 1"
                class="w-full rounded-2xl border border-slate-700 bg-cyan-600 px-6 py-3 text-sm text-white transition hover:bg-cyan-500 disabled:cursor-not-allowed disabled:opacity-50 sm:w-auto"
              >
                下一页
              </button>
            </div>
          </div>
        </section>

        <aside class="rounded-3xl border border-slate-700/50 bg-slate-950/80 p-6 shadow-xl">
          <h3 class="text-xl font-semibold text-white mb-4">页面结构</h3>
          <div class="space-y-3">
            <button
              v-for="(slide, index) in slides"
              :key="slide.title"
              @click="setSlide(index)"
              :class="[
                'w-full rounded-2xl px-4 py-3 text-left text-sm transition',
                currentIndex === index
                  ? 'bg-cyan-500 text-white shadow-lg'
                  : 'bg-slate-900 text-slate-300 hover:bg-slate-800'
              ]"
            >
              <div class="font-medium">{{ index + 1 }}. {{ slide.title }}</div>
              <div class="text-slate-500">{{ slide.subtitle }}</div>
            </button>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Slide {
  title: string
  subtitle: string
  points: string[]
  footer?: string
}

const slides: Slide[] = [
  {
    title: '封面',
    subtitle: '金字塔原理：把复杂内容变成清晰逻辑',
    points: ['标题与副标题直接传递主题', '结合场景定位与价值承诺']
  },
  {
    title: '问题现状',
    subtitle: '为什么很多表达容易失败？',
    points: ['信息碎片化', '结论不明确', '受众理解成本高']
  },
  {
    title: '冲突与认知负荷',
    subtitle: '时间少，信息多',
    points: ['认知资源有限', '线性叙述容易迷失', '沟通效率显著下降']
  },
  {
    title: '核心答案',
    subtitle: '金字塔原理是最优解',
    points: ['结论先行', '从上而下组织内容', '同级逻辑必须 MECE']
  },
  {
    title: '纵向法则',
    subtitle: '上级是下级的提炼',
    points: ['层级关系必须清晰', '每个层级都是对下级的概括', '避免重复与无关内容']
  },
  {
    title: '横向法则',
    subtitle: 'MECE：不重不漏',
    points: ['确保同级逻辑独立', '至少穷尽必要项', '可按时间/结构/重点排序']
  },
  {
    title: '文档与汇报案例',
    subtitle: '把流水账转成结构化总结',
    points: ['核心结论 > 关键理由 > 数据支持', '用标题带出结论', '用列表保证清晰']
  },
  {
    title: '架构与技术案例',
    subtitle: '系统设计也可以用金字塔表达',
    points: ['架构总览 = 塔尖', '模块逻辑 = 塔身', '接口/数据 = 塔基']
  },
  {
    title: '行动建议',
    subtitle: '马上可落地的三步法',
    points: ['先画金字塔草图', '再写标题与支撑', '最后校验 MECE 完整性']
  },
  {
    title: '总结与呼应',
    subtitle: '结构决定表达影响力',
    points: ['金字塔原理是思维模式', '适用于所有表达场景', '请在下一次呈现前先画草图'],
    footer: '结论先行，结构决定影响力'
  }
]

const currentIndex = ref(0)
const currentSlide = computed(() => slides[currentIndex.value])

const prevSlide = () => {
  if (currentIndex.value > 0) currentIndex.value -= 1
}

const nextSlide = () => {
  if (currentIndex.value < slides.length - 1) currentIndex.value += 1
}

const setSlide = (index: number) => {
  currentIndex.value = index
}
</script>
