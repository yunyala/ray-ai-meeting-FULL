import { ref } from 'vue'
import type { TranscriptItem, MeetingSummary, WebSocketMessage } from '@/types'

interface UseWebSocketOptions {
  onTranscript: (data: TranscriptItem) => void
  onSummary: (data: MeetingSummary) => void
}

export function useWebSocket(options: UseWebSocketOptions) {
  const ws = ref<WebSocket | null>(null)
  const isConnected = ref(false)

  const connect = (url: string): Promise<void> => {
    return new Promise((resolve, reject) => {
      try {
        console.log('正在连接 WebSocket:', url)
        ws.value = new WebSocket(url)

        ws.value.onopen = () => {
          isConnected.value = true
          console.log('WebSocket 连接成功')
          resolve()
        }

        ws.value.onmessage = (event) => {
          try {
            console.log('收到消息:', event.data)
            const message: WebSocketMessage = JSON.parse(event.data)
            
            if (message.type === 'transcript') {
              options.onTranscript(message.data as TranscriptItem)
            } else if (message.type === 'summary') {
              options.onSummary(message.data as MeetingSummary)
            }
          } catch (error) {
            console.error('解析消息失败:', error, event.data)
          }
        }

        ws.value.onerror = (error) => {
          console.error('WebSocket 错误:', error)
          isConnected.value = false
        }

        ws.value.onclose = (event) => {
          isConnected.value = false
          console.log('WebSocket 连接关闭, 代码:', event.code, '原因:', event.reason)
          
          // 如果不是正常关闭，拒绝 Promise
          if (event.code !== 1000 && event.code !== 1001) {
            reject(new Error(`WebSocket 异常关闭: ${event.code} - ${event.reason}`))
          }
        }
      } catch (error) {
        console.error('创建 WebSocket 失败:', error)
        reject(error)
      }
    })
  }

  const disconnect = () => {
    if (ws.value) {
      ws.value.close(1000, 'Normal closure')
      ws.value = null
      isConnected.value = false
    }
  }

  const sendAudio = (audioBlob: Blob) => {
    if (ws.value && isConnected.value && ws.value.readyState === WebSocket.OPEN) {
      ws.value.send(audioBlob)
    } else {
      console.warn('WebSocket 未连接，无法发送音频')
    }
  }

  return {
    connect,
    disconnect,
    sendAudio,
    isConnected
  }
}
