<template>
  <NavBar></NavBar>
  <div class="personal" style="padding:30px 40px">
    <h2>我的发布</h2>
    <el-table :data="goodsList" border class="table-style">
      <el-table-column label="商品标题" prop="title"></el-table-column>
      <el-table-column label="价格">
        <template #default="scope">
          {{ scope.row.price ?? 0 }}元
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="scope">
          <span v-if="Number(scope.row.status) === 1">已上架</span>
          <span v-if="Number(scope.row.status) === 0">已下架</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="Number(scope.row.status) === 1" type="danger" @click="offSale(scope.row.id)">下架</el-button>
          <template v-if="Number(scope.row.status) === 0">
            <el-button type="success" @click="onSale(scope.row.id)">上架</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
          <el-button type="primary" @click="toDetail(scope.row.id)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyGoodsApi, offSaleApi, onSaleApi, deleteGoodsApi } from '../../api/goods'
import { GoodsItem } from '../../types'
import { ElMessage, ElMessageBox } from 'element-plus'
import NavBar from '../../components/NavBar.vue'

const router = useRouter()
const goodsList = ref<GoodsItem[]>([])

const loadMyGoods = async () => {
  const res = await getMyGoodsApi(1, 10)
  if (res.code === 200) {
    goodsList.value = res.data.records
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
const toDetail = (id: number) => {
  router.push(`/goods/${id}`)
}

onMounted(() => {
  loadMyGoods()
})
</script>

<style scoped>
.table-style {
  width:100%;
  margin-top:20px;
}
</style>