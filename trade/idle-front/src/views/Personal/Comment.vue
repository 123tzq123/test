<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">我的过往评价</h2>
    <el-table :data="commentList" border stripe style="width:100%;margin-top:20px;">
      <el-table-column label="商品名称" prop="goodsTitle" min-width="160"/>
      <!-- 新增：卖家信息列（头像+昵称，可点击跳转卖家主页） -->
      <el-table-column label="卖家信息" width="180">
        <template #default="scope">
          <div class="user-info" v-if="scope.row.goodsSeller" @click="$router.push({path:'/seller',query:{sellerId:scope.row.goodsSeller.id}})">
            <el-avatar :src="scope.row.goodsSeller.avatar" size="40"></el-avatar>
            <span class="user-name">{{ scope.row.goodsSeller.nickname }}</span>
          </div>
          <span v-else>商品已下架</span>
        </template>
      </el-table-column>
      <el-table-column label="评分" width="100">
        <template #default="scope">
          <el-rate v-model="scope.row.score" disabled size="small"/>
        </template>
      </el-table-column>
      <el-table-column label="评价内容" prop="content" min-width="220"/>
      <el-table-column label="评价图片" min-width="240">
        <template #default="scope">
          <div class="eval-img-box">
            <template v-for="img in splitImgStr(scope.row.imgList)" :key="img">
              <img class="eval-img" :src="img" alt="评价图"/>
            </template>
            <span v-if="splitImgStr(scope.row.imgList).length === 0">无评价图片</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="评价时间" prop="createTime" width="180"/>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import { getMyCommentApi } from '../../api/comment'
import { GoodsCommentVO, Result } from '../../types'

const router = useRouter()
const commentList = ref<GoodsCommentVO[]>([])

// 和首页、卖家主页共用的图片拆分函数
const splitImgStr = (str: string | null | undefined): string[] => {
  if (!str || str.trim() === '') return []
  return str.split(',').filter(url => url.trim())
}

const loadComment = async () => {
  const res = await getMyCommentApi() as unknown as Result<GoodsCommentVO[]>
  if (res.code === 200) {
    commentList.value = res.data
  }
}

onMounted(() => {
  loadComment()
})
</script>

<style scoped>
.page-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
.eval-img-box {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}
.eval-img {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 6px;
}
/* 用户信息行样式，和商品详情统一 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.user-name {
  color: #409EFF;
}
</style>