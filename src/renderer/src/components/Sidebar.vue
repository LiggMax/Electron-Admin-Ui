<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Home from '@/assets/svg/menu/Home.svg'
import User from '@/assets/svg/menu/User.svg'

// 获取路由实例和当前路由
const router = useRouter()
const route = useRoute()

// 当前激活的菜单项
const activeIndex = computed(() => {
  return route.path
})

// 菜单项数据
const menuItems = ref([
  {
    id: 1,
    title: '主页面',
    icon: Home,
    path: '/home'
  },
  {
    id: 2,
    title: '个人中心',
    icon: User,
    path: '/profile'
  }
])

// 处理菜单项点击
const handleMenuClick = (path) => {
  if (route.path !== path) {
    router.push(path)
  }
}
</script>

<template>
  <div class="sidebar">
    <div class="logo-container">
      <div class="logo">卡商平台</div>
    </div>

    <div class="menu-container">
      <div
        v-for="item in menuItems"
        :key="item.id"
        class="menu-item"
        :class="{ active: activeIndex === item.path }"
        @click="handleMenuClick(item.path)"
      >
        <img :src="item.icon" alt="icon" class="menu-icon" />
        <span class="menu-title">{{ item.title }}</span>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.sidebar {
  width: 160px;
  height: 100%;
  background-color: #001529;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  display: flex;
  flex-direction: column;
  color: rgba(255, 255, 255, 0.65);
  overflow: hidden;
}

.logo-container {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);

  .logo {
    color: #fff;
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 1px;
  }
}

.menu-container {
  flex: 1;
  padding: 12px 0;
  overflow-y: auto;

  .menu-item {
    height: 40px;
    padding: 0 16px;
    display: flex;
    align-items: center;
    cursor: pointer;
    transition: all 0.3s;
    margin: 4px 0;

    .menu-icon {
      width: 16px;
      height: 16px;
      margin-right: 10px;
    }

    .menu-title {
      font-size: 14px;
    }

    &:hover {
      color: #fff;
    }

    &.active {
      background-color: rgba(255, 255, 255, 0.09);
      color: #fff;
    }
  }
}
</style>
