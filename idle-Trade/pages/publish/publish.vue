<template>
<view class="page-wrap">
  <text class="page-title">发布闲置商品</text>
  <view class="form-card">
    <!-- 商品标题 -->
    <view class="form-item">
      <text class="label">商品标题</text>
      <input class="input-native" v-model="publishForm.title" placeholder="输入商品标题" />
    </view>

    <!-- 商品价格 -->
    <view class="form-item">
      <text class="label">商品价格</text>
      <input class="input-native" type="number" v-model.number="publishForm.price" placeholder="输入价格" />
    </view>

    <!-- 商品分类 -->
    <view class="form-item">
      <text class="label">商品分类</text>
      <picker 
        :value="categoryIndex" 
        @change="onCategoryChange"
        mode="selector"
        :range="categoryOptions"
      >
        <view class="picker-input">
          {{ showCategoryName }}
        </view>
      </picker>
    </view>

    <!-- 商品图片 -->
    <view class="form-item">
      <text class="label">商品图片</text>
      <view class="upload-box">
        <view v-for="(url,idx) in imgList" :key="idx" class="upload-img-item">
          <image :src="url" class="upload-img" />
          <text class="del" @click="delImg(idx)">×</text>
        </view>
        <view class="add-img" @click="chooseUpload">+</view>
      </view>
      <text class="tip">最多5张，第一张为封面</text>
     <!-- <button type="success" class="ai-btn" @click="aiDetectVisible = true">🤖 AI识物自动填充</button> -->
    </view>

    <!-- 商品描述 -->
    <view class="form-item">
      <text class="label">商品描述</text>
      <textarea class="textarea-native" v-model="publishForm.content" placeholder="描述成色、配件等" />
    </view>

    <view class="form-item">
      <button type="primary" class="submit-btn" @click="submit">发布商品</button>
    </view>
  </view>

  <!-- AI识物弹窗 -->
  <uni-popup v-model:show="aiDetectVisible" type="center">
    <view class="ai-popup-box">
      <text class="popup-title">AI识物（最多5张）</text>
      <view class="ai-upload-box">
        <view v-for="(u,idx) in aiImgUrlList" :key="idx" class="upload-img-item">
          <image :src="u" class="upload-img" />
        </view>
        <view class="add-img" @click="chooseAiUpload">+</view>
      </view>
      <text class="tip">上传商品图片AI自动生成信息</text>
      <view class="popup-btn-row">
        <button @click="aiDetectVisible = false">取消</button>
        <button type="primary" :loading="aiDetectLoading" @click="startAiDetect">开始识别</button>
      </view>
    </view>
  </uni-popup>

  <!-- 全局AI弹窗组件 -->
  <AiChatPopup v-model:show="aiPopupShow" />
  <button class="float-ai" @click="aiPopupShow = true">🤖 AI小助手</button>
</view>
</template>

<script setup lang="ts">
import { ref} from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { publishGoodsApi, uploadImgApi, aiDetectGoodsApi } from '../../src/api/goods'
import { GoodsPublishDTO } from '../../src/types'
import AiChatPopup from '../../src/components/AiChatPopup.vue'

// AI弹窗全局入口
const aiPopupShow = ref(false)

// 分类配置 【和你原有option一一对应】
// 先把分类数组抽成常量，不再使用ref包裹
const categoryOptions = [
  '电子产品',
  '图书教材',
  '衣物鞋帽',
  '生活用品',
  '运动器材'
]
const categoryList = ref(categoryOptions)
const categoryIndex = ref(0)
// 新增显示变量，独立控制视图渲染
const showCategoryName = ref(categoryOptions[0])

// 表单
const publishForm = ref<GoodsPublishDTO>({
  title: '',
  price: 0,
  content: '',
  categoryId: 1,
  originalPrice: 0,
  imgList: []
})
const imgList = ref<string[]>([])

// picker切换分类
const onCategoryChange = (e: any) => {
  const idx = e.target.value
  console.log('选中下标：', idx)
  categoryIndex.value = idx
  publishForm.value.categoryId = Number(idx) + 1
  // 直接从常量数组取值，不再依赖ref包装的categoryList
  showCategoryName.value = categoryOptions[idx]
  console.log('更新后categoryIndex：', categoryIndex.value)
  console.log('分类名称：', showCategoryName.value)
}

// AI识物弹窗变量
const aiDetectVisible = ref(false)
const aiImgUrlList = ref<string[]>([])
const aiDetectLoading = ref(false)

// 普通上传图片
const chooseUpload = () => {
  uni.chooseMedia({
    count: 5 - imgList.value.length,
    mediaType: ['image'],
    success: async (res) => {
      for (const file of res.tempFiles) {
        const uploadRes = await uploadImgApi(file.tempFilePath)
        if (uploadRes.code === 200) {
          imgList.value.push(uploadRes.data)
        }
      }
    }
  })
}

// AI弹窗上传
const chooseAiUpload = () => {
  uni.chooseMedia({
    count: 5 - aiImgUrlList.value.length,
    mediaType: ['image'],
    success: async (res) => {
      for (const file of res.tempFiles) {
        const uploadRes = await uploadImgApi(file.tempFilePath)
        if (uploadRes.code === 200) {
          aiImgUrlList.value.push(uploadRes.data)
        }
      }
    }
  })
}

// 删除图片
const delImg = (idx: number) => imgList.value.splice(idx, 1)

// AI识别
const startAiDetect = async () => {
  if (aiImgUrlList.value.length === 0) {
    uni.showToast({ title: "请上传图片", icon: "none" })
    return
  }
  aiDetectLoading.value = true
  try {
    const res = await aiDetectGoodsApi(aiImgUrlList.value)
    if (res.code === 200) {
      const data = res.data
      publishForm.value.title = data.title
      publishForm.value.price = data.price
      publishForm.value.categoryId = data.categoryId
      publishForm.value.content = data.content
      // 自动同步分类下标
      categoryIndex.value = publishForm.value.categoryId - 1
      // 合并图片
      aiImgUrlList.value.forEach((url) => {
        if (!imgList.value.includes(url)) {
          imgList.value.push(url)
        }
      })
      aiDetectVisible.value = false
      uni.showToast({ title: "AI识别完成" })
    }
  } catch {
    uni.showToast({ title: "识别失败", icon: "none" })
  } finally {
    aiDetectLoading.value = false
  }
}

// 发布提交
const submit = async () => {
  if (!publishForm.value.title) {
    uni.showToast({ title: "标题不能为空", icon: "none" })
    return
  }
  if (publishForm.value.price <= 0) {
    uni.showToast({ title: "价格大于0", icon: "none" })
    return
  }
  publishForm.value.imgList = imgList.value
  const res = await publishGoodsApi(publishForm.value)
  if (res.code === 200) {
    uni.showToast({ title: "发布成功" })
    uni.switchTab({ url: "/pages/index/index" })
  }
}

onLoad(()=>{
  const token = uni.getStorageSync('token')
  if(!token){
    uni.navigateTo({url:"/pages/login/login"})
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
.form-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
}
.form-item {
  margin-bottom: 36rpx;
}
.label {
  font-size: 32rpx;
  display: block;
  margin-bottom: 16rpx;
}
.picker-input {
  border: 1rpx solid #e6e6e6;
  border-radius: 10rpx;
  padding: 24rpx 20rpx;
  font-size: 30rpx;
  color: #333;
  width:100%;
  box-sizing: border-box;
}
.upload-box {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}
.upload-img-item {
  width: 160rpx;
  height: 160rpx;
  position: relative;
}
.upload-img {
  width: 100%;
  height: 100%;
  border-radius: 10rpx;
}
.del {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background: #f53f3f;
  color: #fff;
  text-align: center;
  border-radius: 50%;
  line-height: 40rpx;
}
.add-img {
  width: 160rpx;
  height: 160rpx;
  border: 1rpx dashed #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
  color: #999;
}
.tip {
  font-size: 26rpx;
  color: #999;
  display: block;
  margin-top: 12rpx;
}
.ai-btn {
  margin-top: 24rpx;
  background: #67c23a;
  color: #fff;
  border: none;
  padding: 16rpx 32rpx;
  border-radius: 12rpx;
  font-size: 32rpx;
}
.submit-btn {
  width: 100%;
  height: 80rpx;
  background: #409EFF;
  color: #fff;
  border: none;
  border-radius: 16rpx;
  font-size: 36rpx;
}
.ai-popup-box {
  width: 80vw;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx;
}
.popup-title {
  font-size: 40rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 32rpx;
}
.popup-btn-row {
  display: flex;
  gap: 30rpx;
  margin-top: 40rpx;
}
.float-ai {
  position: fixed;
  right: 40rpx;
  bottom: 80rpx;
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #409EFF;
  color: #fff;
  font-size: 40rpx;
  border: none;
}
uni-input {
  border: 1rpx solid #eeeeee;
  border-radius: 10rpx;
  padding: 24rpx 20rpx;
  margin-top: 10rpx;
}
uni-textarea {
  border: 1rpx solid #eeeeee;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-top: 10rpx;
  min-height: 140rpx;
}
.input-native{
  width: 100%;
  box-sizing: border-box;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 24rpx 20rpx;
  margin-top: 8rpx;
  font-size: 30rpx;
}
.textarea-native{
  width: 100%;
  box-sizing: border-box;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 20rpx;
  margin-top: 8rpx;
  min-height: 140rpx;
  font-size: 30rpx;
}
</style>