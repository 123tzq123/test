<template>
  <div class="login-container">
    <el-card style="width:400px;padding:30px">
      <h2 style="text-align:center;margin-bottom:24px">用户登录</h2>
      <el-form ref="loginRef" :model="loginForm" label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" show-password></el-input>
        </el-form-item>
        <el-button type="primary" style="width:100%" @click="handleLogin">登录</el-button>
      </el-form>
      <!-- 把注册提示放到卡片里面，卡片内部最后 -->
      <div class="tip">还没有账号？<span @click="goRegister">立即注册</span></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Cookies from 'js-cookie'
import { loginApi } from '../../api/user'
import { ElMessage, ElForm } from 'element-plus'

//获取路由实例
const router = useRouter()
const loginRef = ref<InstanceType<typeof ElForm>>()
const loginForm = ref({
  username: '',
  password: ''
})

//登录方法
const handleLogin = async () => {
  if(!loginForm.value.username || !loginForm.value.password){
    ElMessage.warning("用户名和密码不能为空")
    return
  }
  const res = await loginApi(loginForm.value)
  if (res.code === 200) {
    Cookies.set('token', res.data.token)
    //新增：存储userId到Cookie
    Cookies.set('userId', String(res.data.userId))
    ElMessage.success('登录成功')
    router.push('/')
  } else {
    ElMessage.error(res.msg)
  }
}

//跳转到注册页面
const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f5f5;
}
.tip {
  margin-top:18px;
  text-align:center;
}
.tip span {
  color:#409eff;
  cursor: pointer;
}
</style>