<template>
  <NavBar/>
  <div class="detail" v-if="goods">
    <el-row :gutter="40">
      <el-col :span="10">
        <img v-if="goods?.imgUrl" :src="goods.imgUrl" class="img" />
      </el-col>
      <el-col :span="14">
        <h2>{{ goods?.title }}</h2>
        <p class="price">售价：{{ goods?.price }}</p>
        <p>卖家：{{ goods?.sellerName }}</p>
        <p>商品描述：{{ goods?.description }}</p>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getGoodsDetailApi } from '../../api/goods'
import { GoodsItem } from '../../types'
import NavBar from '../../components/NavBar.vue'

const route = useRoute()
const goods = ref<GoodsItem | undefined>()
const getDetail = async () => {
  const goodsId = Number(route.params.goodsId)
  const res = await getGoodsDetailApi(goodsId)
  if (res.code === 200) {
    goods.value = res.data
  }
}

onMounted(() => {
  getDetail()
})
</script>

<style scoped>
.detail {
  padding:30px 40px;
}
.img {
  width:100%;
  height:350px;
  object-fit:cover;
}
.price {
  font-size:22px;
  color:red;
  margin-top:20px;
}
</style>