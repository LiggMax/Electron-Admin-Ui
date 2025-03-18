<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const rememberUser = ref(false)

const login = () => {
  // 登录逻辑
  console.log('登录请求:', {
    username: username.value,
    password: password.value,
    rememberUser: rememberUser.value
  })
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
              v-model="username"
              type="text"
              placeholder="请输入账户名称"
              class="login-input"
            />
          </div>

          <div class="form-item">
            <div class="input-icon">
              <img src="../assets/images/password.png" alt="password" class="icon-image" />
            </div>
            <input
              v-model="password"
              type="password"
              placeholder="请输入账户密码"
              class="login-input"
            />
          </div>

          <div class="form-item remember-row">
            <label class="remember-label">
              <input v-model="rememberUser" type="checkbox" />
              <span>记住用户名</span>
            </label>
          </div>

          <button class="login-button" @click="login">登录</button>
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
  background-image: url('../assets/images/loginbackground.png'); /* 请将此处替换为你自己的PNG图片文件名 */
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-card {
  background: white;
  width: 800px;
  height: 480px;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  display: flex;
  overflow: hidden;
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
}

.remember-row {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.remember-label {
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

  &:hover {
    background-color: #40a9ff;
  }
}
</style>
