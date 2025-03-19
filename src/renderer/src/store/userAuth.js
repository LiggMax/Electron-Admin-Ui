// import { defineStore } from 'pinia'
// import { login, logout, getUserInfo } from '../api/userLogin'
//
// export const useAuthStore = defineStore('auth', {
//   state: () => ({
//     token: null,
//     user: null,
//      loading: false,
//     error: null
//   }),
//   getters: {
//     isAuthenticated: (state) => !!state.token,
//     getToken: (state) => state.token,
//     getUser: (state) => state.user
//   },
//   actions: {
//     /**
//      * 设置token
//      * @param {string} token - JWT令牌
//      */
//     setToken(token) {
//       this.token = token
//     },
//
//     /**
//      * 设置用户信息
//      * @param {Object} user - 用户信息对象
//      */
//     setUser(user) {
//       this.user = user
//     },
//
//     /**
//      * 清除认证信息
//      */
//     clearAuth() {
//       this.token = null
//       this.user = null
//       this.error = null
//     },
//
//     /**
//      * 用户登录
//      * @param {Object} credentials - 登录凭证
//      * @param {string} credentials.account - 用户账号
//      * @param {string} credentials.password - 用户密码
//      * @returns {Promise} - 返回Promise对象
//      */
//     async loginUser(credentials) {
//       this.loading = true
//       this.error = null
//
//       try {
//         const response = await login(credentials)
//
//         // 登录响应处理
//         if (response && response.data) {
//           // 保存token
//           this.setToken(response.data.token)
//
//           // 如果响应中直接包含用户信息，则保存
//           if (response.data.user) {
//             this.setUser(response.data.user)
//           } else {
//             // 否则，可以再次请求获取用户信息
//             await this.fetchUserInfo()
//           }
//
//           return Promise.resolve(response.data)
//         } else {
//           throw new Error('登录成功但未获取到有效数据')
//         }
//       } catch (error) {
//         this.error = error.message || '登录失败'
//         return Promise.reject(error)
//       } finally {
//         this.loading = false
//       }
//     },
//
//     /**
//      * 获取用户信息
//      * @returns {Promise} - 返回Promise对象
//      */
//     async fetchUserInfo() {
//       try {
//         const response = await getUserInfo()
//         if (response && response.data) {
//           this.setUser(response.data)
//           return response.data
//         }
//         return null
//       } catch (error) {
//         console.error('获取用户信息失败:', error)
//         return null
//       }
//     },
//
//     /**
//      * 用户登出
//      * @returns {Promise} - 返回Promise对象
//      */
//     async logoutUser() {
//       try {
//         await logout()
//         this.clearAuth()
//         return true
//       } catch (error) {
//         console.error('登出失败:', error)
//         // 即使API调用失败，也清除本地状态
//         this.clearAuth()
//         return false
//       }
//     }
//   },
//   persist: true
// })
