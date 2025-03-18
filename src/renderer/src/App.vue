<script setup>
import TitleBar from './components/TitleBar.vue'
</script>

<template>
  <div class="app-container">
    <TitleBar />
    <div class="content-container">
      <router-view></router-view>
    </div>
  </div>
</template>

<style lang="less">
html, body {
  margin: 0;
  padding: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  overflow: hidden;
}

* {
  box-sizing: border-box;
}

body {
  background-image: url('./assets/images/loginbackground.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.app-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

.content-container {
  flex: 1;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

// 移除背景装饰元素
// body::before,
// body::after {
//   content: '';
//   position: absolute;
//   width: 200px;
//   height: 200px;
//   border: 2px solid rgba(255, 255, 255, 0.1);
//   transform: rotate(45deg);
// }

// body::before {
//   top: 10%;
//   left: 15%;
// }

// body::after {
//   bottom: 10%;
//   right: 15%;
// }
</style>
