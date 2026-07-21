import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

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
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register/index.vue')
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
  // 个人中心总入口（保留）
  {
    path: '/personal',
    name: 'Personal',
    component: () => import('../views/Personal/index.vue'),
    meta: { requiresAuth: true }
  },
  // 我的发布
  {
    path: '/personal/goods',
    name: 'MyGoods',
    component: () => import('../views/Personal/index.vue'),
    meta: { requiresAuth: true }
  },
  // 我的订单
  {
    path: '/personal/order',
    name: 'MyOrder',
    component: () => import('../views/Personal/Order.vue'),
    meta: { requiresAuth: true }
  },
  // 卖出订单
  {
    path: '/personal/sell',
    name: 'SellerOrder',
    component: () => import('../views/Personal/SellerOrder.vue'),
    meta: { requiresAuth: true }
  },
  // 过往评价
  {
    path: '/personal/comment',
    name: 'MyComment',
    component: () => import('../views/Personal/Comment.vue'),
    meta: { requiresAuth: true }
  },
  // 收到评价
  {
    path: '/personal/receiveComment',
    name: 'ReceiveComment',
    component: () => import('../views/Personal/ReceiveComment.vue'),
    meta: { requiresAuth: true }
  },
  // 我的主页（个人资料设置） 
  {
    path: '/personal/home',
    name: 'MyHome',
    component: () => import('../views/Personal/UserSetting/index.vue'),
    meta: { requiresAuth: true }
  },
  {
  path: '/personal/message',
  name: 'MyMessage',
  component: () => import('../views/Personal/MessageList.vue'),
  meta: { requiresLogin: true }
  },
  // 聊天页面
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('../views/Personal/Chat.vue'),
    meta: {
      requiresAuth: true //必须登录才能进入聊天页面
    }
  },
  // 个人资料设置（备用旧路径，保留兼容）
  {
    path: '/userSetting',
    name: 'UserSetting',
    component: () => import('../views/Personal/UserSetting/index.vue'),
    meta: { requiresAuth: true }
  },
  // 我的收藏
  {
    path: '/personal/collect',
    name: 'MyCollect',
    component: () => import('../views/Personal/MyCollect.vue'),
    meta: { requiresAuth: true }
  },
  // 卖家主页
  {
    path: '/seller',
    name: 'SellerHome',
    component: () => import('../views/SellerHome/index.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

//全局路由守卫
router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('token')
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