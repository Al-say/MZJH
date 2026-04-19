<template>
  <div class="min-h-screen bg-slate-900 text-white py-8">
    <div class="max-w-7xl mx-auto px-6">
      <div class="mb-6 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
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

      <div class="grid gap-6 lg:grid-cols-[1.05fr_0.95fr]">
        <div class="space-y-6">
          <div class="rounded-3xl border border-slate-700/50 bg-slate-950/80 p-6 shadow-xl">
            <h2 class="text-xl font-semibold text-white mb-3">演示页面结构图</h2>
            <div class="space-y-4">
              <div class="rounded-2xl bg-slate-900/80 border border-slate-700 p-4">
                <div class="text-sm text-cyan-300 mb-2">页面流程</div>
                <div class="grid gap-3">
                  <div class="rounded-2xl bg-slate-800/90 p-4">1. 封面</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">2. 问题现状</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">3. 冲突与认知负荷</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">4. 核心答案</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">5. 纵向法则</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">6. 横向法则</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">7. 案例：文档/汇报</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">8. 案例：架构/技术</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">9. 行动建议</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">10. 总结与呼应</div>
                </div>
              </div>

              <div class="rounded-2xl bg-slate-900/80 border border-slate-700 p-4">
                <div class="text-sm text-cyan-300 mb-2">思维节点</div>
                <div class="grid gap-2">
                  <div class="flex items-center gap-3 rounded-xl bg-slate-800/80 p-3">
                    <span class="inline-flex h-8 w-8 items-center justify-center rounded-full bg-cyan-500 text-sm font-semibold">A</span>
                    <div>
                      <div class="text-white font-medium">结论先行</div>
                      <p class="text-slate-400 text-sm">页面 1 / 4 / 9 / 10 均围绕这一原则展开。</p>
                    </div>
                  </div>
                  <div class="flex items-center gap-3 rounded-xl bg-slate-800/80 p-3">
                    <span class="inline-flex h-8 w-8 items-center justify-center rounded-full bg-fuchsia-500 text-sm font-semibold">B</span>
                    <div>
                      <div class="text-white font-medium">MECE 结构</div>
                      <p class="text-slate-400 text-sm">页面 5 / 6 / 7 / 8 用并列逻辑保障不重不漏。</p>
                    </div>
                  </div>
                  <div class="flex items-center gap-3 rounded-xl bg-slate-800/80 p-3">
                    <span class="inline-flex h-8 w-8 items-center justify-center rounded-full bg-emerald-500 text-sm font-semibold">C</span>
                    <div>
                      <div class="text-white font-medium">行动可落地</div>
                      <p class="text-slate-400 text-sm">最后两页设计为明确行动与总结闭环。</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="rounded-3xl border border-slate-700/50 bg-slate-950/80 p-6 shadow-xl">
            <h2 class="text-xl font-semibold text-white mb-3">可视化页面图</h2>
            <div class="space-y-4">
              <div class="rounded-3xl bg-slate-900/80 p-5 border border-slate-700">
                <div class="flex items-center justify-between text-sm text-slate-400 mb-4">
                  <span>页面编号</span>
                  <span>核心内容</span>
                </div>
                <div class="grid gap-3">
                  <div class="rounded-2xl bg-slate-800/90 p-4">封面：标题 + 场景定位</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">问题现状：信息碎片与认知成本</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">冲突：时间少、信息多、理解低</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">核心答案：金字塔原理</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">纵向法则：层级提炼</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">横向法则：MECE 结构</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">案例 1：文档 / 汇报</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">案例 2：架构 / 技术</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">行动建议：立即落地的三步法</div>
                  <div class="rounded-2xl bg-slate-800/90 p-4">总结：结论先行，结构决定影响力</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="space-y-6">
          <div class="rounded-3xl border border-slate-700/50 bg-slate-950/80 p-6 shadow-xl">
            <h2 class="text-xl font-semibold text-white mb-3">页面结构逻辑</h2>
            <div class="rounded-3xl bg-slate-900/80 border border-slate-700 p-5">
              <div class="space-y-4">
                <div class="text-slate-300">通过“自上而下”的信息流，页面结构形成两个核心节点：</div>
                <div class="grid gap-3">
                  <div class="rounded-xl bg-slate-800/90 p-4">
                    <div class="font-semibold text-white">节点 1：问题与答案</div>
                    <div class="text-slate-400 text-sm">页 2-4 聚焦问题识别与解决方案概述。</div>
                  </div>
                  <div class="rounded-xl bg-slate-800/90 p-4">
                    <div class="font-semibold text-white">节点 2：方法与落地</div>
                    <div class="text-slate-400 text-sm">页 5-9 用方法论和案例支撑，最终落到行动。</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="rounded-3xl border border-slate-700/50 bg-slate-950/80 p-6 shadow-xl">
            <h2 class="text-xl font-semibold text-white mb-3">页面交互建议</h2>
            <ul class="space-y-3 text-slate-300 list-disc list-inside">
              <li>封面建议加简洁背景图或渐变色，立即传达“金字塔”主题。</li>
              <li>第 4 页建议用“图文并茂”的核心结论卡片。</li>
              <li>第 5-6 页建议使用并列图表或层级图强化纵横法则。</li>
              <li>案例页建议使用矩阵式卡片展示“场景 → 结构 → 结果”。</li>
              <li>总结页建议使用一句话 + 行动按钮样式视觉锚定。</li>
            </ul>
          </div>

          <div class="rounded-3xl border border-slate-700/50 bg-slate-950/80 p-6 shadow-xl">
            <h2 class="text-xl font-semibold text-white mb-3">推荐模板链接</h2>
            <div class="grid gap-3 text-slate-200">
              <a href="https://www.slidescarnival.com/one-page-business-case-report/331774" target="_blank" class="block rounded-2xl bg-slate-800/90 p-4 text-cyan-300 hover:bg-slate-700">One Page Business Case Report</a>
              <a href="https://www.slidescarnival.com/saas-sales-funnel-infographic/341440" target="_blank" class="block rounded-2xl bg-slate-800/90 p-4 text-cyan-300 hover:bg-slate-700">SaaS Sales Funnel Infographic</a>
              <a href="https://www.canva.com/templates/EAEd0dhRUys-white-blue-simple-business-pitch-deck-presentation/" target="_blank" class="block rounded-2xl bg-slate-800/90 p-4 text-cyan-300 hover:bg-slate-700">White Blue Simple Business Pitch Deck Presentation</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>
