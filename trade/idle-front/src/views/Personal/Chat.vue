<template>
  <NavBar></NavBar>
  <div class="chat-page">
    <div class="chat-container">
      <h3>商品对话聊天</h3>
      <div class="msg-wrapper" ref="scrollRef">
        <div
          class="msg-item"
          v-for="item in msgList"
          :key="item.id"
          :class="{ self: item.fromUserId === loginUserId }"
        >
          <div class="msg-content">
            {{ item.content }}
          </div>
          <div class="time">{{ formatTime(item.createTime) }}</div>
        </div>
      </div>
      <div class="send-area">
        <el-input v-model="content" placeholder="请输入消息内容" @keyup.enter="sendMsg"></el-input>
        <el-button type="primary" @click="sendMsg">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, getCurrentInstance } from 'vue'
import { useRoute } from 'vue-router'
import Cookies from 'js-cookie'
import NavBar from '../../components/NavBar.vue'
import { getHistoryMsgApi } from '../../api/message'
import { ElMessage } from 'element-plus'

const instance = getCurrentInstance()

interface MessageItem {
  id: number
  goodsId: number
  fromUserId: number
  toUserId: number
  content: string
  createTime: string
}

const route = useRoute()
const goodsId = Number(route.query.goodsId ?? 0)
const otherId = Number(route.query.otherId ?? 0)
const token = Cookies.get('token')

const loginUserId = ref(0)
const msgList = ref<MessageItem[]>([])
const content = ref('')
const scrollRef = ref<HTMLDivElement | null>(null)
let ws: WebSocket | null = null
let loaded = false

const formatTime = (timeStr: string) => {
  if (!timeStr) return ""
  const date = new Date(timeStr)
  if (isNaN(date.getTime())) {
    return ""
  }
  const h = date.getHours().toString().padStart(2, '0')
  const m = date.getMinutes().toString().padStart(2, '0')
  return `${h}:${m}`
}
// 加载聊天记录
const loadHistory = async () => {
  const res = await getHistoryMsgApi(goodsId, otherId)
  if (res.code === 200) {
    msgList.value = res.data.records
    await nextTick(() => {
      if (scrollRef.value) {
        scrollRef.value.scrollTop = scrollRef.value.scrollHeight
      }
    })
  }
}

// 初始化websocket连接
const initWebSocket = () => {
  ws = new WebSocket(`ws://localhost:8080/ws/chat?token=${token}`)
  ws.onmessage = (event) => {
  console.log("后端推送的数据", event.data)
  const res = JSON.parse(event.data)
  if (res.type === "init") {
    loginUserId.value = res.userId
    if (!loaded) {
      loaded = true
      loadHistory()
    }
    return
  }
  const data = res as MessageItem
  msgList.value.push(data)
  instance?.proxy?.$forceUpdate()
  nextTick(() => {
    if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight
  })
}
  ws.onerror = () => {
    ElMessage.error('聊天连接失败，请刷新页面重试')
  }
}

//发送消息
const sendMsg = () => {
  if (!content.value.trim()) {
    ElMessage.warning('消息不能为空')
    return
  }
  if (!ws || ws.readyState !== WebSocket.OPEN) {
    ElMessage.warning('连接异常，稍后再试')
    return
  }
  const sendData = {
    goodsId: goodsId,
    toUserId: otherId,
    content: content.value.trim()
  }
  ws.send(JSON.stringify(sendData))
  content.value = ''
}

onMounted(async () => {
  if (isNaN(goodsId) || isNaN(otherId) || goodsId <= 0 || otherId <= 0) {
    ElMessage.error('参数异常')
    return
  }
  initWebSocket()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
</script>

<style scoped>
.chat-page {
  padding: 20px;
}
.chat-container {
  width: 700px;
  margin: 0 auto;
}
.msg-wrapper {
  height: 480px;
  border: 1px solid #dcdcdc;
  padding: 12px;
  overflow-y: auto;
  background-color: #f8f8f8;
}
.msg-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 10px 0;
}
.msg-item.self {
  align-items: flex-end;
}
.msg-content {
  display: inline-block;
  padding: 8px 14px;
  max-width: 300px;
  border-radius: 10px;
  background-color: #ffffff;
  color: #333;
}
.msg-item.self .msg-content {
  background-color: #95ec69;
  color: black;
}
.time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.send-area {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}
.send-area :deep(.el-input) {
  flex: 1;
}
</style>