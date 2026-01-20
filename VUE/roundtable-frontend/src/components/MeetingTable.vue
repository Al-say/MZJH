<template>
  <div class="relative w-full h-[650px] flex justify-center items-center perspective-[1200px]">
    
    <div class="absolute w-[950px] h-[500px] top-[12%] -z-10 rounded-[100%]
                border-[2px] border-cyan-500/30
                bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-cyan-900/20 via-transparent to-transparent
                shadow-[0_0_50px_rgba(6,182,212,0.2),_inset_0_0_20px_rgba(6,182,212,0.1)]
                animate-pulse-slow"></div>
    
    <div class="absolute w-[650px] h-[350px] top-[22%] -z-10 rounded-[100%]
                border border-blue-500/40
                bg-[radial-gradient(ellipse_at_center,_rgba(59,130,246,0.15)_0%,transparent_70%)]
                shadow-[0_0_30px_rgba(59,130,246,0.3)]"></div>

    <div class="absolute top-[42%] text-center pointer-events-none select-none z-0">
        <h1 class="text-7xl font-black tracking-[0.25em] leading-tight
                   text-transparent bg-clip-text bg-gradient-to-b from-cyan-300 via-blue-500 to-indigo-600
                   drop-shadow-[0_0_15px_rgba(0,200,255,0.6)]
                   opacity-80 mix-blend-screen hologram-text">
            ROUND<br>TABLE
        </h1>
    </div>

    <template v-if="agents.length > 0">
        <div v-for="(agent, index) in agents" :key="agent.id"
             class="absolute transition-all duration-700 ease-out-cubic"
             :style="getAgentStyle(index, agents.length)">

            <div class="transform transition-transform duration-300 hover:scale-105 hover:z-50 hover:rotate-0"
                 :style="{ transform: `rotate(${getRotation(index, agents.length)}deg)` }">
                 <AgentCard :agent="agent" />
            </div>

        </div>
    </template>
  </div>
</template>

<script setup>
import AgentCard from './AgentCard.vue';

// 计算卡片位置的辅助函数
const getAgentStyle = (index, total) => {
    // 1. 如果只有 1 个人，直接放在正上方 C 位
    if (total === 1) {
        return {
            top: '5%',
            left: '50%',
            transform: 'translateX(-50%)', // 居中修正
            zIndex: 20
        };
    }

    // 2. 如果是多人，按椭圆分布 (模拟 3D 圆桌)
    // 简单的预设位置映射 (支持最多 5 人混排优化)
    const positions = {
        2: [
            { top: '20%', left: '20%' }, { top: '20%', right: '20%' }
        ],
        3: [
            { top: '5%', left: '50%', transform: 'translateX(-50%)' }, // C位
            { bottom: '15%', left: '15%' },
            { bottom: '15%', right: '15%' }
        ],
        4: [
            { top: '10%', left: '10%' },
            { top: '10%', right: '10%' },
            { bottom: '15%', left: '15%' },
            { bottom: '15%', right: '15%' }
        ],
        5: [
            { top: '10%', left: '2%' },  // 左上
            { bottom: '12%', left: '12%' }, // 左下
            { top: '5%', left: '50%', transform: 'translateX(-50%)', zIndex: 20 }, // C位
            { bottom: '12%', right: '12%' }, // 右下
            { top: '10%', right: '2%' } // 右上
        ]
    };

    // 如果正好命中预设配置，直接返回
    if (positions[total] && positions[total][index]) {
        return positions[total][index];
    }

    // 兜底：如果超过5人或未定义，全部堆叠在中间（防止报错）
    return { top: '50%', left: '50%' };
};

// 计算卡片的微旋转角度 (让卡片看起来面向圆心)
const getRotation = (index, total) => {
    if (total === 1) return 0; // 单人不旋转

    // 简单的角度硬编码
    const rotations = {
        2: [-10, 10],
        3: [0, -5, 5],
        4: [-10, 10, -5, 5],
        5: [-12, -6, 0, 6, 12]
    };

    if (rotations[total] && rotations[total][index] !== undefined) {
        return rotations[total][index];
    }
    return 0;
};
</script>

<style scoped>
.perspective-\[1200px\] { perspective: 1200px; }
.ease-out-cubic { transition-timing-function: cubic-bezier(0.33, 1, 0.68, 1); }
.animate-pulse-slow { animation: pulse 8s cubic-bezier(0.4, 0, 0.6, 1) infinite; }

/* 全息文字扫描线效果 */
.hologram-text {
    background-image: linear-gradient(transparent 0%, rgba(34, 211, 238, 0.2) 50%, transparent 100%);
    background-size: 100% 4px;
    animation: scanline 6s linear infinite;
}
@keyframes scanline {
    0% { background-position: 0 0; }
    100% { background-position: 0 100%; }
}
</style>