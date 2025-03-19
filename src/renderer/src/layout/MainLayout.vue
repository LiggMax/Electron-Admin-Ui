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
}

.main-container {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
</style> 