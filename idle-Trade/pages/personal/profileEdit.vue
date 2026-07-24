<template>
<view class="page-wrap">
  <view class="form-item">
    <text class="label">头像</text>
    <view class="avatar-box" @click="chooseAvatar">
      <image v-if="avatar" class="avatar-img" :src="avatar" mode="aspectFill" />
      <view v-else class="avatar-empty">点击选择</view>
    </view>
  </view>

  <view class="form-item">
    <text class="label">昵称</text>
    <!-- 外层套容器，保证可点击高度 -->
    <view class="input-container">
      <input v-model="nickname" placeholder="请输入昵称" />
    </view>
  </view>

  <button class="save-btn" @click="saveInfo">保存修改</button>
</view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { updateUserInfoApi } from '../../src/api/user'

const avatar = ref('')
const nickname = ref('')

onLoad(() => {
  avatar.value = uni.getStorageSync('avatar') || ''
  nickname.value = uni.getStorageSync('nickname') || ''
})

// 选择头像
const chooseAvatar = () => {
  uni.chooseImage({
    count:1,
    sizeType:['compressed'],
    sourceType:['album','camera'],
    success: res => {
      avatar.value = res.tempFilePaths[0]
    }
  })
}

// 保存信息
const saveInfo = async () => {
  if (!nickname.value.trim()) {
    uni.showToast({title:"昵称不能为空",icon:"none"})
    return
  }
  const res = await updateUserInfoApi({
    nickname: nickname.value,
    avatar: avatar.value
  })
  if(res.code === 200){
    uni.setStorageSync('nickname', nickname.value)
    uni.setStorageSync('avatar', avatar.value)
    uni.showToast({title:"修改成功"})
    setTimeout(()=>{
      uni.navigateBack()
    },1000)
  }
}
</script>

<style scoped>
.page-wrap {
  padding: 32rpx;
}
.form-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}
.label {
  font-size: 32rpx;
  display: block;
  margin-bottom: 24rpx;
}
.avatar-box {
  width: 120rpx;
  height: 120rpx;
}
.avatar-img {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
}
.avatar-empty {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color:#999;
}
/* 外层容器，撑开高度，扩大点击区域 */
.input-container {
  border: 1rpx #eee solid;
  border-radius: 8rpx;
  padding: 0 16rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
}
.save-btn {
  width: 100%;
  height: 88rpx;
  background: #409EFF;
  color: #fff;
  border-radius: 12rpx;
}
</style>