<template>
  <NavBar/>
  <div class="detail-wrap" v-if="goods">
    <el-card class="detail-card">
      <el-row :gutter="40">
        <el-col :span="10">
          <div class="img-list-box">
            <img
              v-for="img in goods.imgList"
              :key="img"
              :src="img"
              class="detail-img"
            />
          </div>
        </el-col>
        <el-col :span="14">
          <h2 class="goods-name">{{ goods.title }}</h2>
          <!--卖家信息栏-->
          <div class="seller-info" v-if="goods.sellerName"
               @click="$router.push({path:'/seller',query:{sellerId:goods.userId}})">
            <el-avatar :src="goods.avatar" size="64"></el-avatar>
            <span class="seller-name">{{ goods.sellerName }}</span>
          </div>
          <p class="price-text">售价：<span class="price-num">{{ goods.price }}</span> 元</p>
          <p class="info-line">卖家：{{ goods.sellerName }}</p>
          <p class="info-line">商品描述：{{ goods.content }}</p>
          <p class="info-line view-num">浏览量：{{ goods.viewCount ?? 0 }}次</p>

          <!--操作按钮组-->
          <div class="btn-group">
            <el-button size="large" @click="backToHome">返回首页</el-button>
            <el-button
              v-if="goods.status === 1 && goods.userId !== loginUserId"
              type="primary"
              size="large"
              @click="buyGoods"
            >
              立即购买
            </el-button>
            <el-button
              v-if="goods.status === 1 && goods.userId !== loginUserId"
              size="large"
              @click="goChat"
            >
              联系卖家
            </el-button>
            <el-button
              v-if="goods.userId !== loginUserId"
              type="warning"
              size="large"
              @click="handleCollect"
            >
              {{ isCollect ? "取消收藏" : "收藏商品" }}
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Cookies from 'js-cookie'
import NavBar from '../../components/NavBar.vue'
import { getGoodsDetailApi, isCollectApi, changeCollectApi } from '../../api/goods'
import { createOrderApi } from '../../api/order'
import { ElMessage } from 'element-plus'
import { GoodsItem } from '../../types'

const route = useRoute()
const router = useRouter()
const goods = ref<GoodsItem>()
const isCollect = ref(false)
const loginUserId = Number(Cookies.get('userId')) || 0
let goodsId = 0

const getDetail = async () => {
  goodsId = route.params.goodsId ? Number(route.params.goodsId) : 0;
  if(isNaN(goodsId)){
    return;
  }
  const res = await getGoodsDetailApi(goodsId)
  if (res.code === 200) {
    goods.value = res.data
  }
  getCollectState()
}

const getCollectState = async ()=>{
  const res = await isCollectApi({ goodsId, userId:loginUserId })
  if(res.code ===200){
    isCollect.value = res.data
  }
}

const handleCollect = async ()=>{
  if(loginUserId ===0){
    ElMessage.warning("请先登录！")
    return
  }
  const res = await changeCollectApi({goodsId,userId:loginUserId})
  if(res.code ===200){
    isCollect.value = !isCollect.value
    ElMessage.success(res.data)
  }else {
    ElMessage.warning(res.msg)
  }
}

const buyGoods = async () => {
  if (!goods.value) return
  const res = await createOrderApi(goods.value.id)
  if (res.code === 200) {
    ElMessage.success('下单成功，请前往我的订单查看')
    router.push('/personal/order')
  }
}

const goChat = () => {
  if(!goods.value) return
  router.push({
    path: '/chat',
    query: {
      goodsId: String(goods.value.id),
      otherId: String(goods.value.userId)
    }
  })
}

// 返回首页刷新标记
const backToHome = () => {
  localStorage.setItem("needRefreshGoods", "1");
  router.push("/");
}

onMounted(() => {
  getDetail()
})
</script>

<style scoped>
.detail-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
.detail-card {
  border-radius: 12px;
  padding: 30px;
}
.img-list-box {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.detail-img {
  width: 100%;
  height: 360px;
  object-fit: cover;
  border-radius: 8px;
}
.goods-name {
  font-size: 24px;
  color: #303133;
  margin: 0 0 20px;
}
.seller-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  cursor: pointer;
}
.seller-name {
  font-size: 18px;
  color: #409EFF;
}
.price-text {
  font-size: 22px;
  margin: 0 0 12px;
}
.price-num {
  color: #f53f3f;
  font-weight: 600;
  font-size: 26px;
}
.info-line {
  font-size: 16px;
  color: #606266;
  margin: 8px 0;
}
.view-num {
  color: #909399;
}
.btn-group {
  display: flex;
  gap: 16px;
  margin-top: 30px;
}
</style>