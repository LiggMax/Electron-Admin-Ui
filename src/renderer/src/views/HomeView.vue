<script setup>
import { ref, inject } from 'vue'
import { getPhoneList } from '../api/phone'
import UploadDialog from '../components/UploadDialog.vue'
import AppHeader from '../components/AppHeader.vue'

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
    <AppHeader title="卡商端-首页" />
    <!-- 内容区 -->
    <div class="content-area">
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
          class="data-table"
        >
          <el-table-column type="selection" width="40"></el-table-column>
          <el-table-column label="序号" width="60" align="center">
            <template #default="scope">
              {{ (pageNum - 1) * pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="phoneNumber" label="手机号码" min-width="120" show-overflow-tooltip></el-table-column>
          <el-table-column label="线路状态" min-width="90" align="center">
            <template #default="scope">
              <span :class="[ scope.row.lineStatus === 1 ? 'online' : 'offline']">
                {{ formatStatus(scope.row.lineStatus, 'line') }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="countryCode" label="号码归属国家" min-width="100" show-overflow-tooltip></el-table-column>
          <el-table-column prop="registrationTime" label="注册时间" min-width="160" show-overflow-tooltip></el-table-column>
          <el-table-column label="状态" min-width="80" align="center">
            <template #default="scope">
              <span :class="[ scope.row.usageStatus === 1 ? 'used' : 'unused']">
                {{ formatStatus(scope.row.usageStatus, 'usage') }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="150" fixed="right">
            <template #default="scope">
              <div class="operation-buttons">
                <el-button size="small" type="primary" text @click="handleView(scope.row)">查看</el-button>
                <el-button size="small" type="primary" text @click="handleEdit(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" text>删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          popper-class="pagination-popper"
          background
        ></el-pagination>
      </div>

      <!-- 上传弹窗 -->
      <UploadDialog v-model:visible="uploadDialogVisible" />
    </div>
  </div>
</template>

<style lang="less" scoped>
.home-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .content-area {
    flex: 1;
    overflow: auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
    position: relative;
    align-items: center;
    height: calc(100% - 60px);
    min-height: 500px;
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
    min-height: 50px;
    max-height: 50px;
    background-color: #fff;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    padding: 0 20px;
    width: 100%;
    max-width: 1200px;

    .project-item {
      display: flex;
      align-items: center;
      height: 32px;

      .label {
        font-size: 14px;
        color: #606266;
        white-space: nowrap;
        margin-right: 8px;
        font-weight: bold;
        height: 32px;
        line-height: 32px;
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
    min-height: 50px;
    max-height: 50px;
    background-color: #fff;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    padding: 0 20px;
    width: 100%;
    max-width: 1200px;

    .filter-items {
      display: flex;
      align-items: center;
      gap: 20px;
      width: 100%;
      height: 100%;

      .filter-item {
        display: flex;
        align-items: center;
        flex-shrink: 0;
        height: 32px;

        .label {
          font-size: 14px;
          color: #606266;
          white-space: nowrap;
          margin-right: 8px;
          height: 32px;
          line-height: 32px;
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
        height: 32px;
      }

      .upload-button {
        margin-left: 50px;
        height: 32px;
      }
    }
  }

  /* 表格容器 */
  .table-container {
    background-color: #fff;
    margin: 10px 0;
    padding: 15px;
    border-radius: 4px;
    border: 1px solid #ebeef5;
    width: 100%;
    max-width: 1200px;
    flex: 1;
    overflow: auto;
    min-height: 300px;
    max-height: calc(100vh - 250px);
    display: flex;
    flex-direction: column;

    .data-table {
      width: 100%;

      :deep(.el-table__header) {
        th {
          background-color: #f5f7fa;
          height: 40px !important;
          padding: 6px 0 !important;
          font-size: 14px;
          font-weight: 500;
        }
      }

      :deep(.el-table__body) {
        td {
          padding: 8px 0 !important;
          height: 48px !important;
          font-size: 14px;
        }
      }

      :deep(.el-table__row) {
        height: 48px !important;

        &:hover {
          background-color: #f5f7fa;
        }
      }
    }

    /* 状态标签样式 */
    .status-tag {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;

      &.online {
        background-color: #e1f3d8;
        color: #67c23a;
      }

      &.offline {
        background-color: #fde2e2;
        color: #f56c6c;
      }
    }

    .usage-tag {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;

      &.used {
        background-color: #e6f7ff;
        color: #1890ff;
      }

      &.unused {
        background-color: #f4f4f5;
        color: #909399;
      }
    }

    .operation-buttons {
      display: flex;
      justify-content: space-around;

      :deep(.el-button) {
        padding: 4px 8px;
        margin: 0 2px;
      }
    }
  }

  /* 分页区域 */
  .pagination-container {
    margin: 10px 0;
    padding: 10px 15px;
    background-color: #fff;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    display: flex;
    justify-content: flex-start;
    width: 100%;
    max-width: 1200px;
    position: sticky;
    bottom: 0;
    z-index: 10;
    height: 52px;

    :deep(.el-pagination) {
      width: 100%;
      padding: 0;
      justify-content: flex-start;
      height: 32px;

      .el-pagination__sizes {
        margin-right: 15px;
      }

      .el-pagination__jump {
        margin-left: 15px;
      }

      .btn-prev,
      .btn-next,
      .number {
        min-width: 32px;
        height: 32px;
        line-height: 32px;
      }
    }
  }

  /* 媒体查询 - 确保在小屏幕上表单元素不会缩得太小 */
  @media screen and (max-width: 1280px) {
    .project-selector-bar,
    .filter-bar,
    .table-container,
    .pagination-container {
      max-width: 100%;
    }

    .filter-items {
      flex-wrap: wrap;
      height: auto !important;
      padding: 5px 0;

      .filter-item,
      .filter-buttons,
      .upload-button {
        margin-bottom: 5px;
      }

      .upload-button {
        margin-left: 20px;
      }
    }
  }

  @media screen and (max-width: 768px) {
    .filter-bar {
      height: auto;
      min-height: 50px;
      max-height: none;
      padding-bottom: 10px;
    }

    .filter-items {
      flex-direction: column;
      align-items: flex-start;
      gap: 10px;

      .filter-item {
        width: 100%;

        .select-with-width {
          width: calc(100% - 60px);
        }
      }

      .filter-buttons,
      .upload-button {
        margin-top: 5px;
      }

      .upload-button {
        margin-left: 0;
      }
    }

    .operation-buttons {
      display: flex;
      flex-direction: column;
      align-items: center;

      :deep(.el-button) {
        margin: 2px 0;
      }
    }
  }

  /* 控制表单元素大小不随页面缩放而改变 */
  :deep(.el-select),
  :deep(.el-input),
  :deep(.el-button) {
    transform: scale(1) !important;
    transform-origin: left center;
    height: 32px !important;
    line-height: 32px !important;
  }

  :deep(.el-input__wrapper) {
    height: 32px !important;
  }

  :deep(.el-input__inner) {
    height: 32px !important;
    line-height: 32px !important;
  }

  :deep(.el-button) {
    &.el-button--small {
      padding: 0 15px !important;
      font-size: 12px !important;
    }
  }

  /* 确保各区域内容不会超出 */
  :deep(.el-table__body-wrapper) {
    overflow-y: auto !important;
  }
}
</style>
