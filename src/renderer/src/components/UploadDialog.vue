<script setup>
import { ref, watch } from 'vue'

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
    width="520px"
    :close-on-click-modal="false"
    @close="handleClose"
    class="upload-dialog"
    append-to-body
  >
    <!-- 步骤条 -->
    <div class="steps-container">
      <div class="step-item" :class="{ active: currentStep >= 1, 'step-done': currentStep > 1 }">
        <div class="step-circle">1</div>
        <div class="step-title">上传文件</div>
        <div class="step-desc">拖拽或选取单个文件</div>
      </div>
      <div class="step-line"></div>
      <div class="step-item" :class="{ active: currentStep >= 2, 'step-done': currentStep > 2 }">
        <div class="step-circle">2</div>
        <div class="step-title">校验数据</div>
        <div class="step-desc">校验上传数据的格式</div>
      </div>
      <div class="step-line"></div>
      <div class="step-item" :class="{ active: currentStep >= 3, 'step-done': currentStep > 3 }">
        <div class="step-circle">3</div>
        <div class="step-title">导入数据</div>
        <div class="step-desc">将有效数据导入系统</div>
      </div>
    </div>

    <!-- 表单内容 -->
    <div class="form-content">
      <div class="form-row">
        <div class="form-item">
          <div class="form-label">国家：</div>
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
          <div class="form-label">项目：</div>
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
          <div class="upload-icon-container">
            <img src="../assets/images/uploadFlie.png" alt="上传" class="folder-icon" />
          </div>
          <div class="upload-text">点击或拖拽文件到此处上传</div>
          <input type="file" class="file-input" multiple @change="handleFileSelect" />
        </div>
      </div>

      <!-- 已上传文件提示 -->
      <div class="upload-tip-text">共9个文件，共3MB</div>

      <!-- 文件列表 -->
      <div class="file-list-container">
        <div class="file-list" v-if="uploadStatus">
          <div class="file-item" v-for="(file, key) in uploadStatus" :key="key">
            <div class="file-icon">
              <img src="../assets/svg/folder.svg" alt="文件" class="doc-icon" />
            </div>
            <div class="file-info">
              <div class="file-name">{{ file.name }}</div>
            </div>
            <div class="file-progress">
              <el-progress
                :percentage="file.progress"
                :status="file.status === 'error' ? 'exception' : file.status === 'success' ? 'success' : ''"
                :stroke-width="6"
                :show-text="false"
              ></el-progress>
            </div>
            <div class="file-actions">
              <el-button v-if="file.status === 'success'" type="text" size="small" class="success-text">成功</el-button>
              <el-button v-else-if="file.status === 'error'" type="text" size="small" class="error-text">重试</el-button>
              <el-button type="text" size="small" class="delete-text">删除</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose" class="cancel-btn">取消</el-button>
        <el-button type="primary" @click="handleSubmit" class="next-btn">下一步</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="less" scoped>
.upload-dialog {
  :deep(.el-dialog__header) {
    padding: 12px 15px;
    border-bottom: 1px solid #ebeef5;
  }

  :deep(.el-dialog__body) {
    padding: 15px;
  }
}

/* 对话框全局样式已在App.vue中定义 */

.steps-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 5px 10px;

  .step-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    position: relative;
    z-index: 2;
    flex: 1;
  }

  .step-circle {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background-color: #e7f1ff;
    color: #409eff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    margin-bottom: 5px;
    position: relative;
    z-index: 2;
    font-size: 12px;
  }

  .step-title {
    font-weight: 500;
    font-size: 13px;
    margin-bottom: 2px;
    color: #303133;
  }

  .step-desc {
    color: #909399;
    font-size: 11px;
  }

  .step-line {
    height: 1px;
    background-color: #dcdfe6;
    flex: 1;
    position: relative;
    z-index: 1;
    max-width: 70px;
  }

  .step-item.active {
    .step-circle {
      background-color: #7f83f7;
      color: white;
    }
    //.step-title {
    //  color: #409eff;
    //}
  }

  .step-item.step-done {
    .step-circle {
      background-color: #67c23a;
      color: white;
    }
  }

  .step-item.step-done + .step-line {
    background-color: #67c23a;
  }
}

.form-content {
  padding: 0;
}

.form-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 10px;

  .form-item {
    flex: 1;
    display: flex;
    align-items: center;

    .form-label {
      margin-right: 5px;
      font-size: 13px;
      white-space: nowrap;
      color: #606266;
      min-width: 40px;
    }

    .form-select {
      width: 100%;
    }
  }
}

/* 表单分隔线 */
.divider {
  height: 1px;
  background-color: #ebeef5;
  margin: 20px 0;
}

/* 底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 15px;
  padding-top: 5px;

  .el-button {
    min-width: 80px;
    padding: 8px 15px;
  }

  .next-btn {
    background-color: #6366f1;
    border-color: #6366f1;

    &:hover, &:focus {
      background-color: #7477f5;
      border-color: #7477f5;
    }
  }
}

.upload-container {
  margin: 15px 0 10px;
}

.upload-area {
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  padding: 20px 0;
  text-align: center;
  cursor: pointer;
  position: relative;
  background-color: #f7f9fc;
  transition: border-color 0.3s;
  margin: 5px 0;

  &:hover {
    border-color: #409eff;
  }

  .upload-icon-container {
    margin-bottom: 8px;
  }

  .folder-icon {
    width: 40px;
    height: 40px;
    color: #409eff;
  }

  .upload-text {
    font-size: 13px;
    color: #606266;
    margin-bottom: 0;
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

.upload-tip-text {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  margin-bottom: 10px;
}

.file-list-container {
  margin-top: 10px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
  padding: 2px 0;
}

.file-list {
  margin-top: 0;

  .file-item {
    display: flex;
    align-items: center;
    padding: 8px 10px;

    &:not(:last-child) {
      border-bottom: 1px dashed #ebeef5;
    }
  }

  .file-icon {
    width: 24px;
    height: 24px;
    margin-right: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .doc-icon {
    width: 20px;
    height: 20px;
    object-fit: contain;
  }

  .file-info {
    width: 120px;
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
    width: 100px;
    display: flex;
    justify-content: flex-end;
    gap: 5px;
  }

  .success-text {
    color: #67c23a;
  }

  .error-text {
    color: #f56c6c;
  }

  .delete-text {
    color: #909399;
  }
}
</style>
