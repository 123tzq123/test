import request from '../utils/request'
import { Result, GoodsItem, PageVO, GoodsPublishDTO, GoodsUpdateDTO } from '../types'

//获取商品列表
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

// ============ 新增：图片上传接口 ============
export function uploadImgApi(file: File): Promise<Result<string>> {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    method: 'post',
    url: '/file/upload',
    data: formData,
    // 覆盖默认请求头，上传文件必须设置multipart/form‑data
    headers: {
      'Content-Type': 'multipart/form-data'
    }
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