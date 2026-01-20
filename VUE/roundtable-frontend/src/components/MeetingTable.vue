<template>
  <div class="relative w-full h-[650px] flex justify-center items-center perspective-[1000px]">
    <div class="absolute w-[900px] h-[450px] border border-slate-200/50 rounded-[100%] top-[10%] -z-10
                bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-slate-200/40 via-slate-100/10 to-transparent"></div>

    <div class="absolute w-[600px] h-[300px] rounded-[100%] top-[20%] -z-20 border border-slate-300/30"></div>

    <div class="absolute left-[5%] top-[5%] transform -rotate-6 hover:rotate-0 hover:z-50 transition-all duration-300">
        <AgentCard :agent="agents[0]" />
    </div>
    <div class="absolute left-[15%] bottom-[15%] transform -rotate-3 hover:rotate-0 hover:z-50 transition-all duration-300">
        <AgentCard :agent="agents[1]" />
    </div>

    <div class="absolute right-[15%] bottom-[15%] transform rotate-3 hover:rotate-0 hover:z-50 transition-all duration-300">
        <AgentCard :agent="agents[2]" />
    </div>
    <div class="absolute right-[5%] top-[5%] transform rotate-6 hover:rotate-0 hover:z-50 transition-all duration-300">
        <AgentCard :agent="agents[3]" />
    </div>

    <div class="absolute top-[40%] text-center pointer-events-none select-none">
        <h1 class="text-7xl font-black text-slate-200 tracking-[0.2em]">ROUND<br>TABLE</h1>
    </div>
  </div>
</template>

<script setup>
import AgentCard from './AgentCard.vue';
defineProps(['agents']);
</script>

<style>
.perspective-\[1000px\] { perspective: 1000px; }
</style>