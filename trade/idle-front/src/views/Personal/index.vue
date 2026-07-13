<template>
  <NavBar></NavBar>
  <div style="padding:30px 40px">
    <h2>我的发布</h2>
    <el-table :data="goodsList" border>
      <el-table-column label="标题" prop="title"></el-table-column>
      <el-table-column label="价格">
        <template #default="scope">
          {{ scope.row.price ?? 0 }}元
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="scope">
          <span v-if="scope.row.status === 1">已上架</span>
          <span v-if="scope.row.status === 0">已下架</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 1" type="danger" @click="offSale(scope.row.id)">下架</el-button>
          <template v-if="scope.row.status === 0">
            <el-button type="success" @click="onSale(scope.row.id)">上架</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
          <el-button type="primary" @click="openEditDialog(scope.row)">编辑商品</el-button>
          <el-button type="info" @click="toDetail(scope.row.id)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--编辑商品弹窗-->
    <el-dialog v-model="editDialogVisible" title="编辑商品" width="600px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="商品标题">
          <el-input v-model="editForm.title"></el-input>
        </el-form-item>
        <el-form-item label="商品价格">
          <el-input v-model.number="editForm.price"></el-input>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="editForm.content" type="textarea" rows="4"></el-input>
        </el-form-item>
        <el-form-item label="商品图片">
          <!-- 回显已上传图片 -->
          <div class="img-box">
            <div class="img-item" v-for="(img,index) in imgList" :key="index">
              <img :src="img" />
              <el-icon class="del-icon" @click="deleteImg(index)"><Delete /></el-icon>
            </div>
          </div>
          <!--继续上传图片-->
          <el-upload :http-request="uploadImg" list-type="picture-card" :limit="6">
            <template #default>
              <el-icon><Plus /></el-icon>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存修改</el-button>
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
//下架、上架、删除、跳转详情原有代码不变省略
const toDetail = (id:number)=>{
  router.push(`/goods/${id}`)
}

onMounted(()=>{
  loadMyGoods()
})
</script>

<style scoped>
.img-box{
  display:flex;
  gap:10px;
  flex-wrap:wrap;
  margin-bottom:10px;
}
.img-item{
  width:80px;
  height:80px;
  position:relative;
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
}
</style>