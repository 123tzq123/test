<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">收到买家评价</h2>
    <el-card class="table-card">
      <el-table :data="receiveCommentList" border stripe>
        <el-table-column label="商品名称" prop="goodsTitle" min-width="200"></el-table-column>
        <!-- 替换原有买家昵称列：头像+昵称 可点击跳转 -->
        <el-table-column label="买家信息" width="180">
          <template #default="scope">
            <div class="user-info" v-if="scope.row.buyerInfo" @click="$router.push({path:'/seller',query:{sellerId:scope.row.buyerInfo.id}})">
              <el-avatar :src="scope.row.buyerInfo.avatar" size="40"></el-avatar>
              <span class="user-name">{{ scope.row.buyerInfo.nickname }}</span>
            </div>
            <span v-else>匿名用户</span>
          </template>
        </el-table-column>
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
      <div class="empty-tip" v-if="receiveCommentList.length === 0">暂无买家评价</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NavBar from '../../components/NavBar.vue'
import { getSellerCommentApi } from '../../api/comment'

const receiveCommentList = ref<any[]>([])
const userIdStr = sessionStorage.getItem('userId')
const loginUserId = userIdStr ? Number(userIdStr) : 0

const loadData = async () => {
  const res = await getSellerCommentApi(loginUserId)
  if(res.code === 200) {
    const allComments: any[] = []
    const sellerData = res.data ?? []
    sellerData.forEach((item) => {
      const commentArr = item.commentList ?? []
      commentArr.forEach(c => {
        let imgArr: string[] = []
        if (c.imgList && typeof c.imgList === 'string') {
          imgArr = (c.imgList as string).split(',').filter((url: string) => url.trim())
        }
        allComments.push({
          goodsTitle: item.goodsTitle,
          // 完整保留买家对象，头像昵称id全部携带
          buyerInfo: c.buyerInfo,
          score: c.score,
          content: c.content,
          imgList: imgArr,
          createTime: c.createTime
        })
      })
    })
    receiveCommentList.value = allComments
  }
}

onMounted(() => {
  loadData()
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
.empty-tip {
  text-align: center;
  padding: 60px 0;
  font-size: 16px;
  color: #909399;
}
.img-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.table-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
}
/* 用户头像昵称行样式 */
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