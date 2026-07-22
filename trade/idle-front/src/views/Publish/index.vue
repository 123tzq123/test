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
            <el-option label="电子产品" :value="1"></el-option>
            <el-option label="图书教材" :value="2"></el-option>
            <el-option label="衣物鞋帽" :value="3"></el-option>
            <el-option label="生活用品" :value="4"></el-option>
            <el-option label="运动器材" :value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            :http-request="uploadImg"
            list-type="picture-card"
            :limit="5"
            :file-list="fileList"
            @remove="handleUploadRemove"
          >
            <template #default>
              <el-icon><Plus /></el-icon>
            </template>
          </el-upload>
          <div class="tip-text">最多上传5张图片，第一张为商品封面</div>
          <!-- ===== 新增AI识物按钮 ===== -->
          <el-button type="success" class="ai-detect-btn" @click="openAiDetectDialog">
            🤖 AI识物，图片自动填充商品信息
          </el-button>
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

    <!-- ========== AI识物弹窗 ========== -->
    <el-dialog v-model="aiDetectVisible" @close="handleAiDialogClose"  title="AI识物（最多5张图片）" width="540px">
      <el-upload
        ref="aiUploadRef"
        list-type="picture-card"
        :limit="5"
        :file-list="aiFileList"
        :http-request="aiUploadImg"
        multiple
      >
        <template #default>
          <el-icon><Plus /></el-icon>
        </template>
      </el-upload>
      <div class="tip-text">上传商品照片，AI自动识别并填充表单信息</div>
      <template #footer>
        <el-button @click="aiDetectVisible = false">取消</el-button>
        <el-button type="primary" @click="startAiDetect" :loading="aiDetectLoading">开始识别</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import NavBar from '../../components/NavBar.vue'
import { publishGoodsApi, uploadImgApi, aiDetectGoodsApi } from '../../api/goods'
import { GoodsPublishDTO } from '../../types'
import { useRouter } from 'vue-router'

const handleAiDialogClose = () => {
  aiFileList.value = []
  aiImgUrlList.value = []
}

const router = useRouter()
const publishForm = ref<GoodsPublishDTO>({
  title: '',
  price: 0,
  content: '',
  categoryId: 1,
  originalPrice: 0,
  imgList: []
})
// 业务存储图片URL数组
const imgList = ref<string[]>([])
// el-upload 内置文件列表
const fileList = ref<any[]>([])

// ===================== AI识物相关变量 =====================
const aiDetectVisible = ref(false)
const aiFileList = ref<any[]>([])
// AI上传成功后的图片url集合
const aiImgUrlList = ref<string[]>([])
const aiDetectLoading = ref(false)

// 自定义上传接口（原有商品图片上传）
const uploadImg = async (options: { file: File }) => {
  const res = await uploadImgApi(options.file)
  if (res.code === 200) {
    const url = res.data
    // 同步业务数组
    imgList.value.push(url)
    // 同步upload组件预览列表
    fileList.value.push({
      url: url
    })
    ElMessage.success('图片上传成功')
  }
}

// AI弹窗内图片上传
const aiUploadImg = async (options: { file: File, onSuccess: Function, onError: Function }) => {
  const res = await uploadImgApi(options.file)
  if (res.code === 200) {
    const url = res.data
    aiImgUrlList.value.push(url)
    options.onSuccess()
  } else {
    ElMessage.error("图片上传失败！")
    options.onError()
  }
}

// 打开AI弹窗，清空缓存
const openAiDetectDialog = () => {
  aiDetectVisible.value = true
  aiFileList.value = []
  aiImgUrlList.value = []
}
const startAiDetect = async () => {
  console.log("aiImageUrlList：", aiImgUrlList.value)
  if (aiImgUrlList.value.length === 0) {
    ElMessage.warning("请先上传商品图片！")
    return
  }
  aiDetectLoading.value = true
  try {
    const res = await aiDetectGoodsApi(aiImgUrlList.value)
    if (res.code === 200) {
      const info = res.data
      publishForm.value.title = info.title
      publishForm.value.price = info.price
      publishForm.value.categoryId = info.categoryId
      publishForm.value.content = info.content

      const newFileList = [...fileList.value]
      const newImgList = [...imgList.value]

      // 只添加本次弹窗刚刚上传的图片
      aiImgUrlList.value.forEach(url => {
        if (!newImgList.includes(url)) {
          newImgList.push(url)
          newFileList.push({
            url: url,
            uid: Date.now() + Math.random(),
            status: 'success'
          })
        }
      })
      fileList.value = newFileList
      imgList.value = newImgList

      ElMessage.success("AI识别完成，信息已自动填充！")
      aiDetectVisible.value = false
    }
  } catch (err) {
    ElMessage.error("AI识别失败，请更换图片重试！")
  } finally {
    aiDetectLoading.value = false
  }
}

// el-upload 自带删除回调（删除时同步清理数组）
const handleUploadRemove = (file: any) => {
  const delUrl = file.url
  // 过滤删除对应图片
  imgList.value = imgList.value.filter(item => item !== delUrl)
}

// 提交发布商品
const submit = async () => {
  if (!publishForm.value.title) {
    ElMessage.warning('标题不能为空');
    return
  }
  if (!publishForm.value.price || publishForm.value.price <= 0) {
    ElMessage.warning('价格必须大于0');
    return
  }
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
.ai-detect-btn {
  margin-top: 12px;
}
</style>