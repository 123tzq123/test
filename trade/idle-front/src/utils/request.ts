import axios from 'axios'
import Cookies from 'js-cookie'

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 8000,
  withCredentials: true
})

//请求拦截器：自动把token放到请求头
service.interceptors.request.use(config => {
  const token = Cookies.get('token')
  if (token) {
    //后端读取 header 里面的 token，字段名保持和后端一致（后端是 request.getHeader("token")）
    config.headers['token'] = token
  }
  return config
})

//响应拦截器
service.interceptors.response.use(
  (res) => {
    return res.data
  },
  (err) => {
    //未登录401，清除cookie，跳转到登录页面
    if (err.response?.status === 401) {
      Cookies.remove('token')
      location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default service