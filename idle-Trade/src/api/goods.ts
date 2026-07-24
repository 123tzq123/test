import request from '../utils/request'
import { Result, GoodsItem, PageVO, GoodsPublishDTO, GoodsUpdateDTO, GoodsCategory, CollectDTO, AiGoodsDetectData } from '../types'

// 基础后端地址（和request.ts统一写死，消除import.meta报错）
const baseUrl = "http://localhost:8080"

//简单分页（个人中心）
export function getGoodsListApi(pageNum = 1, pageSize = 8, categoryId?: number): Promise<Result<PageVO<GoodsItem>>> {
  return request.get<Result<PageVO<GoodsItem>>>('/goods/list', {
    pageNum,
    pageSize,
    categoryId
  })
}

//首页多条件筛选商品
export function searchGoodsApi(data: {
  categoryId?: number | null
  minPrice?: number | null
  maxPrice?: number | null
  title?: string
  pageNum: number
  pageSize: number
}): Promise<Result<PageVO<GoodsItem>>> {
  return request.post<Result<PageVO<GoodsItem>>>('/goods/list', data)
}

//获取全部商品分类
export function getCategoryListApi(): Promise<Result<GoodsCategory[]>> {
  return request.get<Result<GoodsCategory[]>>('/goods/category/list')
}

//商品详情
export function getGoodsDetailApi(goodsId: number): Promise<Result<GoodsItem>> {
  return request.get<Result<GoodsItem>>(`/goods/detail/${goodsId}`)
}

//发布商品
export function publishGoodsApi(data: GoodsPublishDTO): Promise<Result<null>> {
  return request.post<Result<null>>('/goods/publish', data)
}

//下架商品
export function offSaleApi(goodsId: number): Promise<Result<null>> {
  return request.put<Result<null>>(`/goods/offSale/${goodsId}`)
}

//上架商品
export function onSaleApi(goodsId: number): Promise<Result<null>> {
  return request.put<Result<null>>(`/goods/onSale/${goodsId}`)
}

//删除商品
export function deleteGoodsApi(goodsId: number): Promise<Result<null>> {
  return request.delete<Result<null>>(`/goods/delete/${goodsId}`)
}

//我的商品列表
export function getMyGoodsApi(pageNum = 1, pageSize = 8): Promise<Result<PageVO<GoodsItem>>> {
  return request.get<Result<PageVO<GoodsItem>>>('/goods/my', { pageNum, pageSize })
}

//uni-app图片上传（移除import.meta，拼接固定baseUrl）
export function uploadImgApi(tempFilePath: string): Promise<Result<string>> {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${baseUrl}/file/upload`,
      filePath: tempFilePath,
      name: 'file',
      success: (res) => {
        const data = JSON.parse(res.data)
        resolve(data)
      },
      fail: (err) => reject(err)
    })
  })
}

//编辑商品回显详情
export function getGoodsByIdApi(id: number): Promise<Result<GoodsItem>> {
  return request.get<Result<GoodsItem>>(`/goods/getById/${id}`)
}

//编辑商品提交
export function updateGoodsApi(data: GoodsUpdateDTO): Promise<Result<null>> {
  return request.put<Result<null>>('/goods/update', data)
}

//判断是否收藏
export function isCollectApi(data: CollectDTO): Promise<Result<boolean>> {
  return request.post<Result<boolean>>('/collect/isCollect', data)
}

//收藏/取消收藏
export function changeCollectApi(data: CollectDTO): Promise<Result<string>> {
  return request.post<Result<string>>('/collect/change', data)
}

//我的收藏列表
export function getMyCollectApi(): Promise<Result<GoodsItem[]>> {
  return request.post<Result<GoodsItem[]>>('/collect/myList')
}

//AI图片识物
export function aiDetectGoodsApi(imgUrlList: string[]): Promise<Result<AiGoodsDetectData>> {
  return request.post<Result<AiGoodsDetectData>>('/goods/ai/detect', { imgUrlList })
}