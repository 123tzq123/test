<template>
  <NavBar></NavBar>
  <div class="page-wrap">
    <h2 class="page-title">发布闲置商品</h2>
    <el-card class="form-card">
      <el-form label-width="90px" :model="publishForm">
        <el-form-item label="商品标题">
          <el-input v-model="publishForm.title" size="large" placeholder="输入商品标题"></el-input>
        </el-form-item>
        <el-form-item label="商品价格">
          <el-input v-model.number="publishForm.price" size="large" placeholder="输入价格"></el-input>
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="publishForm.categoryId" placeholder="选择分类" size="large">
            <el-option label="闲置数码" :value="1"></el-option>
            <el-option label="书籍资料" :value="2"></el-option>
            <el-option label="生活用品" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片">
          <div class="img-box">
            <div class="img-item" v-for="(img, index) in imgList" :key="index">
              <img :src="img" alt="">
              <el-icon class="del-icon" @click="deleteImg(index)">
                <Delete />
              </el-icon>
            </div>
          </div>
          <el-upload
            :http-request="uploadImg"
            list-type="picture-card"
            :limit="6"
          >
            <template #default>
              <el-icon><Plus /></el-icon>
            </template>
          </el-upload>
          <div class="tip-text">最多上传6张图片，第一张为商品封面</div>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="publishForm.content" type="textarea" rows="4" size="large" placeholder="描述商品成色、使用时长、配件等信息"></el-input>
        </el-form-item>
        <el-form-item>
          <div class="submit-wrap">
            <el-button type="primary" size="large" class="submit-btn" @click="submit">发布商品</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
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
// 修改重点：移除 goodsImg，增加 imgList
const publishForm = ref<GoodsPublishDTO>({
  title: '',
  price: 0,
  content: '',
  categoryId: 1,
  originalPrice: 0,
  imgList: []
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
  // 直接把图片数组赋值给表单里的 imgList，不再拼接字符串
  publishForm.value.imgList = imgList.value
  const res = await publishGoodsApi(publishForm.value)
  if (res.code === 200) {
    ElMessage.success('发布成功')
    router.push('/')
  }
}
</script>

<style scoped>
/* 全站统一页面外层容器 */
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
/* 表单外层卡片 */
.form-card {
  max-width: 800px;
  margin: 0 auto;
  padding: 36px;
  border-radius: 12px;
}
/* 图片预览区域 */
.img-box {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}
.img-item {
  width: 80px;
  height: 80px;
  position: relative;
  border-radius: 6px;
  overflow: hidden;
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
  border-radius: 50%;
  cursor: pointer;
}
/* 上传提示文字 */
.tip-text {
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
}
/* 提交按钮居中 */
.submit-wrap {
  text-align: center;
  margin-top: 12px;
}
.submit-btn {
  width: 240px;
  border-radius: 8px;
}
</style>