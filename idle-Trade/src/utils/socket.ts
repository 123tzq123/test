let socketTask: any | null = null
const WS_URL = "ws://localhost:8080/ws"
// 消息接收回调（原有：给消息列表页面使用）
let onMessageCallback: ((msg: any) => void) | null = null
// ✅ 新增：连接就绪标记
export let isWsConnected = false

// 初始化WebSocket连接
export function initSocket(userId: number, msgCallback?: (msg: any) => void) {
  // 先关闭已有连接
  if (socketTask) {
    socketTask.close()
    socketTask = null
    isWsConnected = false
  }
  socketTask = uni.connectSocket({
    url: `${WS_URL}?userId=${userId}`,
    complete() {}
  })
  if (msgCallback) {
    onMessageCallback = msgCallback
  }

  // 连接成功
  socketTask.onOpen(() => {
    console.log("WebSocket 连接成功")
    isWsConnected = true
  })

  // 收到服务端消息【重点修改此处】
  socketTask.onMessage((res: any) => {
    try {
      const data = JSON.parse(res.data)
      // 1. 执行原有回调（消息列表页面刷新会话）
      if (onMessageCallback) {
        onMessageCallback(data)
      }
      // 2. 全局广播事件，Chat聊天页面监听这条消息
      uni.$emit('ws_message', data)
    } catch (e) {
      console.error("消息解析失败", e)
    }
  })

  // 连接关闭
  socketTask.onClose(() => {
    console.log("WebSocket 连接关闭")
    socketTask = null
    isWsConnected = false
  })

  // 连接错误
  socketTask.onError((err: any) => {
    console.error("WebSocket 异常", err)
    socketTask = null
    isWsConnected = false
  })

  return socketTask
}

// 发送消息
export function sendSocketMsg(data: any) {
  if (!socketTask || !isWsConnected) {
    uni.showToast({ title: "连接未就绪，请稍后重试", icon: "none" })
    return
  }
  socketTask.send({
    data: JSON.stringify(data)
  })
}

// 主动关闭连接
export function closeSocket() {
  if (socketTask) {
    socketTask.close()
  }
  socketTask = null
  isWsConnected = false
  onMessageCallback = null
}