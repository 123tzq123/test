<template>
  <NavBar />
  <div class="page-wrap">
    <h2 class="page-title">个人资料设置</h2>
    <el-card class="setting-card">
      <!-- 头像区域 -->
      <div class="avatar-wrap">
        <span class="label">头像</span>
        <el-avatar class="my-avatar" :src="form.avatar"></el-avatar>
        <el-upload
          action="http://localhost:8080/user/updateAvatar"
          :headers="headers"
          :on-success="onAvatarSuccess"
          show-file-list="false"
        >
          <el-button type="primary" size="large">更换头像</el-button>
        </el-upload>
      </div>

      <!-- 表单区域 -->
      <el-form :model="form" label-width="90px" class="info-form">
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" size="large" placeholder="请输入展示昵称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="submit-btn" @click="submit">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NavBar from '../../../components/NavBar.vue'
import { getUserInfoApi, updateUserInfoApi } from '../../../api/user'
import { ElMessage } from 'element-plus'

const headers = ref({
  token: sessionStorage.getItem('token')
})
const form = ref({
  nickname: '',
  avatar: ''
})

const getUserInfo = async () => {
  const res = await getUserInfoApi()
  form.value.nickname = res.data.nickname
  form.value.avatar = res.data.avatar
}

//头像上传成功，同步更新Cookie
const onAvatarSuccess = (res: any) => {
  form.value.avatar = res.data
  sessionStorage.setItem('avatar', res.data)
  ElMessage.success("头像上传完毕，请点击保存")
}

//提交修改，同步最新头像到Cookie
const submit = async () => {
  await updateUserInfoApi(form.value)
  sessionStorage.setItem('avatar', form.value.avatar)
  ElMessage.success("个人信息修改成功！")
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
/* 全局统一页面外层样式（和所有个人页面保持一致） */
.page-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
/* 页面标题统一样式 */
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
/* 主卡片 */
.setting-card {
  width: 600px;
  margin: 0 auto;
  padding: 36px;
  border-radius: 12px;
}
/* 头像模块 */
.avatar-wrap {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
}
.label {
  font-size: 16px;
  color: #303133;
  width: 60px;
}
.my-avatar {
  width: 100px;
  height: 100px;
}
/* 表单区域 */
.info-form {
  margin-top: 10px;
}
.info-form .el-form-item {
  margin-bottom: 24px;
}
/* 提交按钮 */
.submit-btn {
  width: 100%;
  border-radius: 8px;
}
</style>