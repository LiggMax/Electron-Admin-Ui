<script setup>
import { ref } from 'vue'

// 用户信息
const userInfo = ref({
  username: '张三',
  role: '管理员',
  email: 'zhangsan@example.com',
  phone: '13800138000',
  lastLogin: '2024-01-20 10:30:00'
})

// 修改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 修改密码
const handleChangePassword = () => {
  // 这里实现修改密码逻辑
  console.log('修改密码', passwordForm.value)
}

// 更新个人信息
const handleUpdateInfo = () => {
  // 这里实现更新信息逻辑
  console.log('更新信息', userInfo.value)
}
</script>

<template>
  <div class="profile-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="title">个人中心</div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 基本信息卡片 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
            <el-button type="primary" size="small" @click="handleUpdateInfo">更新信息</el-button>
          </div>
        </template>
        <div class="info-list">
          <div class="info-item">
            <span class="label">用户名：</span>
            <span class="value">{{ userInfo.username }}</span>
          </div>
          <div class="info-item">
            <span class="label">角色：</span>
            <span class="value">{{ userInfo.role }}</span>
          </div>
          <div class="info-item">
            <span class="label">邮箱：</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机：</span>
            <span class="value">{{ userInfo.phone }}</span>
          </div>
          <div class="info-item">
            <span class="label">最后登录：</span>
            <span class="value">{{ userInfo.lastLogin }}</span>
          </div>
        </div>
      </el-card>

      <!-- 修改密码卡片 -->
      <el-card class="password-card">
        <template #header>
          <div class="card-header">
            <span>修改密码</span>
          </div>
        </template>
        <el-form :model="passwordForm" label-width="100px">
          <el-form-item label="原密码：">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码：">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码：">
            <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleChangePassword">确认修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<style lang="less" scoped>
.profile-container {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;
    .title {
      font-size: 20px;
      font-weight: 600;
    }
  }

  .main-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;

    .info-card,
    .password-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }

    .info-list {
      .info-item {
        margin-bottom: 15px;
        display: flex;
        align-items: center;

        .label {
          color: #606266;
          margin-right: 10px;
          min-width: 70px;
        }

        .value {
          color: #303133;
        }
      }
    }
  }
}
</style>