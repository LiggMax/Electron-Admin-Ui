import request from '../utils/request'

/**
 * 用户登录
*/
export const userLoginService1 = (loginData) => {
  const params = new URLSearchParams()
  for (let key in loginData){
    params.append(key, loginData[key]);
  }
  return request.post('/admin/account/login', params)
}

/**
 * 获取用户基本信息
 */
export const getUserInfoService = () => {
  return request.get('/admin/user/info')
}

/**
 * 退出登录
 */
export const userLogoutService = () => {
  return request.get('/admin/user/logout')
}
