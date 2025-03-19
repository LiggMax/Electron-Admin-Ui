<script setup>
import TitleBar from './components/TitleBar.vue'
import { ref, onMounted } from 'vue'

const isMaximized = ref(false)

onMounted(() => {
  if (window.electron) {
    window.electron.ipcRenderer.on('window-state-changed', (_, maximized) => {
      isMaximized.value = maximized
    })
  }
})
</script>

<template>
  <div class="app-container" :class="{ maximized: isMaximized }">
    <div class="app-window">
      <TitleBar />
      <div class="content-container">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style lang="less">
html,
body {
  margin: 0;
  padding: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  overflow: hidden;
  background: transparent;
}

* {
  box-sizing: border-box;
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
  background-image: url('./assets/images/loginbackground.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
  position: relative;

  .maximized & {
    border-radius: 0;
  }
}

.content-container {
  flex: 1;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}
</style>
