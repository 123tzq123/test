import request from '../utils/request'
import { Result, GoodsCommentVO, SellerHomeVO, CommentPublishDTO, SellerGoodsCommentVO } from '../types'

//提交评价
export function publishCommentApi(data: CommentPublishDTO): Promise<Result<null>> {
  return request.post('/comment/publish', data)
}

//获取商品评价列表
export function getCommentListApi(goodsId: number): Promise<Result<GoodsCommentVO[]>> {
  return request.get(`/comment/list/${goodsId}`)
}

//获取卖家主页信息
export function getSellerHomeApi(sellerId: number): Promise<Result<SellerHomeVO>> {
  return request.get(`/comment/seller/${sellerId}`)
}

//【我收到的评价】别人评价我
export function getReceiveCommentApi(sellerId: number): Promise<Result<SellerGoodsCommentVO[]>> {
  return request.get(`/comment/sellerComment/${sellerId}`)
}

//【我做出的评价】我评价别人
export function getMyCreateCommentApi(): Promise<Result<GoodsCommentVO[]>> {
  return request.get('/comment/my')
}