<template>
  <div class="navbar">
    <div class="left">闲置物品交易平台</div>
    <div class="right">
      <router-link to="/home">首页</router-link>
      <router-link to="/publish">发布闲置</router-link>
      <router-link to="/personal">个人中心</router-link>
      <span v-if="!token">
        <router-link to="/login">登录</router-link>
      </span>
      <el-button v-if="token" type="text" @click="logout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Cookies from 'js-cookie'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const token = ref('')

const getToken = () => {
  token.value = Cookies.get('token') || ''
}

const logout = () => {
  Cookies.remove('token')
  ElMessage.success('退出登录成功')
  router.push('/login')
}

onMounted(() => {
  getToken()
})
</script>

<style scoped>
.navbar {
  height: 60px;
  background: #333;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  font-size:16px;
}
.navbar a {
  color: #fff;
  margin:0 15px;
  text-decoration: none;
}
</style>