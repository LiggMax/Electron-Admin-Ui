<script setup>
// 主应用组件
</script>

<template>
  <router-view></router-view>
</template>

<style lang="less">
body {
  margin: 0;
  padding: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-image: url('./assets/images/loginbackground.png'); /* 使用PNG图片作为背景 */
  background-size: cover; /* 确保背景图片覆盖整个页面 */
  background-position: center; /* 背景图片居中 */
  background-repeat: no-repeat; /* 防止背景图片重复 */
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

* {
  box-sizing: border-box;
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
