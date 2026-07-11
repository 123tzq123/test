import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Cookies from 'js-cookie'

//路由元信息，requiresAuth:true代表需要登录
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home/index.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login/index.vue')
  },
  {
    path: '/goods/:goodsId',
    name: 'GoodsDetail',
    component: () => import('../views/GoodsDetail/index.vue')
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('../views/Publish/index.vue'),
    meta: { requiresAuth: true } //必须登录
  },
  {
    path: '/personal',
    name: 'Personal',
    component: () => import('../views/Personal/index.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

//全局路由守卫
router.beforeEach((to, from, next) => {
  const token = Cookies.get('token')
  if (to.meta.requiresAuth) {
    if (token) {
      next()
    } else {
      next('/login') //没有token强制跳登录页
    }
  } else {
    next()
  }
})

export default router