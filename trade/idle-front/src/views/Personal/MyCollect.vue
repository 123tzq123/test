<template>
  <NavBar></NavBar>
  <div style="padding:30px 40px">
    <h2>我的收藏</h2>
    <div v-if="collectList.length ===0" style="margin:30px 0;font-size:16px">暂无收藏商品</div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in collectList" :key="item.id">
        <el-card shadow="hover">
          <img v-if="item.coverImg" :src="item.coverImg" class="goods-img"/>
          <h3>{{ item.title }}</h3>
          <p>价格：{{ item.price ?? 0 }}元</p>
          <el-button type="primary" @click="$router.push('/goods/'+item.id)">查看详情</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref,onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { getMyCollectApi } from '../../api/goods'
import { GoodsItem } from '../../types'

const collectList = ref<GoodsItem[]>([])

//加载我的收藏列表
const loadCollect = async ()=>{
  const res = await getMyCollectApi()
  if(res.code ===200){
    collectList.value = res.data
  }
}

onMounted(()=>{
  loadCollect()
})
</script>

<style scoped>
.goods-img{
  width:100%;
  height:180px;
  object-fit:cover;
}
</style>