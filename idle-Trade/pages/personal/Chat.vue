<template>
<view class="page-wrap">
  <scroll-view class="msg-box" scroll-y scroll-with-animation :scroll-top="scrollTopVal">
    <view class="msg-container">
      <view v-for="m in msgList" :key="m.id">
        <!-- 对方消息 居左 -->
        <view v-if="!m.isSelf" class="msg-row other">
          <view class="avatar-box">
            <image v-if="targetAvatar" class="avatar-img" :src="targetAvatar" mode="aspectFill" />
            <view v-else class="avatar-empty"></view>
          </view>
          <view class="msg-content-wrap">
            <view class="bubble">{{m.content}}</view>
            <text class="time-text">{{formatTime(m.createTime)}}</text>
          </view>
        </view>
        <!-- 自己消息 居右 -->
        <view v-if="m.isSelf" class="msg-row self">
          <view class="msg-content-wrap">
            <view class="bubble">{{m.content}}</view>
            <text class="time-text">{{formatTime(m.createTime)}}</text>
          </view>
          <view class="avatar-box">
            <image v-if="myAvatar" class="avatar-img" :src="myAvatar" mode="aspectFill" />
            <view v-else class="avatar-empty"></view>
          </view>
        </view>
      </view>
      <!-- 底部锚点 -->
      <view id="msg-bottom-anchor"></view>
      <view class="bottom-space"></view>
    </view>
  </scroll-view>

  <!-- 底部输入区域【原生input】 -->
  <view class="input-bar">
    <view class="input-wrap">
      <input 
        v-model="inputText" 
        placeholder="输入消息" 
        confirm-type="send"
        @confirm="sendMsg"
      />
    </view>
    <button type="primary" @click="sendMsg" class="send-btn">发送</button>
  </view>
</view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { sendSocketMsg } from '../../src/utils/socket'
import { getHistoryMsgApi } from '../../src/api/message'
import type { MessageItem } from '../../src/types'

let goodsId = 0
let otherId = Number(0)
const targetNick = ref('')
const targetAvatar = ref('')
const myAvatar = ref(uni.getStorageSync('avatar') || '')

const scrollTopVal = ref(0)
const inputText = ref('')
const msgList = ref<Array<MessageItem & { isSelf?: boolean }>>([])
const myUserId = uni.getStorageSync('userId') ? Number(uni.getStorageSync('userId')) : 0

let socketMessageHandler: ((msg: any) => void) | null = null

// 格式化时间 时:分
const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (isNaN(date.getTime())) return ''
  const h = String(date.getHours()).padStart(2, '0')
  const m = String(date.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    scrollTopVal.value = 999999
  })
}

// 加载历史消息
const loadHistory = async () => {
  const res = await getHistoryMsgApi(goodsId, otherId)
  console.log("消息列表:", res.data.records)
  if (res.code === 200) {
    msgList.value = res.data.records.map(item => ({
      ...item,
      isSelf: item.fromUserId === myUserId
    }))
  }
  setTimeout(() => {
    scrollToBottom()
  }, 100)
}

// 发送消息
const sendMsg = () => {
  const text = inputText.value.trim()
  if (!text) return
  sendSocketMsg({
    goodsId,
    toUserId: otherId,
    content: text
  })
  const tempMsg: MessageItem & { isSelf: boolean } = {
    id: Date.now(),
    goodsId,
    fromUserId: myUserId,
    toUserId: otherId,
    content: text,
    createTime: new Date().toLocaleString(),
    readStatus: 0,
    isSelf: true
  }
  msgList.value.push(tempMsg)
  inputText.value = ''
  scrollToBottom()
}

// 收到websocket新消息处理
const handleSocketMsg = (msg: any) => {
  const isTargetChat =
    ((msg.fromUserId === otherId && msg.toUserId === myUserId)
      || (msg.fromUserId === myUserId && msg.toUserId === otherId))
    && msg.goodsId === goodsId

  if (!isTargetChat) return

  msgList.value.push({
    ...msg,
    isSelf: msg.fromUserId === myUserId
  })
  scrollToBottom()
}

onLoad(async (options) => {
  goodsId = Number(options.goodsId || 0)
  otherId = Number(options.otherId || 0)
  targetNick.value = options.nickname || ''
  targetAvatar.value = options.avatar || ''
  uni.setNavigationBarTitle({
    title: targetNick.value
  })
  await loadHistory()

  // 注册ws监听
  socketMessageHandler = handleSocketMsg
  uni.$on('ws_message', socketMessageHandler)
})

// 页面销毁移除监听
onUnload(() => {
  if (socketMessageHandler) {
    uni.$off('ws_message', socketMessageHandler)
  }
})
</script>

<style scoped>
.page-wrap {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}
.msg-box {
  flex: 1;
}
.msg-container {
  padding: 32rpx;
}
.bottom-space {
  height: 60rpx;
}
.msg-row {
  display: flex;
  margin-bottom: 36rpx;
  gap: 16rpx;
}
.msg-row.other {
  justify-content: flex-start;
}
.msg-row.self {
  justify-content: flex-end;
}
.avatar-box {
  flex-shrink: 0;
}
.avatar-img {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
}
.avatar-empty {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: #eeeeee;
}
.msg-content-wrap {
  display: flex;
  flex-direction: column;
  max-width: 44%;
}
.bubble {
  padding: 20rpx 28rpx;
  border-radius: 16rpx;
  font-size: 32rpx;
}
.other .bubble {
  background: #ffffff;
}
.self .bubble {
  background: #409EFF;
  color: #fff;
}
.time-text {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}
.self-status-row {
  display: flex;
  gap: 16rpx;
  justify-content: flex-end;
  margin-top: 8rpx;
}
.read-text {
  font-size: 24rpx;
  color: #999;
}
/* 输入区域样式 */
.input-bar {
  flex-shrink: 0;
  display: flex;
  gap: 24rpx;
  padding: 24rpx 32rpx;
  background: #fff;
  border-top: 1rpx #eee solid;
  box-sizing: border-box;
}
.input-wrap {
  flex: 1;
  min-width: 0;
  border: 1rpx #eee solid;
  border-radius: 8rpx;
  padding: 0 16rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
}
.input-wrap input {
  width: 100%;
  height: 100%;
  border: none;
  outline: none;
  font-size: 30rpx;
  background: transparent;
}
.send-btn {
  flex-shrink: 0;
  width: 140rpx;
  height: 72rpx;
}
</style>