<template>
<view class="page-wrap">
  <text class="page-title">卖家主页</text>
  <view class="header-card">
    <view class="user-info">
      <image class="avatar-img" :src="sellerInfo.avatar" mode="aspectFill" />
      <view class="text-box">
        <text class="nick">{{sellerInfo.nickname}}</text>
        <text class="score">综合评分：{{avgScore}}分</text>
      </view>
    </view>
    <button v-if="loginUserId !== sellerId" type="primary" @click="goChat">联系卖家</button>
  </view>
  <text class="sub-title">他发布的闲置</text>
  <view class="goods-list">
    <view v-for="item in goodsList" :key="item.id" class="goods-card" @click="toDetail(item.id)">
      <image v-if="item.coverImg" class="goods-img" :src="item.coverImg" mode="aspectFill" />
      <view v-else class="empty-img">无图</view>
      <view class="goods-info">
        <text class="g-title">{{item.title}}</text>
        <text class="g-price">¥{{item.price}}</text>
        <text class="g-view">浏览{{item.viewCount ?? 0}}次</text>
        <button size="mini">查看详情</button>
      </view>

      <!-- 评价区域移到v-for内部 -->
      <view class="comment-wrap" v-if="commentMap.get(item.id)?.length">
        <text class="c-title">买家评价</text>
        <view v-for="c in commentMap.get(item.id)" :key="c.id" class="c-item">
          <uni-rate v-model="c.score" disabled />
          <text class="c-content">{{c.content}}</text>
          <view class="c-img-box">
            <image v-for="img in splitImgStr(c.imgList)" :key="img" class="c-img" :src="img" />
            <text v-if="splitImgStr(c.imgList).length === 0">无评价图片</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getSellerHomeApi, getReceiveCommentApi } from '../../src/api/comment'
import type { GoodsItem, SysUser, SellerGoodsCommentVO, GoodsCommentVO } from '../../src/types'

let sellerId = 0
const loginUserId = uni.getStorageSync('userId') ? Number(uni.getStorageSync('userId')) : 0

const sellerInfo = ref<SysUser>({} as SysUser)
const avgScore = ref(0)
const goodsList = ref<GoodsItem[]>([])
const commentMap = ref<Map<number, GoodsCommentVO[]>>(new Map())

const splitImgStr = (source: string | string[] | null | undefined): string[] => {
  if (Array.isArray(source)) return source.filter(i => i.trim())
  if (!source) return []
  return source.split(',').filter(i => i.trim())
}

const goChat = () => {
  if (!uni.getStorageSync('token')) {
    uni.showToast({ title: '请先登录', icon: "none" })
    return
  }
  uni.navigateTo({
    url: `/pages/personal/messageList?targetOtherId=${sellerId}&targetOtherName=${sellerInfo.value.nickname}&targetOtherAvatar=${sellerInfo.value.avatar}`
  })
}

const toDetail = (gid: number) => {
  uni.navigateTo({ url: `/pages/goodsDetail/goodsDetail?goodsId=${gid}` })
}

const loadData = async () => {
  console.log("loadData开始执行，sellerId = ", sellerId)
  const resHome = await getSellerHomeApi(sellerId)
  console.log("getSellerHome返回结果：", resHome)
  if (resHome.code === 200) {
    sellerInfo.value = resHome.data.sellerInfo
    avgScore.value = resHome.data.avgScore
    goodsList.value = resHome.data.goodsList
  }
  // 加载卖家评价
  const resComment = await getReceiveCommentApi(sellerId)
  if (resComment.code === 200) {
    const map = new Map<number, GoodsCommentVO[]>()
    resComment.data.forEach(item => {
      map.set(item.goodsId, item.commentList)
    })
    commentMap.value = map
  }
}

// ✅ 删除useRoute，改用uni-app原生onLoad接收参数
onLoad((options) => {
  sellerId = Number(options.sellerId || 0)
  loadData()
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
.header-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 32rpx;
}
.nick {
  font-size: 44rpx;
  display: block;
}
.score {
  font-size: 36rpx;
  color: #f53f3f;
  display: block;
  margin-top: 12rpx;
}
.sub-title {
  font-size: 38rpx;
  display: block;
  margin: 60rpx 0 24rpx;
}
.goods-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  display: flex;
  gap: 32rpx;
  margin-bottom: 24rpx;
}
.goods-img {
  width: 280rpx;
  height: 280rpx;
  border-radius: 12rpx;
}
.empty-img {
  width: 280rpx;
  height: 280rpx;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #999;
}
.g-title {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
}
.g-price {
  font-size: 40rpx;
  color: #f53f3f;
  display: block;
  margin: 12rpx 0;
}
.g-view {
  font-size: 28rpx;
  color: #999;
  display: block;
}
.comment-wrap {
  background: #f8f9fa;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-top: 32rpx;
}
.c-title {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 24rpx;
}
.c-item {
  padding: 24rpx 0;
  border-bottom: 1rpx solid #eee;
}
.c-content {
  font-size: 32rpx;
  color: #666;
  display: block;
  margin: 16rpx 0;
}
.c-img-box {
  display: flex;
  gap: 16rpx;
}
.c-img {
  width: 160rpx;
  height: 160rpx;
  border-radius: 10rpx;
}
.avatar-img {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
}
</style>