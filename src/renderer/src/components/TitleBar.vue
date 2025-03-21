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

// 计算当前页面标题
const pageTitle = computed(() => {
  switch (route.path) {
    case '/home':
      return '卡商端 - 首页'
    case '/profile':
      return '卡商端 - 个人中心'
    default:
      return ''
  }
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
  <div class="title-bar">
    <div class="logo-section">
      <span class="app-logo">{{ pageTitle }}</span>
    </div>
<!--    <div class="title-section">-->
<!--      <span class="page-title">{{ pageTitle }}</span>-->
<!--    </div>-->
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
  height: 60px;
  background-color: #2e55ea;
  display: flex;
  justify-content: space-between;
  align-items: center;
  -webkit-app-region: drag;
  user-select: none;
  width: 100%;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  padding: 0;
  margin: 0;
}

.logo-section {
  width: 200px;
  display: flex;
  align-items: center;
  padding-left: 16px;
}

.app-logo {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.title-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.page-title {
  font-size: 15px;
  font-weight: 600;
  color: white;
  letter-spacing: 0.5px;
  text-align: center;
}

.window-controls {
  display: flex;
  -webkit-app-region: no-drag;
  width: 110px;
  justify-content: flex-end;
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
  margin-left: 2px;
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
