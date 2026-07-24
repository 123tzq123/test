<template>
<view class="page-wrap">
  <text class="page-title">我的订单列表</text>
  <view v-if="orderList.length === 0" class="empty-tip">
    <text>暂无订单</text>
    <button @click="goIndex">去首页逛逛</button>
  </view>
  <view v-else class="order-list">
    <view v-for="item in orderList" :key="item.id" class="order-card">
      <view class="order-top">
        <text class="order-no">订单号：{{item.orderNo}}</text>
        <text class="order-status" :class="statusClass(item.status)">
          {{item.status ===0?"待确认":item.status===1?"已完成":"已取消"}}
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
        <button v-if="item.status === 0" @click="cancelOrder(item.id)">取消订单</button>
        <button v-if="item.status === 0" type="primary" @click="finishOrder(item.id)">确认完成</button>
        <button type="primary" @click="goChat(item)">联系卖家</button>
        <button v-if="item.status === 1 && !item.isComment" type="warn" @click="openComment(item.id)">去评价</button>
      </view>
    </view>
  </view>
  <!-- 评价弹窗 增加v-if，弹窗关闭时直接从DOM销毁 -->
  <uni-popup v-if="commentVisible" v-model:show="commentVisible" type="center">
    <view class="comment-box">
      <text class="c-title">商品评价</text>
      <uni-form>
        <uni-form-item label="评分">
          <uni-rate v-model="commentForm.score" />
        </uni-form-item>
        <uni-form-item label="评价内容">
          <uni-textarea v-model="commentForm.content" />
        </uni-form-item>
        <uni-form-item label="上传图片">
          <view class="c-img-row">
            <view v-for="(u,idx) in commentImgList" :key="idx" class="mini-img">
              <image :src="u" />
              <text @click="commentImgList.splice(idx,1)">×</text>
            </view>
            <view class="add-mini" @click="uploadCommentImg">+</view>
          </view>
        </uni-form-item>
      </uni-form>
      <view class="c-btn-row">
        <button @click="commentVisible = false">取消</button>
        <button type="primary" @click="submitComment">提交评价</button>
      </view>
    </view>
  </uni-popup>
</view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMyOrderApi, cancelOrderApi, finishOrderApi } from '../../src/api/order'
import { publishCommentApi } from '../../src/api/comment'
import { uploadImgApi } from '../../src/api/goods'
import { OrderItem, CommentPublishDTO } from '../../src/types'

const orderList = ref<OrderItem[]>([])
const commentVisible = ref(false)
const commentForm = ref<CommentPublishDTO>({
  orderId:0, score:0, content:'', imgList:[]
})
const commentImgList = ref<string[]>([])

// 格式化时间 时:分
const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (isNaN(date.getTime())) return ''
  const h = String(date.getHours()).padStart(2, '0')
  const m = String(date.getMinutes()).padStart(2, '0')
  return `${h}:${m}`
}

// 加载订单
const loadOrder = async () => {
  const userId = uni.getStorageSync('userId')
  if(!userId){
    uni.showToast({title:"请先登录",icon:"none"})
    orderList.value = []
    return
  }
  const res = await getMyOrderApi(1,10)
  if (res.code === 200) {
    orderList.value = res.data.records.map(item => {
      let coverImg = ''
      if (item.goodsImg) {
        const arr = item.goodsImg.split(',').filter(s=>s.trim())
        coverImg = arr[0] ?? ''
      }
      return { ...item, coverImg }
    })
  }
}

// 状态样式
const statusClass = (s: number) => {
  if (s === 0) return "warn"
  if (s === 1) return "success"
  return "info"
}

// 取消订单
const cancelOrder = async (oid: number) => {
  const res = await cancelOrderApi(oid)
  if (res.code === 200) { uni.showToast({ title: "取消成功" }); loadOrder() }
}
// 完成订单
const finishOrder = async (oid: number) => {
  const res = await finishOrderApi(oid)
  if (res.code === 200) { uni.showToast({ title: "交易完成" }); loadOrder() }
}

const goChat = (row: OrderItem) => {
  const token = uni.getStorageSync('token')
  if (!token) {
    uni.showToast({ title: "请先登录", icon: "none" })
    return
  }
  uni.navigateTo({
    url: `/pages/personal/messageList?targetOtherId=${row.sellerId}`
  })
}

// 打开评价弹窗
const openComment = (oid: number) => {
  commentForm.value.orderId = oid
  commentForm.value.score = 0
  commentForm.value.content = ''
  commentImgList.value = []
  commentVisible.value = true
}

// 上传评价图片
const uploadCommentImg = () => {
  uni.chooseMedia({
    count: 6 - commentImgList.value.length,
    mediaType: ['image'],
    success: async (res) => {
      for (const f of res.tempFiles) {
        const upRes = await uploadImgApi(f.tempFilePath)
        if (upRes.code === 200) {
          commentImgList.value.push(upRes.data)
        }
      }
    }
  })
}

// 提交评价
const submitComment = async () => {
  if (commentForm.value.score === 0) {
    uni.showToast({ title: "请打分", icon: "none" })
    return
  }
  commentForm.value.imgList = commentImgList.value
  const res = await publishCommentApi(commentForm.value)
  if (res.code === 200) {
    uni.showToast({ title: "评价成功" })
    commentVisible.value = false
    loadOrder()
  }
}

const goIndex = ()=>{
  uni.switchTab({url:"/pages/index/index"})
}

onShow(() => {
  // 每次进入强制关闭弹窗，防止缓存残留
  commentVisible.value = false
  loadOrder()
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
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 32rpx;
}
button {
  font-size: 28rpx;
  padding: 12rpx 24rpx;
  border-radius: 8rpx;
  border: none;
}
.comment-box {
  width: 85vw;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
}
.c-title {
  font-size: 40rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 32rpx;
}
.c-img-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin: 16rpx 0;
}
.min-img {
  width: 120rpx;
  height: 120rpx;
  position: relative;
}
.min-img image {
  width: 100%;
  height: 100%;
}
.min-img text {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  background: #f53f3f;
  color: #fff;
  width: 32rpx;
  height: 32rpx;
  text-align: center;
  border-radius: 50%;
}
.add-min {
  width: 120rpx;
  height: 120rpx;
  border: 1rpx dashed #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  color: #999;
}
.c-btn-row {
  display: flex;
  gap: 30rpx;
  margin-top: 40rpx;
}
</style>