import request from '../utils/request'
import { Result, PageVO, MessageItem } from '../types'

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