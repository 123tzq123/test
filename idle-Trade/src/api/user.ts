import request from '../utils/request'
import { Result, LoginData, UserLoginDTO } from '../types'

//登录接口
export function loginApi(data: UserLoginDTO): Promise<Result<LoginData>> {
  return request.post('/user/login', data)
}

//注册接口
export function registerApi(data: UserLoginDTO): Promise<Result<null>> {
  return request.post('/user/register', data)
}

//获取当前登录用户信息
export function getUserInfoApi() {
  return request.get('/user/getInfo')
}

//更新昵称和头像
export function updateUserInfoApi(data: { nickname: string; avatar: string }) {
  return request.put('/user/updateInfo', data)
}