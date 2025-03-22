<script setup>
import { ref, watch } from 'vue'
import { Upload } from '@element-plus/icons-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'close'])

// 弹窗可见性
const dialogVisible = ref(props.visible)

// 监听visible属性变化
watch(() => props.visible, (val) => {
  dialogVisible.value = val
})

// 监听dialogVisible变化，同步回父组件
watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false
  emit('close')
}

// 步骤状态
const currentStep = ref(1)
const steps = [
  { id: 1, title: '上传文件', description: '支持.txt/.csv等多种格式' },
  { id: 2, title: '校验数据', description: '系统将校对数据格式' },
  { id: 3, title: '导入数据', description: '将有效数据导入系统' }
]

// 国家选项
const countryOptions = [
  { label: '请选择国家', value: '' },
  { label: '中国', value: '中国' },
  { label: '美国', value: '美国' },
  { label: '英国', value: '英国' },
  { label: '日本', value: '日本' },
  { label: '韩国', value: '韩国' }
]

// 项目选项
const projectOptions = [
  { label: '请选择项目', value: '' },
  { label: 'Facebook', value: 'Facebook' },
  { label: 'TikTok', value: 'TikTok' },
  { label: 'Instagram', value: 'Instagram' }
]

// 表单数据
const uploadForm = ref({
  country: '',
  project: ''
})

// 上传文件相关
// const fileList = ref([])
// const isUploading = ref(false)

// 文件上传成功后的状态
const uploadStatus = ref({
  txtFile: { name: 'file1.txt', status: 'success', progress: 100 },
  csvFile: { name: 'file2.csv', status: 'error', progress: 30 },
})

// 处理文件拖放
const handleDrop = () => {
  console.log('文件已拖放')
}

// 处理文件选择
const handleFileSelect = () => {
  console.log('选择文件')
}

// 处理表单提交
const handleSubmit = () => {
  console.log('表单提交', uploadForm.value)
  handleClose()
}
</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    title="导入数据"
    width="550px"
    :close-on-click-modal="false"
    @close="handleClose"
    class="upload-dialog"
    append-to-body
  >
    <!-- 步骤条 -->
    <div class="steps-container">
      <div
        v-for="step in steps"
        :key="step.id"
        class="step-item"
        :class="{ active: currentStep >= step.id, 'step-done': currentStep > step.id }"
      >
        <div class="step-number">{{ step.id }}</div>
        <div class="step-info">
          <div class="step-title">{{ step.title }}</div>
          <div class="step-desc">{{ step.description }}</div>
        </div>
      </div>
    </div>

    <!-- 表单内容 -->
    <div class="form-content">
      <div class="form-row">
        <div class="form-item">
          <label>国家：</label>
          <el-select
            v-model="uploadForm.country"
            placeholder="请选择国家"
            class="form-select"
          >
            <el-option
              v-for="item in countryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>
        <div class="form-item">
          <label>项目：</label>
          <el-select
            v-model="uploadForm.project"
            placeholder="请选择项目"
            class="form-select"
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

      <!-- 上传区域 -->
      <div class="upload-container">
        <div class="upload-area" @drop.prevent="handleDrop" @dragover.prevent>
          <el-icon class="upload-icon"><Upload /></el-icon>
          <div class="upload-text">点击或拖拽文件到此处上传</div>
          <div class="upload-tip">支持单个或批量上传，最多可上传5个文件</div>
          <input type="file" class="file-input" multiple @change="handleFileSelect" />
        </div>
      </div>

      <!-- 文件列表 -->
      <div class="file-list" v-if="uploadStatus">
        <div class="file-item" v-for="(file, key) in uploadStatus" :key="key">
          <div class="file-info">
            <div class="file-name">{{ file.name }}</div>
          </div>
          <div class="file-progress">
            <el-progress
              :percentage="file.progress"
              :status="file.status === 'error' ? 'exception' : file.status === 'success' ? 'success' : ''"
              :stroke-width="8"
            ></el-progress>
          </div>
          <div class="file-actions">
            <el-button v-if="file.status === 'success'" type="text" size="small" class="success-text">成功</el-button>
            <el-button v-else-if="file.status === 'error'" type="text" size="small" class="error-text">重试</el-button>
            <el-button v-else type="text" size="small" class="upload-text">上传中</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">下一步</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="less" scoped>
.upload-dialog {
  :deep(.el-dialog__header) {
    padding: 15px 20px;
    border-bottom: 1px solid #ebeef5;
  }
  
  :deep(.el-dialog__body) {
    padding: 20px;
  }
}

/* 对话框全局样式已在App.vue中定义 */

.steps-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;

  .step-item {
    display: flex;
    align-items: center;
    flex: 1;
    position: relative;

    &:not(:last-child):after {
      content: '';
      position: absolute;
      top: 20px;
      right: 0;
      width: calc(100% - 40px);
      height: 2px;
      background-color: #e0e0e0;
      z-index: 1;
    }

    &.active {
      .step-number {
        background-color: #409eff;
        color: white;
      }
    }

    &.step-done:not(:last-child):after {
      background-color: #409eff;
    }
  }

  .step-number {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background-color: #f5f7fa;
    border: 1px solid #e0e0e0;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    margin-right: 8px;
    z-index: 2;
  }

  .step-info {
    display: flex;
    flex-direction: column;
  }

  .step-title {
    font-weight: 500;
    font-size: 14px;
  }

  .step-desc {
    color: #909399;
    font-size: 12px;
    margin-top: 4px;
  }
}

.form-content {
  padding: 10px 0;
}

.form-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  gap: 20px;

  .form-item {
    flex: 1;
    display: flex;
    align-items: center;

    label {
      margin-right: 8px;
      font-size: 14px;
      white-space: nowrap;
    }

    .form-select {
      width: 100%;
    }
  }
}

.upload-container {
  margin: 20px 0;
}

.upload-area {
  border: 2px dashed #c0c4cc;
  border-radius: 4px;
  padding: 30px 0;
  text-align: center;
  cursor: pointer;
  position: relative;
  background-color: #f8f9fb;
  transition: border-color 0.3s;

  &:hover {
    border-color: #409eff;
  }

  .upload-icon {
    font-size: 48px;
    color: #909399;
    margin-bottom: 10px;
  }

  .upload-text {
    font-size: 16px;
    color: #606266;
    margin-bottom: 5px;
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
  }

  .file-input {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    opacity: 0;
    cursor: pointer;
  }
}

.file-list {
  margin-top: 15px;

  .file-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }
  }

  .file-info {
    width: 150px;
    overflow: hidden;
    margin-right: 10px;
  }

  .file-name {
    font-size: 14px;
    color: #606266;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .file-progress {
    flex: 1;
    padding: 0 10px;
  }

  .file-actions {
    width: 60px;
    text-align: right;
  }

  .success-text {
    color: #67c23a;
  }

  .error-text {
    color: #f56c6c;
  }

  .upload-text {
    color: #909399;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
