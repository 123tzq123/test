<template>
<view class="page-wrap">
  <text class="page-title">我收到的评价</text>
  <view v-if="list.length === 0" class="empty-tip">
    <text>暂时还没有收到评价</text>
  </view>
  <view v-for="item in list" :key="item.goodsId" class="goods-comment-group">
    <view class="goods-header">
      <image v-if="item.coverImg" class="g-img" :src="item.coverImg" />
      <text class="g-name">{{ item.goodsTitle }}</text>
    </view>
    <view v-for="c in item.commentList" :key="c.id" class="comment-item">
      <view class="user-row" @click="goUserHome(c.buyerInfo?.id)">
        
        <!-- 有头像 -->
        <image 
          v-if="c.buyerInfo?.avatar" 
          class="buyer-avatar" 
          :src="c.buyerInfo.avatar"
          mode="aspectFill"
        />

        <!-- 无头像兜底 -->
        <view v-else class="buyer-avatar-placeholder">
          <text>{{ c.buyerInfo?.nickname?.charAt(0) || "用" }}</text>
        </view>

        <text class="nick">{{ c.buyerInfo?.nickname || "匿名用户" }}</text>
      </view>

      <uni-rate v-model="c.score" disabled />
      <text class="content">{{ c.content }}</text>
      <view class="img-box" v-if="splitImgStr(c.imgList).length > 0">
        <image v-for="img in splitImgStr(c.imgList)" :key="img" class="c-img" :src="img" />
      </view>
      <text class="time">{{ c.createTime }}</text>
    </view>
  </view>
</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getReceiveCommentApi } from '../../src/api/comment'
import type { SellerGoodsCommentVO } from '../../src/types'

const list = ref<SellerGoodsCommentVO[]>([])
const userId = uni.getStorageSync('userId') ? Number(uni.getStorageSync('userId')) : 0

const splitImgStr = (source: string | string[] | null | undefined): string[] => {
  if (Array.isArray(source)) return source.filter(i => i.trim())
  if (!source) return []
  return source.split(',').filter(i => i.trim())
}

const goUserHome = (uid?: number) => {
  if (!uid) {
    uni.showToast({ title: "用户信息异常", icon: "none" })
    return
  }
  uni.navigateTo({
    url: `/pages/SellerHome/sellerHome?sellerId=${uid}`
  })
}

const loadData = async () => {
  const res = await getReceiveCommentApi(userId)
  console.log("我收到的评价接口返回：", res.data)
  if (res.code === 200) {
    list.value = res.data
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.page-wrap {
  padding: 32rpx;
  background: #f5f7fa;
  min-height: 100vh;
}
.page-title {
  font-size: 44rpx;
  font-weight: bold;
  border-left: 8rpx solid #409EFF;
  padding-left: 24rpx;
  display: block;
  margin-bottom: 40rpx;
}
.empty-tip {
  text-align: center;
  padding: 120rpx 0;
  font-size: 36rpx;
  color: #666;
}
.goods-comment-group {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}
.goods-header {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 32rpx;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #eee;
}
.g-img {
  width: 120rpx;
  height: 120rpx;
  border-radius: 10rpx;
}
.g-name {
  font-size: 34rpx;
  font-weight: bold;
}
.comment-item {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}
.comment-item:last-child {
  border-bottom: none;
}
.user-row {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 16rpx;
}
.buyer-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: #f0f2f5;
}
.buyer-avatar-placeholder {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: #409EFF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
}
.nick {
  font-size: 34rpx;
}
.content {
  font-size: 32rpx;
  display: block;
  margin: 16rpx 0;
  color: #333;
}
.img-box {
  display: flex;
  gap: 16rpx;
  margin-bottom: 16rpx;
}
.c-img {
  width: 160rpx;
  height: 160rpx;
  border-radius: 10rpx;
}
.time {
  font-size: 26rpx;
  color: #999;
}
</style>