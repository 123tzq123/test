<template>
  <div class="nav-bar">
    <!-- 左侧Logo与平台名 -->
    <div class="nav-left" @click="$router.push('/')">
      <span class="logo-text">闲置优品交易平台</span>
    </div>
    <!-- 中间导航菜单 -->
    <div class="nav-menu">
      <router-link to="/" class="menu-item" active-class="active">首页</router-link>
      <router-link to="/publish" class="menu-item" active-class="active">发布闲置</router-link>
      <router-link to="/personal/goods" class="menu-item" active-class="active">我的发布</router-link>
      <router-link to="/personal/collect" class="menu-item" active-class="active">我的收藏</router-link>
      <router-link to="/personal/order" class="menu-item" active-class="active">我的订单</router-link>
      <router-link to="/personal/sell" class="menu-item" active-class="active">卖出订单</router-link>
      <router-link to="/personal/comment" class="menu-item" active-class="active">过往评价</router-link>
      <router-link to="/personal/receiveComment" class="menu-item" active-class="active">收到评价</router-link>
    </div>
    <!-- 右侧头像下拉 -->
    <div class="nav-right" v-if="loginUserId">
      <el-dropdown trigger="click">
        <div class="avatar-wrap">
          <el-avatar :src="avatarUrl" size="38" />
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/personal/home')">我的主页</el-dropdown-item>
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div class="nav-right" v-else>
      <el-button text @click="$router.push('/login')">登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Cookies from 'js-cookie'
const router = useRouter()
const loginUserId = ref(Number(Cookies.get('userId')))
const avatarUrl = ref(Cookies.get('avatar') || '')

// 退出登录
const logout = () => {
  Cookies.remove('token')
  Cookies.remove('userId')
  Cookies.remove('avatar')
  router.push('/login')
}
</script>

<style scoped>
.nav-bar {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 48px;
  border-bottom: 1px solid #e5e7eb;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 999;
}
.nav-left .logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #409EFF;
  cursor: pointer;
}
.nav-menu {
  display: flex;
  gap: 32px;
}
.menu-item {
  font-size: 15px;
  color: #333;
  text-decoration: none;
}
.menu-item.active {
  color: #409EFF;
  font-weight: 500;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 6px;
}
.nav-right {
  display: flex;
  align-items: center;
}
.avatar-wrap {
  cursor: pointer;
}
</style>