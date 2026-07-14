<template>
  <div class="navbar">
    <div class="left">闲置物品交易平台</div>
    <div class="right">
      <router-link to="/home">首页</router-link>
      <router-link to="/publish">发布闲置</router-link>
      <router-link to="/personal">个人中心</router-link>
      <template v-if="token">
        <router-link to="/personal/order">我的订单</router-link>
        <router-link to="/personal/seller-order">卖出订单</router-link>
        <!--头像下拉菜单-->
        <el-dropdown @command="handleCommand">
          <span class="avatar-wrap">
            <el-avatar :src="avatar"></el-avatar>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="setting">个人设置</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
      <span v-if="!token">
        <router-link to="/login">登录</router-link>
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Cookies from 'js-cookie'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserInfoApi } from '../api/user'

const router = useRouter()
const token = ref('')
const avatar = ref('')

// 获取token
const getToken = () => {
  token.value = Cookies.get('token') || ''
}

// 获取登录用户头像
const loadUserInfo = async () => {
  if (!token.value) return
  const res = await getUserInfoApi()
  avatar.value = res.data.avatar
}

// 下拉菜单点击事件
const handleCommand = (command: string) => {
  if (command === 'setting') {
    router.push('/userSetting')
  } else if (command === 'logout') {
    Cookies.remove('token')
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}

onMounted(async () => {
  getToken()
  await loadUserInfo()
})
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  padding: 15px 30px;
  background-color: #282c34;
  color: white;
  font-size: 16px;
}
.right {
  display: flex;
  gap: 25px;
  align-items: center;
}
a {
  color: white;
  text-decoration: none;
}
.avatar-wrap {
  cursor: pointer;
}
</style>