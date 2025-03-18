// 登录API
export const login = (username, password) => {
  // TODO: 实现实际的登录API调用
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        success: true,
        data: {
          token: 'mock-token',
          user: {
            id: 1,
            username,
            role: 'admin'
          }
        }
      })
    }, 1000)
  })
}

// 登出API
export const logout = () => {
  // TODO: 实现实际的登出API调用
  return Promise.resolve()
}
