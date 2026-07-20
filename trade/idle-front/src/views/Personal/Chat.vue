<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">商品对话聊天</h2>
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
import { ref, onMounted, onUnmounted, nextTick, getCurrentInstance } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import { getHistoryMsgApi } from '../../api/message'
import { MessageItem  } from '../../types'
import { ElMessage } from 'element-plus'

const instance = getCurrentInstance()
const route = useRoute()
// goodsId无值时赋值0，允许0（无商品聊天）
const goodsId = Number(route.query.goodsId ?? 0)
const otherId = Number(route.query.otherId ?? 0)
const token = sessionStorage.getItem('token')
// 当前登录用户ID、头像
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
    // ========== 新增：监听对方已读消息推送，实时更新状态 ==========
    if (res.type === "read_update") {
      const updatedMsgIds: number[] = res.msgIdList
      msgList.value.forEach(item => {
        if (updatedMsgIds.includes(item.id)) {
          item.readStatus = 1
        }
      })
      return
    }
    // 收到新消息追加列表
    const data = res as MessageItem
    // 兜底字段，解决WebSocket推送缺少头像、状态导致空白/TS报错
    data.fromUserAvatar = data.fromUserAvatar ?? ""
    data.readStatus = data.readStatus ?? 0
    // 删除无效的 instance?.proxy?.$forceUpdate()
    msgList.value.push(data)
    nextTick(() => {
      if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight
    })
    // // 收到新消息追加列表
    // const data = res as MessageItem
    // msgList.value.push(data)
    // instance?.proxy?.$forceUpdate()
    // nextTick(() => {
    //   if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight
    // })
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

// onMounted(async () => {
//   // 只校验对方用户otherId，goodsId允许0（无商品聊天）
//   if (isNaN(otherId) || otherId <= 0) {
//     ElMessage.error('参数异常：聊天对象不存在')
//     return
//   }
//   initWebSocket()
// })
onMounted(async () => {
  if (isNaN(otherId) || otherId <= 0) {
    ElMessage.error('参数异常：聊天对象不存在')
    return
  }
  initWebSocket()
  // 新增：浏览器切回页面自动重载聊天记录（折中方案，不用手动刷新）
  window.addEventListener('focus', loadHistory)
})

// onUnmounted(() => {
//   if (ws) {
//     ws.close()
//   }
// })
onUnmounted(() => {
  // 新增移除窗口焦点监听
  window.removeEventListener('focus', loadHistory)
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.close()
  }
  ws = null
})
</script>

<style scoped>
/* 全站统一页面外层样式 */
.page-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
/* 页面统一标题样式 */
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
/* 聊天主卡片 */
.chat-card {
  width: 700px;
  margin: 0 auto;
  padding: 30px;
  border-radius: 12px;
}
/* 聊天消息滚动区域 */
.msg-wrapper {
  height: 480px;
  padding: 16px;
  overflow-y: auto;
  background-color: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 24px;
}
/* 单条消息容器 */
.msg-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin: 14px 0;
}
/* 自己发送的消息右对齐 */
.msg-item.self {
  align-items: flex-end;
}
/* 头像+消息横向行 */
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
/* 消息气泡容器（包含文字+已读） */
.msg-content-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.msg-item.self .msg-content-box {
  align-items: flex-end;
}
/* 消息气泡通用 */
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
/* 我方气泡主色调 */
.msg-item.self .msg-content {
  background-color: #409EFF;
  color: #fff;
}
/* 已读/未读文字 */
.read-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
/* 消息时间文字 */
.time {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
/* 底部发送栏 */
.send-area {
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
</style>