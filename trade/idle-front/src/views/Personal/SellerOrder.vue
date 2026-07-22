<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">我卖出的商品订单</h2>
    <el-card class="table-card">
      <el-table :data="orderList" border stripe>
        <el-table-column label="订单编号" prop="orderNo" min-width="160"></el-table-column>
        <el-table-column label="商品图片" width="120">
          <template #default="scope">
            <img
              v-if="scope.row.coverImg"
              :src="scope.row.coverImg"
              class="order-img"
            />
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="goodsTitle" min-width="200"></el-table-column>
        <el-table-column label="商品价格" width="120">
          <template #default="scope">
            <span class="price-text">{{ scope.row.price }}元</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="160">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待买家确认交易</el-tag>
            <el-tag v-if="scope.row.status === 1" type="success">交易完成</el-tag>
            <el-tag v-if="scope.row.status === 2" type="info">订单已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" prop="createTime" width="180"></el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goChat(scope.row)">联系买家</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import { getSellerOrderApi } from '../../api/order'
import { OrderItem } from '../../types'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orderList = ref<OrderItem[]>([])

// 加载卖出订单
const loadData = async () => {
  const res = await getSellerOrderApi(1, 10)
  if (res.code === 200) {
    orderList.value = res.data.records
  }
}

// 跳转到聊天页面
const goChat = (row: OrderItem) => {
  const userIdStr = sessionStorage.getItem('userId')
  if (!userIdStr) {
    ElMessage.warning("请先登录！")
    return
  }
  router.push({
    path: '/personal/message',
    query: {
      targetOtherId: String(row.buyerId),
      targetOtherName: "",
      targetOtherAvatar: ""
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 全站统一页面外层容器（所有个人页面共用） */
.page-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
/* 统一页面标题样式 */
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
/* 表格外层卡片 */
.table-card {
  border-radius: 12px;
  padding: 24px;
}
/* 表格内商品图片 */
.order-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}
/* 价格红色文字，和全站商品价格统一 */
.price-text {
  font-size: 16px;
  color: #f53f3f;
  font-weight: 500;
}
</style>