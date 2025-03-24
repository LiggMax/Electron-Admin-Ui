<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getUserInfoService } from '../api/userLogin'
import defaultAvatar from '../assets/images/user.png'
import useUserInfoStore from '../store/userInfo'

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
      <div class="header-icon">
        <el-tooltip content="设置" placement="bottom">
          <i class="el-icon-setting">
            <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="20" height="20">
              <path
                d="M512 682.667c-94.257 0-170.667-76.41-170.667-170.667S417.743 341.333 512 341.333 682.667 417.743 682.667 512 606.257 682.667 512 682.667z m0-256c-47.128 0-85.333 38.205-85.333 85.333s38.205 85.333 85.333 85.333 85.333-38.205 85.333-85.333-38.205-85.333-85.333-85.333z"
                fill="currentColor"
              ></path>
              <path
                d="M938.667 512c0-13.547-10.239-24.915-23.723-26.283l-62.848-6.4a340.053 340.053 0 0 0-19.925-62.165l39.083-50.347c8.576-11.093 7.382-26.965-2.731-36.693l-80.683-80.683c-9.728-10.112-25.6-11.307-36.693-2.73l-50.347 39.082a339.87 339.87 0 0 0-62.165-19.925l-6.4-62.848C630.915 182.239 619.547 172 606 172h-114c-13.547 0-24.915 10.239-26.283 23.723l-6.4 62.848a340.053 340.053 0 0 0-62.165 19.925l-50.347-39.083c-11.093-8.576-26.965-7.382-36.693 2.731l-80.683 80.683c-10.112 9.728-11.307 25.6-2.73 36.693l39.082 50.347a339.87 339.87 0 0 0-19.925 62.165l-62.848 6.4c-13.525 1.368-23.764 12.736-23.764 26.283v114c0 13.547 10.24 24.915 23.723 26.283l62.848 6.4a340.053 340.053 0 0 0 19.925 62.165l-39.083 50.347c-8.576 11.093-7.382 26.965 2.731 36.693l80.683 80.683c9.728 10.112 25.6 11.307 36.693 2.73l50.347-39.082a339.87 339.87 0 0 0 62.165 19.925l6.4 62.848c1.368 13.525 12.736 23.764 26.283 23.764h114c13.547 0 24.915-10.24 26.283-23.723l6.4-62.848a340.053 340.053 0 0 0 62.165-19.925l50.347 39.083c11.093 8.576 26.965 7.382 36.693-2.731l80.683-80.683c10.112-9.728 11.307-25.6 2.73-36.693l-39.082-50.347a339.87 339.87 0 0 0 19.925-62.165l62.848-6.4c13.525-1.368 23.764-12.736 23.764-26.283V512z"
                fill="currentColor"
              ></path>
            </svg>
          </i>
        </el-tooltip>
      </div>
      <div class="header-icon">
        <el-badge is-dot>
          <el-tooltip content="通知" placement="bottom">
            <i class="el-icon-bell">
              <svg
                viewBox="0 0 1024 1024"
                xmlns="http://www.w3.org/2000/svg"
                width="20"
                height="20"
              >
                <path
                  d="M816 768h-24V428c0-141.1-104.3-257.8-240-277.2V112c0-22.1-17.9-40-40-40s-40 17.9-40 40v38.8C336.3 170.2 232 286.9 232 428v340h-24c-17.7 0-32 14.3-32 32v32c0 4.4 3.6 8 8 8h656c4.4 0 8-3.6 8-8v-32c0-17.7-14.3-32-32-32zM512 844c38.5 0 70-31.5 70-70h-140c0 38.5 31.5 70 70 70z"
                  fill="currentColor"
                ></path>
              </svg>
            </i>
          </el-tooltip>
        </el-badge>
      </div>
      <div class="header-icon">
        <el-tooltip content="搜索" placement="bottom">
          <i class="el-icon-search">
            <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" width="20" height="20">
              <path
                d="M909.6 854.5L649.9 594.8C690.2 542.7 712 479 712 412c0-80.2-31.3-155.4-87.9-212.1-56.6-56.7-132-87.9-212.1-87.9s-155.5 31.3-212.1 87.9C143.2 256.5 112 331.8 112 412c0 80.1 31.3 155.5 87.9 212.1C256.5 680.8 331.8 712 412 712c67 0 130.6-21.8 182.7-62l259.7 259.6c3.2 3.2 8.4 3.2 11.6 0l43.6-43.5c3.2-3.2 3.2-8.4 0-11.6zM570.4 570.4C528 612.7 471.8 636 412 636s-116-23.3-158.4-65.6C211.3 528 188 471.8 188 412s23.3-116.1 65.6-158.4C296 211.3 352.2 188 412 188s116.1 23.2 158.4 65.6S636 352.2 636 412s-23.3 116.1-65.6 158.4z"
                fill="currentColor"
              ></path>
            </svg>
          </i>
        </el-tooltip>
      </div>
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
              <el-dropdown-item>设置</el-dropdown-item>
              <el-dropdown-item divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.app-header {
  height: 56px;
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
