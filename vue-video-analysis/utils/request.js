import axios from 'axios'
import { ElMessage } from 'element-plus'

// 后端地址（根据你实际情况确认端口，默认是 Spring Boot 8080）
const baseURL = 'http://localhost:8080'

const instance = axios.create({
  baseURL,
  timeout: 0,     // 0 = 不超时
})

instance.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const response = err.response
    const data = response?.data

    // 如果其实是业务成功
    if (data?.code === 200) {
      return data
    }

    // 真正网络错误
    if (!response) {
      // ElMessage.error('网络连接失败，请检查网络')
      console.log('网络连接失败，请检查网络')

      return Promise.reject({
        message: '网络连接失败',
        httpStatus: null,
      })
    }

    ElMessage.error(`服务器错误：${response.status}`)
    return Promise.reject({
      ...data,
      httpStatus: response.status,
    })
  },
)

export default instance
export { baseURL }
