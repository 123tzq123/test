<template>
  <div class="register-wrap">
    <el-card class="register-card">
      <h2 class="title">账号注册</h2>
      <el-form
        ref="regRef"
        :model="regForm"
        label-width="80px"
        :rules="regRules"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="regForm.username"
            size="large"
            placeholder="设置用户名"
          ></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="regForm.password"
            show-password
            size="large"
            placeholder="设置登录密码"
          ></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPwd">
          <el-input
            v-model="regForm.confirmPwd"
            show-password
            size="large"
            placeholder="再次输入登录密码"
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="reg-btn"
            @click="handleRegister"
          >
            完成注册
          </el-button>
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
import { ElMessage, ElForm, FormItemRule } from 'element-plus'

const router = useRouter()
// 移除nickname字段
const regForm = ref({
  username: '',
  password: '',
  confirmPwd: ''
})
const regRef = ref<InstanceType<typeof ElForm>>()

// 校验规则删除nickname相关
const regRules: Record<string, FormItemRule[]> = {
  username: [
    { required: true, message: '用户名不能为空', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_, value, callback) => {
        if (value !== regForm.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 注册提交
const handleRegister = async () => {
  await regRef.value?.validate(async (valid) => {
    if (!valid) return
    // 提交数据只传用户名+密码
    const submitData = {
      username: regForm.value.username,
      password: regForm.value.password
    }
    const res = await registerApi(submitData)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.msg)
    }
  })
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