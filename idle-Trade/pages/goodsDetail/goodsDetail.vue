<template>
	<view class="detail-wrap">
		<view v-if="goods" class="detail-card">
		    <scroll-view scroll-x class="img-scroll">
		      <image v-for="img in imgList" :key="img" :src="img" class="detail-img" mode="aspectFill" />
		    </scroll-view>
			<view class="info-box">
			    <text class="title">{{goods.title}}</text>
			    <text class="price">¥{{goods.price}}</text>
			    <view class="seller-row" @click="goSeller(goods.userId)">
			      <image v-if="goods.avatar" :src="goods.avatar" class="avatar-img" mode="aspectFill" />
			      <view v-else class="avatar-default"></view>
			      <text class="seller-name">{{goods.sellerName}}</text>
			    </view>
			    <text class="desc">商品描述：{{goods.content}}</text>
			    <text class="view-text">浏览{{goods.viewCount ?? 0}}次</text>
			</view>
			  
			<view class="btn-group">
			    <button @click="backHome">返回首页</button>
			    <button v-if="goods.userId !== loginUserId" type="primary" @click="buyGoods">立即购买</button>
			    <button v-if="goods.userId !== loginUserId" type="primary" @click="goChat">联系卖家</button>
			    <button v-if="goods.userId !== loginUserId" type="warning" @click="handleCollect">
			      {{isCollect ? "取消收藏":"收藏商品"}}
			    </button>
			</view>
		</view>
	</view>
</template>

<script setup lang="ts">
	import { ref } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	import { getGoodsDetailApi, isCollectApi, changeCollectApi } from '../../src/api/goods'
	import { createOrderApi } from '../../src/api/order'
	import { GoodsItem } from '../../src/types'
	
	const goods = ref<GoodsItem>()
	const imgList = ref<string[]>([])
	const isCollect = ref(false)
	let loginUserId = uni.getStorageSync('userId') ? Number(uni.getStorageSync('userId')) : 0
	let goodsId = 0
	
	const getDetail = async (id: number) => {
	  goodsId = id
	  const res = await getGoodsDetailApi(goodsId)
	  if (res.code === 200) {
	    goods.value = res.data
	    // 重点：由goodsImg字符串拆分图片数组
	    if(goods.value?.goodsImg){
	      imgList.value = goods.value.goodsImg.split(',').filter(s=>s.trim())
	    }else{
	      imgList.value = []
	    }
	  }
	  await getCollectState()
	}
	
	const getCollectState = async () => {
	  if (loginUserId === 0) return
	  const res = await isCollectApi({ goodsId, userId: loginUserId })
	  if (res.code === 200) isCollect.value = res.data
	}
	
	const handleCollect = async () => {
	  if (loginUserId === 0) {
	    uni.showToast({ title: "请先登录", icon: "none" })
	    return
	  }
	  const res = await changeCollectApi({ goodsId, userId: loginUserId })
	  if (res.code === 200) {
	    isCollect.value = !isCollect.value
	    uni.showToast({ title: res.data })
	  } else {
	    uni.showToast({ title: res.msg, icon: "none" })
	  }
	}
	
	const buyGoods = async () => {
	  if (!uni.getStorageSync('token')) {
	    uni.showToast({ title: "请先登录", icon: "none" })
	    setTimeout(()=>uni.navigateTo({url:"/pages/login/login"}),800)
	    return
	  }
	  if (!goods.value) return
	  const res = await createOrderApi(goods.value.id)
	  if (res.code === 200) {
	    uni.showToast({ title: "下单成功" })
	    uni.navigateTo({ url: "/pages/personal/order" })
	  }
	}
	
	const goChat = () => {
	  if (!uni.getStorageSync('token')) {
	    uni.showToast({ title: "请先登录", icon: "none" })
	    setTimeout(()=>uni.navigateTo({url:"/pages/login/login"}),800)
	    return
	  }
	  if(!goods.value) return
	  // 修复：防止参数undefined
	  const otherId = goods.value.userId
	  const name = goods.value.sellerName ?? ''
	  const avatar = goods.value.avatar ?? ''
	  uni.navigateTo({
	    url: `/pages/personal/messageList?targetOtherId=${otherId}&targetOtherName=${name}&targetOtherAvatar=${avatar}`
	  })
	}
	
	const goSeller = (uid: number) => {
	  uni.navigateTo({ url: `/pages/SellerHome/sellerHome?sellerId=${uid}` })
	}
	
	const backHome = () => {
	  uni.setStorageSync("needRefreshGoods", "1")
	  uni.switchTab({ url: "/pages/index/index" })
	}
	
	onLoad((option) => {
	  if(option.goodsId){
	    getDetail(Number(option.goodsId))
	  }
	})
</script>

<style scoped>
	.detail-wrap {
	  padding: 32rpx;
	  background: #f5f7fa;
	  min-height: 100vh;
	}
	.detail-card {
	  background: #fff;
	  border-radius: 24rpx;
	  padding: 40rpx;
	}
	.img-scroll {
	  width: 100%;
	  white-space: nowrap;
	}
	.detail-img {
	  width: 600rpx;
	  height: 600rpx;
	  margin-right: 20rpx;
	  border-radius: 16rpx;
	  display: inline-block;
	}
	.info-box {
	  margin: 40rpx 0;
	}
	.title {
	  font-size: 44rpx;
	  font-weight: bold;
	  display: block;
	}
	.price {
	  font-size: 48rpx;
	  color: #f53f3f;
	  display: block;
	  margin: 16rpx 0 32rpx;
	}
	.seller-row {
	  display: flex;
	  align-items: center;
	  gap: 20rpx;
	  margin-bottom: 24rpx;
	}
	.avatar-img{
	  width: 80rpx;
	  height: 80rpx;
	  border-radius: 50%;
	}
	.avatar-default{
	  width: 80rpx;
	  height: 80rpx;
	  border-radius: 50%;
	  background:#eee;
	}
	.seller-name {
	  font-size: 36rpx;
	  color: #409EFF;
	}
	.desc, .view-text {
	  font-size: 32rpx;
	  color: #666;
	  display: block;
	  margin: 12rpx 0;
	}
	.btn-group {
	  display: flex;
	  flex-wrap: wrap;
	  gap: 20rpx;
	  margin-top:30rpx;
	}
	button {
	  padding: 20rpx 40rpx;
	  border-radius: 12rpx;
	  font-size: 32rpx;
	}
	button[type="primary"] {
	  background: #409EFF;
	  color: #fff;
	  border: none;
	}
	button[type="warning"] {
	  background: #ff7d00;
	  color: #fff;
	  border: none;
	}
</style>