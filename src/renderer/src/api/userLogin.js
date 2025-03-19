import request from '../utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录参数
 * @param {string} data.account - 用户账号
 * @param {string} data.password - 用户密码
 * @returns {Promise} - 返回Promise对象
 */
export const userLoginService = (data) => {
  // 使用URLSearchParams，适合表单提交
  const params = new URLSearchParams()
  params.append('account', data.account)
  params.append('password', data.password)

  return request.post('/api/login', params, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// /**
//  * 用户登录 (使用主进程)
//  * 通过Electron主进程发送HTTP请求，完全避开CSP和CORS限制
//  * @param {Object} data - 登录参数
//  * @returns {Promise} - 返回Promise对象
//  */
// export const userLoginWithMainProcess = async (data) => {
//   try {
//     const params = new URLSearchParams()
//     params.append('account', data.account)
//     params.append('password', data.password)
//
//     const result = await window.api.http.request({
//       method: 'POST',
//       url: 'http://127.0.0.1:8090/api/login',
//       data: params.toString(),
//       headers: {
//         'Content-Type': 'application/x-www-form-urlencoded'
//       }
//     })
//
//     // 处理响应结果
//     if (result.statusCode === 200 && result.data.code === 200) {
//       return result.data
//     } else {
//       throw new Error(result.data.message || '登录失败')
//     }
//   } catch (error) {
//     console.error('通过主进程登录失败:', error)
//     throw error
//   }
// }

/**
 * 用户登出
 * @returns {Promise} - 返回Promise对象
 */
export function logout() {
  return request.get('/api/logout')
}

/**
 * 获取用户信息
 * @returns {Promise} - 返回Promise对象
 */
export function getUserInfo() {
  return request.get('/api/userInfo')
}
