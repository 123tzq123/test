import request from '../utils/request'
import { Result, GoodsCommentVO, SellerHomeVO, CommentPublishDTO, SellerGoodsCommentVO } from '../types'

//提交评价
export function publishCommentApi(data: CommentPublishDTO): Promise<Result<null>> {
  return request({
    method: 'post',
    url: '/comment/publish',
    data
  })
}

//获取商品评价列表
export function getCommentListApi(goodsId: number): Promise<Result<GoodsCommentVO[]>> {
  return request({
    method: 'get',
    url: `/comment/list/${goodsId}`
  })
}

//获取卖家主页信息
export function getSellerHomeApi(sellerId: number): Promise<Result<SellerHomeVO>> {
  return request({
    method: 'get',
    url: `/comment/seller/${sellerId}`
  })
}

//获取卖家所有商品的评价
export function getSellerCommentApi(sellerId: number): Promise<Result<SellerGoodsCommentVO[]>> {
  return request({
    method: 'get',
    url: `/comment/sellerComment/${sellerId}`
  })
}

// 获取当前买家自己发布的所有评价（过往评价页面）
export function getMyCommentApi(): Promise<Result<GoodsCommentVO[]>> {
  return request({
    method: 'get',
    url: '/comment/my'
  })
}