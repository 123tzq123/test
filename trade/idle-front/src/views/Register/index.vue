<template>
  <div class="login-container">
    <h2>闲置平台‑用户注册</h2>
    <el-form ref="loginRef" :model="registerForm" label-width="70px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitRegister">注册</el-button>
      </el-form-item>
      <div>已有账号？<span class="link-text" @click="$router.push('/login')">去登录</span></div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { registerApi } from '../../api/user'
import { UserLoginDTO } from '../../types'

const router = useRouter()
const registerForm = ref<UserLoginDTO>({
  username: '',
  password: ''
})

//提交注册
const submitRegister = async () => {
  if (!registerForm.value.username || !registerForm.value.password) {
    ElMessage.warning("用户名和密码不能为空");
    return;
  }
  const res = await registerApi(registerForm.value);
  if (res.code === 200) {
    ElMessage.success("注册成功，请登录");
    router.push("/login");
  } else {
    ElMessage.error(res.msg);
  }
}
</script>

<style scoped>
.login-container {
  width: 400px;
  margin: 80px auto;
}
.link-text {
  color: #409eff;
  cursor: pointer;
}
</style>