<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">收到买家评价</h2>
    <el-card class="table-card">
      <el-table :data="receiveCommentList" border stripe>
        <el-table-column label="商品名称" prop="goodsTitle" min-width="200"></el-table-column>
        <el-table-column label="买家昵称" prop="buyerNickname" width="140"></el-table-column>
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
import Cookies from 'js-cookie'
import NavBar from '../../components/NavBar.vue'
import { getSellerCommentApi } from '../../api/comment'

const receiveCommentList = ref<any[]>([])
const loginUserId = Number(Cookies.get('userId') || '0')

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
          buyerNickname: c.buyerInfo?.nickname ?? "匿名用户",
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
/* 新增图片布局样式 */
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
</style>