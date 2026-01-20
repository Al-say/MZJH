import { ref, reactive } from 'vue';
import { Client } from '@stomp/stompjs';

// 初始化数据
const initialAgents = [
    { id: 'agent_ceo', name: 'Steve (CEO)', role: '商业战略', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix', content: '', status: 'idle' },
    { id: 'agent_tech', name: 'Linus (CTO)', role: '技术架构', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka', content: '', status: 'idle' },
    { id: 'agent_pm', name: 'Alice (Product)', role: '用户体验', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Baby', content: '', status: 'idle' },
    { id: 'agent_cfo', name: 'Buffett (CFO)', role: '财务风控', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Grandma', content: '', status: 'idle' },
];

export function useRoundtable() {
    const agents = reactive(initialAgents);
    const client = ref(null);
    const connectionStatus = ref('DISCONNECTED'); // CONNECTING, CONNECTED, DISCONNECTED
    const processedSeqs = new Set();

    const connect = () => {
        connectionStatus.value = 'CONNECTING';
        
        client.value = new Client({
            // ✅ 修改点：端口改为 22222
            brokerURL: 'ws://localhost:22222/roundtable-ws', 
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: () => {
                connectionStatus.value = 'CONNECTED';
                console.log('✅ Connected to port 22222');
                
                // 订阅 demo 频道
                client.value.subscribe('/topic/roundtable/demo', (message) => {
                    const event = JSON.parse(message.body);
                    handleEvent(event);
                });
            },
            onDisconnect: () => { 
                connectionStatus.value = 'DISCONNECTED'; 
                console.log('❌ Disconnected');
            },
            onStompError: (frame) => {
                console.error('⚠️ Broker error: ' + frame.headers['message']);
            }
        });

        client.value.activate();
    };

    const handleEvent = (event) => {
        const { agentId, type, content, payload, seq } = event;

        // 简单的 Seq 去重
        if (processedSeqs.has(seq)) return;
        processedSeqs.add(seq);

        const agent = agents.find(a => a.id === agentId);
        if (!agent) return;

        switch (type) {
            case 'START':
                // 开始时清空上一轮，并进入思考状态
                agent.content = ''; 
                agent.status = 'thinking';
                break;
            case 'DELTA':
                // 一旦收到第一个字符，状态立即转为 speaking
                agent.status = 'speaking';
                agent.content += (content || '');
                break;
            case 'END':
                agent.status = 'idle';
                if (payload && payload.summary) {
                    agent.content += `\n\n> **Summary**: ${payload.summary}`;
                }
                break;
            case 'ERROR':
                agent.status = 'error';
                agent.content += '\n\n`[System Error: Connection Lost]`';
                break;
        }
    };

    const sendTopic = (topic) => {
        if (connectionStatus.value !== 'CONNECTED') return;

        // UI 乐观更新：所有 Agent 立即进入思考状态
        agents.forEach(a => { 
            a.content = ''; 
            a.status = 'thinking'; 
        });
        processedSeqs.clear();

        client.value.publish({
            destination: '/app/startDiscussion',
            body: JSON.stringify({ topic: topic, topicId: 'demo' })
        });
    };

    return { agents, connectionStatus, connect, sendTopic };
}