import request from '../utils/request'

/**
 * 用户登录
*/
export const userLoginService1 = (loginData) => {
  const params = new URLSearchParams()
  for (let key in loginData){
    params.append(key, loginData[key]);
  }
  return request.post('/merchant/account/login', params)
}

/**
 * 获取用户基本信息
 */
export const getUserInfoService = () => {
  return request.get('/merchant/user/info')
}
/**
 * 获取交易记录
 */
export const getTransactionRecordService = () => {
  return request.get('/merchant/user/order')
}

/**
 * 退出登录
 */
export const userLogoutService = () => {
  return request.get('/merchant/user/logout')
}
