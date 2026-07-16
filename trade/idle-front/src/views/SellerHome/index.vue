<template>
  <div class="home-container">
    <!--卖家头像和平均分-->
    <div class="seller-header">
      <el-avatar :src="sellerInfo.avatar" size="80"></el-avatar>
      <h2>{{ sellerInfo.nickname }}</h2>
      <p>综合评分：{{ avgScore }} 分</p>
    </div>

    <h3>他发布的闲置商品</h3>
    <div class="goods-list">
      <div class="goods-item" v-for="item in goodsList" :key="item.id">
  <img :src="item.coverImg ?? ''" alt="">
  <p>{{ item.title }}</p>
  <p>价格：{{ item.price }}元</p>
  <!-- 根据goodsId获取对应的评论 -->
  <div class="comment-block" v-if="commentMap.get(item.id) && commentMap.get(item.id)!.length > 0">
    <h4>买家评价</h4>
    <div v-for="c in commentMap.get(item.id)" :key="c.id">
      <el-rate v-model="c.score" disabled></el-rate>
      <p>评价内容：{{ c.content }}</p>
      <div class="img-box">
        <img v-for="img in c.imgList" :key="img" :src="img" style="width:80px;height:80px;margin:4px">
      </div>
    </div>
  </div>
</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSellerHomeApi, getSellerCommentApi } from '../../api/comment'
import { GoodsItem, SysUser, SellerGoodsCommentVO, GoodsCommentVO, Result, SellerHomeVO } from '../../types'

const route = useRoute()
const sellerId = Number(route.query.sellerId)
const sellerInfo = ref<SysUser>({} as SysUser)
const avgScore = ref(0)
const goodsList = ref<GoodsItem[]>([])
const commentMap = ref<Map<number, GoodsCommentVO[]>>(new Map())

const loadData = async () => {
  // 显式指定返回类型
  const res: Result<SellerHomeVO> = await getSellerHomeApi(sellerId)
  if (res.code === 200) {
    sellerInfo.value = res.data.sellerInfo
    avgScore.value = Number(res.data.avgScore)
    goodsList.value = res.data.goodsList
  }
  // 指定评论接口返回类型
  const commentRes: Result<SellerGoodsCommentVO[]> = await getSellerCommentApi(sellerId)
  if (commentRes.code === 200) {
    const map = new Map<number, GoodsCommentVO[]>()
    commentRes.data.forEach((item) => {
      map.set(item.goodsId, item.commentList)
    })
    commentMap.value = map
  }
}
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.seller-header{
  display:flex;
  align-items:center;
  gap:15px;
  margin-bottom:20px;
}
.goods-item{
  padding:15px;
  border:1px solid #eee;
  margin-bottom:12px;
}
.comment-block{
  margin-top:10px;
  padding:10px;
  background:#f8f8f8;
}
</style>