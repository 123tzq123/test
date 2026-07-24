<template>
<view class="page-wrap">
  <text class="page-title">我卖出的商品订单</text>
  <view v-if="orderList.length === 0" class="empty-tip">
    <text>暂无卖出订单</text>
    <button @click="goPublish">去发布商品</button>
  </view>
  <view v-else class="order-list">
    <view v-for="item in orderList" :key="item.id" class="order-card">
      <view class="order-top">
        <text class="order-no">订单号：{{item.orderNo}}</text>
        <text class="order-status" :class="statusClass(item.status)">
          {{item.status ===0?"待买家确认":item.status===1?"交易完成":"已取消"}}
        </text>
      </view>
      <view class="order-main">
        <image v-if="item.coverImg" class="o-img" :src="item.coverImg" />
        <view class="o-info">
          <text class="o-title">{{item.goodsTitle}}</text>
          <text class="o-price">¥{{item.price}}</text>
          <text class="o-time">{{item.createTime}}</text>
        </view>
      </view>
      <view class="btn-row">
        <button type="primary" @click="goChat(item)">联系买家</button>
      </view>
    </view>
  </view>
</view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getSellerOrderApi } from '../../src/api/order'
import { OrderItem } from '../../src/types'

const orderList = ref<OrderItem[]>([])

const loadData = async () => {
  const userIdStr = uni.getStorageSync('userId')
  console.log("缓存userId", userIdStr)
  if(!userIdStr){
    uni.showToast({title:"请先登录",icon:"none"})
    orderList.value = []
    return
  }
  const res = await getSellerOrderApi(1,10)
  console.log("卖家订单接口返回", res)
  if (res.code === 200) {
    const rawList = res.data.records ?? []
    console.log("原始订单数组", rawList)
    const formatList = rawList.map(item => {
      let coverImg = ''
      if (item.goodsImg) {
        const arr = item.goodsImg.split(',').filter(s=>s.trim())
        coverImg = arr[0] ?? ''
      }
      return { ...item, coverImg }
    })
    orderList.value = [...formatList]
    console.log("赋值后orderList", orderList.value)
  }
}

const statusClass = (s: number) => {
  if (s === 0) return "warn"
  if (s === 1) return "success"
  return "info"
}

const goChat = (row: OrderItem) => {
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.showToast({ title: "请先登录", icon: "none" })
    return
  }
  uni.navigateTo({
    url: `/pages/personal/messageList?targetOtherId=${row.buyerId}`
  })
}

const goPublish = ()=>{
  uni.navigateTo({url:"/pages/publish/publish"})
}

onShow(async () => {
  try {
    console.log("【进入卖出订单页面】触发onShow")
    await loadData()
  } catch(err){
    console.error("加载订单捕获异常", err)
  }
})
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
  color: #606266;
}
.empty-tip button {
  margin-top: 40rpx;
  padding: 20rpx 40rpx;
  background: #409EFF;
  color: #fff;
  border: none;
  border-radius: 12rpx;
  font-size: 32rpx;
}
.order-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}
.order-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}
.order-no {
  font-size: 32rpx;
  color: #666;
}
.order-status {
  font-size: 28rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
}
.warn {
  background: #e6a23c22;
  color: #e6a23c;
}
.success {
  background: #67c23a22;
  color: #67c23a;
}
.info {
  background: #90939922;
  color: #909399;
}
.order-main {
  display: flex;
  gap: 32rpx;
  align-items: flex-start;
}
.o-img {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
}
.o-title {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
}
.o-price {
  font-size: 40rpx;
  color: #f53f3f;
  display: block;
  margin: 12rpx 0;
}
.o-time {
  font-size: 28rpx;
  color: #999;
  display: block;
}
.btn-row {
  margin-top: 32rpx;
}
button {
  font-size: 28rpx;
  padding: 12rpx 24rpx;
  border-radius: 8rpx;
  border: none;
  background: #409EFF;
  color: #fff;
}
</style>