<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPhoneList } from '../api/phone'
import UploadDialog from '../components/UploadDialog.vue'
import AppHeader from '../components/AppHeader.vue'

// 路由
const router = useRouter()

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
  { label: '韩国', value: '韩国' }
]

// 表格数据
const tableData = ref([])
const countryCode = ref('') // 国家地区
const usageStatus = ref('') // 使用状态
const searchKeyword = ref('') // 搜索关键字

// 表格多选
const multipleSelection = ref([])
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection
}

// 表格操作 - 查看详情
const handleView = (row) => {
  console.log('查看详情', row)
  // 跳转到详情页面，携带手机号ID参数
  router.push({
    path: '/phone-detail',
    query: { phoneId: row.phoneId }
  })
}

const pageNum = ref(1) // 当前页码
const pageSize = ref(10) // 每页条数

// 防抖函数
const debounce = (fn, delay = 300) => {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    // 立即设置加载状态
    loading.value = true
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// // 分页切换 - 使用防抖
// const handleCurrentChange = debounce((page) => {
//   pageNum.value = page
//   getCardDataList()
// })
//
// // 每页条数切换 - 使用防抖
// const handleSizeChange = debounce((size) => {
//   pageSize.value = size
//   pageNum.value = 1
//   getCardDataList()
// })

// 搜索 - 使用防抖
const handleSearch = debounce(() => {
  pageNum.value = 1
  getCardDataList()
})

// 重置搜索
const resetSearch = debounce(() => {
  selectedProject.value = 'all'
  countryCode.value = ''
  usageStatus.value = ''
  searchKeyword.value = '' // 清空搜索关键字
  pageNum.value = 1
  getCardDataList()
})

/**
 * 格式化日期
 * @param {string} dateStr - 日期字符串
 * @returns {string} 格式化后的日期
 */
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${year}/${month}/${day}`
}

// 数据加载状态
const loading = ref(false)

/**
 * 获取卡号数据列表
 */
const getCardDataList = async () => {
  try {
    // 获取卡号数据列表的逻辑
    let params = {
      countryCode: countryCode.value || '',
      usageStatus: usageStatus.value === undefined ? '' : usageStatus.value,
      project: selectedProject.value === 'all' ? '' : selectedProject.value,
      keyword: searchKeyword.value || '' // 添加搜索关键字参数
    }

    // 移除所有undefined或null的参数
    Object.keys(params).forEach((key) => {
      if (params[key] === undefined || params[key] === null) {
        params[key] = ''
      }
    })

    // 确保至少显示加载状态一定时间，避免闪烁
    const minLoadingTime = 600 // 最小加载时间（毫秒）
    const startTime = Date.now()

    let result = await getPhoneList(params)

    const elapsedTime = Date.now() - startTime
    const remainingTime = Math.max(0, minLoadingTime - elapsedTime)

    // 如果加载太快，则等待一段时间再更新UI，避免闪烁
    setTimeout(() => {
      // 先更新数据
      tableData.value = result.data

      // 数据更新后再关闭loading状态
      nextTick(() => {
        loading.value = false
      })
    }, remainingTime)
  } catch (error) {
    setTimeout(() => {
      loading.value = false
    }, 600) // 确保错误状态也有显示最小时间
    console.error('获取数据失败', error)
  }
}

// 上传
const handleUpload = () => {
  uploadDialogVisible.value = true
  // 触发上传弹窗关闭后的回调
  setTimeout(() => {
    if (Math.random() > 0.5) {
      // 不再使用message
    }
  }, 1000)
}

// 处理上传成功事件
const handleUploadSuccess = () => {
  // 设置加载状态
  loading.value = true

  // 重新加载数据
  setTimeout(() => {
    getCardDataList()
  }, 100)
}

// 窗口大小调整防抖
let resizeTimer = null
const handleResize = () => {
  if (resizeTimer) clearTimeout(resizeTimer)
  resizeTimer = setTimeout(() => {
    // 触发表格重新计算布局
    if (tableRef.value) {
      tableRef.value.doLayout()
    }
  }, 100)
}

// 表格行类名
const tableRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 表格引用
const tableRef = ref(null)

// 组件挂载和卸载时的事件处理
onMounted(() => {
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize)

  // 设置初始加载状态
  loading.value = true

  // 延迟加载数据，避免初始渲染压力
  setTimeout(() => {
    getCardDataList()
  }, 100)
})

onUnmounted(() => {
  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize)
  // 清除定时器
  if (resizeTimer) clearTimeout(resizeTimer)
})
</script>

<template>
  <div class="home-container">
    <AppHeader title="卡商端-首页" />
    <!-- 内容区 -->
    <div class="content-area">
      <!-- 接码项目独立一栏 -->
      <div class="project-selector-bar">
        <div class="project-selector-content">
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

          <!--上传按钮-->
          <div class="upload-button">
            <el-button type="primary" size="small" @click="handleUpload" class="custom-upload-btn">
              <img src="../assets/svg/add.svg" alt="" class="uploadIcon" /> 上传
            </el-button>
          </div>
        </div>
      </div>

      <!-- 搜索筛选区 -->
      <div class="filter-bar">
        <div class="filter-items">
          <div class="left-filters">
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

            <!--搜索输入框-->
            <div class="filter-item search-item">
              <span class="label">搜索：</span>
              <el-input
                v-model="searchKeyword"
                placeholder="请输入搜索内容"
                clearable
                size="small"
                class="input-with-width"
                @keyup.enter="handleSearch"
              ></el-input>
            </div>
            <div class="search-icon-wrapper" @click="handleSearch">
              <img src="../assets/svg/iocn/search.svg" width="25px" alt="搜索" />
            </div>
          </div>
          <div class="right-aligned-controls">
            <div class="filter-buttons">
              <el-button size="small" @click="resetSearch">重置</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 表格区域 -->
      <div class="table-container">
        <!-- 表格加载状态指示器 -->
        <div class="table-loading-mask" v-if="loading">
          <div class="table-loading-indicator">
            <div class="spinner-icon"></div>
            <span class="loading-text">加载数据中...</span>
          </div>
        </div>

        <el-table
          :data="tableData"
          border
          style="width: 100%"
          @selection-change="handleSelectionChange"
          class="data-table"
          height="100%"
          v-bind="{ 'scroll-wheel-enabled': true }"
          :row-class-name="tableRowClassName"
          :virtual-scrolling="true"
          :estimate-row-height="48"
          :cache="200"
        >
          <el-table-column type="selection" width="40"></el-table-column>
          <el-table-column label="序号" width="60" align="center">
            <template #default="scope">
              {{ (pageNum - 1) * pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column
            prop="phoneNumber"
            label="手机号码"
            min-width="120"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column
            prop="regionName"
            label="号码归属国家"
            min-width="100"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column label="注册时间" min-width="110" show-overflow-tooltip>
            <template #default="scope">
              {{ formatDate(scope.row.registrationTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="100" fixed="right" align="center">
            <template #default="scope">
              <el-button
                type="text"
                size="small"
                @click="handleView(scope.row)"
                class="text-button"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="empty-placeholder" v-if="!loading && tableData.length === 0">
          <el-empty description="暂无数据"></el-empty>
        </div>
      </div>

      <!-- 上传弹窗 -->
      <UploadDialog
        v-model:visible="uploadDialogVisible"
        @uploadSuccess="handleUploadSuccess"
      />
    </div>
  </div>
</template>

<style lang="less" scoped>
.home-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  user-select: none;
  -webkit-user-select: none;

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
    contain: content;
    will-change: transform;
    -webkit-overflow-scrolling: touch;
    overscroll-behavior: contain;
    scroll-behavior: smooth;
  }

  /* 表格加载状态样式 */
  .table-loading-mask {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.9);
    z-index: 10;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: opacity 0.3s;
    border-radius: 4px;
  }

  .table-loading-indicator {
    display: flex;
    flex-direction: row;
    align-items: center;
    animation: fadeInScale 0.3s ease-out;

    .spinner-icon {
      width: 24px;
      height: 24px;
      border: 2px solid rgba(24, 144, 255, 0.2);
      border-top: 2px solid #1890ff;
      border-radius: 50%;
      animation: rotating 0.8s linear infinite;
      margin-right: 12px;
    }

    .loading-text {
      font-size: 16px;
      font-weight: 500;
      color: #606266;
    }
  }

  @keyframes fadeInScale {
    from {
      opacity: 0;
      transform: scale(0.9);
    }
    to {
      opacity: 1;
      transform: scale(1);
    }
  }

  .uploadIcon {
    width: 16px;
    height: 16px;
    margin-right: 5px;
  }

  .custom-upload-btn {
    background-color: #1890ff;
    border-color: #1890ff;
    color: white;
    padding: 8px 16px;
    font-size: 14px;
    height: auto;
  }

  .custom-upload-btn:hover {
    background-color: rgba(24, 144, 255, 0.94);
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
    will-change: transform;

    .project-selector-content {
      display: flex;
      align-items: center;
      width: 100%;
      justify-content: space-between;

      .project-item {
        display: flex;
        align-items: center;
        height: 32px;
        margin-right: 20px;

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
          flex-shrink: 0;
        }
      }

      .upload-button {
        height: 32px;
        margin-left: auto;

        .custom-upload-btn {
          background-color: #1890ff;
          border-color: #1890ff;
          color: white;
          padding: 6px 16px;
          font-size: 14px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.3s;

          &:hover {
            background-color: rgba(24, 144, 255, 0.9);
            transform: translateY(-1px);
          }

          &:active {
            transform: translateY(0);
          }

          .uploadIcon {
            width: 16px;
            height: 16px;
            margin-right: 5px;
          }
        }
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
    contain: layout style;
    will-change: transform;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .filter-items {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      height: 100%;
      position: relative;

      .left-filters {
        display: flex;
        align-items: center;

        .filter-item {
          display: flex;
          align-items: center;
          flex-shrink: 0;
          height: 32px;
          margin-right: 20px;

          &:last-of-type {
            margin-right: 15px;
          }

          .label {
            font-size: 14px;
            color: #606266;
            white-space: nowrap;
            margin-right: 8px;
            display: inline-flex;
            align-items: center;
            height: 32px;
          }

          .select-with-width {
            width: 100px;
            flex-shrink: 0;
          }

          .input-with-width {
            width: 180px;
            flex-shrink: 0;
          }
        }

        .search-item {
          display: flex;
          align-items: center;
          position: relative;
        }

        .search-icon-wrapper {
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          padding: 5px 8px;
          margin-left: 10px;
          margin-right: 10px;
          border-radius: 4px;
          transition: all 0.2s ease;

          img {
            display: block;
            opacity: 0.7;
            transition: opacity 0.2s;
          }

          &:hover {
            background-color: rgba(24, 144, 255, 0.1);

            img {
              opacity: 1;
            }
          }

          &:active {
            transform: scale(0.95);
          }
        }
      }

      .right-aligned-controls {
        display: flex;
        align-items: center;
        margin-left: auto;
        height: 32px;

        .filter-buttons {
          display: flex;
          margin-right: 15px;

          .el-button {
            transition: all 0.3s;

            &:hover {
              transform: translateY(-1px);
              box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            }

            &:active {
              transform: translateY(0);
            }
          }
        }

        .upload-button {
          height: 32px;
        }
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
    contain: content;
    will-change: transform;
    position: relative;

    .empty-placeholder {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    }

    .data-table {
      width: 100%;
      will-change: transform;
      // 表格在数据加载中或加载完成时的过渡
      transition: opacity 0.3s ease;
      opacity: 1;

      // 当数据正在加载时，表格内容稍微淡出
      &.loading {
        opacity: 0.6;
      }

      :deep(.el-table__header) {
        th {
          background-color: #f0f0f0;
          height: 40px !important;
          padding: 6px 0 !important;
          font-size: 14px;
          font-weight: 500;
        }
      }

      :deep(.el-table__body-wrapper) {
        will-change: transform;
        contain: content;
        overflow-y: auto !important;

        &::-webkit-scrollbar {
          width: 6px;
          height: 6px;
        }

        &::-webkit-scrollbar-thumb {
          border-radius: 3px;
          background-color: rgba(144, 147, 153, 0.3);
        }

        &::-webkit-scrollbar-track {
          background-color: transparent;
        }
      }

      :deep(.el-table__body) {
        td {
          padding: 8px 0 !important;
          height: 48px !important;
          font-size: 14px;
          user-select: none;
          -webkit-user-select: none;
          border-bottom: 1px solid #ebeef5;
          transition: none;
        }
      }

      :deep(.el-table__row) {
        height: 48px !important;

        &:hover > td {
          background-color: #f5f7fa !important;
        }
      }

      :deep(.even-row) {
        background-color: #fafafa;
      }

      :deep(.odd-row) {
        background-color: #ffffff;
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
    will-change: transform;
    contain: layout style;
    transition: opacity 0.3s;

    &:has(> .el-pagination[disabled='true']) {
      opacity: 0.6;
      pointer-events: none;
    }

    :deep(.el-pagination) {
      width: 100%;
      padding: 0;
      justify-content: flex-start;
      height: 32px;
      transition: opacity 0.3s;

      &[disabled='true'] {
        opacity: 0.7;
        pointer-events: none;
      }

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

  @keyframes rotating {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }

  /* 媒体查询 - 确保在小屏幕上表单元素不会缩得太小 */
  @media screen and (max-width: 1280px) {
    .project-selector-bar,
    .filter-bar,
    .table-container,
    .pagination-container {
      max-width: 100%;
      contain: content;
    }
  }

  @media screen and (max-width: 768px) {
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
    transform: none !important;
  }

  :deep(.el-input__wrapper) {
    line-height: 1;
    padding: 1px 11px;
    box-shadow: 0 0 0 1px #dcdfe6 inset;
    transition: all 0.2s;

    &:hover {
      box-shadow: 0 0 0 1px #c0c4cc inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px #409eff inset !important;
    }
  }

  :deep(.el-input__inner) {
    height: 30px !important;
    line-height: 30px !important;
    user-select: text;
    -webkit-user-select: text;
  }

  :deep(.el-select .el-input__wrapper) {
    padding: 0 11px;
  }

  /* 表格操作按钮下拉菜单样式 */

  :deep(.el-dropdown) {
    vertical-align: middle;
  }

  .text-button {
    color: #409eff;
    cursor: pointer;
    font-size: 14px;
  }

  .text-button:hover {
    color: #66b1ff;
    text-decoration: underline;
  }

  :deep(.el-dropdown-menu__item) {
    display: flex;
    align-items: center;
    padding: 8px 16px;

    .el-icon {
      margin-right: 8px;
      font-size: 16px;
    }

    &.is-disabled {
      cursor: not-allowed;
      opacity: 0.5;
    }
  }
}
</style>
