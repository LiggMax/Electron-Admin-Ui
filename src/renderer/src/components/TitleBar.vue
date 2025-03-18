<script setup>
import { ref, onMounted } from 'vue'

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
      <span class="title">卡商平台</span>
    </div>
    <div class="window-controls">
      <button class="control-button minimize" @click="handleMinimize" title="最小化">
        <svg width="12" height="12" viewBox="0 0 12 12">
          <rect x="1" y="5.5" width="10" height="1" fill="currentColor" />
        </svg>
      </button>
      <button class="control-button maximize" @click="handleMaximize" :title="isMaximized ? '向下还原' : '最大化'">
        <svg v-if="!isMaximized" width="12" height="12" viewBox="0 0 12 12">
          <rect
            x="1.5"
            y="1.5"
            width="9"
            height="9"
            stroke="currentColor"
            fill="none"
            stroke-width="1"
          />
        </svg>
        <svg v-else width="12" height="12" viewBox="0 0 12 12">
          <path
            d="M3.5,1.5v2h-2v7h7v-2h2v-7H3.5z M7.5,9.5h-5v-5h5V9.5z M9.5,7.5h-1v-5h-5v-1h6V7.5z"
            fill="currentColor"
          />
        </svg>
      </button>
      <button class="control-button close" @click="handleClose" title="关闭">
        <svg width="12" height="12" viewBox="0 0 12 12">
          <path
            d="M2.4,1.4l8.2,8.2 M1.4,9.6l8.2-8.2"
            stroke="currentColor"
            stroke-width="1.5"
            stroke-linecap="round"
          />
        </svg>
      </button>
    </div>
  </div>
</template>

<style lang="less" scoped>
.title-bar {
  height: 36px;
  background: transparent;
  display: flex;
  justify-content: space-between;
  align-items: center;
  -webkit-app-region: drag;
  user-select: none;
  padding: 0 12px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
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
    background-color: rgba(232, 17, 35, 0.9);
    color: white;
  }
}
</style>
