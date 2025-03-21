<script setup>
import Sidebar from '../components/Sidebar.vue'

</script>

<template>
  <div class="main-layout">
    <Sidebar />
    <div class="main-container">
      <router-view></router-view>
    </div>
  </div>
</template>

<style lang="less" scoped>
.main-layout {
  width: 100%;
  height: 100%;
  display: flex;
  overflow: hidden;
  margin: 0;
  padding: 0;
  user-select: none;
}

.main-container {
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s;
  margin: 0;
  padding: 0;
  background-color: #fff;
  user-select: none;
}
</style> 