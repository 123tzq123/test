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
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Cookies from 'js-cookie'
import { loginApi } from '../../api/user'
import { ElMessage, ElForm } from 'element-plus'

const router = useRouter()
const loginRef = ref<InstanceType<typeof ElForm>>()
const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  const res = await loginApi(loginForm.value)
  if (res.code === 200) {
    Cookies.set('token', res.data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } else {
    ElMessage.error(res.msg)
  }
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
</style>