import axios from 'axios'
import Cookies from 'js-cookie'

const service = axios.create({
  baseURL: 'http://127.0.0.1:8080',
  timeout: 8000
})

//请求拦截器：自动携带token
service.interceptors.request.use(config => {
  const token = Cookies.get('token')
  if (token) {
    config.headers.token = token
  }
  return config
})

//响应拦截器
service.interceptors.response.use(
  (res) => {
    return res.data
  },
  (err) => {
    //未登录401，清除token跳转到登录页
    if (err.response?.status === 401) {
      Cookies.remove('token')
      location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default service