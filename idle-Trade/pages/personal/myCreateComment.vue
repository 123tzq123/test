<template>
<view class="page-wrap">
  <text class="page-title">我做出的评价</text>
  <view v-if="list.length === 0" class="empty-tip">
    <text>暂无评价记录</text>
  </view>
  <view v-for="item in list" :key="item.id" class="comment-item">
    <view class="goods-row">
      <!-- 头像单独点击，跳转卖家主页，阻止冒泡 -->
      <image 
        v-if="item.goodsSeller?.avatar" 
        class="g-img" 
        :src="item.goodsSeller.avatar" 
        @click.stop="goUserHome(item.goodsSeller.id)"
      />
      <view v-else class="g-img empty-avatar" @click.stop="goUserHome(item.goodsSeller.id)">
        <text>头像</text>
      </view>
      <!-- 文字区域点击进入商品详情 -->
      <view class="g-info">
        <text class="g-title">{{item.goodsTitle}}</text>
        <text class="seller-name">卖家：{{item.goodsSeller?.nickname}}</text>
      </view>
    </view>
    <uni-rate v-model="item.score" disabled />
    <text class="content">{{item.content}}</text>
    <view class="img-box" v-if="splitImgStr(item.imgList).length > 0">
      <image v-for="img in splitImgStr(item.imgList)" :key="img" class="c-img" :src="img" />
    </view>
    <text class="time">{{item.createTime}}</text>
  </view>
</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyCreateCommentApi } from '../../src/api/comment'
import type { GoodsCommentVO } from '../../src/types'

const list = ref<GoodsCommentVO[]>([])

const splitImgStr = (source: string | string[] | null | undefined): string[] => {
  if (Array.isArray(source)) return source.filter(i => i.trim())
  if (!source) return []
  return source.split(',').filter(i => i.trim())
}

const loadData = async () => {
  const res = await getMyCreateCommentApi()
  if (res.code === 200) {
    list.value = res.data
  }
}

// 新增：跳转卖家个人主页
const goUserHome = (uid: number) => {
  uni.navigateTo({
    url: `/pages/SellerHome/sellerHome?sellerId=${uid}`
  })
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
.comment-item {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}
.goods-row {
  display: flex;
  gap: 24rpx;
  margin-bottom: 16rpx;
}
.g-img {
  width: 140rpx;
  height: 140rpx;
  border-radius: 10rpx;
}
.empty-avatar {
  width: 140rpx;
  height: 140rpx;
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color:#999;
}
.g-title {
  font-size: 32rpx;
  display: block;
}
.seller-name {
  font-size: 26rpx;
  color: #999;
  display: block;
  margin-top: 8rpx;
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