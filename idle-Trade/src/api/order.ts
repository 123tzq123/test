import request from '../utils/request'
import { Result, PageVO, OrderItem } from '../types'

//创建订单
export function createOrderApi(goodsId: number): Promise<Result<null>> {
  return request.post<Result<null>>('/order/create', {
    goodsId
  })
}

//取消订单
export function cancelOrderApi(orderId: number): Promise<Result<null>> {
  return request.post<Result<null>>(`/order/cancel/${orderId}`)
}

//完成订单
export function finishOrderApi(orderId: number): Promise<Result<null>> {
  return request.post<Result<null>>(`/order/finish/${orderId}`)
}

//获取我的订单列表（买家订单）
export function getMyOrderApi(pageNum = 1, pageSize = 10): Promise<Result<PageVO<OrderItem>>> {
  return request.get<Result<PageVO<OrderItem>>>('/order/my', {
    pageNum,
    pageSize
  })
}

//卖家查看收到的订单
export function getSellerOrderApi(pageNum = 1, pageSize = 10): Promise<Result<PageVO<OrderItem>>> {
  return request.get<Result<PageVO<OrderItem>>>('/order/seller', {
    pageNum,
    pageSize
  })
}