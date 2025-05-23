<script setup>
import { onMounted, ref } from 'vue'
import AppHeader from '../components/AppHeader.vue'
import userAvatar from '../assets/images/user.png'
import useUserStore from '../store/userInfo'
import { format } from '../utils/DateFormatter'
import { getTransactionRecordService } from '../api/user'

const userInfoStore = useUserStore()
// 用户信息
const userInfo = ref({
  nickName: userInfoStore.info.nickName,
  avatar: userInfoStore.info.userAvatar,
  money: userInfoStore.info.money
})

// 交易记录
const transactions = ref([
  {
    id: '',
    amount: '',
    date: '',
    phoneNumber: '',
    status: ''
  }
])

//获取交易记录
const getTransactions = async () => {
  const response = await getTransactionRecordService()
  //数据转换
  transactions.value = response.data.map((item) => {
    return {
      id: item.id,
      amount: item.phoneMoney,
      date: format(item.createdAt),
      phoneNumber: item.phoneNumber,
      status: item.state
    }
  })
}
// 充值余额
const handleRecharge = () => {
  // 充值逻辑
  console.log('充值余额')
}
onMounted(() => {
  getTransactions()
})
</script>

<template>
  <div class="profile-container">
    <AppHeader title="卡商端-用户中心" />
    <!-- 内容区 -->
    <div class="content-area">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="avatar-section">
          <img
            :src="userInfo.avatar ? userInfo.avatar : userAvatar"
            alt="用户头像"
            class="avatar"
          />
          <h2 class="username">用户昵称：{{ userInfo.nickName }}</h2>
        </div>
        <div class="balance-section">
          <div class="balance-label">余额:</div>
          <div class="balance-value">{{ userInfo.money }}</div>
          <el-button type="primary" class="recharge-btn" @click="handleRecharge"
            >余额充值</el-button
          >
        </div>
      </div>

      <!-- 交易记录 -->
      <div class="transaction-card">
        <div class="card-header">
          <h3>交易记录</h3>
        </div>
        <el-table :data="transactions" stripe style="width: 100%">
          <el-table-column prop="id" label="订单号"  />
          <el-table-column prop="amount" label="交易金额" />
          <el-table-column prop="date" label="交易时间" />
          <el-table-column prop="phoneNumber" label="手机号" />
          <!--状态 0=未使用 1=带结算 2=已结算-->
          <el-table-column prop="status" label="状态">
            <template v-slot="scope">
              <el-tag v-if="scope.row.status === 0" type="info">未使用</el-tag>
              <el-tag v-else-if="scope.row.status === 1" type="warning">待结算</el-tag>
              <el-tag v-else type="success">已结算</el-tag>
            </template>
          </el-table-column>

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
        background-color: #fc5b4c;
        border: none;
        padding: 8px 16px;
        border-radius: 15px;
        cursor: pointer;
        font-weight: bold;
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
