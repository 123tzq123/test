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
    </div>
    <!-- 右侧头像下拉 -->
    <div class="nav-right" v-if="getLoginInfo().loginUserId">
      <el-dropdown trigger="click" @visible-change="handleDropdownOpen">
        <!-- 头像+未读红点 -->
        <div class="avatar-wrap relative">
          <el-avatar :src="getLoginInfo().avatarUrl" size="38" />
          <!-- 顶部头像未读红点 -->
          <span v-if="totalUnread > 0" class="unread-badge">{{ totalUnread > 99 ? '99+' : totalUnread }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/personal/home')">我的主页</el-dropdown-item>
            <!-- 我的消息 新增红点 -->
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
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { computed, ref, onMounted, onUnmounted, watch } from 'vue'
import { getUnreadTotalApi } from '../api/message'
const router = useRouter()

// 未读消息总数
const totalUnread = ref(0)
let unreadTimer: number | null = null

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
  // 缩短轮询为10秒，更快同步后端新消息
  unreadTimer = window.setInterval(loadUnreadCount, 10000)
  // 浏览器窗口切回可见时刷新
  window.addEventListener('focus', loadUnreadCount)
})

onUnmounted(() => {
  if (unreadTimer) clearInterval(unreadTimer)
  window.removeEventListener('focus', loadUnreadCount)
})

// 退出登录：清空当前标签页sessionStorage
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
</style>