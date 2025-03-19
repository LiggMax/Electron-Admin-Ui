<script setup>
import { ref } from 'vue'

// 订单数据
const orders = ref([
  { id: 'ORD20230001', product: '游戏充值卡50元', customer: '张三', amount: '48.00', time: '2023-12-10 10:30:45', status: '已完成' },
  { id: 'ORD20230002', product: '话费充值卡100元', customer: '李四', amount: '98.50', time: '2023-12-11 14:25:32', status: '已完成' },
  { id: 'ORD20230003', product: '购物卡200元', customer: '王五', amount: '196.00', time: '2023-12-12 09:18:24', status: '处理中' },
  { id: 'ORD20230004', product: '视频会员卡-年卡', customer: '赵六', amount: '228.00', time: '2023-12-12 16:42:11', status: '已完成' },
  { id: 'ORD20230005', product: '游戏充值卡100元', customer: '钱七', amount: '95.00', time: '2023-12-13 11:05:39', status: '已取消' }
])

// 订单状态选项
const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '已完成', value: '已完成' },
  { label: '处理中', value: '处理中' },
  { label: '已取消', value: '已取消' }
]

// 搜索条件
const searchForm = ref({
  orderId: '',
  customer: '',
  status: '',
  dateRange: []
})

// 查看订单详情
const viewOrderDetail = (row) => {
  console.log('查看订单详情：', row)
  // 这里应该实现查看详情逻辑
}

// 取消订单
const cancelOrder = (row) => {
  console.log('取消订单：', row)
  // 这里应该实现取消订单逻辑
}

// 搜索方法
const handleSearch = () => {
  console.log('搜索条件：', searchForm.value)
  // 这里应该实现搜索逻辑
}

// 重置搜索条件
const resetSearch = () => {
  searchForm.value = {
    orderId: '',
    customer: '',
    status: '',
    dateRange: []
  }
}
</script>

<template>
  <div class="orders-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="title">订单管理</div>
      <div class="actions">
        <i class="el-icon-refresh"></i>
        <i class="el-icon-setting"></i>
        <i class="el-icon-message"></i>
        <i class="el-icon-user"></i>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-form">
          <el-form :inline="true" :model="searchForm" class="form-inline">
            <el-form-item label="订单号">
              <el-input v-model="searchForm.orderId" placeholder="请输入订单号" size="small" />
            </el-form-item>
            <el-form-item label="客户名">
              <el-input v-model="searchForm.customer" placeholder="请输入客户名" size="small" />
            </el-form-item>
            <el-form-item label="订单状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" size="small">
                <el-option 
                  v-for="item in statusOptions" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
            <el-form-item label="下单时间">
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

      <!-- 表格 -->
      <div class="table-container">
        <el-table
          :data="orders"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        >
          <el-table-column prop="id" label="订单号" min-width="120" />
          <el-table-column prop="product" label="商品" min-width="150" />
          <el-table-column prop="customer" label="客户" width="100" />
          <el-table-column prop="amount" label="金额" width="100">
            <template #default="scope">
              ¥{{ scope.row.amount }}
            </template>
          </el-table-column>
          <el-table-column prop="time" label="下单时间" min-width="150" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="
                scope.row.status === '已完成' ? 'success' : 
                scope.row.status === '处理中' ? 'warning' : 'info'
              ">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="text" size="small" @click="viewOrderDetail(scope.row)">详情</el-button>
              <el-button 
                v-if="scope.row.status !== '已取消' && scope.row.status !== '已完成'"
                type="text" 
                size="small" 
                style="color: #F56C6C" 
                @click="cancelOrder(scope.row)"
              >
                取消
              </el-button>
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
.orders-container {
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

.search-section {
  background-color: #fff;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
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
</style> 