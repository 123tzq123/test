import request from '../utils/request'
import { Result, PageVO, OrderItem } from '../types'

//创建订单
export function createOrderApi(goodsId: number): Promise<Result<null>> {
  return request({
    method: 'post',
    url: '/order/create',
    data: {
      goodsId
    }
  })
}

//取消订单
export function cancelOrderApi(orderId: number): Promise<Result<null>> {
  return request({
    method: 'post',
    url: `/order/cancel/${orderId}`
  })
}

//完成订单
export function finishOrderApi(orderId: number): Promise<Result<null>> {
  return request({
    method: 'post',
    url: `/order/finish/${orderId}`
  })
}

//获取我的订单列表
export function getMyOrderApi(pageNum = 1, pageSize = 10): Promise<Result<PageVO<OrderItem>>> {
  return request({
    method: 'get',
    url: '/order/my',
    params: {
      pageNum,
      pageSize
    }
  })
}

//卖家查看收到的订单
export function getSellerOrderApi(pageNum = 1, pageSize = 10):Promise<Result<PageVO<OrderItem>>>{
    return request({
        method:"get",
        url:"/order/seller",
        params:{
            pageNum,
            pageSize
        }
    })
}