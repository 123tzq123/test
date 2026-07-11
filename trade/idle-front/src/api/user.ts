import request from '../utils/request'
import { Result, LoginData } from '../types'

interface LoginForm {
  username: string
  password: string
}

//登录接口
export function loginApi(data: LoginForm): Promise<Result<LoginData>> {
  return request({
    method: 'post',
    url: '/user/login',
    data
  })
}