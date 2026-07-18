import request from '../utils/request'
import { Result, LoginData, UserLoginDTO } from '../types'

// interface LoginForm {
//   username: string
//   password: string
// }

//登录接口
export function loginApi(data: UserLoginDTO): Promise<Result<LoginData>> {
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

//获取当前登录用户信息
export function getUserInfoApi() {
  return request({
    method: 'get',
    url: '/user/getInfo'
  })
}

//更新昵称和头像
export function updateUserInfoApi(data: { nickname: string; avatar: string }) {
  return request({
    method: 'put',
    url: '/user/updateInfo',
    data
  })
}