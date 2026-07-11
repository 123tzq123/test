//商品条目
export interface GoodsItem {
  id: number
  title: string
  price: number | null
  imgUrl: string | null
  description: string | null
  categoryId: number
  sellerName: string | null
  userId: number
  status: number
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
  description: string
  categoryId: number
  imgUrl: string
}

//后端统一返回结果
export interface Result<T = any> {
  code: number
  msg: string
  data: T
}