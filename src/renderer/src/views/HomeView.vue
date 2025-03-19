<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 表格数据
const tableData = ref([
  {
    id: '1001',
    cardNumber: '6225 **** **** 1234',
    transactionAmount: 10000.00,
    paymentMethod: '支付宝',
    transactionTime: '2023-05-15 14:30:22',
    status: '成功',
    operator: '张三'
  },
  {
    id: '1002',
    cardNumber: '6224 **** **** 5678',
    transactionAmount: 5000.00,
    paymentMethod: '微信支付',
    transactionTime: '2023-05-15 13:25:10',
    status: '处理中',
    operator: '李四'
  },
  {
    id: '1003',
    cardNumber: '6223 **** **** 9012',
    transactionAmount: 8000.00,
    paymentMethod: '银联',
    transactionTime: '2023-05-15 11:45:33',
    status: '失败',
    operator: '王五'
  },
  {
    id: '1004',
    cardNumber: '6222 **** **** 3456',
    transactionAmount: 12000.00,
    paymentMethod: '支付宝',
    transactionTime: '2023-05-14 16:20:18',
    status: '成功',
    operator: '赵六'
  },
  {
    id: '1005',
    cardNumber: '6221 **** **** 7890',
    transactionAmount: 6000.00,
    paymentMethod: '微信支付',
    transactionTime: '2023-05-14 09:10:45',
    status: '成功',
    operator: '张三'
  },
  {
    id: '1006',
    cardNumber: '6220 **** **** 1357',
    transactionAmount: 9000.00,
    paymentMethod: '银联',
    transactionTime: '2023-05-13 15:30:22',
    status: '处理中',
    operator: '李四'
  },
  {
    id: '1007',
    cardNumber: '6219 **** **** 2468',
    transactionAmount: 15000.00,
    paymentMethod: '支付宝',
    transactionTime: '2023-05-13 11:05:38',
    status: '成功',
    operator: '王五'
  },
  {
    id: '1008',
    cardNumber: '6218 **** **** 3579',
    transactionAmount: 7500.00,
    paymentMethod: '微信支付',
    transactionTime: '2023-05-12 14:40:12',
    status: '失败',
    operator: '赵六'
  },
  {
    id: '1009',
    cardNumber: '6217 **** **** 4680',
    transactionAmount: 11000.00,
    paymentMethod: '银联',
    transactionTime: '2023-05-12 10:15:30',
    status: '成功',
    operator: '张三'
  },
  {
    id: '1010',
    cardNumber: '6216 **** **** 5791',
    transactionAmount: 4500.00,
    paymentMethod: '支付宝',
    transactionTime: '2023-05-11 16:55:08',
    status: '成功',
    operator: '李四'
  }
])

// 分页设置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 100
})

// 搜索表单
const searchForm = reactive({
  cardNumber: '',
  timeRange: '',
  status: '',
  operator: ''
})

// 状态选项
const statusOptions = [
  { label: '全部', value: '' },
  { label: '成功', value: '成功' },
  { label: '处理中', value: '处理中' },
  { label: '失败', value: '失败' }
]

// 操作员选项
const operatorOptions = [
  { label: '全部', value: '' },
  { label: '张三', value: '张三' },
  { label: '李四', value: '李四' },
  { label: '王五', value: '王五' },
  { label: '赵六', value: '赵六' }
]

// 表格多选
const multipleSelection = ref([])
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

// 表格操作
const handleView = (row) => {
  console.log('查看详情', row)
}

const handleEdit = (row) => {
  console.log('编辑', row)
}

// 搜索
const handleSearch = () => {
  console.log('搜索条件', searchForm)
  // 实际应该调用API进行搜索
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
}

// 批量操作
const handleBatchOperation = (operation) => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请先选择记录')
    return
  }
  console.log(`批量${operation}`, multipleSelection.value)
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  // 重新加载数据
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  // 重新加载数据
}

// 导出数据
const exportData = () => {
  console.log('导出数据')
}
</script>

<template>
  <div class="home-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="title">卡商平台管理系统</div>
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
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="卡号">
            <el-input v-model="searchForm.cardNumber" placeholder="请输入卡号" clearable></el-input>
          </el-form-item>
          <el-form-item label="交易时间">
            <el-date-picker
              v-model="searchForm.timeRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="交易状态">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable>
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="操作员">
            <el-select v-model="searchForm.operator" placeholder="请选择" clearable>
              <el-option
                v-for="item in operatorOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <div class="table-area">
        <div class="table-header">
          <div class="left">
            <el-button type="primary" size="small" icon="el-icon-plus">新增交易</el-button>
            <el-button type="danger" size="small" icon="el-icon-delete" @click="handleBatchOperation('删除')">批量删除</el-button>
            <el-button type="success" size="small" icon="el-icon-check" @click="handleBatchOperation('审核')">批量审核</el-button>
          </div>
          <div class="right">
            <el-button type="info" size="small" icon="el-icon-download" @click="exportData">导出数据</el-button>
          </div>
        </div>

        <el-table
          :data="tableData"
          border
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            type="selection"
            width="55"
          ></el-table-column>
          <el-table-column
            prop="id"
            label="交易ID"
            width="80"
          ></el-table-column>
          <el-table-column
            prop="cardNumber"
            label="卡号"
            width="180"
          ></el-table-column>
          <el-table-column
            prop="transactionAmount"
            label="交易金额"
            width="120"
          >
            <template #default="scope">
              <span>¥ {{ scope.row.transactionAmount.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="paymentMethod"
            label="支付方式"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="transactionTime"
            label="交易时间"
            width="180"
          ></el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="100"
          >
            <template #default="scope">
              <el-tag
                :type="scope.row.status === '成功' ? 'success' : scope.row.status === '处理中' ? 'warning' : 'danger'"
              >{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="operator"
            label="操作员"
            width="100"
          ></el-table-column>
          <el-table-column
            label="操作"
            width="150"
          >
            <template #default="scope">
              <el-button
                size="mini"
                type="text"
                @click="handleView(scope.row)"
              >查看</el-button>
              <el-button
                size="mini"
                type="text"
                @click="handleEdit(scope.row)"
              >编辑</el-button>
              <el-button
                size="mini"
                type="text"
                style="color: #F56C6C"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pagination.currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
          ></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.home-container {
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

.search-area {
  background-color: #fff;
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.table-area {
  background-color: #fff;
  padding: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    .left {
      display: flex;
      gap: 10px;
    }
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 