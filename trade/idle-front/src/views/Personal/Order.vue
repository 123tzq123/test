<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">我的订单列表</h2>
    <el-card class="table-card">
      <el-table :data="orderList" border stripe>
        <el-table-column label="订单编号" prop="orderNo" min-width="160"></el-table-column>
        <el-table-column label="商品图片" width="120">
          <template #default="scope">
            <img v-if="scope.row.coverImg" :src="scope.row.coverImg" class="order-img" />
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="goodsTitle" min-width="200"></el-table-column>
        <el-table-column label="商品价格" width="120">
          <template #default="scope">
            <span class="price-text">{{ scope.row.price }}元</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="140">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待确认交易</el-tag>
            <el-tag v-if="scope.row.status === 1" type="success">交易完成</el-tag>
            <el-tag v-if="scope.row.status === 2" type="info">订单已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180"></el-table-column>
        <el-table-column label="操作" width="480">
          <template #default="scope">
            <el-button v-if="scope.row.status === 0" type="success" size="small" @click="finish(scope.row.id)">确认完成交易</el-button>
            <el-button v-if="scope.row.status === 0" type="danger" size="small" @click="cancel(scope.row.id)">取消订单</el-button>
            <el-button type="primary" size="small" @click="goChat(scope.row)">联系卖家</el-button>
            <!-- 交易完成并且未评价，展示去评价按钮 -->
            <el-button
              v-if="scope.row.status === 1 && !scope.row.isComment"
              type="warning"
              size="small"
              @click="openCommentDialog(scope.row.id)"
            >
              去评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!--评价弹窗-->
    <el-dialog v-model="commentDialog.visible" title="商品评价" width="540px">
      <el-form label-width="90px">
        <el-form-item label="商品评分">
          <el-rate v-model="commentForm.score" size="large"></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="commentForm.content"
            type="textarea"
            rows="4"
            size="large"
            placeholder="写下你的体验感受"
          ></el-input>
        </el-form-item>
        <el-form-item label="评价图片">
          <el-upload
            action="http://localhost:8080/user/uploadFile"
            :headers="headers"
            list-type="picture-card"
            :on-success="uploadSuccess"
            :limit="6"
          >
            <template #default>
              <el-icon><Plus /></el-icon>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="closeDialog">取消</el-button>
          <el-button type="primary" size="large" @click="submitComment">提交评价</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import NavBar from '../../components/NavBar.vue'
import { getMyOrderApi, cancelOrderApi, finishOrderApi } from '../../api/order'
import { publishCommentApi } from '../../api/comment'
import { ElMessage } from 'element-plus'
import { OrderItem } from '../../types'

const router = useRouter()
const orderList = ref<OrderItem[]>([])
// 携带token请求头，和UserSetting页面保持一致
const headers = ref({
  token: sessionStorage.getItem('token') || ''
})

//评价弹窗数据
const commentDialog = ref({
  visible: false
})
const commentForm = ref({
  orderId: 0,
  score: 0,
  content: '',
  imgList: [] as string[]
})

const loadOrder = async () => {
  const res = await getMyOrderApi(1,10)
  if (res.code === 200) {
    orderList.value = res.data.records
  }
}

//打开评价弹窗，重置表单
const openCommentDialog = (orderId: number) => {
  commentForm.value.orderId = orderId
  commentForm.value.score = 0
  commentForm.value.content = ''
  commentForm.value.imgList = []
  commentDialog.value.visible = true
}

//关闭弹窗重置数据
const closeDialog = ()=>{
  commentDialog.value.visible = false
  commentForm.value.score = 0
  commentForm.value.content = ''
  commentForm.value.imgList = []
}

//图片上传成功回调，拿到图片URL存入imgList数组
const uploadSuccess = (res:{data:string})=>{
  commentForm.value.imgList.push(res.data)
  ElMessage.success("图片上传成功")
}

//提交评价
const submitComment = async () => {
  if(commentForm.value.score === 0){
    ElMessage.warning("请给出评分！")
    return
  }
  const res = await publishCommentApi(commentForm.value)
  if(res.code === 200){
    ElMessage.success("评价提交成功！")
    commentDialog.value.visible = false
    loadOrder() //刷新订单列表，isComment变为true，按钮消失
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

// 跳转到消息页面，联系卖家
const goChat = (row: OrderItem) => {
  const userIdStr = sessionStorage.getItem('userId')
  if (!userIdStr) {
    ElMessage.warning("请先登录！")
    return
  }
  router.push({
    path: '/personal/message',
    query: {
      targetOtherId: String(row.sellerId),
      targetOtherName: "",
      targetOtherAvatar: ""
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
/* 表格卡片容器 */
.table-card {
  border-radius: 12px;
  padding: 24px;
}
/* 订单列表商品图片 */
.order-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}
/* 订单价格红色字体 */
.price-text {
  font-size: 16px;
  color: #f53f3f;
  font-weight: 500;
}
/* 弹窗底部按钮居中 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>