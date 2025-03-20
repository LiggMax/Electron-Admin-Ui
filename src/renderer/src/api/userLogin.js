import request from '../utils/request'


// export const userLoginService = (data) => {
//   // 使用URLSearchParams，适合表单提交
//   const params = new URLSearchParams()
//   params.append('account', data.account)
//   params.append('password', data.password)
//
//   return request.post('/api/login', params, {
//     headers: {
//       'Content-Type': 'application/x-www-form-urlencoded'
//     }
//   })
// }
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
