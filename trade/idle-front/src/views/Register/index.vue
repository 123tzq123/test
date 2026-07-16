<template>
  <div class="register-wrap">
    <el-card class="register-card">
      <h2 class="title">账号注册</h2>
      <el-form ref="regRef" :model="regForm" label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="regForm.username" size="large" placeholder="设置用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="regForm.password" show-password size="large" placeholder="设置登录密码"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="regForm.nickname" size="large" placeholder="设置展示昵称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="reg-btn" @click="handleRegister">完成注册</el-button>
        </el-form-item>
      </el-form>
      <div class="tip">已有账号？<span @click="$router.push('/login')">去登录</span></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '../../api/user'
import { ElMessage, ElForm } from 'element-plus'
const router = useRouter()
const regRef = ref<InstanceType<typeof ElForm>>()
const regForm = ref({
  username: '',
  password: '',
  nickname: ''
})

const handleRegister = async () => {
  if(!regForm.value.username || !regForm.value.password || !regForm.value.nickname){
    ElMessage.warning('全部输入框不能为空')
    return
  }
  const res = await registerApi(regForm.value)
  if(res.code === 200){
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  }else{
    ElMessage.error(res.msg)
  }
}
</script>

<style scoped>
.register-wrap {
  width: 100vw;
  height: 100vh;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.register-card {
  width: 420px;
  padding: 36px;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(64, 158, 255, 0.08);
}
.title {
  text-align: center;
  font-size: 24px;
  margin: 0 0 30px;
  color: #303133;
}
.reg-btn {
  width: 100%;
  border-radius: 8px;
}
.tip {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}
.tip span {
  color: #409EFF;
  cursor: pointer;
}
</style>