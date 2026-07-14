<template>
  <NavBar></NavBar>
  <div style="padding:30px">
    <h2>我卖出的商品订单</h2>
    <el-table :data="orderList" border>
      <el-table-column label="订单编号" prop="orderNo"></el-table-column>
      <el-table-column label="商品图片">
        <template #default="scope">
          <img
            v-if="scope.row.coverImg"
            :src="scope.row.coverImg"
            style="width:80px;height:80px"
          />
        </template>
      </el-table-column>
      <el-table-column label="商品名称" prop="goodsTitle"></el-table-column>
      <el-table-column label="商品价格">
        <template #default="scope">{{ scope.row.price }}元</template>
      </el-table-column>
      <el-table-column label="订单状态">
        <template #default="scope">
          <span v-if="scope.row.status === 0">待买家确认交易</span>
          <span v-if="scope.row.status === 1">交易完成</span>
          <span v-if="scope.row.status === 2">订单已取消</span>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" prop="createTime"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <!-- ============新增联系买家按钮 ============ -->
          <el-button type="primary" @click="goChat(scope.row)">联系买家</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import { getSellerOrderApi } from '../../api/order'
import { OrderItem } from '../../api/order'

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
  router.push({
    path: '/chat',
    query: {
      goodsId: String(row.goodsId),
      otherId: String(row.buyerId)
    }
  })
}

onMounted(() => {
  loadData()
})
</script>