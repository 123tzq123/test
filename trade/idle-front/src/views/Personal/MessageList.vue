<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">我的消息</h2>
    <div class="chat-container">
      <div class="session-left">
  <div class="session-scroll" :key="sessionList.length">
    <div v-if="sessionList.length > 0">
      <div
        class="session-item"
        v-for="item in sessionList"
        :key="item.otherId"
        :class="{ active: selectSession?.otherId === item.otherId }"
        @click="openChat(item)"
      >
        <el-avatar :src="item.otherAvatar" style="width:44px;height:44px;"></el-avatar>
        <div class="session-info">
          <div class="session-top">
            <span class="name">{{ item.otherName }}</span>
            <span class="time">{{ formatTime(item.lastMsgTime) }}</span>
          </div>
          <div class="session-bottom">
            <span class="last-text">{{ item.lastMsg }}</span>
            <span v-if="item.unreadNum > 0" class="session-badge">{{ item.unreadNum >99 ? '99+' : item.unreadNum }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="no-session-tip">暂无聊天会话</div>
  </div>
</div>
<!-- 右侧聊天区域 -->
<div class="chat-right" v-if="selectSession">
  <ChatVue 
    :key="selectSession.otherId"
    :other-id="selectSession.otherId" 
    :goods-id="0" 
    @send-success="loadSessionList"
  />
</div>
<div class="chat-empty" v-else>
  <p>请左侧选择聊天会话查看消息</p>
</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import NavBar from '../../components/NavBar.vue'
import ChatVue from './Chat.vue'
import { getChatSessionListApi } from '../../api/message'
import { ChatSessionItem } from '../../types'
import { useRoute } from 'vue-router'

// 会话列表（后端只返回有聊天记录的用户）
const sessionList = ref<ChatSessionItem[]>([])
// 当前选中会话（可以是后端真实会话 / 前端临时虚拟会话）
const selectSession = ref<ChatSessionItem | null>(null)
const route = useRoute()

// 格式化时间
const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const h = date.getHours().toString().padStart(2, '0')
  const m = date.getMinutes().toString().padStart(2, '0')
  return `${h}:${m}`
}

// 加载后端会话列表
const loadSessionList = async () => {
  const resp = await getChatSessionListApi()
  if (resp.code === 200) {
    sessionList.value = resp.data
    // 列表加载完成后处理路由目标用户
    await handleRouteTargetUser()
    localStorage.setItem('refreshUnreadFlag', String(Date.now()))
  }
}

// 处理路由传入的目标聊天用户【核心新增方法】
const handleRouteTargetUser = async () => {
  const targetId = route.query.targetOtherId
  const targetName = route.query.targetOtherName
  const targetAvatar = route.query.targetOtherAvatar
  if (!targetId) return

  const targetOtherId = Number(targetId)
  // 1. 在后端会话列表查找是否存在该用户
  const existSession = sessionList.value.find(item => item.otherId === targetOtherId)

  if (existSession) {
    // 已有聊天记录，直接打开真实会话
    selectSession.value = { ...existSession, goodsId: 0 }
  } else {
    // ====== 没有聊天记录，构造【临时虚拟会话】打开右侧聊天窗口 ======
    selectSession.value = {
      otherId: targetOtherId,
      otherName: String(targetName || '未知用户'),
      otherAvatar: String(targetAvatar || ''),
      lastMsg: '',
      lastMsgTime: '',
      unreadNum: 0,
      goodsId: 0
    }
  }
}

// 点击左侧已有会话
const openChat = async (item: ChatSessionItem) => {
  selectSession.value = {
    ...item,
    goodsId: 0
  }
  await loadSessionList()
}

// 监听路由参数变化
watch(
  () => route.query,
  async () => {
    if (sessionList.value.length > 0) {
      await handleRouteTargetUser()
    }
  },
  { immediate: true }
)

onMounted(() => {
  loadSessionList()
})
</script>

<style scoped>
.page-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
  overflow: hidden; /* 新增：页面整体禁止滚动 */
}
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
/* 外层双栏容器 */
/* 外层双栏容器 */
.chat-container {
  display: flex;
  width: 100%;
  height: 600px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 12px rgba(0,0,0,0.08);
}
/* 正确深度穿透：类名前面加 ::v-deep */
:deep(.session-left) {
  width: 320px;
  border-right: 1px solid #eee;
  height: 100%;
}
:deep(.session-scroll) {
  height: 100%;
  overflow-y: auto;
  background: #f0f0f0;
}
.session-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
}
.session-item:hover {
  background: #f8f9fa;
}
.session-item.active {
  background: #e6f7ff;
}
.session-info {
  flex: 1;
  overflow: hidden;
}
.session-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.name {
  font-size: 15px;
  color: #333;
}
.time {
  font-size: 12px;
  color: #999;
}
.session-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.last-text {
  font-size: 13px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}
.session-badge {
  background: #f53f3f;
  color: white;
  font-size: 10px;
  border-radius: 10px;
  padding: 0 6px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* 右侧聊天区 */
.chat-right {
  flex: 1;
  height: 100%; /* 新增，继承父容器600px高度 */
}
.chat-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 16px;
}
</style>