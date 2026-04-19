export class IncompleteStreamError extends Error {
  public partialContent: string

  constructor(message: string, partialContent: string) {
    super(message)
    this.name = 'IncompleteStreamError'
    this.partialContent = partialContent
  }
}

export interface AgentResponseChunk {
  content: string
  isFinal?: boolean
}

export async function streamAgentResponse(userContent: string): Promise<AsyncIterable<AgentResponseChunk>> {
  async function* generator() {
    let accumulated = ''
    try {
      const chunks = [
        '正在分析你的发音与语义...',
        '评测结果显示：你在语调控制上有一定优势，',
        '但建议更注意停顿与重音的节奏。'
      ]

      for (let i = 0; i < chunks.length; i += 1) {
        await new Promise(resolve => setTimeout(resolve, 600))
        const chunk = chunks[i]
        accumulated += chunk
        yield {
          content: chunk,
          isFinal: i === chunks.length - 1
        }
      }
    } catch (error) {
      throw new IncompleteStreamError('Stream was interrupted before completion', accumulated)
    }
  }

  return generator()
}
