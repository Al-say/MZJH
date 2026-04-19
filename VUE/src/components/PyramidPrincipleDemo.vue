<template>
  <div class="bg-slate-50 border border-slate-200 rounded-2xl shadow-xl overflow-hidden text-slate-900">
    <div class="flex h-[650px] w-full">
      <div class="w-1/3 bg-white border-r border-slate-200 flex flex-col">
        <div class="p-6 border-b bg-gray-50">
          <h2 class="text-xl font-bold text-gray-800">金字塔原理多维映射</h2>
          <p class="text-sm text-gray-500 mt-1">请选择受众角色推演逻辑</p>
        </div>
        <div class="flex-1 overflow-y-auto p-4 space-y-2">
          <button
            v-for="role in scenarios"
            :key="role.id"
            @click="activeRoleId = role.id"
            :class="[
              'w-full text-left px-5 py-4 rounded-xl transition-all duration-300 font-medium',
              activeRoleId === role.id
                ? 'bg-blue-50 text-blue-700 ring-2 ring-blue-500/50 shadow-sm'
                : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
            ]"
          >
            {{ role.name }}
          </button>
        </div>
      </div>

      <div class="w-2/3 p-8 flex flex-col">
        <div class="flex-1 flex flex-col items-center justify-center p-8 relative">
          <div class="w-full max-w-md flex flex-col items-center gap-3 transition-all duration-500">
            <div
              class="w-1/3 py-4 rounded-t-xl text-center font-bold transition-all duration-500"
              :class="getLayerStyle('top')"
            >
              塔尖：核心结论
            </div>

            <div
              class="w-2/3 py-4 text-center font-bold transition-all duration-500"
              :class="getLayerStyle('middle')"
            >
              塔身：逻辑支撑 (MECE)
            </div>

            <div
              class="w-full py-4 rounded-b-xl text-center font-bold transition-all duration-500"
              :class="getLayerStyle('bottom')"
            >
              塔基：事实论据 / 底层数据
            </div>
          </div>
        </div>

        <div class="h-56 bg-white rounded-xl shadow-md border p-6 flex flex-col relative overflow-hidden">
          <div class="absolute -right-6 -top-6 text-9xl text-gray-50 opacity-50 font-black select-none pointer-events-none">
            {{ activeScenario.focusLayer.toUpperCase() }}
          </div>

          <div class="relative z-10 h-full flex flex-col">
            <div class="flex justify-between items-start mb-4">
              <h3 class="text-2xl font-bold text-gray-800">{{ activeScenario.title }}</h3>
              <span class="px-3 py-1 bg-blue-100 text-blue-700 text-xs font-bold rounded-full">
                {{ activeScenario.coreAction }}
              </span>
            </div>

            <p class="text-gray-600 leading-relaxed mb-4 flex-1">
              {{ activeScenario.description }}
            </p>

            <div class="bg-slate-50 border rounded-lg p-3">
              <div class="text-xs font-bold text-gray-500 mb-1">实战样例 (Best Practice)：</div>
              <ul class="space-y-1">
                <li
                  v-for="(example, index) in activeScenario.examples"
                  :key="index"
                  class="text-sm text-gray-700 flex items-start"
                >
                  <span class="mr-2 text-blue-500">✦</span>
                  {{ example }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

type FocusLayer = 'top' | 'middle' | 'bottom' | 'all' | 'top-bottom'

interface RoleScenario {
  id: string
  name: string
  focusLayer: FocusLayer
  title: string
  description: string
  coreAction: string
  examples: string[]
}

const scenarios: RoleScenario[] = [
  {
    id: 'decision-maker',
    name: '决策者 (CEO/高管)',
    focusLayer: 'top',
    title: '结论先行，直击核心 ROI',
    description: '决策者认知带宽极度稀缺，必须在开篇30秒内给出明确结论与选择。',
    coreAction: '抛弃流水账，做选择题',
    examples: ['汇报逻辑：核心结论 > 潜在风险 > 需要的资源支持']
  },
  {
    id: 'project-manager',
    name: '项目经理 (PM)',
    focusLayer: 'middle',
    title: '归类分组，MECE 原则',
    description: '项目失控往往源于边界模糊。利用相互独立、完全穷尽的法则拆解任务。',
    coreAction: '构建 WBS (工作分解结构)',
    examples: ['任务拆解：按时间节点划分，或按功能模块划分，确保不重不漏']
  },
  {
    id: 'ai-pm',
    name: 'Ai产品经理',
    focusLayer: 'all',
    title: '逻辑递进，SCQA 模型推演',
    description: '大语言模型是概率推演器。结构化的 Prompt 能够指数级提升输出的确定性。',
    coreAction: '结构化指令工程',
    examples: ['Prompt架构：Background(背景) > Role(设定) > Task(任务) > Output(格式)']
  },
  {
    id: 'designer',
    name: '设计师 (UI/UX)',
    focusLayer: 'all',
    title: '逻辑层级映射视觉层级',
    description: '优秀的信息架构 (IA) 本质上是一座倒置的金字塔，引导用户的视觉流向。',
    coreAction: '建立视觉权重体系',
    examples: ['界面排版：H1(核心结论) > 模块卡片(逻辑支撑) > 列表细节(事实论据)']
  },
  {
    id: 'field-staff',
    name: '现场/交付人员',
    focusLayer: 'top-bottom',
    title: '结果前置，事实托底',
    description: '面对突发故障，需要迅速定性问题，并提供精确的底层排障数据。',
    coreAction: '结论+关键事实汇报',
    examples: ['故障上报：当前影响面与结论 > 关键报错日志 > 已采取的应急措施']
  }
]

const activeRoleId = ref<string>(scenarios[0].id)

const activeScenario = computed(() => scenarios.find(s => s.id === activeRoleId.value) || scenarios[0])

const getLayerStyle = (layer: 'top' | 'middle' | 'bottom') => {
  const focus = activeScenario.value.focusLayer
  const isHighlighted =
    focus === 'all' ||
    focus === layer ||
    (focus === 'top-bottom' && (layer === 'top' || layer === 'bottom'))

  return isHighlighted
    ? 'bg-blue-500 text-white shadow-lg scale-105 z-10'
    : 'bg-gray-100 text-gray-400 border-gray-200 border-2'
}
</script>
