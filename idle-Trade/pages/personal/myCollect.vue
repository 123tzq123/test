<template>
<view class="page-wrap">
  <text class="page-title">我的收藏</text>
  <view v-if="collectList.length === 0" class="empty-tip">
    <text>暂无收藏商品</text>
    <button @click="goIndex">去首页逛逛</button>
  </view>
  <uni-grid column-num="2" gap="32rpx" v-else>
    <uni-grid-item v-for="item in collectList" :key="item.id">
      <view class="goods-card">
        <image v-if="item.coverImg" class="goods-img" :src="item.coverImg" @click="toDetail(item.id)" />
        <view v-else class="empty-img" @click="toDetail(item.id)">无图</view>
        <text class="g-title">{{item.title}}</text>
        <text class="g-price">¥{{item.price}}</text>
        <text class="g-view">浏览{{item.viewCount ?? 0}}次</text>
        <view class="btn-row">
          <button @click="toDetail(item.id)">详情</button>
          <button type="warn" @click="cancelCollect(item.id)">取消收藏</button>
        </view>
      </view>
    </uni-grid-item>
  </uni-grid>
</view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMyCollectApi, changeCollectApi } from '../../src/api/goods'
import { GoodsItem, CollectDTO } from '../../src/types'

const collectList = ref<GoodsItem[]>([])
let loginUserId = uni.getStorageSync('userId') ? Number(uni.getStorageSync('userId')) : 0

// 加载收藏列表
const loadCollect = async () => {
  loginUserId = uni.getStorageSync('userId') ? Number(uni.getStorageSync('userId')) : 0
  if(loginUserId === 0){
    uni.showToast({title:"请先登录",icon:"none"})
    collectList.value = []
    return
  }
  const res = await getMyCollectApi()
  if (res.code === 200) {
    collectList.value = res.data.map((item: GoodsItem) => {
      let coverImg = ''
      if (item.goodsImg && item.goodsImg.trim()) {
        const imgArr = item.goodsImg.split(',').filter(s => s.trim())
        coverImg = imgArr[0] ?? ''
      }
      return {
        ...item,
        coverImg
      }
    })
  }
}

// 取消收藏
const cancelCollect = async (goodsId: number) => {
  const params: CollectDTO = {
    goodsId,
    userId: loginUserId
  }
  const res = await changeCollectApi(params)
  if (res.code === 200) {
    uni.showToast({ title: "取消收藏成功" })
    loadCollect()
  } else {
    uni.showToast({ title: res.msg, icon: "none" })
  }
}

// 跳转商品详情
const toDetail = (id: number) => {
  uni.navigateTo({ url: `/pages/goodsDetail/goodsDetail?goodsId=${id}` })
}

// 跳转首页
const goIndex = () => {
  uni.switchTab({
    url: '/pages/index/index'
  })
}

// 页面每次显示刷新（重点！）
onShow(() => {
  loadCollect()
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
.goods-card {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  padding: 24rpx;
}
.goods-img {
  width: 100%;
  height: 280rpx;
  border-radius: 12rpx;
}
.empty-img {
  width: 100%;
  height: 280rpx;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #999;
}
.g-title {
  font-size: 32rpx;
  display: block;
  margin: 16rpx 0 8rpx;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.g-price {
  font-size: 36rpx;
  color: #f53f3f;
  font-weight: bold;
  display: block;
}
.g-view {
  font-size: 24rpx;
  color: #909399;
  display: block;
  margin: 8rpx 0 16rpx;
}
.btn-row {
  display: flex;
  gap: 12rpx;
}
.btn-row button {
  flex: 1;
  font-size: 28rpx;
  padding: 12rpx 0;
  border-radius: 8rpx;
  border: none;
}
.btn-row button:first-child {
  background: #409EFF;
  color: #fff;
}
.btn-row button[type="warn"] {
  background: #f53f3f;
  color: #fff;
}
</style>