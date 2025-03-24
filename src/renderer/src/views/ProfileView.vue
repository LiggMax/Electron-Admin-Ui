<script setup>
import { ref } from 'vue'
import AppHeader from '../components/AppHeader.vue'
import userAvatar from '../assets/images/user.png'
import useUserStore from '../store/userInfo'
const userInfoStore = useUserStore()
// 用户信息
const userInfo = ref({
  nickName: userInfoStore.info.nickName,
  avatar: userInfoStore.info.userAvatar,
  balance: '¥ 10.00',
})

// 交易记录
const transactions = ref([
  { type: '收入', amount: '¥1000', date: '2023-09-01 10:24' },
  { type: '收入', amount: '¥500', date: '2023-09-02 14:18' },
  { type: '收入', amount: '¥200', date: '2023-09-03 09:50' }
])


// 充值余额
const handleRecharge = () => {
  // 充值逻辑
  console.log('充值余额')
}
//从pinia中获取用户信息

</script>

<template>
  <div class="profile-container">
    <AppHeader title="卡商端-用户中心" />
    <!-- 内容区 -->
    <div class="content-area">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="avatar-section">
          <img :src="userInfo.avatar ? userInfo.avatar : userAvatar " alt="用户头像" class="avatar" />
          <h2 class="username">{{ userInfo.nickName }}</h2>
        </div>
        <div class="balance-section">
          <div class="balance-label">余额:</div>
          <div class="balance-value">{{ userInfo.balance }}</div>
          <el-button type="primary" class="recharge-btn" @click="handleRecharge">余额充值</el-button>
        </div>
      </div>

      <!-- 交易记录 -->
      <div class="transaction-card">
        <div class="card-header">
          <h3>交易记录</h3>
          <el-button type="success" size="small" plain>筛选</el-button>
        </div>
        <el-table :data="transactions" stripe style="width: 100%">
          <el-table-column prop="type" label="交易类型" width="120" />
          <el-table-column prop="amount" label="交易金额" width="120" />
          <el-table-column prop="date" label="交易时间" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.profile-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .content-area {
    flex: 1;
    overflow: auto;
    padding: 20px;
    background-color: #f5f7fa;
  }

  .user-card {
    background-color: #1890ff;
    color: white;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .avatar-section {
      display: flex;
      align-items: center;

      .avatar {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        border: 2px solid white;
        object-fit: cover;
        margin-right: 15px;
      }

      .username {
        font-size: 22px;
        margin: 0;
        font-weight: 500;
      }
    }

    .balance-section {
      display: flex;
      align-items: center;

      .balance-label {
        font-size: 16px;
        margin-right: 5px;
      }

      .balance-value {
        font-size: 22px;
        font-weight: bold;
        margin-right: 20px;
      }

      .recharge-btn {
        background-color: white;
        color: #1890ff;
        border: none;
        padding: 8px 16px;
        border-radius: 4px;
        cursor: pointer;
        font-weight: bold;

        &:hover {
          background-color: #f5f5f5;
        }
      }
    }
  }

  .transaction-card {
    background-color: white;
    border-radius: 8px;
    margin-bottom: 20px;
    padding: 15px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      h3 {
        margin: 0;
        font-size: 18px;
        color: #303133;
      }
    }
  }

  .info-password-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 20px;
    margin-bottom: 20px;

    .info-card,
    .password-card {
      height: 100%;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

      :deep(.el-card__header) {
        border-bottom: 1px solid #ebeef5;
        padding: 15px 20px;
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 16px;
        font-weight: bold;
        color: #303133;
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
          font-size: 14px;
        }

        .value {
          color: #303133;
          font-size: 14px;
        }
      }
    }
  }
}
</style>
