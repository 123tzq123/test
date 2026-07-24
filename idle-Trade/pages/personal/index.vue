<template>
<view class="page-wrap">
  <text class="page-title">我的发布</text>
  <view class="list-box">
    <view v-for="item in goodsList" :key="item.id" class="goods-card">
      <view class="card-left">
        <image v-if="item.coverImg" class="card-img" :src="item.coverImg" />
        <view v-else class="empty-img">无图</view>
      </view>
      <view class="card-right">
        <text class="g-title">{{item.title}}</text>
        <text class="g-price">¥{{item.price}}</text>
        <text class="g-view">浏览{{item.viewCount ?? 0}}次</text>
        <text class="status" :class="{'on':item.status === 1,'off':item.status ===0,'sold':item.status ===3}">
          {{item.status ===1 ? "已上架":item.status===0?"已下架":"已售出"}}
        </text>
        <view class="btn-row">
          <button v-if="item.status === 1" @click="offSale(item.id)">下架</button>
          <template v-if="item.status === 0">
            <button @click="onSale(item.id)">上架</button>
            <button type="warn" @click="delGoods(item.id)">删除</button>
          </template>
          <button @click="openEdit(item.id)">编辑</button>
          <button @click="toDetail(item.id)">详情</button>
        </view>
      </view>
    </view>
  </view>

  <uni-popup v-if="editVisible" v-model:show="editVisible" type="center">
    <view class="edit-box">
      <text class="edit-title">编辑商品</text>

      <view class="form-item">
        <text class="label">标题</text>
        <input class="native-input" v-model="editForm.title" placeholder="输入商品标题" />
      </view>

      <view class="form-item">
        <text class="label">价格</text>
        <input class="native-input" v-model="editForm.price" type="digit" placeholder="输入价格" />
      </view>

      <view class="form-item">
        <text class="label">描述</text>
        <textarea class="native-textarea" v-model="editForm.content" placeholder="商品描述"></textarea>
      </view>

      <view class="form-item">
        <text class="label">图片</text>
        <view class="img-row">
          <view v-for="(u,idx) in imgList" :key="idx" class="mini-img">
            <image :src="u" />
            <text @click="imgList.splice(idx,1)">×</text>
          </view>
          <view class="add-mini" @click="editUpload">+</view>
        </view>
      </view>

      <view class="edit-btn-row">
        <button @click="editVisible = false">取消</button>
        <button type="primary" @click="submitEdit">保存</button>
      </view>
    </view>
  </uni-popup>
</view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getMyGoodsApi, offSaleApi, onSaleApi, deleteGoodsApi, getGoodsByIdApi, updateGoodsApi, uploadImgApi } from '../../src/api/goods'
import { GoodsItem, GoodsUpdateDTO } from '../../src/types'

const goodsList = ref<GoodsItem[]>([])
const editVisible = ref(false)
// 表单统一使用字符串接收，杜绝null导致输入框空白
const editForm = ref({
  id: 0,
  title: '',
  price: '',
  content: '',
  goodsImg: ''
})
const imgList = ref<string[]>([])

// 加载列表
const load = async () => {
  const res = await getMyGoodsApi(1,10)
  if (res.code === 200) {
    goodsList.value = res.data.records.map(item => {
      let coverImg = ''
      if (item.goodsImg) {
        const arr = item.goodsImg.split(',').filter(s=>s.trim())
        coverImg = arr[0] ?? ''
      }
      return { ...item, coverImg }
    })
  }
}

// 下架
const offSale = async (id: number) => {
  const res = await offSaleApi(id)
  if (res.code === 200) { uni.showToast({ title: "下架成功" }); load() }
}
// 上架
const onSale = async (id: number) => {
  const res = await onSaleApi(id)
  if (res.code === 200) { uni.showToast({ title: "上架成功" }); load() }
}
// 删除
const delGoods = async (id: number) => {
  uni.showModal({ title: "提示", content: "确定删除？", success: async (res) => {
    if (res.confirm) {
      const r = await deleteGoodsApi(id)
      if (r.code === 200) { uni.showToast({ title: "删除成功" }); load() }
    }
  }})
}
// 跳详情
const toDetail = (id: number) => uni.navigateTo({url:`/pages/goodsDetail/goodsDetail?goodsId=${id}`})

// 【重点修复】打开编辑，顺序：先请求赋值 → 再打开弹窗
const openEdit = async (id: number) => {
  const res = await getGoodsByIdApi(id)
  if (res.code === 200) {
    const g = res.data
    // 先给表单全部赋值
    editForm.value.id = g.id
    editForm.value.title = g.title
    editForm.value.price = String(g.price)
    editForm.value.content = g.content ?? ''
    imgList.value = g.goodsImg ? g.goodsImg.split(',').filter(s => s.trim()) : []
    // 等待DOM更新完成，再显示弹窗
    await nextTick()
    editVisible.value = true
  }
}

// 编辑上传图片
const editUpload = () => {
  uni.chooseMedia({
    count: 6 - imgList.value.length,
    mediaType: ['image'],
    success: async (res) => {
      for (const f of res.tempFiles) {
        const upRes = await uploadImgApi(f.tempFilePath)
        if (upRes.code === 200) {
          imgList.value.push(upRes.data)
        }
      }
    }
  })
}

// 提交编辑
const submitEdit = async () => {
  if (!editForm.value.title.trim()) {
    uni.showToast({title:"标题不能为空",icon:"none"})
    return
  }
  const priceNum = Number(editForm.value.price)
  if (isNaN(priceNum) || priceNum <= 0) {
    uni.showToast({title:"请输入合法价格",icon:"none"})
    return
  }
  const submitData:GoodsUpdateDTO = {
    id: editForm.value.id,
    title: editForm.value.title,
    price: priceNum,
    content: editForm.value.content,
    goodsImg: imgList.value.join(',')
  }
  const res = await updateGoodsApi(submitData)
  if (res.code === 200) {
    uni.showToast({ title: "修改成功" })
    editVisible.value = false
    load()
  }
}

onLoad(()=>{
  const token = uni.getStorageSync('token')
  if(!token){
    uni.navigateTo({url:"/pages/login/login"})
  }
})

onShow(load)
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
.goods-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  display: flex;
  gap: 32rpx;
  margin-bottom: 32rpx;
}
.card-img {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
}
.empty-img {
  width: 200rpx;
  height: 200rpx;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
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
.status {
  font-size: 28rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  display: inline-block;
  margin: 16rpx 0;
}
.on {
  background: #67c23a22;
  color: #67c23a;
}
.off {
  background: #90939922;
  color: #909399;
}
.sold {
  background: #f53f3f22;
  color: #f53f3f;
}
.btn-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 16rpx;
}
button {
  font-size: 28rpx;
  padding: 12rpx 24rpx;
  border-radius: 8rpx;
  border: none;
}
.edit-box {
  width: 85vw;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
}
.edit-title {
  font-size: 40rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 32rpx;
}
.img-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
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
.edit-btn-row {
  display: flex;
  gap: 30rpx;
  margin-top: 40rpx;
}
.form-item {
  margin-bottom: 30rpx;
}
.label {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 16rpx;
}
.native-input {
  width: 100%;
  box-sizing: border-box;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 24rpx 20rpx;
  font-size: 28rpx;
}
.native-textarea {
  width: 100%;
  box-sizing: border-box;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 24rpx 20rpx;
  font-size: 28rpx;
  min-height: 160rpx;
}
</style>