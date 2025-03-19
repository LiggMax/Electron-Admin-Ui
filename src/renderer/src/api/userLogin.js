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
