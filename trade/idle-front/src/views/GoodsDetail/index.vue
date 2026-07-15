<template>
  <NavBar/>
  <div class="detail" v-if="goods">
    <el-row :gutter="40">
      <el-col :span="10">
        <div class="img-container">
          <img
            v-for="img in goods.imgList"
            :key="img"
            :src="img"
            class="good-img"
          />
        </div>
      </el-col>
      <el-col :span="14">
        <h2>{{ goods.title }}</h2>
        <p class="price">售价：{{ goods.price }} 元</p>
        <p>卖家：{{ goods.sellerName }}</p>
        <p>商品描述：{{ goods.content }}</p>
        <!--下单按钮-->
        <el-button
          v-if="goods.status === 1 && goods.userId !== loginUserId"
          type="primary"
          size="large"
          @click="buyGoods"
        >
          立即购买
        </el-button>
        <!-- ============ 新增联系卖家按钮 ============ -->
        <el-button
          v-if="goods.status === 1 && goods.userId !== loginUserId"
          size="large"
          @click="goChat"
        >
          联系卖家
        </el-button>
        <!-- 收藏按钮：自己发布的商品不显示收藏按钮 -->
        <el-button
          v-if="goods.userId !== loginUserId"
          type="warning"
          size="large"
          @click="handleCollect"
        >
          {{ isCollect ? "取消收藏" : "收藏商品" }}
        </el-button>
      </el-col>
    </el-row>
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
// 是否收藏
const isCollect = ref(false)
// 获取当前登录人的userId
const loginUserId = Number(Cookies.get('userId')) || 0
console.log(loginUserId)
let goodsId = 0

// 加载商品详情
const getDetail = async () => {
  goodsId = route.params.goodsId ? Number(route.params.goodsId) : 0;
  if(isNaN(goodsId)){
    return;
  }
  const res = await getGoodsDetailApi(goodsId)
  if (res.code === 200) {
    goods.value = res.data
  }
  // 获取收藏状态
  getCollectState()
}

//页面加载判断收藏状态
const getCollectState = async ()=>{
  const res = await isCollectApi({ goodsId, userId:loginUserId })
  if(res.code ===200){
    isCollect.value = res.data
  }
}

//点击收藏
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

// 下单
const buyGoods = async () => {
  if (!goods.value) return
  const res = await createOrderApi(goods.value.id)
  if (res.code === 200) {
    ElMessage.success('下单成功，请前往我的订单查看')
    router.push('/personal/order')
  }
}

// 跳转到聊天页面
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

onMounted(() => {
  getDetail()
})
</script>

<style scoped>
.detail {
  padding: 30px;
}
.good-img {
  width: 350px;
  height: 350px;
  object-fit: cover;
  margin-bottom: 8px;
}
.price {
  font-size: 22px;
  color: red;
  margin: 10px 0;
}
</style>