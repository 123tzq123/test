<template>
  <NavBar></NavBar>
  <div class="publish">
    <el-card style="width:600px;margin:20px auto;padding:30px">
      <h2>发布闲置商品</h2>
      <el-form :model="form">
        <el-form-item label="商品标题">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model.number="form.price"></el-input>
        </el-form-item>
        <el-form-item label="分类id">
          <el-input v-model.number="form.categoryId"></el-input>
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imgUrl"></el-input>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea"></el-input>
        </el-form-item>
        <el-button type="primary" @click="submit">提交发布</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { publishGoodsApi } from '../../api/goods'
import { ElMessage } from 'element-plus'
import NavBar from '../../components/NavBar.vue'

const router = useRouter()
const form = ref({
  title: '',
  price: 0,
  categoryId: 1,
  imgUrl: '',
  description: ''
})

const submit = async () => {
  const res = await publishGoodsApi(form.value)
  if (res.code === 200) {
    ElMessage.success('发布成功')
    router.push('/personal')
  } else {
    ElMessage.error(res.msg)
  }
}
</script>