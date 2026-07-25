<template>
<view class="register-wrap">
  <view class="register-card">
    <text class="title">账号注册</text>

    <view class="form-item">
      <text class="label">用户名</text>
      <input 
        class="input-native" 
        :value="regForm.username"
        @input="(e) => regForm.username = e.target.value"
        placeholder="设置用户名"
        confirm-type="next"
        cursor-spacing="20"
      />
    </view>
    <view class="form-item">
      <text class="label">密码</text>
      <input 
        class="input-native" 
        :value="regForm.password"
        @input="(e) => regForm.password = e.target.value"
        password 
        placeholder="设置登录密码"
        confirm-type="next"
        cursor-spacing="20"
      />
    </view>
    <view class="form-item">
      <text class="label">确认密码</text>
      <input 
        class="input-native" 
        :value="regForm.confirmPwd"
        @input="(e) => regForm.confirmPwd = e.target.value"
        password 
        placeholder="再次输入密码"
        confirm-type="done"
        cursor-spacing="20"
        @confirm="handleRegister"
      />
    </view>
    <view class="form-item">
      <view class="reg-btn" @click="handleRegister">完成注册</view>
    </view>

    <view class="tip">
      已有账号？
      <text class="link" @click="toLogin">去登录</text>
    </view>
  </view>
</view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { registerApi } from '../../src/api/user'
import { UserLoginDTO } from '../../src/types'

const regForm = ref({
  username: '',
  password: '',
  confirmPwd: ''
})

// 跳登录页面
const toLogin = () => {
  uni.navigateTo({url:'/pages/login/login'})
}

const handleRegister = async () => {
  if (!regForm.value.username) {
    uni.showToast({ title: "用户名不能为空", icon: "none" })
    return
  }
  if (!regForm.value.password) {
    uni.showToast({ title: "密码不能为空", icon: "none" })
    return
  }
  if (regForm.value.password !== regForm.value.confirmPwd) {
    uni.showToast({ title: "两次密码不一致", icon: "none" })
    return
  }
  const submitData: UserLoginDTO = {
    username: regForm.value.username,
    password: regForm.value.password
  }
  const res = await registerApi(submitData)
  if (res.code === 200) {
    uni.showToast({ title: "注册成功" })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/login/login' })
    }, 1000)
  } else {
    uni.showToast({ title: res.msg, icon: "none" })
  }
}
</script>

<style scoped>
.register-wrap {
  width: 100%;
  min-height: 100vh;
  box-sizing: border-box;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}
.register-card {
  width: 100%;
  max-width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
  box-sizing: border-box;
}
.title {
  font-size: 48rpx;
  text-align: center;
  margin-bottom: 60rpx;
  display: block;
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
  background: #fff;
}
/* view模拟按钮，替代原生button */
.reg-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  background: #409EFF;
  color: #fff;
  border-radius: 16rpx;
  font-size: 32rpx;
}
.tip {
  margin-top: 40rpx;
  text-align: center;
  font-size: 28rpx;
  color: #606266;
}
.link {
  color: #409EFF;
  margin-left: 12rpx;
}
</style>