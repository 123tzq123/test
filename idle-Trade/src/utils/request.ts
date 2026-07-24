// 直接写死后端地址，规避import.meta报错
const baseUrl = "http://10.243.160.88:8080"

const request = {
  get<T = any>(url: string, params?: any): Promise<T> {
    return new Promise((resolve, reject) => {
      uni.request({
        url: baseUrl + url,
        method: 'GET',
        data: params,
        header: {
          token: uni.getStorageSync('token') || ''
        },
        success: res => resolve(res.data as T),
        fail: reject
      })
    })
  },
  post<T = any>(url: string, data?: any): Promise<T> {
    return new Promise((resolve, reject) => {
      uni.request({
        url: baseUrl + url,
        method: 'POST',
        data,
        header: {
          token: uni.getStorageSync('token') || ''
        },
        success: res => resolve(res.data as T),
        fail: reject
      })
    })
  },
  put<T = any>(url: string, data?: any): Promise<T> {
    return new Promise((resolve, reject) => {
      uni.request({
        url: baseUrl + url,
        method: 'PUT',
        data,
        header: {
          token: uni.getStorageSync('token') || ''
        },
        success: res => resolve(res.data as T),
        fail: reject
      })
    })
  },
  delete<T = any>(url: string, data?: any): Promise<T> {
    return new Promise((resolve, reject) => {
      uni.request({
        url: baseUrl + url,
        method: 'DELETE',
        data,
        header: {
          token: uni.getStorageSync('token') || ''
        },
        success: res => resolve(res.data as T),
        fail: reject
      })
    })
  }
}

export default request