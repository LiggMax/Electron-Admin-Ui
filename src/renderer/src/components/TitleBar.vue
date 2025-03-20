<script setup>
import { ref, onMounted } from 'vue'
// svg图标
import minimizeIcon from '../assets/svg/minimize.svg'
import maximizeIcon from '../assets/svg/Maximize-1.svg'
import restoreIcon from '../assets/svg/Maximize-2.svg'
import closeIcon from '../assets/svg/Shut down.svg'

const isMaximized = ref(false)

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
    <div class="title-bar-drag">
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
  height: 36px;
  background-color: #2e55ea;
  display: flex;
  justify-content: space-between;
  align-items: center;
  -webkit-app-region: drag;
  user-select: none;
  padding: 0 12px;
  width: 100%;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.title-bar-drag {
  padding-left: 12px;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.window-controls {
  display: flex;
  -webkit-app-region: no-drag;
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
  margin-left: 6px;
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
