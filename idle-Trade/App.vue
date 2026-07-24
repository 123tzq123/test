<script setup lang="ts">
import { onLaunch } from '@dcloudio/uni-app'
import { getChatSessionListApi } from './src/api/message'

// 更新【我的】tab未读角标
const updateTabBadge = async () => {
  const token = uni.getStorageSync('token')
  if (!token) {
    // 未登录，清除红点
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

onLaunch(() => {
  // 监听全局WebSocket消息事件
  uni.$on('ws_message', () => {
    updateTabBadge()
  })
})
</script>