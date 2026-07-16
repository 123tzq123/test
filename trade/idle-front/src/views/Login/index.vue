<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2 class="title">用户登录</h2>
      <el-form ref="loginRef" :model="loginForm" label-width="70px" class="form-wrap">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" show-password placeholder="请输入密码" size="large"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="tip">还没有账号？<span @click="goRegister">立即注册</span></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Cookies from 'js-cookie'
// 新增导入获取用户信息接口
import { loginApi, getUserInfoApi } from '../../api/user'
import { ElMessage, ElForm } from 'element-plus'

const router = useRouter()
const loginRef = ref<InstanceType<typeof ElForm>>()
const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if(!loginForm.value.username || !loginForm.value.password){
    ElMessage.warning("用户名和密码不能为空")
    return
  }
  const res = await loginApi(loginForm.value)
  if (res.code === 200) {
    Cookies.set('token', res.data.token)
    Cookies.set('userId', String(res.data.userId))

    // ========== 新增：拉取用户信息，存储头像Cookie ==========
    const userInfoRes = await getUserInfoApi()
    // 兜底：头像为null时存空字符串
    const avatarUrl = userInfoRes.data.avatar ?? ''
    Cookies.set('avatar', avatarUrl)

    ElMessage.success('登录成功')
    router.push('/')
  } else {
    ElMessage.error(res.msg)
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-wrap {
  width: 100vw;
  height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 420px;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(64, 158, 255, 0.08);
  padding: 36px;
}
.title {
  text-align: center;
  font-size: 24px;
  color: #303133;
  margin: 0 0 30px;
}
.form-wrap {
  margin-bottom: 24px;
}
.login-btn {
  width: 100%;
  border-radius: 8px;
}
.tip {
  text-align: center;
  font-size: 14px;
  color: #606266;
}
.tip span {
  color: #409EFF;
  cursor: pointer;
  margin-left: 6px;
}
</style>