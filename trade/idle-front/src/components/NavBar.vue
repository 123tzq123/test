<template>
  <div class="nav-bar">
    <!-- 左侧Logo与平台名 -->
    <div class="nav-left" @click="$router.push('/')">
      <span class="logo-text">闲置优品二手交易平台</span>
    </div>
    <!-- 中间导航菜单 -->
    <div class="nav-menu">
      <router-link to="/" class="menu-item" active-class="active">首页</router-link>
      <router-link to="/publish" class="menu-item" active-class="active">发布闲置</router-link>
      <router-link to="/personal/goods" class="menu-item" active-class="active">我的发布</router-link>
      <router-link to="/personal/collect" class="menu-item" active-class="active">我的收藏</router-link>
      <router-link to="/personal/order" class="menu-item" active-class="active">我的订单</router-link>
      <router-link to="/personal/sell" class="menu-item" active-class="active">卖出订单</router-link>
      <router-link to="/personal/comment" class="menu-item" active-class="active">过往评价</router-link>
      <router-link to="/personal/receiveComment" class="menu-item" active-class="active">收到评价</router-link>
      <!-- 新增AI助手按钮【修改点击事件】 -->
      <div class="menu-item ai-btn" @click="openAiDialog">🤖 AI小助手</div>
    </div>
    <!-- 右侧头像下拉 -->
    <div class="nav-right" v-if="getLoginInfo().loginUserId">
      <el-dropdown trigger="click" @visible-change="handleDropdownOpen">
        <div class="avatar-wrap relative">
          <el-avatar :src="getLoginInfo().avatarUrl" size="38" />
          <span v-if="totalUnread > 0" class="unread-badge">{{ totalUnread > 99 ? '99+' : totalUnread }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/personal/home')">我的主页</el-dropdown-item>
            <el-dropdown-item @click="$router.push('/personal/message')" class="msg-menu-item">
              <span>我的消息</span>
              <span v-if="totalUnread > 0" class="menu-badge">{{ totalUnread >99 ? '99+' : totalUnread }}</span>
            </el-dropdown-item>
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div class="nav-right" v-else>
      <el-button text @click="$router.push('/login')">登录</el-button>
    </div>

    <!-- ========== AI助手弹窗 全局悬浮遮罩 ========== -->
    <div v-if="showAiDialog" class="ai-mask" @click.self="showAiDialog = false">
      <div class="ai-dialog" @click.stop>
        <!-- 弹窗头部 -->
        <div class="ai-header">
          <h3>🤖 AI问答助手</h3>
          <span class="close-btn" @click="showAiDialog = false">×</span>
        </div>
        <!-- 对话滚动区域 -->
        <div class="ai-msg-box" ref="aiScrollRef">
          <div
            class="ai-msg-item"
            v-for="(item, index) in aiMsgList"
            :key="index"
            :class="{ user: item.type === 'user', ai: item.type === 'ai' }"
          >
            <div class="msg-bubble">{{ item.content }}</div>
          </div>
          <div v-if="aiLoading" class="loading-text">AI思考中...</div>
        </div>
        <!-- 输入发送区域 -->
        <div class="ai-input-area">
          <el-input
            v-model="aiInputText"
            placeholder="输入你的问题，回车发送"
            @keyup.enter="sendAiMsg"
          ></el-input>
          <el-button type="primary" @click="sendAiMsg" :loading="aiLoading">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { computed, ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { getUnreadTotalApi, askAiApi } from '../api/message'
const router = useRouter()

// 未读消息总数
const totalUnread = ref(0)
let unreadTimer: number | null = null

// AI助手弹窗状态
const showAiDialog = ref(false)
const aiScrollRef = ref<HTMLDivElement | null>(null)
const aiInputText = ref('')
const aiLoading = ref(false)
// AI对话列表 type: user/ai
interface AiMsg {
  type: 'user' | 'ai'
  content: string
}
const aiMsgList = ref<AiMsg[]>([])

/**
 * 打开AI弹窗，不清空历史消息，仅首次打开自动追加欢迎语
 */
const openAiDialog = () => {
  showAiDialog.value = true
  // 判断是否已经存在欢迎消息，不存在才添加
  const existWelcome = aiMsgList.value.some(m =>
    m.content.includes('闲置优品二手交易平台AI助手')
  )
  if (!existWelcome) {
    aiMsgList.value.push({
      type: 'ai',
      content: '我是闲置优品二手交易平台AI助手，仅解答平台发布商品、订单、收藏、评价、聊天交易相关问题。'
    })
  }
  nextTick(() => {
    if (aiScrollRef.value) {
      aiScrollRef.value.scrollTop = aiScrollRef.value.scrollHeight
    }
  })
}

const sendAiMsg = async () => {
  const text = aiInputText.value.trim()
  if (!text) return
  aiMsgList.value.push({ type: 'user', content: text })
  aiInputText.value = ''
  aiLoading.value = true

  await nextTick(() => {
    if (aiScrollRef.value) aiScrollRef.value.scrollTop = aiScrollRef.value.scrollHeight
  })

  try {
    const res = await askAiApi(text)
    if (res.code === 200) {
      aiMsgList.value.push({ type: 'ai', content: res.data.answer })
    }
  } catch {
    aiMsgList.value.push({ type: 'ai', content: 'AI服务暂时异常，请稍后重试' })
  } finally {
    aiLoading.value = false
    nextTick(() => {
      if (aiScrollRef.value) aiScrollRef.value.scrollTop = aiScrollRef.value.scrollHeight
    })
  }
}

// 每次渲染实时读取 sessionStorage 获取最新登录ID、头像
const getLoginInfo = () => {
  const userIdStr = sessionStorage.getItem('userId')
  const avatar = sessionStorage.getItem('avatar') || ''
  return {
    loginUserId: userIdStr ? Number(userIdStr) : null,
    avatarUrl: avatar
  }
}

// 实时响应登录状态
const loginInfo = computed(() => getLoginInfo())

// 拉取未读总数
const loadUnreadCount = async () => {
  if (!loginInfo.value.loginUserId) {
    totalUnread.value = 0
    return
  }
  const res = await getUnreadTotalApi()
  if (res.code === 200) {
    totalUnread.value = res.data
  }
}

// 下拉框打开时刷新未读数
const handleDropdownOpen = () => {
  loadUnreadCount()
}

// 监听本地存储变化，MessageList打开会话清除未读后自动刷新红点
watch(
  () => localStorage.getItem('refreshUnreadFlag'),
  () => {
    loadUnreadCount()
  }
)

onMounted(() => {
  loadUnreadCount()
  unreadTimer = window.setInterval(loadUnreadCount, 10000)
  window.addEventListener('focus', loadUnreadCount)
})

onUnmounted(() => {
  if (unreadTimer) clearInterval(unreadTimer)
  window.removeEventListener('focus', loadUnreadCount)
})

// 退出登录
const logout = () => {
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('userId')
  sessionStorage.removeItem('avatar')
  localStorage.removeItem('refreshUnreadFlag')
  totalUnread.value = 0
  router.push('/login')
}
</script>


<style scoped>
.nav-bar {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 48px;
  border-bottom: 1px solid #e5e7eb;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 999;
}
.nav-left .logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #409EFF;
  cursor: pointer;
}
.nav-menu {
  display: flex;
  gap: 32px;
}
.menu-item {
  font-size: 15px;
  color: #333;
  text-decoration: none;
}
.menu-item.active {
  color: #409EFF;
  font-weight: 500;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 6px;
}
.nav-right {
  display: flex;
  align-items: center;
}
.avatar-wrap {
  cursor: pointer;
  position: relative;
}
/* 未读红点 */
.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #f53f3f;
  color: #fff;
  font-size: 10px;
  padding: 0 5px;
  border-radius: 10px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* 下拉菜单我的消息红点 */
.msg-menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.menu-badge {
  background: #f53f3f;
  color: #fff;
  font-size: 10px;
  border-radius: 10px;
  padding: 0 5px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* AI导航按钮 */
.ai-btn {
  cursor: pointer;
}
.ai-btn:hover {
  color: #409EFF;
}

/* AI遮罩层 */
.ai-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0,0,0,0.4);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* AI弹窗主体 */
.ai-dialog {
  width: 520px;
  height: 620px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
/* 弹窗头部 */
.ai-header {
  height: 56px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
}
.close-btn {
  font-size: 24px;
  cursor: pointer;
  color: #999;
}
.close-btn:hover {
  color: #f53f3f;
}
/* 对话消息滚动区 */
.ai-msg-box {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f8f9fa;
}
/* 单条消息 */
.ai-msg-item {
  margin: 12px 0;
  display: flex;
}
.ai-msg-item.user {
  justify-content: flex-end;
}
.ai-msg-item.ai {
  justify-content: flex-start;
}
.msg-bubble {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 10px;
  font-size: 14px;
}
.ai-msg-item.user .msg-bubble {
  background: #409EFF;
  color: #fff;
}
.ai-msg-item.ai .msg-bubble {
  background: #fff;
  color: #333;
}
.loading-text {
  color: #999;
  font-size: 13px;
}
/* 底部输入区域 */
.ai-input-area {
  padding: 12px 16px;
  display: flex;
  gap: 12px;
  align-items: center;
  border-top: 1px solid #eee;
}
.ai-input-area :deep(.el-input) {
  flex: 1;
}
</style>