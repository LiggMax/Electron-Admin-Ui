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
    </div>
  </el-config-provider>
</template>

<style lang="less">
html,
body {
  margin: 0;
  padding: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  width: 100vw;
  overflow-x: hidden; /* 禁止水平滚动 */
  overflow-y: hidden; /* 默认禁止垂直滚动，由内部容器控制 */
  background: transparent;
  user-select: none; /* 防止文本被选中 */
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  user-select: none; /* 防止所有元素内容被选中 */
}

body {
  background: transparent;
  max-width: 100vw; /* 确保不会超出视口宽度 */
  overflow-x: hidden; /* 再次确保禁用水平滚动 */
}

.app-container {
  width: 100%;
  height: 100vh;
  padding: 5px; /* 留出边缘空间，显示圆角效果 */
  display: flex;
  flex-direction: column;
  position: relative;
  background: transparent;

  &.maximized {
    padding: 0;
  }
}

.app-window {
  width: 100%;
  height: 100%;
  border-radius: 10px; /* QQ音乐风格的圆角 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f5f7fa;
  position: relative;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
  user-select: none;

  .maximized & {
    border-radius: 0;
  }
}

.content-container {
  flex: 1;
  width: 100%;
  display: flex;
  overflow-x: hidden;
  overflow-y: auto;
  padding: 0;
  margin: 0;
  position: relative;
  top: 0;
  left: 0;
  user-select: none;
}

/* 全局禁用文本选择的视觉反馈 */
::selection {
  background: transparent;
}

/* 对于表格、输入框等元素，只有当用户需要复制文本时才允许选择 */
input, textarea, [contenteditable="true"] {
  user-select: text;
}

/* 修改滚动条样式，完全隐藏水平滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 0;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* 全局表格样式，防止水平滚动 */
.el-table {
  width: 100% !important;
  max-width: 100% !important;
  overflow-x: hidden !important;
}

/* 防止任何元素产生水平滚动 */
.el-container, 
.el-main, 
.el-card, 
.el-row,
.el-form,
.el-tabs__content,
.el-dialog__body {
  overflow-x: hidden !important;
  max-width: 100% !important;
}

/* 确保输入框不会引起布局问题 */
.el-input,
.el-select,
.el-date-editor {
  max-width: 100%;
}

/* 确保任何内容区域都不会有水平滚动条 */
div, section, article, aside, table {
  overflow-x: hidden;
  max-width: 100%;
}
</style>
