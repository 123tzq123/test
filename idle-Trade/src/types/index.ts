//商品条目
export interface GoodsItem {
  id: number
  title: string
  price: number | null
  content: string | null
  categoryId: number
  sellerName: string | null
  userId: number
  avatar: string | null//补充头像字段
  status: number
  goodsImg: string | null
  imgList: string[] //所有图片数组
  coverImg: string | null //封面图片（第一张）
  viewCount?: number
}
//分页通用结构 Mybatis‑Plus返回格式
export interface PageVO<T> {
  records: T[]
  total: number
  current: number
  size: number
}
//登录返回data类型（原来字段是id，改成userId）
export interface LoginData {
  token: string
  userId: number  //把id修改为userId，和后端返回字段一致
  username: string
}
export interface GoodsPublishDTO {
  categoryId: number
  title: string
  content?: string
  price: number
  originalPrice?: number
  imgList: string[] //替换原来的 goodsImg:string
}
//后端统一返回结果
export interface Result<T = any> {
  code: number
  msg: string
  data: T
}
//用户登录接口
export interface UserLoginDTO {
  username: string
  password: string
}
export interface GoodsUpdateDTO {
  id: number
  title: string
  price: number | null
  content: string
  goodsImg: string  //多张图片逗号分隔
}
export interface MessageItem {
  id: number
  goodsId: number
  fromUserId: number
  toUserId: number
  content: string
  createTime: string
  // 可选，后端分页历史接口初次加载可能无值
  fromUserAvatar?: string;
  readStatus?: number;
}
export interface SysUser {
  id: number
  username: string
  nickname: string
  avatar: string
}
export interface GoodsCategory {
  id: number
  categoryName: string
  sort: number
  createTime: string
}
//收藏DTO
export interface CollectDTO {
  goodsId: number
  userId: number
}
export interface CommentPublishDTO {
  orderId: number
  score: number
  content?: string
  imgList: string[]
}
//商品评价返回项（商品详情页展示）
export interface GoodsCommentVO {
  id: number
  goodsId: number
  score: number
  content: string | null
  imgList: string[]
  createTime: string
  // 新增：商品标题，联查直接返回
  goodsTitle?: string
  //评价者信息
  buyerInfo: SysUser
  // 新增：商品卖家信息
  goodsSeller?: SysUser
}
//卖家主页返回数据
export interface SellerHomeVO {
  sellerInfo: SysUser
  avgScore: number //平均分
  goodsList: GoodsItem[] //卖家发布的商品列表
}
export interface OrderItem {
  id: number
  orderNo: string
  goodsId: number
  buyerId: number
  sellerId: number
  price: number
  status: number
  createTime: string
  goodsTitle: string
  goodsImg: string
  coverImg: string | null
  isComment: boolean //新增：true=已评价 false=未评价
}
export interface SellerGoodsCommentVO {
  goodsId: number
  goodsTitle: string
  coverImg: string | null
  commentList: GoodsCommentVO[]
}
// 聊天会话列表（我的消息页面左侧）
export interface ChatSessionItem {
  otherId: number; // 对方用户ID
  otherAvatar: string; // 对方头像
  otherName: string; // 对方昵称
  lastMsg: string; // 最后一条消息内容
  lastMsgTime: string; // 最后消息时间
  unreadNum: number; // 当前会话未读数量
  goodsId: number; // 关联商品ID，0=无商品聊天
}
// ===================== AI助手新增类型 =====================
// AI对话单条消息
export interface AiMsg {
  type: 'user' | 'ai'
  content: string
}
// AI问答接口返回data结构
export interface AiChatData {
  answer: string
}
// AI识物返回结构
export interface AiGoodsDetectData {
  title: string
  categoryId: number
  price: number
  content: string
}