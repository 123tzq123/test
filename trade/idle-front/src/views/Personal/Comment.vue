<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">我的过往评价</h2>
    <el-card class="table-card">
      <el-table :data="commentList" border stripe>
        <el-table-column label="商品名称" prop="goodsTitle" min-width="200"></el-table-column>
        <el-table-column label="评分" width="120">
          <template #default="scope">
            <el-rate v-model="scope.row.score" disabled size="small"></el-rate>
          </template>
        </el-table-column>
        <el-table-column label="评价内容" prop="content" min-width="260"></el-table-column>
        <el-table-column label="评价图片" width="140">
          <template #default="scope">
            <div class="img-row" v-if="scope.row.imgList.length > 0">
              <img v-for="img in scope.row.imgList" :key="img" :src="img" class="table-img" />
            </div>
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column label="评价时间" prop="createTime" width="180"></el-table-column>
      </el-table>
      <div class="empty-tip" v-if="commentList.length === 0">暂无评价记录</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { getMyCommentApi } from '../../api/comment'
import { GoodsCommentVO } from '../../types'

const commentList = ref<GoodsCommentVO[]>([])

const loadComment = async () => {
  const res = await getMyCommentApi()
  if(res.code === 200) {
    // 兜底null字段，避免渲染报错
    const safeArr = (res.data ?? []).map(item => {
      return {
        ...item,
        imgList: item.imgList ?? [],
        buyerInfo: item.buyerInfo ?? { id:0, nickname:"匿名用户", avatar:"" }
      }
    })
    commentList.value = safeArr
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
.table-card {
  border-radius: 12px;
  padding: 24px;
}
.table-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  margin: 0 4px 4px 0;
}
.empty-tip {
  text-align: center;
  padding: 60px 0;
  font-size: 16px;
  color: #909399;
}
.img-row {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
</style>