import request from '../utils/request'
import { Result, LoginData, UserLoginDTO } from '../types'

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

//注册接口
export function registerApi(data: UserLoginDTO): Promise<Result<null>> {
  return request({
    method: 'post',
    url: '/user/register',
    data
  })
}