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
          <el-button type="primary" @click="goChat(scope.row)">联系卖家</el-button>
          <!-- 交易完成并且未评价，展示去评价按钮 -->
          <el-button
            v-if="scope.row.status === 1 && !scope.row.isComment"
            type="warning"
            @click="openCommentDialog(scope.row.id)"
          >
            去评价
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--评价弹窗-->
    <el-dialog v-model="commentDialog.visible" title="商品评价" width="500px">
      <div class="form-item">
        <p>商品评分：</p>
        <el-rate v-model="commentForm.score"></el-rate>
      </div>
      <div class="form-item" style="margin-top:15px">
        <p>评价内容：</p>
        <el-input
          v-model="commentForm.content"
          type="textarea"
          rows="4"
          placeholder="写下你的体验感受"
        ></el-input>
      </div>
      <!--复用已有OSS上传接口，和修改头像共用后端上传接口-->
      <div style="margin:10px 0">
        <p>上传评价图片：</p>
        <el-upload
  action="http://localhost:8080/user/uploadFile"
  :headers="headers"
  list-type="picture-card"
  :on-success="uploadSuccess"
>
  <template #default>
    <el-icon><Plus /></el-icon>
  </template>
</el-upload>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="submitComment">提交评价</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import Cookies from 'js-cookie'
import NavBar from '../../components/NavBar.vue'
import { getMyOrderApi, cancelOrderApi, finishOrderApi } from '../../api/order'
import { publishCommentApi } from '../../api/comment'
import { ElMessage } from 'element-plus'
import { OrderItem } from '../../types'

const router = useRouter()
const orderList = ref<OrderItem[]>([])
// 携带token请求头，和UserSetting页面保持一致
const headers = ref({
  token: Cookies.get('token') || ''
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