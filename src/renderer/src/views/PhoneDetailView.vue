<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPhoneDetail } from '../api/phone'
import AppHeader from '../components/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const phoneId = ref(route.query.phoneId)
const loading = ref(true)
const phoneDetail = reactive({
  basicInfo: {},
  projects: []
})

// 获取手机号详情信息
const fetchPhoneDetail = async () => {
  if (!phoneId.value) {
    ElMessage.error('参数错误，未找到手机号ID')
    router.push('/home')
    return
  }

  loading.value = true
  try {
    const response = await getPhoneDetail(phoneId.value)
    if (response.code === 200 && response.data) {
      const data = response.data
      phoneDetail.basicInfo = data.basicInfo || {}
      phoneDetail.projects = data.projects || []
    } else {
      ElMessage.warning('获取手机号详情失败')
    }
  } catch (error) {
    ElMessage.error('获取手机号详情出错')
  } finally {
    loading.value = false
  }
}

// 返回列表页
const goBack = () => {
  router.push('/home')
}

// 格式化状态
const formatStatus = (status, type) => {
  if (type === 'line') {
    return status === 1 ? '在线' : '离线'
  }
  if (type === 'usage') {
    return status === 1 ? '已使用' : '未使用'
  }
  return status
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  fetchPhoneDetail()
})
</script>

<template>
  <div class="phone-detail-container">
    <AppHeader title="卡商端-号码详情页" />
    <div class="content-area">
      <!-- 返回按钮 -->
      <div class="back-button-container">
        <el-button type="text" size="small">
          <img
            src="../assets/svg/return.svg"
            alt=""
            style="height: 20px"
            width="40px"
            @click="goBack"
          />
        </el-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>

      <!-- 详情内容 -->
      <div v-else class="detail-content">
        <!-- 基本信息卡片 -->
        <div class="detail-card basic-info-card">
          <div class="card-header">
            <h3>基本信息</h3>
          </div>
          <div class="card-body">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="手机号码"
                >{{ phoneDetail.basicInfo.phoneNumber }}
              </el-descriptions-item>
              <el-descriptions-item label="归属国家"
                >{{ phoneDetail.basicInfo.countryCode }}
              </el-descriptions-item>
              <el-descriptions-item label="线路状态">
                <el-tag :type="phoneDetail.basicInfo.lineStatus === 1 ? 'success' : 'danger'">
                  {{ formatStatus(phoneDetail.basicInfo.lineStatus, 'line') }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="使用状态"> {{phoneDetail.basicInfo.usageStatus}}</el-descriptions-item>
              <el-descriptions-item label="导入时间">
                {{ formatDate(phoneDetail.basicInfo.registrationTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="导出时间">
                {{ formatDate(phoneDetail.basicInfo.registrationTime) }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>

        <!-- 项目列表卡片 -->
        <div class="detail-card projects-card">
          <div class="card-header">
            <h3>关联项目</h3>
          </div>
          <div class="card-body">
            <el-table :data="phoneDetail.projects" style="width: 100%" border stripe>
              <el-table-column type="index" label="序号" width="80" align="center" />
              <el-table-column prop="projectName" label="项目名称" min-width="150" />
              <el-table-column label="注册时间" min-width="180">
                <template #default="scope">
                  {{ formatDate(scope.row.timeOfUse) }}
                </template>
              </el-table-column>
            </el-table>
            <div v-if="phoneDetail.projects.length === 0" class="empty-placeholder">
              <el-empty description="暂无关联项目"></el-empty>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.phone-detail-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .content-area {
    flex: 1;
    overflow: auto;
    padding: 20px;
    height: calc(100% - 60px);
  }

  .back-button-container {
    margin-bottom: 15px;
  }

  .loading-container {
    padding: 20px;
    background-color: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  .detail-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .detail-card {
    background-color: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .card-header {
      padding: 15px 20px;
      border-bottom: 1px solid #ebeef5;
      background-color: #f9fafc;

      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 500;
        color: #303133;
      }
    }

    .card-body {
      padding: 20px;
    }
  }

  .empty-placeholder {
    padding: 20px;
    display: flex;
    justify-content: center;
  }

  :deep(.el-descriptions) {
    .el-descriptions__label {
      width: 120px;
      text-align: right;
      padding-right: 15px;
      font-weight: bold;
    }

    .el-descriptions__content {
      padding-left: 15px;
    }
  }
}
</style>
