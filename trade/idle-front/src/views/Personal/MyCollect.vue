<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">我的收藏</h2>
    <!-- 空收藏提示 -->
    <div class="empty-tip" v-if="collectList.length === 0">
      暂无收藏商品，快去首页挑选心仪闲置吧
      <el-button type="primary" class="tip-btn" @click="$router.push('/')">前往首页</el-button>
    </div>

    <!-- 收藏商品网格 -->
    <el-row :gutter="24" class="goods-grid" v-else>
      <el-col :span="6" v-for="item in collectList" :key="item.id">
        <el-card shadow="hover" class="goods-card">
          <!-- 图片加载失败兜底处理 -->
            <img 
              v-if="item.coverImg ?? ''" 
              :src="item.coverImg ?? ''" 
              class="goods-img" 
              alt="商品封面"

            />
          <div v-else class="empty-img">暂无商品图</div>
          
          <h3 class="goods-title">{{ item.title }}</h3>
          <p class="goods-price">¥{{ item.price ?? 0 }}</p>
          <p class="view-text">浏览 {{ item.viewCount ?? 0 }} 次</p>
          <el-button size="large" type="primary" class="detail-btn" @click="$router.push('/goods/' + item.id)">查看详情</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { getMyCollectApi } from '../../api/goods'
import { GoodsItem, Result } from '../../types'

const collectList = ref<GoodsItem[]>([])
// 记录加载404失败的商品ID
const errorImgIds = ref<number[]>([])

// 图片加载失败回调
const handleImgError = (goodsId: number) => {
  if (!errorImgIds.value.includes(goodsId)) {
    errorImgIds.value.push(goodsId)
  }
}

//加载我的收藏列表
const loadCollect = async () => {
  // 接口直接返回商品数组，不是分页对象，删除PageVO包装
  const res = await getMyCollectApi() as unknown as Result<GoodsItem[]>
  if (res.code === 200) {
    // 拆分goodsImg逗号字符串，取第一张赋值coverImg
    collectList.value = res.data.map(item => {
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
}

onMounted(() => {
  loadCollect()
})
</script>

<style scoped>
/* 全站统一页面外层 */
.page-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
/* 页面统一标题 */
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
/* 空收藏提示区域 */
.empty-tip {
  text-align: center;
  padding: 60px 0;
  font-size: 16px;
  color: #606266;
}
.tip-btn {
  margin-top: 16px;
  border-radius: 8px;
}
/* 商品网格间距 */
.goods-grid {
  row-gap: 24px;
}
/* 商品卡片统一样式 */
.goods-card {
  border-radius: 12px;
  overflow: hidden;
}
/* 和首页、卖家主页图片尺寸统一 */
.goods-img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}
/* 无图/图片404占位样式 */
.empty-img {
  width: 100%;
  height: 140px;
  background: #f0f2f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}
.goods-title {
  font-size: 16px;
  margin: 12px 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  margin: 0 0 16px;
}
.detail-btn {
  width: 100%;
  border-radius: 8px;
}
</style>