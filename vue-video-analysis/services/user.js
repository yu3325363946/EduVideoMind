import { request } from '../utils/request'

//登录接口
export const userLoginService = ({ id, password }) => {
  return request.post('/api/auth/login', { id, password })
}
