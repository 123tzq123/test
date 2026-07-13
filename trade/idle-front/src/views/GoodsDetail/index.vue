<template>
  <NavBar/>
  <div class="detail" v-if="goods">
    <el-row :gutter="40">
      <el-col :span="10">
        <!--遍历imgList，展示所有图片-->
        <div class="img-container">
          <img v-for="(img, idx) in goods.imgList" :key="idx" :src="img" class="img-item" />
        </div>
      </el-col>
      <el-col :span="14">
        <h2>{{ goods.title }}</h2>
        <p class="price">售价：{{ goods.price }}</p>
        <p>卖家：{{ goods.sellerName }}</p>
        <p>商品描述：{{ goods.content }}</p>
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
  padding: 30px;
}
.img-container{
  display: flex;
  flex-wrap: wrap;
  gap:8px;
}
.img-item {
  width:170px;
  height:170px;
  object-fit: cover;
}
.price {
  font-size: 20px;
  color: red;
}
</style>