<script setup>
import { ref, inject } from 'vue'
import { getPhoneList } from '../api/phone'
import UploadDialog from '../components/UploadDialog.vue'

// 获取全局消息服务
const message = inject('message')

// 上传弹窗可见性
const uploadDialogVisible = ref(false)

// 顶部接码项目选项
const projectOptions = [
  { label: '全部项目', value: 'all' },
  { label: 'Facebook', value: 'Facebook' },
  { label: 'TikTok', value: 'TikTok' },
  { label: 'Instagram', value: 'Instagram' },
  { label: 'YouTube', value: 'YouTube' }
]

// 选中的接码项目
const selectedProject = ref('all')

// 国家选项
const countryOptions = [
  { label: '全部地区', value: '' },
  { label: '中国', value: '中国' },
  { label: '美国', value: '美国' },
  { label: '英国', value: '英国' },
  { label: '日本', value: '日本' },
  { label: '韩国', value: '韩国' },
  { label: '俄罗斯', value: '俄罗斯' }
]

// 表格数据
const tableData = ref([
  {
    phoneId: '1001',
    phoneNumber: '199*****909',
    lineStatus: 1,
    countryCode: '中国香港',
    registrationTime: '2023-05-15 14:30:22',
    usageStatus: '1'
  }
])

// // 分页设置
// const pagination = reactive({
//   currentPage: 1,
//   pageSize: 10,
//   total: 100
// })

const countryCode = ref('') // 国家地区
const usageStatus = ref('') // 使用状态

// 状态
const statusOptions = [
  { label: '未使用', value: 0 },
  { label: '已使用', value: 1 }
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

// 批量操作
// const handleBatchOperation = (operation) => {
//   if (multipleSelection.value.length === 0) {
//     message.warning('请先选择记录')
//     return
//   }
//   console.log(`批量${operation}`, multipleSelection.value)
// }

const pageNum = ref(1) // 当前页码
const total = ref(22) // 总数
const pageSize = ref(10) // 每页条数

// 分页切换
const handleCurrentChange = (page) => {
  pageNum.value = page
  getCardDataList()
}

// 分页切换
const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  getCardDataList()
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  getCardDataList()
}

// 重置搜索
const resetSearch = () => {
  selectedProject.value = 'all'
  countryCode.value = ''
  usageStatus.value = ''
  pageNum.value = 1
  getCardDataList()
}

/**
 * 获取卡号数据列表
 */
const formatStatus = (status, type) => {
  if (type === 'line') {
    return status === 1 ? '在线' : '离线'
  }
  if (type === 'usage') {
    return status === 1 ? '已使用' : '未使用'
  }
}

const getCardDataList = async () => {
  // 获取卡号数据列表的逻辑
  let params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    countryCode: countryCode.value ? countryCode.value : '',
    usageStatus: usageStatus.value
  }
  let result = await getPhoneList(params)
  total.value = result.data.total
  tableData.value = result.data.items
}

// 上传
const handleUpload = () => {
  uploadDialogVisible.value = true
  // 触发上传弹窗关闭后的回调
  setTimeout(() => {
    if (Math.random() > 0.5) {
      message.success('数据导入成功')
    }
  }, 1000)
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
            v-model="usageStatus"
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
        <!--上传按钮-->
        <div class="upload-button">
          <el-button type="custom-green" size="small" @click="handleUpload" class="custom-upload-btn">
            <img src="../assets/svg/add.svg" alt="" class="uploadIcon"> 上传
          </el-button>
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
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (pageNum - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="phoneNumber" label="手机号码" width="180"></el-table-column>
        <el-table-column label="线路状态" width="120">
          <template #default="scope">
            {{ formatStatus(scope.row.lineStatus, 'line') }}
          </template>
        </el-table-column>
        <el-table-column prop="countryCode" label="号码归属国家" width="120"></el-table-column>
        <el-table-column prop="registrationTime" label="注册时间" width="180"></el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            {{ formatStatus(scope.row.usageStatus, 'usage') }}
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
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </div>

    <!-- 上传弹窗 -->
    <UploadDialog v-model:visible="uploadDialogVisible" />
  </div>
</template>

<style lang="less" scoped>
.home-container {
  width: 100%;
  height: 100%;
  min-width: 1200px;
  display: flex;
  flex-direction: column;
  background-color: #f8f8f8;
  overflow: auto;
}

.uploadIcon {
  width: 16px;
  height: 16px;
  margin-right: 5px;
}

.custom-upload-btn {
  background-color: #1890ff; /* 自定义按钮颜色 */
  border-color: #1890ff;
  color: white;
  padding: 8px 16px; /* 调整内边距改变大小 */
  font-size: 14px;
  height: auto; /* 覆盖Element Plus默认高度 */
}

.custom-upload-btn:hover {
  background-color: rgba(24, 144, 255, 0.94); /* 悬停时的颜色 */
  border-color: rgba(24, 144, 255, 0.94);
  color: white;
}

/* 顶部标题区 */
.header-title {
  height: 50px;
  background-color: rgb(255, 255, 255);
  color: #000000;
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
  min-width: 800px;

  .filter-items {
    display: flex;
    align-items: center;
    gap: 20px;
    width: 100%;

    .filter-item {
      display: flex;
      align-items: center;
      flex-shrink: 0;

      .label {
        font-size: 14px;
        color: #606266;
        white-space: nowrap;
        margin-right: 8px;
      }

      .select-with-width {
        width: 180px;
        flex-shrink: 0;
      }
    }

    .filter-buttons {
      display: flex;
      gap: 10px;
      flex-shrink: 0;
    }

    .upload-button {
      margin-left: 50px;
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
  display: flex;
  flex-direction: column;
  min-width: 800px;
  overflow: hidden;

  .el-table {
    flex: 1;
    overflow: auto;
  }
}

/* 分页区域 */
.pagination {
  margin-top: 15px;
  padding: 10px 0;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-start;

  :deep(.el-pagination) {
    width: 100%;
    padding: 0;
    justify-content: flex-start;

    .el-pagination__sizes {
      margin-right: 15px;
    }
  }
}

/* 弹窗样式已在App.vue中全局定义 */
</style>
