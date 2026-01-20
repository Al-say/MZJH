<template>
  <div class="w-full h-full max-w-7xl mx-auto p-4 flex flex-col relative z-50">

    <div class="flex-1 flex flex-col bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden animate-fade-in-up">

      <div class="shrink-0 px-8 py-6 border-b border-slate-200 flex justify-between items-center bg-slate-50">
        <div>
          <h2 class="text-2xl font-bold text-slate-900 tracking-widest uppercase flex items-center gap-3">
            <span class="w-2 h-7 bg-emerald-500 block"></span>
            PROTOCOL CONFIG
          </h2>
          <p class="text-slate-400 text-xs mt-1 ml-5 tracking-widest">
            SYSTEM_ID: ROUNDTABLE_V1 // DEFINE_NEURAL_PATTERNS
          </p>
        </div>
        <button @click="addAgent" class="group relative px-5 py-2.5 text-sm font-semibold text-emerald-700 border border-emerald-200 rounded-lg hover:bg-emerald-50 transition-all overflow-hidden">
          <span class="absolute inset-0 w-full h-full bg-emerald-200/40 opacity-0 group-hover:opacity-100 transition-opacity"></span>
          <div class="flex items-center gap-2">
            <span>+</span>
            <span>ADD NODE</span>
          </div>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto p-8 custom-scrollbar">
        <div class="grid grid-cols-1 lg:grid-cols-2 2xl:grid-cols-3 gap-6">

          <div v-for="(agent, index) in localAgents" :key="agent.id"
               class="relative group bg-white border border-slate-200 hover:border-emerald-300 rounded-xl p-5 transition-all duration-300 hover:shadow-md">

            <button @click="removeAgent(index)" class="absolute top-3 right-3 p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded transition-all opacity-0 group-hover:opacity-100 z-10">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
            </button>

            <div class="flex gap-5 mb-5 pb-5 border-b border-slate-100">
              <div class="relative cursor-pointer shrink-0 group/avatar" @click="refreshAvatar(index)">
                <div class="absolute -inset-1 rounded-lg bg-emerald-200/40 opacity-0 group-hover/avatar:opacity-100 transition-opacity"></div>
                <img :src="agent.avatar" class="relative w-16 h-16 rounded-lg bg-slate-200 object-cover border border-slate-200 group-hover/avatar:border-emerald-300 transition-colors" />
                <div class="absolute -bottom-2 -right-2 bg-white text-[9px] text-emerald-600 px-1.5 py-0.5 border border-emerald-200 rounded">
                  RND
                </div>
              </div>

              <div class="flex-1 space-y-3 min-w-0 pt-0.5">
                <div class="group/input">
                  <label class="text-[9px] text-slate-500 uppercase tracking-wider block mb-1 group-focus-within/input:text-emerald-600 transition-colors">Codename</label>
                  <input v-model="agent.name" class="w-full bg-transparent border-b border-slate-200 focus:border-emerald-500 outline-none text-base font-semibold text-slate-900 placeholder-slate-400 transition-colors py-0.5" placeholder="Name" />
                </div>
                <div class="group/input">
                  <label class="text-[9px] text-slate-500 uppercase tracking-wider block mb-1 group-focus-within/input:text-emerald-600 transition-colors">Role / Function</label>
                  <input v-model="agent.role" class="w-full bg-transparent border-b border-slate-200 focus:border-emerald-500 outline-none text-xs text-slate-700 placeholder-slate-400 transition-colors py-0.5" placeholder="Role description" />
                </div>
              </div>
            </div>

            <div class="space-y-4">

              <div class="relative group/field">
                <label class="flex justify-between text-[9px] text-slate-500 uppercase tracking-wider mb-1.5">
                  <span class="group-focus-within/field:text-emerald-600 transition-colors">Primary Directive / 任务目标</span>
                  <span class="text-emerald-600 text-[9px] border border-emerald-200 px-1 rounded">REQUIRED</span>
                </label>
                <textarea
                  v-model="agent.goal"
                  rows="2"
                  placeholder="Defines what the agent is trying to achieve..."
                  class="w-full bg-slate-50 border border-slate-200 focus:border-emerald-400 rounded-lg p-2.5 text-xs text-slate-700 leading-relaxed outline-none resize-none custom-scrollbar focus:bg-white transition-colors"
                ></textarea>
              </div>

              <div class="grid grid-cols-2 gap-3">
                <div class="group/field">
                   <label class="text-[9px] text-slate-500 uppercase tracking-wider mb-1.5 block group-focus-within/field:text-emerald-600 transition-colors">Thinking Style</label>
                   <input
                      v-model="agent.style"
                      placeholder="e.g. Critical, Optimistic"
                      class="w-full bg-slate-50 border border-slate-200 focus:border-emerald-400 rounded-lg p-2 text-xs text-slate-700 outline-none focus:bg-white transition-colors"
                   />
                </div>

                <div class="group/field">
                   <label class="text-[9px] text-slate-500 uppercase tracking-wider mb-1.5 block group-focus-within/field:text-emerald-600 transition-colors">Reasoning Logic</label>
                   <input
                      v-model="agent.logic"
                      placeholder="e.g. First Principles"
                      class="w-full bg-slate-50 border border-slate-200 focus:border-emerald-400 rounded-lg p-2 text-xs text-slate-700 outline-none focus:bg-white transition-colors"
                   />
                </div>
              </div>
            </div>

            <div class="absolute bottom-0 right-0 w-2 h-2 border-b border-r border-slate-200 rounded-br pointer-events-none"></div>
            <div class="absolute top-0 left-0 w-2 h-2 border-t border-l border-slate-200 rounded-tl pointer-events-none"></div>

          </div>
        </div>
      </div>

      <div class="shrink-0 px-8 py-6 border-t border-slate-200 flex justify-end bg-slate-50 gap-4">
        <div class="flex items-center gap-2 mr-auto text-[10px] text-slate-500">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            SYSTEM_READY
        </div>
        <button
          @click="confirmSetup"
          class="bg-emerald-500 hover:bg-emerald-600 text-white font-semibold py-3 px-10 rounded-lg shadow-sm transition-all transform hover:scale-[1.02] active:scale-[0.99] flex items-center gap-3">
          <span>INITIALIZE SESSION</span>
          <span class="text-emerald-100">></span>
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps(['initialAgents']);
const emit = defineEmits(['confirm']);

// 深拷贝 props 数据
const localAgents = ref(JSON.parse(JSON.stringify(props.initialAgents)));

// 头像种子
const seeds = ['Felix', 'Aneka', 'Baby', 'Grandma', 'Jack', 'Loki', 'Thor', 'Hulk', 'Odin'];

const refreshAvatar = (index) => {
    const randomSeed = seeds[Math.floor(Math.random() * seeds.length)] + Math.random();
    localAgents.value[index].avatar = `https://api.dicebear.com/7.x/avataaars/svg?seed=${randomSeed}`;
};

const addAgent = () => {
    const id = 'agent_' + Date.now();
    localAgents.value.push({
        id: id,
        name: 'Agent ' + (localAgents.value.length + 1),
        role: 'Observer',
        avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${id}`,
        // 默认空值，等待用户输入
        goal: '',
        style: '',
        logic: '',
        content: '',
        status: 'idle'
    });
};

const removeAgent = (index) => {
    if (localAgents.value.length > 1) {
        localAgents.value.splice(index, 1);
    }
};

const confirmSetup = () => {
    emit('confirm', localAgents.value);
};
</script>

<style scoped>
/* 滚动条微调 */
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: rgba(15, 23, 42, 0.06); }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(16, 185, 129, 0.25); border-radius: 3px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(16, 185, 129, 0.45); }

/* 入场动画 */
@keyframes fadeInUp {
    from { opacity: 0; transform: translateY(20px); }
    to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up {
    animation: fadeInUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}
</style>
