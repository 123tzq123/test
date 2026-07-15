import request from '../utils/request'
import { Result, GoodsItem, PageVO, GoodsPublishDTO, GoodsUpdateDTO, GoodsCategory, CollectDTO } from '../types'

//原来简单分页（保留不动，给个人中心页面使用）
export function getGoodsListApi(pageNum = 1, pageSize = 8, categoryId?: number): Promise<Result<PageVO<GoodsItem>>> {
  return request({
    method: 'get',
    url: '/goods/list',
    params: {
      pageNum,
      pageSize,
      categoryId
    }
  })
}

// ============新增接口开始============
//首页多条件筛选商品（分类+价格区间+商品名称搜索）
export function searchGoodsApi(data:{
  categoryId?: number|null
  minPrice?: number|null
  maxPrice?: number|null
  title?: string
  pageNum: number
  pageSize: number
}): Promise<Result<PageVO<GoodsItem>>> {
  return request({
    method: 'post',
    url: '/goods/list',
    data
  })
}

//获取全部商品分类列表
export function getCategoryListApi(): Promise<Result<GoodsCategory[]>> {
  return request({
    method: 'get',
    url: '/goods/category/list'
  })
}
// ============新增接口结束============

//获取商品详情
export function getGoodsDetailApi(goodsId: number): Promise<Result<GoodsItem>> {
  return request({
    method: 'get',
    url: `/goods/detail/${goodsId}`
  })
}

//发布商品
export function publishGoodsApi(data: GoodsPublishDTO): Promise<Result<null>> {
  return request({
    method: 'post',
    url: '/goods/publish',
    data
  })
}

//下架
export function offSaleApi(goodsId: number): Promise<Result<null>> {
  return request({method:'put',url:`/goods/offSale/${goodsId}`})
}

//上架
export function onSaleApi(goodsId: number): Promise<Result<null>> {
  return request({method:'put',url:`/goods/onSale/${goodsId}`})
}

//物理删除商品
export function deleteGoodsApi(goodsId: number): Promise<Result<null>> {
  return request({method:'delete',url:`/goods/delete/${goodsId}`})
}

//获取我的商品
export function getMyGoodsApi(pageNum = 1, pageSize = 8): Promise<Result<PageVO<GoodsItem>>> {
  return request({
    method: 'get',
    url: '/goods/my',
    params: { pageNum, pageSize }
  })
}

//图片上传接口
export function uploadImgApi(file: File): Promise<Result<string>> {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    method: 'post',
    url: '/file/upload',
    data: formData
  })
}

//根据id获取商品详情，编辑页面回显
export function getGoodsByIdApi(id:number):Promise<Result<GoodsItem>>{
  return request({
    method:'get',
    url:`/goods/getById/${id}`
  })
}
//编辑商品提交
export function updateGoodsApi(data:GoodsUpdateDTO):Promise<Result<null>>{
  return request({
    method:'put',
    url:'/goods/update',
    data
  })
}

//收藏或者取消收藏
export function isCollectApi(data: CollectDTO): Promise<Result<boolean>> {
  return request({
    method: 'post',
    url: '/collect/isCollect',
    data
  })
}

export function changeCollectApi(data: CollectDTO): Promise<Result<string>> {
  return request({
    method: 'post',
    url: '/collect/change',
    data
  })
}
//获取我的收藏列表
//获取我的收藏列表
export function getMyCollectApi():Promise<Result<GoodsItem[]>>{
  return request({
    method:'post',
    url:'/collect/myList'
  })
}

