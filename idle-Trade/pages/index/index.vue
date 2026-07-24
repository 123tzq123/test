<template>
<view class="index-wrap">
  <!-- 筛选区域 -->
  <view class="filter-card">
    <picker :value="categoryIndex" :range="categoryNameList" @change="onCategoryChange">
      <view class="picker-box">{{categoryNameList[categoryIndex]}}</view>
    </picker>
    <input class="search-input" v-model="searchTitle" placeholder="搜索商品名称" @confirm="loadList(true)" />
  </view>
  
  <!-- 价格筛选区域 -->
    <view class="price-filter-card">
      <input class="price-input" v-model="minPrice" placeholder="最低价格" />
      <text class="line">—</text>
      <input class="price-input" v-model="maxPrice" placeholder="最高价格" />
      <button class="search-btn" @click="loadList(true)">搜索</button>
      <button class="reset-btn" @click="resetCondition">清空筛选</button>
    </view>

  <!-- 商品列表 -->
  <scroll-view scroll-y class="goods-scroll">
    <view v-if="goodsList.length === 0" class="empty-tip">暂无闲置商品</view>
    <view class="goods-list">
      <view v-for="item in goodsList" :key="item.id" class="goods-item" @click="goDetail(item.id)">
        <image v-if="item.coverImg" :src="item.coverImg" class="item-img" mode="aspectFill" />
        <view v-else class="img-empty">暂无图片</view>
        <view class="item-info">
          <text class="item-title">{{item.title}}</text>
          <text class="item-price">¥{{item.price}}</text>
          <text class="view-count">浏览 {{item.viewCount ?? 0}} 次</text>
        </view>
      </view>
    </view>
    <!-- 分页 -->
    <view class="page-bar" v-if="pageCount > 1">
      <button :disabled="currentPage === 1" @click="prevPage">上一页</button>
      <text>第{{currentPage}}/{{pageCount}}页</text>
      <button :disabled="currentPage === pageCount" @click="nextPage">下一页</button>
    </view>
  </scroll-view>
</view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { searchGoodsApi, getCategoryListApi } from '../../src/api/goods'
import { GoodsItem, GoodsCategory } from '../../src/types'

// 分类
const categoryList = ref<GoodsCategory[]>([])
const categoryNameList = ref<string[]>(['全部分类'])
const categoryIndex = ref(0)
let selectCategoryId: number | null = null

// 搜索
const searchTitle = ref('')
const minPrice = ref('')
const maxPrice = ref('')

// 分页
const goodsList = ref<GoodsItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 8
const pageCount = computed(()=> Math.ceil(total.value / pageSize))

// 封面处理：分割goodsImg字符串取第一张
const formatCover = (record: GoodsItem) => {
  let coverImg = ''
  if(record.goodsImg){
    const arr = record.goodsImg.split(',').filter(s=>s.trim())
    if(arr.length) coverImg = arr[0]
  }
  return {...record, coverImg}
}

// 加载分类
const loadCategory = async () => {
  const res = await getCategoryListApi()
  if(res.code === 200){
    categoryList.value = res.data
    const names = ['全部分类']
    res.data.forEach(cate=>names.push(cate.categoryName))
    categoryNameList.value = names
  }
}

// 【修复重点】loadList 增加 total 赋值
const loadList = async (resetPage = false) => {
  if(resetPage) currentPage.value = 1

  let min: number | null = null
  let max: number | null = null
  // 价格校验
  if (minPrice.value.trim() !== '') {
    if (!/^\d+(\.\d+)?$/.test(minPrice.value)) {
      uni.showToast({ title: '最低价格只能输入数字！', icon: 'none' })
      return
    }
    min = Number(minPrice.value)
  }
  if (maxPrice.value.trim() !== '') {
    if (!/^\d+(\.\d+)?$/.test(maxPrice.value)) {
      uni.showToast({ title: '最高价格只能输入数字！', icon: 'none' })
      return
    }
    max = Number(maxPrice.value)
  }
  if (min !== null && max !== null && min > max) {
    uni.showToast({ title: '最低价不能大于最高价！', icon: 'none' })
    return
  }

  const sendData = {
    categoryId: selectCategoryId,
    title: searchTitle.value,
    minPrice: min,
    maxPrice: max,
    pageNum: currentPage.value,
    pageSize
  }
  const res = await searchGoodsApi(sendData)
  console.log("首页商品列表返回", res.data)
  if (res.code === 200) {
    goodsList.value = res.data.records.map(formatCover)
    // ✅ 缺失的关键代码！更新总条数
    total.value = res.data.total
  }
}

// 分类切换
const onCategoryChange = (e:any)=>{
  const idx = e.target.value
  categoryIndex.value = idx
  if(idx === 0){
    selectCategoryId = null
  }else{
    selectCategoryId = categoryList.value[idx - 1].id
  }
  loadList(true)
}

// 清空所有筛选条件
const resetCondition = () => {
  categoryIndex.value = 0
  selectCategoryId = null
  searchTitle.value = ''
  minPrice.value = ''
  maxPrice.value = ''
  loadList(true)
  uni.showToast({title:"筛选条件已清空"})
}

// 分页
const prevPage = ()=>{
  if(currentPage.value > 1){
    currentPage.value--
    loadList()
  }
}
const nextPage = ()=>{
  if(currentPage.value < pageCount.value){
    currentPage.value++
    loadList()
  }
}

// 跳转商品详情
const goDetail = (goodsId:number)=>{
  uni.navigateTo({url:`/pages/goodsDetail/goodsDetail?goodsId=${goodsId}`})
}

// 页面展示时检测是否需要刷新（发布商品返回首页）
onShow(()=>{
  const flag = uni.getStorageSync("needRefreshGoods")
  if(flag === "1"){
    loadList(true)
    uni.removeStorageSync("needRefreshGoods")
  }
})

// 等待分类加载完成再加载商品
onLoad(async ()=>{
  await loadCategory()
  loadList(true)
})
</script>

<style scoped>
.index-wrap {
  padding: 32rpx;
  background: #f5f7fa;
  min-height: 100vh;
}
.filter-card {
  display: flex;
  gap: 20rpx;
  align-items: center;
  background: #fff;
  padding: 30rpx;
  border-radius: 20rpx;
  margin-bottom: 30rpx;
}
.picker-box {
  padding: 20rpx 30rpx;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  min-width: 180rpx;
  font-size: 28rpx;
}
.search-input {
  flex: 1;
  padding: 20rpx;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  font-size: 28rpx;
}
.search-btn {
  background: #409EFF;
  color: #fff;
  border: none;
  padding: 20rpx 30rpx;
  border-radius: 10rpx;
  font-size: 28rpx;
}
.goods-scroll {
  height: calc(100vh - 240rpx);
}
.empty-tip {
  text-align: center;
  color: #999;
  font-size: 30rpx;
  margin-top: 100rpx;
}
.goods-item {
  display: flex;
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;
  gap: 30rpx;
}
.item-img {
  width: 200rpx;
  height: 200rpx;
  border-radius: 16rpx;
}
.img-empty {
  width: 200rpx;
  height: 200rpx;
  background: #eee;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color:#999;
}
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.item-title {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 12rpx;
}
.item-price {
  font-size: 40rpx;
  color: #f53f3f;
  margin-bottom: 12rpx;
}
.view-count {
  font-size: 26rpx;
  color:#666;
}
.page-bar {
  display: flex;
  gap: 20rpx;
  align-items: center;
  justify-content: center;
  margin-top: 40rpx;
}
.page-bar button {
  padding: 16rpx 32rpx;
  border-radius: 8rpx;
}

.price-filter-card {
  display: flex;
  gap: 16rpx;
  align-items: center;
  background: #fff;
  padding: 30rpx;
  border-radius: 20rpx;
  margin-bottom: 30rpx;
}
.price-input {
  width: 200rpx;
  padding: 20rpx 16rpx;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  font-size: 28rpx;
}
.line {
  font-size: 30rpx;
  color:#666;
}
.reset-btn {
  background: #fff;
  color:#666;
  border:1rpx solid #ddd;
  padding: 20rpx 30rpx;
  border-radius: 10rpx;
  font-size: 28rpx;
}
</style>