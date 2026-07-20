<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">我的发布</h2>

    <el-card class="table-card">
      <el-table :data="goodsList" border stripe>
        <el-table-column label="商品标题" prop="title" min-width="200"></el-table-column>
        <el-table-column label="售价" width="120">
          <template #default="scope">
            <span class="price-text">{{ scope.row.price ?? 0 }}元</span>
          </template>
        </el-table-column>
        <el-table-column label="浏览量" width="100">
          <template #default="scope">
            {{ scope.row.viewCount ?? 0 }}次
          </template>
        </el-table-column>
        <el-table-column label="商品状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">已上架</el-tag>
            <el-tag v-if="scope.row.status === 0" type="info">已下架</el-tag>
            <el-tag v-if="scope.row.status === 3" type="danger">已售出</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="420">
          <template #default="scope">
            <el-button v-if="scope.row.status === 1" type="danger" size="small" @click="offSale(scope.row.id)">下架</el-button>
            <template v-if="scope.row.status === 0">
              <el-button type="success" size="small" @click="onSale(scope.row.id)">上架</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑商品</el-button>
            <el-button type="info" size="small" @click="toDetail(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!--编辑商品弹窗-->
    <el-dialog v-model="editDialogVisible" title="编辑闲置商品" width="620px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="商品标题">
          <el-input v-model="editForm.title" size="large" placeholder="请输入商品标题"></el-input>
        </el-form-item>
        <el-form-item label="商品价格">
          <el-input v-model.number="editForm.price" size="large" placeholder="请输入售价"></el-input>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="editForm.content" type="textarea" rows="4" size="large" placeholder="描述商品成色、使用时长等信息"></el-input>
        </el-form-item>
        <el-form-item label="商品图片">
          <!-- 已上传图片预览 -->
          <div class="img-box">
            <div class="img-item" v-for="(img,index) in imgList" :key="index">
              <img :src="img" />
              <el-icon class="del-icon" @click="deleteImg(index)"><Delete /></el-icon>
            </div>
          </div>
          <!-- 继续上传图片 -->
          <el-upload :http-request="uploadImg" list-type="picture-card" :limit="6">
            <template #default>
              <el-icon><Plus /></el-icon>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="large" @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" size="large" @click="submitEdit">保存修改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref,onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete,Plus } from '@element-plus/icons-vue'
import NavBar from '../../components/NavBar.vue'
import { getMyGoodsApi,offSaleApi,onSaleApi,deleteGoodsApi,getGoodsByIdApi,updateGoodsApi,uploadImgApi } from '../../api/goods'
import { GoodsItem, GoodsUpdateDTO } from '../../types'
import { useRouter } from 'vue-router'

const router = useRouter()
const goodsList = ref<GoodsItem[]>([])
const editDialogVisible = ref(false)

//编辑表单
const editForm = ref<GoodsUpdateDTO>({
  id:0,
  title:'',
  price:null,
  content:'',
  goodsImg:''
})
//图片数组
const imgList = ref<string[]>([])

//加载我的商品列表
const loadMyGoods = async ()=>{
  const res = await getMyGoodsApi(1,10)
  if(res.code === 200){
    goodsList.value = res.data.records
  }
}

//打开编辑弹窗，回显数据
const openEditDialog = async (row:GoodsItem)=>{
  const res = await getGoodsByIdApi(row.id)
  if(res.code === 200){
    const goods = res.data
    editForm.value.id = goods.id
    editForm.value.title = goods.title
    editForm.value.price = goods.price
    editForm.value.content = goods.content ?? ''
    //拆分图片
    if(goods.goodsImg){
      imgList.value = goods.goodsImg.split(",")
    }else{
      imgList.value = []
    }
    editDialogVisible.value = true
  }
}

//上传图片
const uploadImg = async (options:{file:File})=>{
  const res = await uploadImgApi(options.file)
  if(res.code === 200){
    imgList.value.push(res.data)
    ElMessage.success("图片上传成功")
  }
}

//删除单张图片
const deleteImg = (index:number)=>{
  imgList.value.splice(index,1)
}

//提交编辑
const submitEdit = async ()=>{
  //把图片数组拼接成逗号分隔字符串
  editForm.value.goodsImg = imgList.value.join(",")
  const res = await updateGoodsApi(editForm.value)
  if(res.code === 200){
    ElMessage.success("修改成功")
    editDialogVisible.value = false
    loadMyGoods() //刷新列表
  }
}
//下架
const offSale = async (id: number) => {
  const res = await offSaleApi(id)
  if (res.code === 200) {
    ElMessage.success('下架成功')
    loadMyGoods()
  }
}

//上架
const onSale = async (id: number) => {
  const res = await onSaleApi(id)
  if (res.code === 200) {
    ElMessage.success('上架成功')
    loadMyGoods()
  }
}

//删除，弹出确认框
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定永久删除商品吗？删除之后数据不可恢复！', '警告', {
    type: 'warning'
  }).then(async () => {
    const res = await deleteGoodsApi(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadMyGoods()
    }
  }).catch(() => {})
}
//跳转到商品详情页
const toDetail = (id:number)=>{
  router.push(`/goods/${id}`)
}

onMounted(()=>{
  loadMyGoods()
})
</script>

<style scoped>
/* 全站统一页面外层 */
.page-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
/* 页面标题统一样式 */
.page-title {
  font-size: 22px;
  margin: 0 0 24px;
  color: #303133;
  border-left: 4px solid #409EFF;
  padding-left: 12px;
}
/* 顶部跳转按钮组 */
.btn-nav-group {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
/* 表格卡片容器 */
.table-card {
  border-radius: 12px;
  padding: 24px;
}
/* 表格价格文字红色 */
.price-text {
  font-size: 16px;
  color: #f53f3f;
  font-weight: 500;
}
/* 图片预览区域 */
.img-box{
  display:flex;
  gap:12px;
  flex-wrap:wrap;
  margin-bottom:16px;
}
.img-item{
  width:80px;
  height:80px;
  position:relative;
  border-radius: 6px;
  overflow: hidden;
}
.img-item img{
  width:100%;
  height:100%;
  object-fit:cover;
}
.del-icon{
  position:absolute;
  top:2px;
  right:2px;
  background:#fff;
  cursor:pointer;
  border-radius: 50%;
}
/* 弹窗底部按钮居中 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>