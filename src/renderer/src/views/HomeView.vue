<script setup>
import { ref, reactive, inject } from 'vue'
import { getPhoneList } from '../api/phone'

// 获取全局消息服务
const message = inject('message')

// 顶部接码项目选项
const projectOptions = [
  { label: '全部项目', value: 'all' },
  { label: '淘宝', value: 'taobao' },
  { label: '京东', value: 'jd' },
  { label: '拼多多', value: 'pdd' },
  { label: '抖音', value: 'douyin' }
]

// 选中的接码项目
const selectedProject = ref('all')

// 国家选项
const countryOptions = [
  { label: '全部地区', value: '' },
  { label: '中国', value: 'CN' },
  { label: '美国', value: 'US' },
  { label: '英国', value: 'UK' },
  { label: '日本', value: 'JP' },
  { label: '韩国', value: 'KR' },
  { label: '俄罗斯', value: 'RU' },
  { label: '其他', value: 'OTHER' }
]

// 表格数据
const tableData = ref([
  {
    phoneId: '1001',
    phoneNumber: '199*****909',
    lineStatus: 1,
    countryCode: '中国香港',
    registrationTime: '2023-05-15 14:30:22',
    status: '1'
  },
  {
    id: '1002',
    cardNumber: '6224 **** **** 5678',
    transactionAmount: 5000.0,
    paymentMethod: '微信支付',
    transactionTime: '2023-05-15 13:25:10',
    status: '处理中',
    operator: '李四'
  },
  {
    id: '1003',
    cardNumber: '6223 **** **** 9012',
    transactionAmount: 8000.0,
    paymentMethod: '银联',
    transactionTime: '2023-05-15 11:45:33',
    status: '失败',
    operator: '王五'
  }
])

// 分页设置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 100
})

const countryCode =ref('')// 国家地区
const usageStatus = ref('')

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
  console.log('搜索条件', {
    project: selectedProject.value,
    ...searchForm
  })
  // 实际应该调用API进行搜索
}

// 重置搜索
const resetSearch = () => {
  selectedProject.value = 'all'
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
}

// 批量操作
const handleBatchOperation = (operation) => {
  if (multipleSelection.value.length === 0) {
    message.warning('请先选择记录')
    return
  }
  console.log(`批量${operation}`, multipleSelection.value)
}

const pageNum = ref(1)// 当前页码
const total = ref(22)// 总数
const pageSize = ref(10)// 每页条数

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  // 重新加载数据
}

// 导出数据
const exportData = () => {
  console.log('导出数据')
}
/**
 * 获取卡号数据列表
 */
const getCardDataList = async () => {
  // 这里应该实现获取卡号数据列表的逻辑
  let params ={
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    countryCode: countryCode.value ? countryCode.value : '',
    usageStatus: usageStatus.value ? usageStatus.value : ''
  }
  let result = await getPhoneList(params)
  total.value = result.data.total
  tableData.value = result.data.items
}
getCardDataList()
</script>

<template>
  <div class="home-container">
    <!-- 顶部标题区 -->
    <div class="header-title">
      <span>卡商平台管理系统</span>
    </div>

    <!-- 接码项目独立一栏 -->
    <div class="project-selector-bar">
      <div class="project-item">
        <span class="label project-label">接码项目：</span>
        <el-select
          v-model="selectedProject"
          placeholder="请选择项目"
          clearable
          size="small"
          class="select-with-width"
        >
          <el-option
            v-for="item in projectOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </div>
    </div>

    <!-- 搜索筛选区 -->
    <div class="filter-bar">
      <div class="filter-items">
        <div class="filter-item">
          <span class="label">国家：</span>
          <el-select
            v-model="countryCode"
            placeholder="请选择"
            clearable
            size="small"
            class="select-with-width"
          >
            <el-option
              v-for="item in countryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>

        <div class="filter-item">
          <span class="label">状态：</span>
          <el-select
            v-model="UsageStatus"
            placeholder="请选择"
            clearable
            size="small"
            class="select-with-width"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>

        <div class="filter-buttons">
          <el-button type="primary" size="small" @click="handleSearch">搜索</el-button>
          <el-button size="small" @click="resetSearch">重置</el-button>
        </div>
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="table-container">
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40"></el-table-column>
        <el-table-column prop="phoneId" label="序号" width="80"></el-table-column>
        <el-table-column prop="phoneNumber" label="手机号码" width="180"></el-table-column>
        <el-table-column prop="lineStatus" label="线路状态" width="120"></el-table-column>
        <el-table-column prop="countryCode" label="号码归属国家" width="120"></el-table-column>
        <el-table-column prop="registrationTime" label="注册时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag
              :type="
                scope.row.status === '成功'
                  ? 'success'
                  : scope.row.status === '处理中'
                    ? 'warning'
                    : 'danger'
              "
              >{{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="mini" type="text" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" style="color: #f56c6c">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
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
  background-color: #f8f8f8;
}

/* 顶部标题区 */
.header-title {
  height: 50px;
  background-color: #4169E1;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

/* 接码项目选择栏 */
.project-selector-bar {
  height: 50px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  padding: 0 20px;

  .project-item {
    display: flex;
    align-items: center;

    .label {
      font-size: 14px;
      color: #606266;
      white-space: nowrap;
      margin-right: 8px;
      font-weight: bold;
    }

    .project-label {
      font-size: 15px;
      color: #303133;
    }

    .select-with-width {
      width: 180px;
    }
  }
}

/* 筛选栏样式 */
.filter-bar {
  height: 50px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  padding: 0 20px;

  .filter-items {
    display: flex;
    align-items: center;
    width: 100%;

    .filter-item {
      display: flex;
      align-items: center;
      margin-right: 20px;

      .label {
        font-size: 14px;
        color: #606266;
        white-space: nowrap;
        margin-right: 8px;
      }

      .select-with-width {
        width: 180px;
      }
    }

    .filter-buttons {
      display: flex;
      gap: 10px;
    }
  }
}

/* 表格容器 */
.table-container {
  background-color: #fff;
  margin: 10px;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  flex: 1;
  overflow: auto;
}

/* 分页区域 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
