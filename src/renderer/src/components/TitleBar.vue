<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
// svg图标
import minimizeIcon from '../assets/svg/minimize.svg'
import maximizeIcon from '../assets/svg/Maximize-1.svg'
import restoreIcon from '../assets/svg/Maximize-2.svg'
import closeIcon from '../assets/svg/Shut down.svg'

const isMaximized = ref(false)
const route = useRoute()

// 根据路由计算标题栏样式
const titleBarClass = computed(() => {
  // 登录页使用蓝色
  if (route.path === '/login') {
    return 'login-theme'
  }
  // 其他页面使用主题色
  return 'main-theme'
})

// 在组件挂载时监听窗口状态变化
onMounted(() => {
  // 如果electron API可用，添加窗口状态监听
  if (window.electron) {
    window.electron.ipcRenderer.on('window-state-changed', (_, maximized) => {
      isMaximized.value = maximized
    })
  }
})

const handleMinimize = () => {
  window.api.window.minimize()
}

const handleMaximize = () => {
  window.api.window.maximize()
  isMaximized.value = !isMaximized.value
}

const handleClose = () => {
  window.api.window.close()
}
</script>

<template>
  <div class="title-bar" :class="titleBarClass">
    <div class="title-section">
    </div>
    <div class="window-controls">
      <button class="control-button minimize" title="最小化" @click="handleMinimize">
        <img :src="minimizeIcon" class="svg-icon" alt="最小化" />
      </button>
      <button
        class="control-button maximize"
        :title="isMaximized ? '向下还原' : '最大化'"
        @click="handleMaximize"
      >
        <img v-if="!isMaximized" :src="maximizeIcon" class="svg-icon" alt="最大化" />
        <img v-else :src="restoreIcon" class="svg-icon" alt="还原" />
      </button>
      <button class="control-button close" title="关闭" @click="handleClose">
        <img :src="closeIcon" class="svg-icon" alt="关闭" />
      </button>
    </div>
  </div>
</template>

<style lang="less" scoped>
.title-bar {
  height: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  -webkit-app-region: drag;//拖动
  user-select: none;
  width: 100%;
  z-index: 1000;
  padding: 0;
  margin: 0;
  transition: all 0.3s ease;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    bottom: -1px;
    height: 1px;
    background: linear-gradient(to bottom, rgba(255, 255, 255, 0.05), transparent);
    pointer-events: none;
    opacity: 0.3;
  }

  // 登录页主题
  &.login-theme {
    background-color: #2e55ea;
  }

  // 主页面主题
  &.main-theme {
    background-color: #2196f3;
  }
}

.title-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.window-controls {
  display: flex;
  -webkit-app-region: no-drag;
  width: 110px;
  justify-content: flex-end;
  padding-right: 10px;
}

.control-button {
  width: 34px;
  height: 34px;
  border: none;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #e0e0e0;
  border-radius: 4px;
  margin-left: 4px;
  transition: all 0.2s;

  &:hover {
    background-color: rgba(255, 255, 255, 0.15);
    color: #fff;
  }

  &.close:hover {
    background-color: rgba(255, 255, 255, 0.26);
    color: white;
  }

  .svg-icon {
    width: 16px;
    height: 16px;
    filter: brightness(10); /* 使SVG图标变白 */
  }
}
</style>
