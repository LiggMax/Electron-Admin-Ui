<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Home from '@/assets/svg/menu/Home.svg'
import User from '@/assets/svg/menu/User.svg'

// 获取路由实例和当前路由
const router = useRouter()
const route = useRoute()

// 添加侧边栏折叠状态，默认展开
const collapsed = ref(false)

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  collapsed.value = !collapsed.value
}

// 当前激活的菜单项
const activeIndex = computed(() => {
  return route.path
})

// 菜单项数据
const menuItems = ref([
  {
    id: 1,
    title: '首页',
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
  <div class="sidebar" :class="{ 'sidebar-collapsed': collapsed }">
    <div class="menu-container">
      <div
        v-for="item in menuItems"
        :key="item.id"
        class="menu-item"
        :class="{ active: activeIndex === item.path }"
        @click="handleMenuClick(item.path)"
      >
        <img :src="item.icon" alt="icon" class="menu-icon" />
        <span class="menu-title" v-if="!collapsed">{{ item.title }}</span>
      </div>
    </div>

    <!-- 添加折叠按钮 -->
    <div class="collapse-button" @click="toggleCollapse">
      <div class="collapse-icon">
        <span class="arrow" :class="{ 'arrow-right': collapsed, 'arrow-left': !collapsed }"></span>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.sidebar {
  width: 160px;
  height: 100%; /* 使用全高度 */
  background-color: #001529;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  display: flex;
  flex-direction: column;
  color: rgba(255, 255, 255, 0.65);
  overflow: hidden;
  transition: width 0.3s ease;

  &.sidebar-collapsed {
    width: 60px;
  }
}

.menu-container {
  flex: 1;
  overflow-y: auto;
  padding-top: 0; /* 移除顶部内边距 */
}

.menu-item {
  height: 50px; /* 增加高度 */
  padding: 0 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  margin: 0;

  .menu-icon {
    width: 16px;
    height: 16px;
    margin-right: 10px;
  }

  .menu-title {
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
  }

  &:hover {
    color: #fff;
    background-color: rgba(255, 255, 255, 0.05);
  }

  &.active {
    background-color: rgba(255, 255, 255, 0.09);
    color: #fff;
  }
}

// 添加折叠按钮样式
.collapse-button {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  cursor: pointer;

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }

  .collapse-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;

    .arrow {
      display: inline-block;
      width: 8px;
      height: 8px;
      border-top: 2px solid rgba(255, 255, 255, 0.65);
      border-right: 2px solid rgba(255, 255, 255, 0.65);
      transition: transform 0.3s;

      &.arrow-left {
        transform: rotate(-135deg);
      }

      &.arrow-right {
        transform: rotate(45deg);
      }
    }
  }
}
</style>
