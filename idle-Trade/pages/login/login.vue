<template>
  <view class="login-page">
    <view class="login-card">
      <text class="title">用户登录</text>

      <view class="form-item">
        <text class="label">用户名</text>
        <input class="input-native" v-model="loginForm.username" placeholder="请输入用户名" />
      </view>
      <view class="form-item">
        <text class="label">密码</text>
        <input class="input-native" v-model="loginForm.password" password placeholder="请输入密码" />
      </view>
      <view class="form-item">
        <button type="primary" class="login-btn" @click="handleLogin">登录</button>
      </view>

      <view class="tip">
        还没有账号？
        <text class="link" @click="goRegister">立即注册</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { loginApi, getUserInfoApi } from '../../src/api/user'
import type { LoginData } from '../../src/types'

const loginForm = ref({
  username: "",
  password: ""
})

// 登录逻辑
const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    uni.showToast({ title: "用户名和密码不能为空", icon: "none" })
    return
  }
  const res = await loginApi(loginForm.value)
  if (res.code === 200) {
    const data = res.data as LoginData
    // 存储登录凭证
    uni.setStorageSync("token", data.token)
    uni.setStorageSync("userId", String(data.userId))
    // 获取并存储头像
    const userRes = await getUserInfoApi()
    const avatar = userRes.data.avatar ?? ""
    uni.setStorageSync("avatar", avatar)

    uni.showToast({ title: "登录成功" })
    setTimeout(() => {
      uni.switchTab({ url: "/pages/index/index" })
    }, 1000)
  } else {
    uni.showToast({ title: res.msg, icon: "none" })
  }
}

// 跳转注册页
const goRegister = () => {
  uni.navigateTo({ url: "/pages/register/register" })
}
</script>

<style scoped>
.login-page {
  width: 100%;
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}
.login-card {
  width: 100%;
  max-width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
}
.title {
  display: block;
  font-size: 48rpx;
  text-align: center;
  color: #303133;
  margin-bottom: 60rpx;
}
.form-item {
  margin-bottom: 36rpx;
}
.label {
  font-size: 32rpx;
  display: block;
  margin-bottom: 12rpx;
}
.input-native {
  width: 100%;
  box-sizing: border-box;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 24rpx 20rpx;
  font-size: 30rpx;
}
.login-btn {
  width: 100%;
  height: 88rpx;
  background: #409EFF;
  color: #fff;
  border-radius: 16rpx;
  font-size: 32rpx;
}
.tip {
  text-align: center;
  margin-top: 40rpx;
  font-size: 28rpx;
  color: #606266;
}
.link {
  color: #409EFF;
  margin-left: 12rpx;
}
</style>