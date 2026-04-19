import type { Session, EventLog } from '@/types'

export async function startRoundtableSession(title: string): Promise<Session> {
  return {
    id: `session_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`,
    title,
    startTime: Date.now(),
    status: 'running',
    agents: {}
  }
}

export async function postEventLog(sessionId: string, event: EventLog): Promise<void> {
  // 这里预留后端同步逻辑，当前仅用于调试和工程底座。
  console.debug('[roundtable api] postEventLog', sessionId, event)
}
