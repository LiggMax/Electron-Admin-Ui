<script setup>
import { ref, reactive, inject } from 'vue'

// 获取全局消息服务
const message = inject('message')

// 基本设置表单
const basicForm = reactive({
  companyName: '卡商平台有限公司',
  adminEmail: 'admin@example.com',
  contactPhone: '13800138000',
  address: '北京市朝阳区xxx路xxx号',
  logoUrl: '',
  theme: 'blue'
})

// 安全设置表单
const securityForm = reactive({
  enableTwoFactor: false,
  passwordExpiry: 30,
  maxLoginAttempts: 5,
  lockoutDuration: 30,
  ipWhitelist: ''
})

// 通知设置表单
const notificationForm = reactive({
  emailNotifications: true,
  smsNotifications: false,
  lowStockAlert: true,
  lowStockThreshold: 10,
  orderNotifications: true,
  paymentNotifications: true
})

// 主题选项
const themeOptions = [
  { label: '蓝色主题', value: 'blue' },
  { label: '绿色主题', value: 'green' },
  { label: '紫色主题', value: 'purple' },
  { label: '橙色主题', value: 'orange' }
]

// 当前激活的标签页
const activeTab = ref('basic')

// 保存基本设置
const saveBasicSettings = () => {
  message.success('基本设置已保存')
  // 这里应该实现保存逻辑
}

// 保存安全设置
const saveSecuritySettings = () => {
  message.success('安全设置已保存')
  // 这里应该实现保存逻辑
}

// 保存通知设置
const saveNotificationSettings = () => {
  message.success('通知设置已保存')
  // 这里应该实现保存逻辑
}

// 重置账户密码
const resetPassword = () => {
  message.success('密码重置邮件已发送')
  // 这里应该实现重置密码逻辑
}

// 上传logo处理
const handleLogoUpload = (file) => {
  // 这里应该实现文件上传逻辑
  console.log('上传文件:', file)
  return false
}
</script>

<template>
  <div class="settings-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="title">系统设置</div>
      <div class="actions">
        <i class="el-icon-refresh"></i>
        <i class="el-icon-setting"></i>
        <i class="el-icon-message"></i>
        <i class="el-icon-user"></i>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <div class="settings-wrapper">
        <!-- 设置导航 -->
        <div class="settings-nav">
          <div 
            class="nav-item" 
            :class="{ active: activeTab === 'basic' }"
            @click="activeTab = 'basic'"
          >
            基本设置
          </div>
          <div 
            class="nav-item" 
            :class="{ active: activeTab === 'security' }"
            @click="activeTab = 'security'"
          >
            安全设置
          </div>
          <div 
            class="nav-item" 
            :class="{ active: activeTab === 'notification' }"
            @click="activeTab = 'notification'"
          >
            通知设置
          </div>
        </div>

        <!-- 设置内容 -->
        <div class="settings-content">
          <!-- 基本设置 -->
          <div v-show="activeTab === 'basic'" class="settings-panel">
            <h2 class="panel-title">基本设置</h2>
            <el-form :model="basicForm" label-width="120px">
              <el-form-item label="公司名称">
                <el-input v-model="basicForm.companyName" />
              </el-form-item>
              <el-form-item label="管理员邮箱">
                <el-input v-model="basicForm.adminEmail" />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="basicForm.contactPhone" />
              </el-form-item>
              <el-form-item label="公司地址">
                <el-input v-model="basicForm.address" type="textarea" rows="2" />
              </el-form-item>
              <el-form-item label="上传Logo">
                <el-upload
                  action="#"
                  :http-request="handleLogoUpload"
                  :show-file-list="false"
                  :before-upload="handleLogoUpload"
                >
                  <el-button size="small" type="primary">选择文件</el-button>
                  <span class="upload-tip">支持jpg, png格式，大小不超过2MB</span>
                </el-upload>
              </el-form-item>
              <el-form-item label="主题颜色">
                <el-select v-model="basicForm.theme">
                  <el-option 
                    v-for="item in themeOptions" 
                    :key="item.value" 
                    :label="item.label" 
                    :value="item.value" 
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 安全设置 -->
          <div v-show="activeTab === 'security'" class="settings-panel">
            <h2 class="panel-title">安全设置</h2>
            <el-form :model="securityForm" label-width="150px">
              <el-form-item label="启用双因素认证">
                <el-switch v-model="securityForm.enableTwoFactor" />
              </el-form-item>
              <el-form-item label="密码有效期(天)">
                <el-input-number v-model="securityForm.passwordExpiry" :min="0" :max="365" />
                <span class="input-tip">设为0表示永不过期</span>
              </el-form-item>
              <el-form-item label="最大登录尝试次数">
                <el-input-number v-model="securityForm.maxLoginAttempts" :min="1" :max="10" />
              </el-form-item>
              <el-form-item label="账户锁定时间(分钟)">
                <el-input-number v-model="securityForm.lockoutDuration" :min="5" :max="1440" />
              </el-form-item>
              <el-form-item label="IP白名单">
                <el-input v-model="securityForm.ipWhitelist" type="textarea" rows="3" placeholder="每行一个IP地址" />
                <span class="input-tip">留空表示不限制IP</span>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveSecuritySettings">保存设置</el-button>
                <el-button type="warning" @click="resetPassword" style="margin-left: 10px">重置账户密码</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 通知设置 -->
          <div v-show="activeTab === 'notification'" class="settings-panel">
            <h2 class="panel-title">通知设置</h2>
            <el-form :model="notificationForm" label-width="150px">
              <el-form-item label="启用邮件通知">
                <el-switch v-model="notificationForm.emailNotifications" />
              </el-form-item>
              <el-form-item label="启用短信通知">
                <el-switch v-model="notificationForm.smsNotifications" />
              </el-form-item>
              <el-form-item label="库存不足提醒">
                <el-switch v-model="notificationForm.lowStockAlert" />
              </el-form-item>
              <el-form-item label="库存提醒阈值">
                <el-input-number v-model="notificationForm.lowStockThreshold" :min="1" :max="1000" :disabled="!notificationForm.lowStockAlert" />
              </el-form-item>
              <el-form-item label="订单通知">
                <el-switch v-model="notificationForm.orderNotifications" />
              </el-form-item>
              <el-form-item label="支付通知">
                <el-switch v-model="notificationForm.paymentNotifications" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.settings-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
}

.page-header {
  height: 50px;
  background-color: #4169E1;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  .title {
    font-size: 18px;
    font-weight: 500;
  }

  .actions {
    display: flex;
    gap: 15px;

    i {
      font-size: 18px;
      cursor: pointer;
    }
  }
}

.main-content {
  flex: 1;
  padding: 15px;
  overflow: auto;
}

.settings-wrapper {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  height: 100%;
}

.settings-nav {
  width: 200px;
  border-right: 1px solid #ebeef5;
  padding: 20px 0;

  .nav-item {
    padding: 12px 20px;
    color: #606266;
    cursor: pointer;
    transition: all 0.3s;
    font-size: 14px;

    &:hover {
      background-color: #f5f7fa;
    }

    &.active {
      color: #409EFF;
      background-color: #ecf5ff;
      border-right: 2px solid #409EFF;
    }
  }
}

.settings-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
}

.settings-panel {
  .panel-title {
    font-size: 18px;
    color: #303133;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
  }
}

.upload-tip, .input-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style> 