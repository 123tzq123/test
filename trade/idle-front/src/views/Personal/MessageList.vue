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
  />
</div>
<div class="chat-empty" v-else>
  <p>请左侧选择聊天会话查看消息</p>
</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import NavBar from '../../components/NavBar.vue'
import ChatVue from './Chat.vue'
import { getChatSessionListApi } from '../../api/message'
import { ChatSessionItem } from '../../types'
import { useRoute } from 'vue-router'

// 会话列表
const sessionList = ref<ChatSessionItem[]>([])
// 当前选中会话
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

// 存储需要自动打开的目标用户ID
const targetOpenOtherId = ref<number | null>(null)

const loadSessionList = async () => {
  const resp = await getChatSessionListApi()
  console.log('完整后端返回体：', resp)
  if (resp.code === 200) {
    sessionList.value = resp.data
    console.log('赋值后的会话列表长度：', sessionList.value.length, sessionList.value)

    // 列表加载完成后，如果存在待打开的目标用户，自动选中会话
    if (targetOpenOtherId.value) {
      const targetSession = sessionList.value.find(item => item.otherId === targetOpenOtherId.value)
      if (targetSession) {
        selectSession.value = {
          ...targetSession,
          goodsId: 0
        }
        // 清空标记，避免重复触发
        targetOpenOtherId.value = null
      }
    }
    // 标记未读数量变更，让NavBar实时刷新红点
    localStorage.setItem('refreshUnreadFlag', String(Date.now()))
  }
}

// 打开聊天会话，强制goodsId=0，查询该用户全部聊天
const openChat = async (item: ChatSessionItem) => {
  selectSession.value = {
    ...item,
    goodsId: 0
  }
  // 关键：打开会话后重新拉取会话列表，后端已标记已读，列表unreadNum清零
  await loadSessionList()
}

// 监听路由参数：保存需要打开的用户ID，等待列表加载完成后再匹配
watch(
  () => route.query.targetOtherId,
  (targetId) => {
    if (!targetId) {
      targetOpenOtherId.value = null
      return
    }
    targetOpenOtherId.value = Number(targetId)
    // 如果列表已经加载完毕，直接匹配
    if (sessionList.value.length > 0) {
      const targetSession = sessionList.value.find(item => item.otherId === targetOpenOtherId.value)
      if (targetSession) {
        selectSession.value = {
          ...targetSession,
          goodsId: 0
        }
        targetOpenOtherId.value = null
      }
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