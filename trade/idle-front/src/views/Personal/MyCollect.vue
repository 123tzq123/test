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
          <!-- 双按钮并排 -->
          <div class="btn-row">
            <el-button size="small" type="primary" class="detail-btn" @click="$router.push('/goods/' + item.id)">查看详情</el-button>
            <el-button size="small" type="default" class="cancel-btn" @click="handleCancelCollect(item.id)">取消收藏</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { getMyCollectApi, changeCollectApi } from '../../api/goods'
import { GoodsItem, Result, CollectDTO } from '../../types'
import { ElMessage } from 'element-plus'

const collectList = ref<GoodsItem[]>([])
const userIdStr = sessionStorage.getItem('userId')
const loginUserId = userIdStr ? Number(userIdStr) : 0

// 加载收藏列表
const loadCollect = async () => {
  const res = await getMyCollectApi() as unknown as Result<GoodsItem[]>
  if (res.code === 200) {
    collectList.value = res.data.map((item: GoodsItem) => {
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

// 取消收藏方法
const handleCancelCollect = async (goodsId: number) => {
  const params: CollectDTO = {
    goodsId: goodsId,
    userId: loginUserId
  }
  const res = await changeCollectApi(params)
  // 请求成功
  if (res.code === 200) {
    // 弹出成功提示
    ElMessage.success("取消收藏成功")
    // 重新拉取最新收藏列表，页面自动刷新
    await loadCollect()
  } else {
    ElMessage.error(res.msg || "操作失败")
  }
}

onMounted(() => {
  loadCollect()
})
</script>

<style scoped>
.page-wrap {
  padding: 24px 48px;
  background: #f5f5f5;
  min-height: calc(100vh - 64px);
}
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
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
.goods-grid {
  row-gap: 24px;
}
.goods-card {
  border-radius: 12px;
  overflow: hidden;
}
.goods-img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}
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
.btn-row {
  display: flex;
  gap: 8px;
}
.detail-btn, .cancel-btn {
  flex: 1;
  border-radius: 8px;
}
</style>