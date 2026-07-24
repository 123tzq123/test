import request from '../utils/request'
import { Result, PageVO, MessageItem, ChatSessionItem, AiChatData } from '../types'

// 获取历史聊天记录（必须传商品id+对方用户id）
export function getHistoryMsgApi(goodsId: number, otherId: number): Promise<Result<PageVO<MessageItem>>> {
  return request.get<Result<PageVO<MessageItem>>>('/message/history', {
    goodsId,
    otherId
  })
}

// 获取当前用户所有聊天会话（我的消息左侧列表）
export function getChatSessionListApi(): Promise<Result<ChatSessionItem[]>> {
  return request.get<Result<ChatSessionItem[]>>('/message/session/list')
}

// 获取全局总未读消息数量（导航栏红点）
export function getUnreadTotalApi(): Promise<Result<number>> {
  return request.get<Result<number>>('/message/unread/total')
}

export function saveMessageApi(data: { goodsId: number, toUserId: number, content: string }): Promise<Result<MessageItem>> {
  return request.post<Result<MessageItem>>('/message/save', data)
}

// AI提问接口
export function askAiApi(question: string): Promise<Result<AiChatData>> {
  return request.post<Result<AiChatData>>('/ai/chat', { question })
}