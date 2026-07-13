<template>
  <NavBar></NavBar>
  <div class="home">
    <h2>闲置商品列表</h2>
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in goodsList" :key="item.id">
        <el-card shadow="hover">
          <img v-if="item.coverImg" :src="item.coverImg" class="goods-img" />
          <h3>{{ item.title }}</h3>
          <p>价格：{{ item.price ?? 0 }}元</p>
          <el-button @click="router.push('/goods/' + item.id)">查看详情</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, onBeforeRouteUpdate } from 'vue-router'
import { getGoodsListApi } from '../../api/goods'
import { GoodsItem } from '../../types'
import NavBar from '../../components/NavBar.vue'

const router = useRouter()
const goodsList = ref<GoodsItem[]>([])

const loadList = async () => {
  const res = await getGoodsListApi(1, 8)
  if (res.code === 200) {
    goodsList.value = res.data.records
  }
}

onMounted(() => {
  loadList()
})
//切回首页就刷新商品列表
onBeforeRouteUpdate(()=>{
  loadList()
})
</script>

<style scoped>
.home {
  padding: 20px 40px;
}
.goods-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}
</style>