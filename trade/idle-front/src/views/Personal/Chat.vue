<template>
  <div class="chat-wrap">
    <el-card class="chat-card">
      <!-- 聊天消息区域 -->
      <div class="msg-wrapper" ref="scrollRef">
        <div
          class="msg-item"
          v-for="item in msgList"
          :key="item.id"
          :class="{ self: item.fromUserId === loginUserId }"
        >
          <!-- 头像 + 消息内容行 -->
          <div class="msg-row">
            <!-- 对方头像（我方隐藏） -->
            <el-avatar 
              v-if="item.fromUserId !== loginUserId" 
              :src="item.fromUserAvatar || ''" 
              size="40"
              class="avatar"
            ></el-avatar>
            <!-- 消息气泡 -->
            <div class="msg-content-box">
              <div class="msg-content">
                {{ item.content }}
              </div>
              <!-- 我方消息：显示已读/未读 -->
              <span v-if="item.fromUserId === loginUserId" class="read-text">
                {{ item.readStatus === 1 ? "已读" : "未读" }}
              </span>
            </div>
            <!-- 自己头像（对方隐藏） -->
            <el-avatar 
              v-if="item.fromUserId === loginUserId" 
              :src="myAvatar" 
              size="40"
              class="avatar"
            ></el-avatar>
          </div>
          <div class="time">{{ formatTime(item.createTime) }}</div>
        </div>
      </div>
      <!-- 发送消息区域 -->
      <div class="send-area">
        <el-input
          v-model="content"
          placeholder="请输入消息内容"
          size="large"
          @keyup.enter="sendMsg"
        ></el-input>
        <el-button type="primary" size="large" @click="sendMsg">发送</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getHistoryMsgApi, saveMessageApi } from '../../api/message'
import { MessageItem  } from '../../types'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
// 消息去重追加工具函数
const addMsgNoRepeat = (newMsg: MessageItem) => {
  // 通过消息唯一id判断列表是否已有这条消息
  const isExist = msgList.value.some(item => item.id === newMsg.id)
  if (!isExist) {
    msgList.value.push(newMsg)
    // 追加后自动滚动到底部
    nextTick(() => {
      if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight
    })
  }
}

// 接收父组件传参
const props = defineProps<{
  otherId?: number
  goodsId?: number
}>()
//const emit = defineEmits(['close'])
const emit = defineEmits(['close','send-success'])
const route = useRoute()

// 优先级：props > 路由query，兼容两种打开方式
let tempOtherId = props.otherId
let tempGoodsId = props.goodsId ?? 0
// 路由跳转（商品详情/卖家主页）时读取query
if(tempOtherId === undefined || tempOtherId <= 0){
  tempOtherId = Number(route.query.otherId ?? 0)
  tempGoodsId = Number(route.query.goodsId ?? 0)
}
const goodsId = tempGoodsId
const otherId = tempOtherId

const token = sessionStorage.getItem('token')
const loginUserId = ref(0)
const myAvatar = ref(sessionStorage.getItem('avatar') || '')

const msgList = ref<MessageItem[]>([])
const content = ref('')
const scrollRef = ref<HTMLDivElement | null>(null)
let ws: WebSocket | null = null
let loaded = false

const formatTime = (timeStr: string) => {
  if (!timeStr) return ""
  const date = new Date(timeStr)
  if (isNaN(date.getTime())) return ""
  const h = date.getHours().toString().padStart(2, '0')
  const m = date.getMinutes().toString().padStart(2, '0')
  return `${h}:${m}`
}

const loadHistory = async () => {
  // 双重校验参数，非法直接终止
  if (otherId <= 0) {
    ElMessage.error('参数异常：聊天对象不存在')
    emit('close')
    return
  }
  const res = await getHistoryMsgApi(goodsId, otherId)
  if (res.code === 200) {
    msgList.value = res.data.records
    await nextTick(() => {
      if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight
    })
  }
}

const initWebSocket = () => {
  ws = new WebSocket(`ws://localhost:8080/ws/chat?token=${token}`)
  ws.onmessage = (event) => {
  const res = JSON.parse(event.data)
  if (res.type === "init") {
    loginUserId.value = res.userId
    if (!loaded) {
      loaded = true
      loadHistory()
    }
    return
  }
  if (res.type === "read_update") {
    const updatedMsgIds: number[] = res.msgIdList
    msgList.value.forEach(item => {
      if (updatedMsgIds.includes(item.id)) item.readStatus = 1
    })
    return
  }
  // 区分消息类型
  if(res.type !== "chat_msg") return
  const data = res as MessageItem
  // 统一转数字对比，防止字符串/数字不匹配导致过滤失效
  if(Number(data.fromUserId) === loginUserId.value) {
    return
  }
  data.fromUserAvatar = data.fromUserAvatar ?? ""
  data.readStatus = data.readStatus ?? 0
  // 替换原来 msgList.value.push(data)
  addMsgNoRepeat(data)
  // 新消息抵达，标记刷新未读红点
  localStorage.setItem('refreshUnreadFlag', String(Date.now()))
}
  ws.onerror = () => {
    ElMessage.error('聊天连接失败，请刷新页面重试')
  }
}
const sendMsg = async () => {
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
  try {
    const res = await saveMessageApi(sendData)
    if (res.code === 200) {
  const newMsg = res.data
  addMsgNoRepeat(newMsg)
  content.value = ''
  // 发送成功通知父组件刷新会话列表
  emit('send-success')
}
  } catch {
    ElMessage.error('发送失败，请重试')
  }
}

onMounted(async () => {
  if (isNaN(otherId) || otherId <= 0) {
    ElMessage.error('参数异常：聊天对象不存在')
    emit('close')
    return
  }
  initWebSocket()
  window.addEventListener('focus', loadHistory)
})

onUnmounted(() => {
  window.removeEventListener('focus', loadHistory)
  if (ws && ws.readyState === WebSocket.OPEN) ws.close()
  ws = null
})
</script>

<style scoped>
.chat-wrap {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.chat-card {
  flex: 1;
  padding: 20px;
  border-radius: 12px;
  position: relative; /* 绝对定位父容器 */
  height: 100%;
  margin: 0;
  box-sizing: border-box;
}
/* 消息滚动区域：底部预留输入框高度，不会被遮挡 */
.msg-wrapper {
  width: 100%;
  height: calc(100% - 30px); /* 固定扣除输入框区域高度 */
  padding: 16px;
  overflow-y: auto;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-sizing: border-box;
}
/* 发送栏：绝对定位固定卡片底部，永远不动 */
.send-area {
  position: absolute;
  bottom: 10px;
  left: 20px;
  right: 20px;
  display: flex;
  gap: 16px;
  align-items: center;
}
.send-area :deep(.el-input__inner) {
  border-radius: 8px;
}
.send-area :deep(.el-input) {
  flex: 1;
}
/* 消息条目样式不变 */
.msg-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 14px 0;
}
.msg-item.self {
  align-items: flex-end;
}
.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}
.msg-item.self .msg-row {
  flex-direction: row-reverse;
}
.avatar {
  flex-shrink: 0;
}
.msg-content-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.msg-item.self .msg-content-box {
  align-items: flex-end;
}
.msg-content {
  display: inline-block;
  padding: 10px 16px;
  max-width: 320px;
  border-radius: 12px;
  font-size: 15px;
  background-color: #ffffff;
  color: #303133;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.msg-item.self .msg-content {
  background-color: #409EFF;
  color: #fff;
}
.read-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.time {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
</style>