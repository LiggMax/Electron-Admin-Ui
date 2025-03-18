import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null,
    user: null,
    isAuthenticated: false
  }),

  actions: {
    async login(username, password) {
      try {
        const response = await loginApi(username, password)
        if (response.success) {
          this.token = response.data.token
          this.user = response.data.user
          this.isAuthenticated = true
          return true
        }
        return false
      } catch (error) {
        console.error('Login failed:', error)
        return false
      }
    },

    async logout() {
      try {
        await logoutApi()
        this.token = null
        this.user = null
        this.isAuthenticated = false
        return true
      } catch (error) {
        console.error('Logout failed:', error)
        return false
      }
    }
  }
})
