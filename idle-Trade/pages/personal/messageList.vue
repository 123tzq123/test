<template>
<view class="page-wrap">
  <text class="page-title">我的消息</text>
  <view v-if="sessionList.length === 0" class="empty-tip">
    <text>暂无聊天会话</text>
  </view>
  <view v-for="item in sessionList" :key="item.otherId" class="session-item" @click="goChat(item)">
    <!-- 头像容器（相对定位，承载角标） -->
    <view class="avatar-wrap">
      <image v-if="item.otherAvatar" class="user-avatar" :src="item.otherAvatar" mode="aspectFill" />
      <view v-else class="avatar-empty">
        <text>头像</text>
      </view>
      <!-- ✅ 手写角标，替换uni-badge，兼容性拉满 -->
      <view v-if="item.unreadNum > 0" class="unread-badge">
        {{ item.unreadNum > 99 ? '99+' : item.unreadNum }}
      </view>
    </view>

    <view class="session-info">
      <view class="row1">
        <text class="nick">{{item.otherName}}</text>
        <text class="time">{{item.lastMsgTime}}</text>
      </view>
      <view class="row2">
        <text class="last-msg">{{item.lastMsg}}</text>
      </view>
    </view>
  </view>
</view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getChatSessionListApi } from '../../src/api/message'
import { initSocket } from '../../src/utils/socket'
import type { ChatSessionItem } from '../../src/types'

const sessionList = ref<ChatSessionItem[]>([])
const userId = uni.getStorageSync('userId') ? Number(uni.getStorageSync('userId')) : 0

// 进入聊天页面 携带goodsId otherId
const goChat = (item: ChatSessionItem) => {
  uni.navigateTo({
    url: `/pages/personal/Chat?goodsId=${item.goodsId}&otherId=${item.otherId}&nickname=${item.otherName}&avatar=${item.otherAvatar}`
  })
}

// 加载会话列表
const loadSession = async () => {
  const res = await getChatSessionListApi()
  console.log("会话列表", res.data)
  if (res.code === 200) {
    sessionList.value = res.data
  }
}

// 收到新消息刷新列表
const onReceiveMsg = () => {
  loadSession()
}

// 切换页面回来自动刷新会话列表
onShow(() => {
  initSocket(userId, onReceiveMsg)
  loadSession()
})
</script>

<style scoped>
.page-wrap {
  padding: 32rpx;
  background: #f5f7fa;
  min-height: 100vh;
}
.page-title {
  font-size: 46rpx;
  font-weight: bold;
  display: block;
  margin: 20rpx 0 36rpx;
}
.empty-tip {
  text-align: center;
  padding: 120rpx 0;
  font-size: 36rpx;
  color: #666;
}
.session-item {
  background: #ffffff;
  border-radius: 24rpx;
  padding: 32rpx;
  display: flex;
  align-items: center;
  gap: 32rpx;
  margin-bottom: 20rpx;
}
/* 头像外层相对定位，角标绝对定位基准 */
.avatar-wrap {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  flex-shrink: 0;
}
.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
}
.avatar-empty {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #eeeeee;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #999;
}
.session-info {
  flex: 1;
}
.row1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.nick {
  font-size: 36rpx;
  color: #222;
}
.time {
  font-size: 24rpx;
  color: #999;
}
.row2 {
  margin-top: 8rpx;
}
.last-msg {
  font-size: 30rpx;
  color: #777;
  max-width: 65%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

/* ✅ 手写未读红点角标样式 */
.unread-badge {
  position: absolute;
  right: -8rpx;
  top: -8rpx;
  min-width: 36rpx;
  height: 36rpx;
  background-color: #f53f3f;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22rpx;
  color: #fff;
  padding: 0 8rpx;
  z-index: 9;
}
</style>