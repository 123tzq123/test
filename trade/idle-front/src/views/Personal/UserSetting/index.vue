<template>
  <div class="setting-page">
    <h2>个人资料设置</h2>
    <el-card style="width:600px;margin:20px auto;padding:30px">
      <div class="avatar-item">
        <p>头像：</p>
        <el-avatar class="my-avatar" :src="form.avatar"></el-avatar>
        <el-upload
          action="http://localhost:8080/user/updateAvatar"
          :headers="headers"
          :on-success="onAvatarSuccess"
        >
          <el-button type="primary" size="small">更换头像</el-button>
        </el-upload>
      </div>
      <el-form :model="form" label-width="80px" style="margin-top:30px">
        <el-form-item label="昵称">
          <el-input v-model="form.nickname"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Cookies from 'js-cookie'
import { getUserInfoApi, updateUserInfoApi } from '../../../api/user'
import { ElMessage } from 'element-plus'

const headers = ref({
  token: Cookies.get('token')
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

//头像上传成功
const onAvatarSuccess = (res: any) => {
  form.value.avatar = res.data
  ElMessage.success("头像上传完毕，请点击保存")
}

//提交修改
const submit = async () => {
  await updateUserInfoApi(form.value)
  ElMessage.success("个人信息修改成功！")
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.setting-page {
  padding:20px;
}
.avatar-item {
  display:flex;
  align-items:center;
  gap:20px;
}
.my-avatar {
  width:100px;
  height:100px;
}
</style>