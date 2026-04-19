export type SessionStatus = 'idle' | 'running' | 'paused' | 'ended'
export type EventType = 'system' | 'user' | 'agent'
export type ConnectionStatus = 'DISCONNECTED' | 'CONNECTING' | 'CONNECTED'
export type EventStatus = 'complete' | 'interrupted'

export interface Session {
  id: string
  title: string
  startTime: number
  status: SessionStatus
  agents: Record<string, AgentRuntimeState>
}

export interface Agent {
  id: string
  name: string
  role: string
  avatar?: string
}

export interface AgentDraft extends Agent {
  agentKey: string
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

export interface AgentRuntimeState {
  status: 'idle' | 'streaming' | 'finished' | 'cancelled' | 'error'
  seq: number
  raw: string
  rendered: string
  buffer: string
  lastEventAt: number
}

export interface EventLog {
  id: string
  timestamp: number
  senderId: string
  content: string
  type: EventType
  status?: EventStatus
  relatedLogId?: string
}
