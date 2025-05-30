<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPhoneDetail } from '../api/phone'
import AppHeader from '../components/AppHeader.vue'
import {format} from '../utils/DateFormatter'

const route = useRoute()
const router = useRouter()
const phoneId = ref(route.query.phoneId)
const loading = ref(true)
const phoneDetail = reactive({
  phoneId: null,
  phoneNumber: null,
  lineStatus: null,
  usageStatus: null,
  registrationTime: '',
  countryCode: '',
  regionName: '',
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
      // 直接使用返回的数据格式
      const data = response.data
      phoneDetail.phoneId = data.phoneId
      phoneDetail.phoneNumber = data.phoneNumber
      phoneDetail.lineStatus = data.lineStatus
      phoneDetail.usageStatus = data.usageStatus
      phoneDetail.registrationTime = data.registrationTime
      phoneDetail.countryCode = data.countryCode
      phoneDetail.regionName = data.regionName
      
      // 确保 projects 数组有效，过滤掉无效数据
      phoneDetail.projects = (data.projects || []).filter(project => 
        project && typeof project === 'object'
      ).map(project => ({
        projectId: project.projectId || null,
        projectName: project.projectName || '未知项目',
        projectPrice: Number(project.projectPrice) || 0
      }))
    } else {
      ElMessage.warning('获取手机号详情失败')
    }
  } catch (error) {
    console.error('获取手机号详情出错:', error)
    ElMessage.error('获取手机号详情出错')
  } finally {
    loading.value = false
  }
}

// 返回列表页
const goBack = () => {
  // 返回上一页
  router.go(-1)
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
        <el-button type="text" size="small" @click="goBack">
          <img
            src="../assets/svg/return.svg"
            alt=""
            style="height: 20px"
            width="40px"
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
              <el-descriptions-item label="手机号码">{{ phoneDetail.phoneNumber }}</el-descriptions-item>
              <el-descriptions-item label="归属国家/地区">
                {{ phoneDetail.countryCode || phoneDetail.regionName || '未知' }}
              </el-descriptions-item>
              <el-descriptions-item label="导入时间">
                {{ format(phoneDetail.registrationTime) }}
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
            <el-table 
              v-if="phoneDetail.projects && phoneDetail.projects.length > 0" 
              :data="phoneDetail.projects" 
              style="width: 100%" 
              border 
              stripe>
              <el-table-column type="index" label="序号" width="80" align="center" />
              <el-table-column prop="projectName" label="项目名称" min-width="150">
                <template #default="scope">
                  {{ scope.row?.projectName || '未知项目' }}
                </template>
              </el-table-column>
              <el-table-column prop="projectPrice" label="项目价格" min-width="100">
                <template #default="scope">
                  ￥{{ scope.row?.projectPrice ?? 0 }}
                </template>
              </el-table-column>
            </el-table>
            <div v-if="!phoneDetail.projects || phoneDetail.projects.length === 0" class="empty-placeholder">
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

    .el-button {
      padding: 8px 12px;
      border-radius: 4px;
      transition: all 0.3s ease;

      &:hover {
        background-color: #f5f7fa;
        transform: translateX(-2px);
      }

      img {
        display: block;
        transition: all 0.3s ease;
      }

      &:hover img {
        opacity: 0.8;
      }
    }
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
