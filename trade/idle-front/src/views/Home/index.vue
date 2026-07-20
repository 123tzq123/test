<template>
  <NavBar></NavBar>
  <div class="home-wrap">
    <!--筛选栏-->
    <div class="filter-bar">
      <el-select v-model="query.categoryId" placeholder="全部分类" clearable @change="loadList" size="large">
        <el-option label="全部分类" :value="null"></el-option>
        <el-option v-for="cate in categoryList" :key="cate.id" :label="cate.categoryName" :value="cate.id"></el-option>
      </el-select>
      <div class="price-input">
        <el-input v-model="query.minPrice" placeholder="最低价格" size="large"></el-input>
        <span class="line">—</span>
        <el-input v-model="query.maxPrice" placeholder="最高价格" size="large"></el-input>
      </div>
      <el-input v-model="query.title" placeholder="输入商品名称搜索" clearable @keyup.enter="loadList" size="large"></el-input>
      <el-button type="primary" size="large" @click="loadList">搜索</el-button>
      <el-button size="large" @click="resetCondition">清空筛选</el-button>
    </div>

    <!--分页标题行-->
    <div class="page-header">
      <h2 class="page-title">闲置商品列表（共{{total}}条，共{{pageCount}}页）</h2>
      <div v-if="pageCount > 1" class="page-btn-group">
        <el-button size="large" :disabled="currentPage === 1" @click="currentPage--; loadList()">上一页</el-button>
        <span class="page-text">第 {{ currentPage }} / {{ pageCount }} 页</span>
        <el-button size="large" :disabled="currentPage === pageCount" @click="currentPage++; loadList()">下一页</el-button>
      </div>
    </div>

    <!--商品网格列表-->
    <el-row :gutter="24" class="goods-grid">
      <el-col :span="6" v-for="item in goodsList" :key="item.id">
        <el-card shadow="hover" class="goods-card">
          <img 
          v-if="item.coverImg ?? ''" 
          :src="item.coverImg ?? ''" 
          class="goods-img" 
          alt="商品封面"
          />
          <div v-else class="empty-img">暂无商品图</div>
          <h3 class="goods-title">{{ item.title }}</h3>
          <p class="goods-price">¥{{ item.price ?? 0 }}</p>
          <p class="view-text">浏览 {{ item.viewCount ?? 0 }} 次</p>
          <el-button type="primary" size="large" class="detail-btn" @click="router.push('/goods/' + item.id)">查看详情</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, onBeforeRouteUpdate } from 'vue-router'
import { searchGoodsApi, getCategoryListApi } from '../../api/goods'
import { GoodsItem, GoodsCategory, Result, PageVO } from '../../types'
import NavBar from '../../components/NavBar.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const goodsList = ref<GoodsItem[]>([])
const categoryList = ref<GoodsCategory[]>([])
const total = ref(0)
const currentPage = ref(1)

const query = ref({
  categoryId: null as number | null,
  minPrice: '' as string,
  maxPrice: '' as string,
  title: '',
  pageNum: 1,
  pageSize: 8
})

// 计算总页数
const pageCount = computed(() => {
  return Math.ceil(total.value / query.value.pageSize)
})

const loadCategory = async () => {
  const res = await getCategoryListApi() as unknown as Result<GoodsCategory[]>
  if (res.code === 200) {
    categoryList.value = res.data
  }
}

// 统一加载列表：新增参数resetPage，筛选时重置到第一页
const loadList = async (resetPage: boolean = false) => {
  // 筛选、切换分类、搜索时重置页码
  if (resetPage) {
    currentPage.value = 1
  }

  let min: number | null = null
  let max: number | null = null

  if (query.value.minPrice.trim() !== '') {
    if (!/^\d+(\.\d+)?$/.test(query.value.minPrice)) {
      ElMessage.warning('最低价格只能输入数字！')
      return
    }
    min = Number(query.value.minPrice)
  }
  if (query.value.maxPrice.trim() !== '') {
    if (!/^\d+(\.\d+)?$/.test(query.value.maxPrice)) {
      ElMessage.warning('最高价格只能输入数字！')
      return
    }
    max = Number(query.value.maxPrice)
  }

  if (min !== null && max !== null) {
    if (min > max) {
      ElMessage.warning('最低价格不能大于最高价格！')
      return
    }
  }

  const sendData = {
    categoryId: query.value.categoryId,
    minPrice: min,
    maxPrice: max,
    title: query.value.title,
    pageNum: currentPage.value,
    pageSize: query.value.pageSize
  }
  const res = await searchGoodsApi(sendData) as unknown as Result<PageVO<GoodsItem>>
  // 核心：拆分goodsImg逗号字符串，取第一张作为coverImg
  goodsList.value = res.data.records.map(item => {
    let coverImg = ''
    if (item.goodsImg && item.goodsImg.trim() !== '') {
      // 按逗号分割，取第一个图片链接
      const imgArr = item.goodsImg.split(',').filter(s => s.trim())
      coverImg = imgArr.length > 0 ? imgArr[0] : ''
    }
    return {
      ...item,
      coverImg: coverImg
    }
  })
  total.value = res.data.total
}

//重置筛选条件
const resetCondition = () => {
  query.value.categoryId = null
  query.value.minPrice = ''
  query.value.maxPrice = ''
  query.value.title = ''
  currentPage.value = 1
  loadList()
  ElMessage.success('筛选条件已清空')
}

onMounted(async () => {
  await loadCategory();
  const flag = localStorage.getItem("needRefreshGoods");
  if (flag === "1") {
    currentPage.value = 1;
    await loadList();
    localStorage.removeItem("needRefreshGoods");
  } else {
    await loadList();
  }
})

onBeforeRouteUpdate(() => {
  currentPage.value = 1
  loadList()
})
</script>

<style scoped>
.home-wrap {
  padding: 24px 48px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}
.filter-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;
}
.price-input {
  display: flex;
  align-items: center;
  gap: 10px;
}
.line {
  color: #909399;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-title {
  font-size: 20px;
  margin: 0;
  color: #303133;
}
.page-btn-group {
  display: flex;
  align-items: center;
  gap: 12px;
}
.page-text {
  font-size: 15px;
  color: #606266;
}
.goods-grid {
  row-gap: 24px;
}
.goods-card {
  border-radius: 12px;
  overflow: hidden;
}
/* 统一和卖家主页商品封面样式 */
.goods-img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}
/* 无图片兜底样式，和页面视觉统一 */
.empty-img {
  width: 100%;
  height: 140px;
  background: #f0f2f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14;
}
.goods-title {
  font-size: 16px;
  margin: 12px 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.goods-price {
  font-size: 20px;
  color: #f53f3f;
  font-weight: 600;
  margin: 0 0 6px;
}
.view-text {
  font-size: 13px;
  color: #909399;
  margin: 0 0 16px;
}
.detail-btn {
  width: 100%;
  border-radius: 8px;
}
</style>