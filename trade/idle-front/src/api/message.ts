import request from '../utils/request'
import { Result, PageVO, MessageItem, ChatSessionItem } from '../types'

// 获取历史聊天记录
export function getHistoryMsgApi(goodsId: number, otherId: number): Promise<Result<PageVO<MessageItem>>> {
  return request({
    method: 'get',
    url: '/message/history',
    params: {
      goodsId,
      otherId
    }
  })
}

// 获取当前用户所有聊天会话（我的消息左侧列表）
export const getChatSessionListApi = async (): Promise<Result<ChatSessionItem[]>> => {
  return await request.get('/message/session/list')
}

// 获取全局总未读消息数量（导航栏红点）
export const getUnreadTotalApi = async (): Promise<Result<number>> => {
  return await request.get('/message/unread/total')
}

export function saveMessageApi(data: {goodsId:number,toUserId:number,content:string}): Promise<Result<MessageItem>> {
  return request.post('/message/save', data)
}