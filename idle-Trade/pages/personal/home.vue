<template>
<view class="page-wrap">
  <!-- 用户信息头部 -->
  <view class="user-header">
    <image v-if="userInfo.avatar" class="avatar" :src="userInfo.avatar" />
    <view v-else class="avatar empty-avatar">
      <text>头像</text>
    </view>
    <view class="user-text">
      <text class="nickname">{{userInfo.nickname || "未登录"}}</text>
      <text class="tip" v-if="!hasLogin">点击前往登录</text>
    </view>
  </view>

  <!-- 功能菜单列表 -->
  <view class="menu-card">
    <view class="menu-item" @click="goPage('/pages/personal/messageList')">
      <view class="menu-left">
        <text>💬 我的消息</text>
        <!-- 未读角标 -->
        <view v-if="totalUnread > 0" class="unread-badge">
          {{ totalUnread > 99 ? '99+' : totalUnread }}
        </view>
      </view>
      <text class="arrow">▶</text>
    </view>
    <view class="menu-item" @click="goPage('/pages/personal/profileEdit')">
      <text>⚙️ 个人信息设置</text>
      <text class="arrow">▶</text>
    </view>
    <view class="menu-item" @click="goPage('/pages/personal/myCollect')">
      <text>❤️ 我的收藏</text>
      <text class="arrow">▶</text>
    </view>
    <view class="menu-item" @click="goPage('/pages/personal/order')">
      <text>📦 我买到的订单</text>
      <text class="arrow">▶</text>
    </view>
    <view class="menu-item" @click="goPage('/pages/personal/sellOrder')">
      <text>💰 我卖出的订单</text>
      <text class="arrow">▶</text>
    </view>
    <view class="menu-item" @click="goPage('/pages/personal/index')">
      <text>📋 我的发布</text>
      <text class="arrow">▶</text>
    </view>
    <view class="menu-item" @click="goPage('/pages/personal/myCreateComment')">
      <text>✍ 我发布的评价</text>
      <text class="arrow">▶</text>
    </view>
    <view class="menu-item" @click="goPage('/pages/personal/myReceiveComment')">
      <text>💬 收到的评价</text>
      <text class="arrow">▶</text>
    </view>
  </view>

  <button v-if="hasLogin" class="logout-btn" @click="logout">退出登录</button>
</view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow, onUnload } from '@dcloudio/uni-app'
import { getUserInfoApi } from '../../src/api/user'
import { getChatSessionListApi } from '../../src/api/message'
import { SysUser as UserItem } from '../../src/types'

const hasLogin = ref(false)
const userInfo = ref<UserItem>({
  id:0,
  username:'',
  nickname:'',
  avatar:''
})
// ✅ 总未读消息数
const totalUnread = ref(0)
// 保存ws监听回调
let wsHandler: ((msg: any)=>void) | null = null

// 加载用户信息
const loadUserInfo = async () => {
  const token = uni.getStorageSync('token')
  if(!token){
    hasLogin.value = false
    userInfo.value = {id:0,username:'',nickname:'',avatar:''}
    totalUnread.value = 0
    return
  }
  hasLogin.value = true
  const res = await getUserInfoApi()
  if(res.code === 200){
    userInfo.value = res.data
    uni.setStorageSync('nickname', res.data.nickname)
    uni.setStorageSync('avatar', res.data.avatar)
  }
  // 登录状态加载未读总数
  loadUnreadCount()
  // 新增：刷新底部tab红点
  updateTabBadge()
}

// 加载会话列表，统计全部未读消息
const loadUnreadCount = async () => {
  const res = await getChatSessionListApi()
  if(res.code === 200){
    let sum = 0
    res.data.forEach((item:any)=>{
      sum += item.unreadNum || 0
    })
    totalUnread.value = sum
  }
}

// 更新底部tab【我的】未读角标
const updateTabBadge = async () => {
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.removeTabBarBadge({ index: 2 })
    return
  }
  const res = await getChatSessionListApi()
  if (res.code === 200) {
    let totalUnread = 0
    res.data.forEach((item: any) => {
      totalUnread += item.unreadNum || 0
    })
    if (totalUnread > 0) {
      uni.setTabBarBadge({
        index: 2,
        text: totalUnread > 99 ? '99+' : String(totalUnread)
      })
    } else {
      uni.removeTabBarBadge({ index: 2 })
    }
  }
}

// 收到websocket消息，刷新未读数量
const handleWsMsg = () => {
  loadUnreadCount()
}

// 跳转页面
const goPage = (url:string) => {
  if(!hasLogin.value){
    uni.navigateTo({url:"/pages/login/login"})
    return
  }
  uni.navigateTo({url})
}

// 退出登录
const logout = () => {
  uni.showModal({
    title:"提示",
    content:"确定退出登录？",
    success:res=>{
      if(res.confirm){
        uni.removeStorageSync('token')
        uni.removeStorageSync('userId')
        uni.removeStorageSync('nickname')
        uni.removeStorageSync('avatar')
        loadUserInfo()
        uni.switchTab({url:"/pages/index/index"})
      }
    }
  })
}

onShow(()=>{
  loadUserInfo()
  // 监听全局ws消息事件
  wsHandler = handleWsMsg
  uni.$on('ws_message', wsHandler)
})

// 页面销毁移除监听
onUnload(()=>{
  if(wsHandler){
    uni.$off('ws_message', wsHandler)
  }
})
</script>

<style scoped>
.page-wrap {
  padding: 32rpx;
  background: #f5f7fa;
  min-height: 100vh;
}
.user-header {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  display: flex;
  align-items: center;
  gap: 32rpx;
  margin-bottom: 32rpx;
}
.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
}
.empty-avatar {
  width: 120rpx;
  height: 120rpx;
  background: #eee;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color:#999;
}
.nickname {
  font-size: 40rpx;
  font-weight: bold;
  display: block;
}
.tip {
  font-size: 28rpx;
  color:#999;
  margin-top: 8rpx;
}
.menu-card {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 40rpx;
}
.menu-item {
  padding: 36rpx 32rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1rpx solid #f2f2f2;
  font-size: 32rpx;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.arrow {
  color:#999;
  font-size: 34rpx;
}
.logout-btn {
  width: 100%;
  background: #fff;
  color:#f53f3f;
  border: 1rpx solid #f53f3f;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 32rpx;
}
/* 未读红点角标样式 */
.unread-badge {
  min-width: 36rpx;
  height: 36rpx;
  background-color: #f53f3f;
  border-radius: 18rpx;
  color: #fff;
  font-size: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 8rpx;
}
</style>