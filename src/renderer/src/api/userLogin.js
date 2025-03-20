import request from '../utils/request'

/**
 * 用户登录
 */
export const userLoginService1 = (loginData) => {
  const params = new URLSearchParams()
  for (let key in loginData){
    params.append(key, loginData[key]);
  }
  return request.post('/login', params)
}
