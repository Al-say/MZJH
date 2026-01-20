import { ref } from 'vue';
import { Client } from '@stomp/stompjs';

// ✅ 单人模式默认数据
const defaultAgents = [
    {
        id: 'agent_ceo',
        name: 'Steve',
        role: 'Visionary CEO',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix',
        goal: '寻找颠覆性创新机会，评估商业价值上限',
        style: '极简主义，直击本质，略带现实扭曲力场',
        logic: '不要告诉我现在的限制，告诉我未来的可能性',
        content: '',
        status: 'idle'
    }
];

export function useRoundtable() {
    const agents = ref(defaultAgents);
    const client = ref(null);
    const connectionStatus = ref('DISCONNECTED');
    const processedSeqs = new Set();
    const roundCount = ref(0);
    // 新增：全局消息流（所有 agent 的内容按序聚合）
    const messages = ref([]);  // [{ id: string, agentId: string, type: 'system'|'agent'|'user', content: string, timestamp: Date }]

    // ... connect 方法保持不变 ...
    const connect = () => {
        // 端口注意保持和你配置的一致 (11111 或 22222 等，根据你的后端)
        // 这里假设是后端端口 22222
        connectionStatus.value = 'CONNECTING';
        client.value = new Client({
            brokerURL: 'ws://localhost:22222/roundtable-ws',
            reconnectDelay: 5000,
            onConnect: () => {
                connectionStatus.value = 'CONNECTED';
                console.log('✅ Connected');
                
                // 添加欢迎消息
                messages.value.push({
                    id: 'welcome_' + Date.now(),
                    type: 'system',
                    content: '🎯 RoundTable OS 已连接 - 输入指令开始对话',
                    timestamp: new Date()
                });
                
                client.value.subscribe('/topic/roundtable/demo', (message) => {
                    handleEvent(JSON.parse(message.body));
                });
            },
            onDisconnect: () => { connectionStatus.value = 'DISCONNECTED'; }
        });
        client.value.activate();
    };

    const handleEvent = (event) => {
        const { agentId, type, content, payload, seq } = event;
        if (processedSeqs.has(seq)) return;
        processedSeqs.add(seq);

        const agent = agents.value.find(a => a.id === agentId);
        if (!agent) return;

        const timestamp = new Date();

        switch (type) {
            case 'START':
                agent.status = 'thinking';
                // 可选：push 一个"正在思考..."系统消息
                break;

            case 'DELTA': {
                agent.status = 'speaking';
                // 增量追加到最后一条该 agent 的消息（或新建）
                const lastMsg = messages.value[messages.value.length - 1];
                if (lastMsg && lastMsg.agentId === agentId && lastMsg.type === 'agent') {
                    lastMsg.content += content || '';
                } else {
                    messages.value.push({
                        id: `${seq}-${Date.now()}`,
                        agentId,
                        type: 'agent',
                        content: content || '',
                        timestamp
                    });
                }
                break;
            }

            case 'END':
                agent.status = 'idle';
                // 可选：添加总结或分割
                if (payload?.summary) {
                    messages.value.push({
                        id: `summary-${seq}`,
                        agentId,
                        type: 'system',
                        content: `**总结**：${payload.summary}`,
                        timestamp: new Date()
                    });
                }
                break;

            case 'ERROR':
                agent.status = 'error';
                break;
        }
    };

    const updateAgents = (newAgents) => {
        // 确保新数据覆盖旧数据，保留状态字段
        agents.value = newAgents.map(a => ({
            ...a,
            content: '',
            status: 'idle'
        }));
    };

    const sendTopic = (topic) => {
        roundCount.value++;
        // processedSeqs.clear();   // 通常不需要清，除非你确定要丢弃所有历史 seq（极少场景）

        // push 用户消息
        messages.value.push({
            id: `user-${Date.now()}`,
            agentId: null,
            type: 'user',
            content: topic,
            timestamp: new Date()
        });

        // 如果未连接，发送演示消息
        if (connectionStatus.value !== 'CONNECTED') {
            setTimeout(() => {
                messages.value.push({
                    id: `demo-${Date.now()}`,
                    agentId: 'agent_ceo',
                    type: 'agent',
                    content: `💡 **演示模式**: 收到指令 "${topic}"\n\n作为AI CEO，我会从以下几个维度分析这个议题：\n\n1. **市场机会** - 评估潜在的市场规模和增长空间\n2. **技术可行性** - 分析实现路径和所需资源\n3. **商业价值** - 计算ROI和投资回报预期\n\n请连接后端服务器以获得完整的AI对话体验！`,
                    timestamp: new Date()
                });
            }, 1000);
            return;
        }

        // 系统分割消息
        messages.value.push({
            id: `sys-${Date.now()}`,
            agentId: null,
            type: 'system',
            content: `─── 新议题 ─ Round ${roundCount.value} ─ ${new Date().toLocaleString('zh-CN', {
                hour12: false,
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            })} ───\n**议题**：${topic.replace(/\n/g, ' ')}`,
            timestamp: new Date()
        });

        // 组装配置
        const agentConfigs = agents.value.map(a => ({
            id: a.id, name: a.name, role: a.role, avatar: a.avatar,
            goal: a.goal || '', style: a.style || '', logic: a.logic || ''
        }));

        agents.value.forEach(a => {
            a.status = 'thinking';

            // 防止 content 过长导致卡顿（可选，但强烈建议）
            if (a.content.length > 120_000) {  // 约 30–40 万中文字符
                a.content = a.content.slice(-80_000) + '\n\n…（历史内容已自动截断）\n\n';
            }

            if (a.content.length > 0) a.content += '\n\n';

            // 更清晰的分割风格（视觉上更强区分度）
            a.content += `> <span style="color:#22d3ee;font-weight:bold;">─── 新议题 ─ Round ${roundCount.value} ─ ${new Date().toLocaleString('zh-CN', {
                hour12: false,
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            })} ───</span>\n`
                       + `> **议题**：${topic.replace(/\n/g, ' ')}\n`
                       + `>\n`
                       + `---\n\n`;
        });

        client.value.publish({
            destination: '/app/startDiscussion',
            body: JSON.stringify({
                topic: topic,
                topicId: 'demo',
                agents: agentConfigs
            })
        });
    };

    // 清空历史
    const clearHistory = () => {
        messages.value = [];
        agents.value.forEach(a => {
            a.content = '';
            a.status = 'idle';
        });
        roundCount.value = 0;
    };

    return {
        agents,
        messages,           // ← 新暴露
        connectionStatus,
        connect,
        sendTopic,
        updateAgents,
        clearHistory
    };
}