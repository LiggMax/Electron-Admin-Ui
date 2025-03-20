<script setup>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { userLoginService } from '../api/userLogin'

// 获取全局消息服务
const message = inject('message')

// 获取路由实例
const router = useRouter()

// 表单数据
const loginForm = ref({
  account: '',
  password: '',
  rememberUser: false
})

// 是否使用主进程发送请求
const useMainProcess = ref(false)

// 加载状态
const loading = ref(false)

/**
 * 执行登录
 */
const handleLogin = async () => {
  // 表单验证
  if (!loginForm.value.account.trim()) {
    message.error('请输入账号')
    return
  }
  if (!loginForm.value.password.trim()) {
    message.error('请输入密码')
    return
  }

  loading.value = true // 设置加载状态

  try {
    console.log(`使用${useMainProcess.value ? '主进程' : 'Axios'}方式发送登录请求`)

    const res = await userLoginService(loginForm.value)
    console.log('登录成功:', res)

    // 登录成功提示
    message.success('登录成功')

    // 如果不需要记住用户名，清除账户
    if (!loginForm.value.rememberUser) {
      loginForm.value.account = ''
    }
    loginForm.value.password = '' // 清空密码

    // 登录成功后跳转到首页
    router.push('/home')
  } catch (error) {
    // 使用消息服务展示错误信息
    message.error(error.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false // 无论成功失败都结束加载状态
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-left">
        <img src="../assets/images/login.png" alt="登录插图" class="login-image" />
      </div>
      <div class="login-right">
        <h1 class="login-title">欢迎使用卡商平台</h1>

        <div class="login-form">
          <div class="form-item">
            <div class="input-icon">
              <img src="../assets/images/user.png" alt="user" class="icon-image" />
            </div>
            <input
              v-model="loginForm.account"
              type="text"
              placeholder="请输入账户名称"
              class="login-input"
              :disabled="loading"
            />
          </div>

          <div class="form-item">
            <div class="input-icon">
              <img src="../assets/images/password.png" alt="password" class="icon-image" />
            </div>
            <input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入账户密码"
              class="login-input"
              :disabled="loading"
              @keyup.enter="handleLogin"
            />
          </div>

          <div class="form-item remember-row">
            <label class="remember-label">
              <input v-model="loginForm.rememberUser" type="checkbox" :disabled="loading" />
              <span>记住用户名</span>
            </label>


          </div>

          <button class="login-button" :disabled="loading" @click="handleLogin">
            <span v-if="loading">登录中...</span>
            <span v-else>登录</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  perspective: 1000px;
  overflow: hidden;
  padding: 10px; /* 添加内边距确保登录卡片不会太靠近窗口边缘 */
}

.login-card {
  background: rgba(255, 255, 255, 0.98);
  width: 800px;
  max-width: 90%;
  height: 480px;
  max-height: 90%;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  overflow: hidden;
  backdrop-filter: blur(5px);
  animation: fadeIn 0.5s ease-out;
}

.login-left {
  width: 45%;
  background-color: #f6f9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
}

.login-image {
  max-width: 100%;
  height: auto;
}

.login-right {
  width: 55%;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-title {
  font-size: 24px;
  color: #333;
  text-align: center;
  margin-bottom: 40px;
  font-weight: 500;
}

.login-form {
  width: 100%;
}

.form-item {
  margin-bottom: 20px;
  position: relative;
}

.input-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.icon-image {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.login-input {
  width: 100%;
  height: 45px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0 15px 0 40px;
  font-size: 14px;
  color: #333;
  transition: all 0.3s;

  &:focus {
    border-color: #1890ff;
    outline: none;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  }

  &::placeholder {
    color: #bbb;
  }

  &:disabled {
    background-color: #f5f5f5;
    cursor: not-allowed;
  }
}

.remember-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remember-label,
.request-method-label {
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  cursor: pointer;

  input {
    margin-right: 6px;
  }
}

.login-button {
  width: 100%;
  height: 45px;
  background-color: #1890ff;
  border: none;
  border-radius: 22.5px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover:not(:disabled) {
    background-color: #40a9ff;
  }

  &:disabled {
    background-color: #d9d9d9;
    cursor: not-allowed;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
