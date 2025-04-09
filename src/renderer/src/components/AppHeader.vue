<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getUserInfoService, userLogoutService } from '../api/userLogin'
import defaultAvatar from '../assets/images/user.png'
import useUserInfoStore from '../store/userInfo'
import message from '../utils/message'
const userInfoStore = useUserInfoStore()
// 定义props
const props = defineProps({
  title: {
    type: String,
    default: '卡商端-首页'
  }
})

const router = useRouter()
const route = useRoute()

// 计算当前页面是否是首页
const isHomePage = computed(() => {
  return route.path === '/home' || route.path === '/'
})

// 跳转到首页
const goToHome = () => {
  if (!isHomePage.value) {
    router.push('/home')
  }
}

// 跳转到个人中心
const goToProfile = () => {
  router.push('/profile')
}

// 用户信息
const userInfo = ref({
  userAvatar: defaultAvatar,
  nickName: '用户'
})

// 获取用户信息
const getUserInfo = async () => {
  const res = await getUserInfoService()
  userInfo.value = res.data
  // 将用户信息存储到 store 中
  userInfoStore.setInfo(res.data)
}
const logout = async () => {
  const res = await userLogoutService()
  // 清除用户信息
  userInfoStore.removeInfo()
  // 跳转到登录页
  message.success(res.data)
  await router.push('/login')
}

// 页面加载时获取用户信息
onMounted(() => {
  getUserInfo()
})
</script>

<template>
  <div class="app-header">
    <div class="header-left">
      <div class="logo" @click="goToHome">
        {{ props.title }}
      </div>
    </div>
    <div class="header-right">
      <div class="user-info">
        <el-dropdown trigger="click">
          <div class="user-avatar-container">
            <div class="user-avatar">
              <img
                :src="userInfo.userAvatar ? userInfo.userAvatar : defaultAvatar"
                alt="用户头像"
              />
            </div>
            <span class="user-name">{{ userInfo.nickName }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.app-header {
  height: 75px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  background-color: #2196f3;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-icon {
  margin-right: 16px;
  cursor: pointer;
  position: relative;

  i {
    font-size: 20px;
    color: white;
  }

  &:hover {
    opacity: 0.8;
  }
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;

  .user-avatar-container {
    display: flex;
    align-items: center;
  }

  .user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 8px;
    border: 1px solid rgba(255, 255, 255, 0.5);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .user-name {
    color: white;
    font-size: 14px;
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

:deep(.el-badge__content.is-dot) {
  background-color: #ff5722;
}
</style>
