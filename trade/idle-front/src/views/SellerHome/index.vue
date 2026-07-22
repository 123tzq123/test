<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">卖家主页</h2>
    <!-- 卖家头部信息卡片（新增联系卖家按钮） -->
    <el-card class="seller-header-card">
      <div class="seller-header">
        <div class="user-base">
          <el-avatar :src="sellerInfo.avatar || ''" size="80"></el-avatar>
          <div class="seller-text">
            <h2>{{ sellerInfo.nickname }}</h2>
            <p class="score-text">综合评分：{{ avgScore }} 分</p>
          </div>
        </div>
        <!-- 联系卖家按钮 -->
        <!-- <el-button type="primary" size="large" @click="goChat">联系卖家</el-button> -->
        <!-- 联系卖家按钮 -->
        <el-button v-if="loginUserId !== sellerId" type="primary" size="large" @click="goChat">联系卖家</el-button>
      </div>
    </el-card>

    <h3 class="sub-title">他发布的闲置商品</h3>
    <div class="goods-wrap">
      <el-card class="goods-item-card" v-for="item in goodsList" :key="item.id">
        <!-- 商品封面：和首页逻辑完全统一 -->
        <div class="goods-main">
          <img 
            v-if="item.coverImg ?? ''" 
            :src="item.coverImg || ''" 
            class="goods-img" 
            alt=""
          />
          <div v-else class="empty-img">暂无商品图</div>
          <div class="goods-info">
            <h4 class="goods-title">{{ item.title }}</h4>
            <p class="goods-price">¥{{ item.price }}</p>
            <p class="view-text">浏览 {{ item.viewCount ?? 0 }} 次</p>
            <el-button type="primary" size="small" @click="$router.push('/goods/' + item.id)">查看详情</el-button>
          </div>
        </div>

        <!-- 商品买家评价 -->
        <div class="comment-block" v-if="commentMap.get(item.id) && commentMap.get(item.id)!.length > 0">
          <h4 class="comment-title">买家评价</h4>
          <div class="comment-item" v-for="c in commentMap.get(item.id)" :key="c.id">
            <el-rate v-model="c.score" disabled size="small"></el-rate>
            <p class="comment-content">评价内容：{{ c.content }}</p>
            <div class="img-box">
              <template 
                v-for="img in splitImgStr(c.imgList)" 
                :key="img"
              >
                <img class="comment-img" :src="img" alt="评价图"/>
              </template>
              <span v-if="splitImgStr(c.imgList).length === 0">无评价图片</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavBar from '../../components/NavBar.vue'
import { getSellerHomeApi, getSellerCommentApi } from '../../api/comment'
import { GoodsItem, SysUser, SellerGoodsCommentVO, GoodsCommentVO, Result, SellerHomeVO } from '../../types'

const route = useRoute()
const router = useRouter()
const sellerId = Number(route.query.sellerId)
const userIdStr = sessionStorage.getItem('userId')
const loginUserId = userIdStr ? Number(userIdStr) : 0
const sellerInfo = ref<SysUser>({} as SysUser)
const avgScore = ref(0)
const goodsList = ref<GoodsItem[]>([])
const commentMap = ref<Map<number, GoodsCommentVO[]>>(new Map())

// 统一图片拆分工具函数
const splitImgStr = (source: string | string[] | null | undefined): string[] => {
  if (Array.isArray(source)) {
    return source.filter(url => url && url.trim())
  }
  if (!source || source.trim() === '') return []
  return source.split(',').filter(url => url.trim())
}


const goChat = () => {
  if (loginUserId === 0) {
    ElMessage.warning("请先登录！")
    return
  }
  router.push({
    path: '/personal/message',
    query: {
      targetOtherId: String(sellerId),
      targetOtherName: sellerInfo.value.nickname,
      targetOtherAvatar: sellerInfo.value.avatar
    }
  })
}


const loadData = async () => {
  const res: Result<SellerHomeVO> = await getSellerHomeApi(sellerId)
  if (res.code === 200) {
    sellerInfo.value = res.data.sellerInfo
    avgScore.value = Number(res.data.avgScore)
    goodsList.value = res.data.goodsList.map((item: GoodsItem) => {
      let coverImg = ''
      if (item.goodsImg && item.goodsImg.trim() !== '') {
        const imgArr = item.goodsImg.split(',').filter(s => s.trim())
        coverImg = imgArr.length > 0 ? imgArr[0] : ''
      }
      return {
        ...item,
        coverImg: coverImg
      }
    })
  }
  // 加载评价数据
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
/* 全站统一页面外层容器 */
.page-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
/* 统一页面主标题 */
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
/* 次级小标题 */
.sub-title {
  font-size: 18px;
  margin: 30px 0 16px;
  color: #303133;
}
/* 卖家头部卡片 */
.seller-header-card {
  border-radius: 12px;
  padding: 30px;
}
.seller-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.user-base {
  display: flex;
  align-items: center;
  gap: 24px;
}
.seller-text h2 {
  font-size: 24px;
  margin: 0 0 8px;
}
.score-text {
  font-size: 16px;
  color: #f53f3f;
  font-weight: 500;
}
/* 商品列表容器 */
.goods-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
/* 单个商品卡片 */
.goods-item-card {
  border-radius: 12px;
  padding: 24px;
}
/* 商品头部图文区域 */
.goods-main {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}
.goods-img {
  width: 140px;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}
.empty-img {
  width: 140px;
  height: 140px;
  background: #f0f2f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}
.goods-info {
  flex: 1;
}
.goods-title {
  font-size: 18px;
  margin: 0 0 8px;
}
.goods-price {
  font-size: 20px;
  color: #f53f3f;
  font-weight: 600;
  margin: 0 0 6px;
}
.view-text {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px;
}
/* 评价区块 */
.comment-block {
  margin-top: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}
.comment-title {
  font-size: 16px;
  margin: 0 0 16px;
  color: #303133;
}
.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #e5e7eb;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-content {
  font-size: 15px;
  color: #606266;
  margin: 8px 0;
}
/* 评价图片 */
.img-box {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 8px;
}
.comment-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}
</style>