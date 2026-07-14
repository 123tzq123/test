<template>
  <NavBar></NavBar>
  <div style="padding:30px">
    <h2>我的订单列表</h2>
    <el-table :data="orderList" border>
      <el-table-column label="订单编号" prop="orderNo"></el-table-column>
      <el-table-column label="商品图片">
        <template #default="scope">
          <img v-if="scope.row.coverImg" :src="scope.row.coverImg" style="width:80px;height:80px">
        </template>
      </el-table-column>
      <el-table-column label="商品名称" prop="goodsTitle"></el-table-column>
      <el-table-column label="商品价格">
        <template #default="scope">{{ scope.row.price }}元</template>
      </el-table-column>
      <el-table-column label="订单状态">
        <template #default="scope">
          <span v-if="scope.row.status === 0">待确认交易</span>
          <span v-if="scope.row.status === 1">交易完成</span>
          <span v-if="scope.row.status === 2">订单已取消</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 0" type="success" @click="finish(scope.row.id)">确认完成交易</el-button>
          <el-button v-if="scope.row.status === 0" type="danger" @click="cancel(scope.row.id)">取消订单</el-button>
          <!-- 新增联系卖家按钮 -->
          <el-button type="primary" @click="goChat(scope.row)">联系卖家</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../../components/NavBar.vue'
import { getMyOrderApi, cancelOrderApi, finishOrderApi } from '../../api/order'
import { ElMessage } from 'element-plus'
import { OrderItem } from '../../api/order'

const router = useRouter()
const orderList = ref<OrderItem[]>([])

const loadOrder = async () => {
  const res = await getMyOrderApi(1,10)
  if (res.code === 200) {
    orderList.value = res.data.records
  }
}

//取消订单
const cancel = async (orderId: number) => {
  const res = await cancelOrderApi(orderId)
  if (res.code === 200) {
    ElMessage.success("取消订单成功")
    loadOrder()
  }
}

//确认完成交易
const finish = async (orderId: number) => {
  const res = await finishOrderApi(orderId)
  if (res.code === 200) {
    ElMessage.success("交易完成")
    loadOrder()
  }
}

//跳转到聊天页面，otherId = 卖家id sellerId
const goChat = (row: OrderItem) => {
  router.push({
    path: '/chat',
    query: {
      goodsId: String(row.goodsId),
      otherId: String(row.sellerId)
    }
  })
}

onMounted(() => {
  loadOrder()
  //30秒刷新一次订单状态
  setInterval(()=>{
    loadOrder()
  },30000)
})
</script>