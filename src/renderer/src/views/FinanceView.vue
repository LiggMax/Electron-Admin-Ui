<script setup>
import { ref } from 'vue'

// 财务数据
const transactions = ref([
  { id: 'TR001', type: '销售收入', amount: '2000.00', time: '2023-12-01 10:30:45', status: '已完成', remark: '卡券销售' },
  { id: 'TR002', type: '进货支出', amount: '-1500.00', time: '2023-12-05 14:25:32', status: '已完成', remark: '进货支出' },
  { id: 'TR003', type: '销售收入', amount: '1800.00', time: '2023-12-10 09:18:24', status: '已完成', remark: '卡券销售' },
  { id: 'TR004', type: '退款', amount: '-200.00', time: '2023-12-15 16:42:11', status: '已完成', remark: '客户退款' },
  { id: 'TR005', type: '销售收入', amount: '3500.00', time: '2023-12-20 11:05:39', status: '处理中', remark: '卡券销售' }
])

// 财务数据统计
const statistics = ref({
  totalIncome: '7300.00',
  totalExpense: '1700.00',
  balance: '5600.00',
  pendingAmount: '3500.00'
})

// 交易类型选项
const transactionTypes = [
  { label: '全部类型', value: '' },
  { label: '销售收入', value: '销售收入' },
  { label: '进货支出', value: '进货支出' },
  { label: '退款', value: '退款' },
  { label: '其他', value: '其他' }
]

// 搜索条件
const searchForm = ref({
  id: '',
  type: '',
  dateRange: []
})

// 搜索方法
const handleSearch = () => {
  console.log('搜索条件：', searchForm.value)
  // 这里应该实现搜索逻辑
}

// 重置搜索条件
const resetSearch = () => {
  searchForm.value = {
    id: '',
    type: '',
    dateRange: []
  }
}

// 导出数据
const exportData = () => {
  console.log('导出数据')
  // 这里应该实现导出功能
}
</script>

<template>
  <div class="finance-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="title">财务管理</div>
      <div class="actions">
        <i class="el-icon-refresh"></i>
        <i class="el-icon-setting"></i>
        <i class="el-icon-message"></i>
        <i class="el-icon-user"></i>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 财务概览 -->
      <div class="finance-overview">
        <div class="overview-card">
          <div class="card-title">总收入</div>
          <div class="card-value income">¥{{ statistics.totalIncome }}</div>
        </div>
        <div class="overview-card">
          <div class="card-title">总支出</div>
          <div class="card-value expense">¥{{ statistics.totalExpense }}</div>
        </div>
        <div class="overview-card">
          <div class="card-title">账户余额</div>
          <div class="card-value">¥{{ statistics.balance }}</div>
        </div>
        <div class="overview-card">
          <div class="card-title">待处理金额</div>
          <div class="card-value pending">¥{{ statistics.pendingAmount }}</div>
        </div>
      </div>

      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-form">
          <el-form :inline="true" :model="searchForm" class="form-inline">
            <el-form-item label="交易号">
              <el-input v-model="searchForm.id" placeholder="请输入交易号" size="small" />
            </el-form-item>
            <el-form-item label="交易类型">
              <el-select v-model="searchForm.type" placeholder="请选择类型" size="small">
                <el-option 
                  v-for="item in transactionTypes" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
            <el-form-item label="交易时间">
              <el-date-picker
                v-model="searchForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="small"
                style="width: 240px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch" size="small">查询</el-button>
              <el-button @click="resetSearch" size="small">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- 表格操作栏 -->
      <div class="table-actions">
        <div class="left-actions">
          <el-button type="primary" size="small">新增交易</el-button>
        </div>
        <div class="right-actions">
          <el-button type="primary" size="small" icon="Download" @click="exportData">导出数据</el-button>
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-container">
        <el-table
          :data="transactions"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        >
          <el-table-column prop="id" label="交易号" min-width="100" />
          <el-table-column prop="type" label="交易类型" min-width="120" />
          <el-table-column prop="amount" label="金额" width="120">
            <template #default="scope">
              <span :class="Number(scope.row.amount) >= 0 ? 'income-text' : 'expense-text'">
                ¥{{ scope.row.amount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="交易时间" min-width="150" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已完成' ? 'success' : 'warning'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button type="text" size="small">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="50"
          :page-size="10"
        />
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.finance-container {
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

.finance-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 15px;

  .overview-card {
    background-color: #fff;
    border-radius: 4px;
    padding: 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    
    .card-title {
      font-size: 14px;
      color: #909399;
      margin-bottom: 10px;
    }
    
    .card-value {
      font-size: 24px;
      font-weight: 500;
      color: #303133;

      &.income {
        color: #67C23A;
      }
      
      &.expense {
        color: #F56C6C;
      }
      
      &.pending {
        color: #E6A23C;
      }
    }
  }
}

.search-section {
  background-color: #fff;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.table-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.table-container {
  margin-bottom: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}

.income-text {
  color: #67C23A;
}

.expense-text {
  color: #F56C6C;
}
</style> 