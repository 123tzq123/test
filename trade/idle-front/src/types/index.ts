//商品条目
export interface GoodsItem {
  id: number
  title: string
  price: number | null
  content: string | null
  categoryId: number
  sellerName: string | null
  userId: number
  status: number
  goodsImg: string | null
  imgList: string[] //所有图片数组
  coverImg: string | null //封面图片（第一张）
}

//分页通用结构 Mybatis‑Plus返回格式
export interface PageVO<T> {
  records: T[]
  total: number
  current: number
  size: number
}

//登录返回data类型
export interface LoginData {
  token: string
  id: number
  username: string
}

//发布商品参数
export interface GoodsPublishDTO {
  title: string
  price: number
  content: string
  categoryId: number
  goodsImg: string
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
}

export interface SysUser {
  id: number
  username: string
  nickname: string
  avatar: string
}