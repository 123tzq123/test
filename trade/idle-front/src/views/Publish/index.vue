<template>
  <NavBar></NavBar>
  <div style="padding:40px">
    <h2>发布闲置商品</h2>
    <el-form label-width="80px" :model="publishForm">
      <el-form-item label="商品标题">
        <el-input v-model="publishForm.title" placeholder="输入商品标题"></el-input>
      </el-form-item>
      <el-form-item label="商品价格">
        <el-input v-model.number="publishForm.price" placeholder="输入价格"></el-input>
      </el-form-item>
      <el-form-item label="商品分类">
        <el-select v-model="publishForm.categoryId" placeholder="选择分类">
          <!--后续你有分类数据再替换下拉选项，现在默认写死1也可以-->
          <el-option label="闲置数码" :value="1"></el-option>
          <el-option label="书籍资料" :value="2"></el-option>
          <el-option label="生活用品" :value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品图片">
        <!-- 预览已经上传的图片 -->
        <div class="img-box">
          <div class="img-item" v-for="(img, index) in imgList" :key="index">
            <img :src="img" alt="">
            <el-icon class="del-icon" @click="deleteImg(index)">
              <Delete />
            </el-icon>
          </div>
        </div>
        <!-- 图片上传组件，最多6张 -->
        <el-upload
          :http-request="uploadImg"
          list-type="picture-card"
          :limit="6"
        >
          <template #default>
            <el-icon><Plus /></el-icon>
          </template>
        </el-upload>
        <div style="font-size:12px;color:#999;margin-top:6px;">最多上传6张图片，第一张为商品封面</div>
      </el-form-item>
      <el-form-item label="商品描述">
        <el-input v-model="publishForm.content" type="textarea" rows="4" placeholder="描述商品情况"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">发布商品</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import NavBar from '../../components/NavBar.vue'
import { publishGoodsApi, uploadImgApi } from '../../api/goods'
import { GoodsPublishDTO } from '../../types'
import { useRouter } from 'vue-router'

const router = useRouter()
// 表单数据
const publishForm = ref<GoodsPublishDTO>({
  title: '',
  price: 0,
  content: '',
  categoryId: 1,
  goodsImg: ''
})
// 图片URL数组
const imgList = ref<string[]>([])

// 上传图片
const uploadImg = async (options: { file: File }) => {
  const res = await uploadImgApi(options.file)
  if (res.code === 200) {
    imgList.value.push(res.data)
    ElMessage.success('图片上传成功')
  }
}

// 删除选中的某一张图片
const deleteImg = (index: number) => {
  imgList.value.splice(index, 1)
}

// 提交发布商品
const submit = async () => {
  //简单前端校验
  if (!publishForm.value.title) {
    ElMessage.warning('标题不能为空');
    return
  }
  if (!publishForm.value.price || publishForm.value.price <= 0) {
    ElMessage.warning('价格必须大于0');
    return
  }
  // 将图片数组拼接成逗号分隔字符串传给后端
  publishForm.value.goodsImg = imgList.value.join(",")
  const res = await publishGoodsApi(publishForm.value)
  if (res.code === 200) {
    ElMessage.success('发布成功')
    router.push('/home')
  }
}
</script>

<style scoped>
.img-box {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}
.img-item {
  width: 80px;
  height: 80px;
  position: relative;
}
.img-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.del-icon {
  position: absolute;
  top: 2px;
  right: 2px;
  background-color: #fff;
  cursor: pointer;
}
</style>