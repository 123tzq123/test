<template>
  <view v-if="showAiDialog" class="ai-mask" @click.self="emit('update:showAiDialog', false)">
    <view class="ai-dialog" @click.stop>
      <view class="ai-header">
        <text class="ai-title">🤖 AI问答助手</text>
        <text class="close-btn" @click="emit('update:showAiDialog', false)">×</text>
      </view>
      <!-- 关键：绑定 scroll-top -->
      <scroll-view class="ai-msg-box" scroll-y :scroll-top="scrollTopVal">
        <view
          class="ai-msg-item"
          v-for="(item, index) in aiMsgList"
          :key="index"
          :class="[item.type === 'user' ? 'user' : 'ai']"
        >
          <view class="msg-bubble">
            <text>{{ item.content }}</text>
          </view>
        </view>
        <view v-if="aiLoading" class="loading-text">AI思考中...</view>
      </scroll-view>
      <view class="ai-input-area">
        <uni-input v-model="aiInputText" placeholder="输入你的问题" @confirm="sendAi" />
        <button type="primary" @click="sendAi" :loading="aiLoading">发送</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { askAiApi } from '../api/message'
import type { AiMsg } from '../types'

// 接收props，解决【找不到props】
const props = defineProps<{
  showAiDialog: boolean
}>()
const emit = defineEmits(['update:showAiDialog'])

// 滚动控制变量（核心方案，替代ref调用滚动方法）
const scrollTopVal = ref(0)
const aiInputText = ref('')
const aiLoading = ref(false)
const aiMsgList = ref<AiMsg[]>([])

// 滚动到底部通用方法
const scrollToBottom = () => {
  scrollTopVal.value = 999999
}

// 打开弹窗初始化欢迎语
const openAi = () => {
  const existWelcome = aiMsgList.value.some(m => m.content.includes('闲置优品二手交易平台AI助手'))
  if (!existWelcome) {
    aiMsgList.value.push({
      type: 'ai',
      content: '我是闲置优品二手交易平台AI助手，仅解答平台发布商品、订单、收藏、评价、聊天交易相关问题。'
    })
  }
  nextTick(() => {
    scrollToBottom()
  })
}

// 监听弹窗打开，解决watch报错
watch(() => props.showAiDialog, (val) => {
  if (val) openAi()
})

const sendAi = async () => {
  const text = aiInputText.value.trim()
  if (!text) return
  aiMsgList.value.push({ type: 'user', content: text })
  aiInputText.value = ''
  aiLoading.value = true
  nextTick(() => scrollToBottom())

  try {
    const res = await askAiApi(text)
    if (res.code === 200) {
      aiMsgList.value.push({ type: 'ai', content: res.data.answer })
    }
  } catch {
    aiMsgList.value.push({ type: 'ai', content: 'AI服务暂时异常，请稍后重试' })
  } finally {
    aiLoading.value = false
    nextTick(() => scrollToBottom())
  }
}
</script>

<style scoped>
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
.ai-dialog {
  width: 90vw;
  height: 80vh;
  background: #fff;
  border-radius: 24rpx;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.ai-header {
  height: 112rpx;
  padding: 0 40rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid #eee;
}
.ai-title {
  font-size: 36rpx;
}
.close-btn {
  font-size: 48rpx;
  color: #999;
}
.ai-msg-box {
  flex: 1;
  padding: 32rpx;
  background: #f8f9fa;
}
.ai-msg-item {
  margin: 24rpx 0;
  display: flex;
}
.ai-msg-item.ai {
  justify-content: flex-start;
}
.ai-msg-item.user {
  justify-content: flex-end;
}
.msg-bubble {
  max-width: 70%;
  padding: 20rpx 28rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
}
.ai .msg-bubble {
  background: #fff;
  color: #333;
}
.user .msg-bubble {
  background: #409EFF;
  color: #fff;
}
.loading-text {
  font-size: 26rpx;
  color: #999;
  padding: 10rpx;
}
.ai-input-area {
  padding: 24rpx 32rpx;
  display: flex;
  gap: 24rpx;
  align-items: center;
  border-top: 1rpx solid #eee;
}
</style>