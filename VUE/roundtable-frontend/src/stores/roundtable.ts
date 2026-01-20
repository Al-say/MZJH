import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'

// AgentSeatDraft 数据结构
export interface AgentSeatDraft {
  agentKey: string
  codename: string
  role: string
  avatarUrl: string
  goal: string
  thinkingStyle: string
  reasoningLogic: string
  systemPromptOverride?: string
  modelParams?: {
    provider: string
    model: string
    temperature: number
    maxTokens: number
  }
  ui: {
    collapsed: boolean
    dirty: boolean
    validationErrors: string[]
  }
}

// TopicSession 数据结构
export interface TopicSession {
  topicId: string
  topicText: string
  status: 'idle' | 'running' | 'cancelled' | 'finished' | 'error'
  agents: Record<string, AgentRuntimeState>
  createdAt: number
}

// AgentRuntimeState 数据结构
export interface AgentRuntimeState {
  status: 'idle' | 'streaming' | 'finished' | 'cancelled' | 'error'
  seq: number
  raw: string
  rendered: string
  buffer: string
  lastEventAt: number
}

export const useRoundtableStore = defineStore('roundtable', () => {
  // 当前会话
  const currentSession = ref<TopicSession | null>(null)

  // 代理席位配置
  const agentDrafts = ref<AgentSeatDraft[]>([
    {
      agentKey: 'agent_ceo',
      codename: 'Steve',
      role: 'Visionary CEO',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix',
      goal: '寻找颠覆性创新机会，评估商业价值上限',
      thinkingStyle: '极简主义，直击本质，略带现实扭曲力场',
      reasoningLogic: '不要告诉我现在的限制，告诉我未来的可能性',
      ui: {
        collapsed: true,
        dirty: false,
        validationErrors: []
      }
    },
    {
      agentKey: 'agent_cto',
      codename: 'Alex',
      role: 'Chief Technology Officer',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Alex',
      goal: '评估技术可行性，设计系统架构',
      thinkingStyle: '系统性思维，关注技术细节与可扩展性',
      reasoningLogic: '从第一性原理出发，优先考虑长期技术债务',
      ui: {
        collapsed: true,
        dirty: false,
        validationErrors: []
      }
    },
    {
      agentKey: 'agent_cfo',
      codename: 'Jordan',
      role: 'Chief Financial Officer',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jordan',
      goal: '分析财务可行性，计算投资回报',
      thinkingStyle: '数据驱动，关注ROI和风险控制',
      reasoningLogic: '现金流为王，关注边际成本与边际收益',
      ui: {
        collapsed: true,
        dirty: false,
        validationErrors: []
      }
    },
    {
      agentKey: 'agent_cmo',
      codename: 'Taylor',
      role: 'Chief Marketing Officer',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Taylor',
      goal: '制定市场策略，分析用户需求',
      thinkingStyle: '用户中心，关注市场趋势与品牌定位',
      reasoningLogic: '用户需求驱动，关注LTV与CAC比率',
      ui: {
        collapsed: true,
        dirty: false,
        validationErrors: []
      }
    },
    {
      agentKey: 'agent_coo',
      codename: 'Morgan',
      role: 'Chief Operating Officer',
      avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Morgan',
      goal: '制定运营策略，确保执行效率',
      thinkingStyle: '务实高效，关注流程优化与资源配置',
      reasoningLogic: '效率优先，关注关键绩效指标与运营杠杆',
      ui: {
        collapsed: true,
        dirty: false,
        validationErrors: []
      }
    }
  ])

  // UI状态
  const ui = reactive({
    leftPanelCollapsed: false,
    rightPanelCollapsed: false,
    isMobile: false
  })

  // 连接状态
  const connectionStatus = ref<'DISCONNECTED' | 'CONNECTING' | 'CONNECTED'>('DISCONNECTED')

  // Actions
  const updateAgentDraft = (agentKey: string, updates: Partial<AgentSeatDraft>) => {
    const agent = agentDrafts.value.find(a => a.agentKey === agentKey)
    if (agent) {
      Object.assign(agent, updates)
      agent.ui.dirty = true
    }
  }

  const toggleAgentCollapsed = (agentKey: string) => {
    const agent = agentDrafts.value.find(a => a.agentKey === agentKey)
    if (agent) {
      agent.ui.collapsed = !agent.ui.collapsed
    }
  }

  const validateAgentDraft = (agentKey: string) => {
    const agent = agentDrafts.value.find(a => a.agentKey === agentKey)
    if (!agent) return

    const errors: string[] = []

    if (!agent.codename.trim()) errors.push('代号不能为空')
    if (!agent.role.trim()) errors.push('角色不能为空')
    if (!agent.goal.trim()) errors.push('目标不能为空')
    if (agent.goal.length > 500) errors.push('目标描述不能超过500字符')

    agent.ui.validationErrors = errors
    return errors.length === 0
  }

  const createSession = (topicText: string): TopicSession => {
    const session: TopicSession = {
      topicId: `topic_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      topicText,
      status: 'idle',
      agents: {},
      createdAt: Date.now()
    }

    // 初始化所有代理的运行时状态
    agentDrafts.value.forEach(agent => {
      session.agents[agent.agentKey] = {
        status: 'idle',
        seq: 0,
        raw: '',
        rendered: '',
        buffer: '',
        lastEventAt: 0
      }
    })

    currentSession.value = session
    return session
  }

  const updateAgentRuntimeState = (agentKey: string, updates: Partial<AgentRuntimeState>) => {
    if (!currentSession.value) return

    const agentState = currentSession.value.agents[agentKey]
    if (agentState) {
      Object.assign(agentState, updates)
    }
  }

  const appendToBuffer = (agentKey: string, content: string) => {
    if (!currentSession.value) return

    const agentState = currentSession.value.agents[agentKey]
    if (agentState) {
      agentState.buffer += content
      agentState.lastEventAt = Date.now()
    }
  }

  const flushBuffer = (agentKey: string) => {
    if (!currentSession.value) return

    const agentState = currentSession.value.agents[agentKey]
    if (agentState && agentState.buffer) {
      agentState.raw += agentState.buffer
      agentState.buffer = ''
    }
  }

  const resetSession = () => {
    currentSession.value = null
  }

  return {
    // State
    currentSession,
    agentDrafts,
    ui,
    connectionStatus,

    // Actions
    updateAgentDraft,
    toggleAgentCollapsed,
    validateAgentDraft,
    createSession,
    updateAgentRuntimeState,
    appendToBuffer,
    flushBuffer,
    resetSession
  }
})