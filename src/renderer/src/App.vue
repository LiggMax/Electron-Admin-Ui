<script setup>
import TitleBar from './components/TitleBar.vue'
import { ref, onMounted, provide } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.min'
import message from './utils/message'

const isMaximized = ref(false)

onMounted(() => {
  if (window.electron) {
    window.electron.ipcRenderer.on('window-state-changed', (_, maximized) => {
      isMaximized.value = maximized
    })
  }
})

// 提供给所有组件使用
provide('message', message)

// Element Plus配置
const config = {}
</script>

<template>
  <el-config-provider :locale="zhCn" :message="config">
    <div class="app-container" :class="{ maximized: isMaximized }">
      <div class="app-window">
        <TitleBar />
        <div class="content-container">
          <router-view></router-view>
        </div>
      </div>
      <!-- 边缘遮罩 -->
      <div class="edge-mask" :class="{ hidden: isMaximized }"></div>
    </div>
  </el-config-provider>
</template>

<style lang="less">
#app {
  width: 100vw;
  height: 100vh;
  background: transparent;
  display: flex;
  flex-direction: column;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100vh;
  background: transparent;
  overflow: hidden;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: rgba(144, 147, 153, 0.3);
  border-radius: 3px;
  transition: all 0.2s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(144, 147, 153, 0.5);
}

::-webkit-scrollbar-corner {
  background: transparent;
}

body {
  background: transparent;
}

.app-container {
  width: 100%;
  height: 100vh;
  padding: 5px; /* 留出边缘空间，显示圆角效果 */
  display: flex;
  flex-direction: column;
  position: relative;
  background: transparent;
  overflow: hidden; /* 防止溢出 */

  &.maximized {
    padding: 0;
  }
}

.app-window {
  width: 100%;
  height: 100%;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f5f7fa;
  position: relative;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
  isolation: isolate; /* 创建新的堆叠上下文 */

  /* 使用伪元素确保圆角效果，防止内容溢出 */
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    pointer-events: none;
    z-index: 1;
  }

  .maximized & {
    border-radius: 0;
  }
}

.content-container {
  flex: 1;
  width: 100%;
  display: flex;
  overflow: auto;
  padding: 0;
  margin: 0;
  position: relative;
  top: 0;
  left: 0;
}

html, body, #app, .app-window {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}
</style>
