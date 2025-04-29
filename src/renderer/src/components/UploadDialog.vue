<script setup>
import { ref, watch, inject, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadPhoneNumbers, getProjectAndRegionData } from '../api/phone'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'close'])

// 获取全局消息服务
const message = inject('message') || ElMessage

// 弹窗可见性
const dialogVisible = ref(props.visible)

// 监听visible属性变化
watch(
  () => props.visible,
  (val) => {
    dialogVisible.value = val
  }
)

// 监听dialogVisible变化，同步回父组件
watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false

  // 清除所有数据
  uploadForm.value = {
    country: '',
    projects: []
  }
  uploadedFiles.value = []
  fileStats.value = {
    count: 0,
    totalSize: 0
  }
  validationResults.value = {
    valid: [],
    invalid: []
  }
  uploading.value = false
  uploadProgress.value = 0
  uploadResult.value = null
  currentStep.value = 1

  emit('close')
}

// 步骤状态
const currentStep = ref(1)

// 切换步骤
// const changeStep = (step) => {
//   // 只允许点击已完成的步骤或下一个步骤
//   if(step <= currentStep.value + 1) {
//     currentStep.value = step
//   }
// }

// 国家选项
const countryOptions = ref([
  { label: '请选择国家', value: '' }
])

// 项目选项
const projectOptions = ref([])

// 表单数据
const uploadForm = ref({
  country: '',
  projects: []
})

// 加载国家和项目数据
const loadProjectAndRegionData = async () => {
  try {
    const response = await getProjectAndRegionData()
    if (response && response.data) {
      // 设置国家选项
      const regions = response.data.region || []
      countryOptions.value = [
        { label: '请选择国家', value: '' },
        ...regions.map(region => ({ 
          label: region.regionName, 
          value: region.regionId.toString(),
          mark: region.regionMark
        }))
      ]
      
      // 设置项目选项
      const projects = response.data.project || []
      projectOptions.value = projects.map(project => ({ 
        label: project.projectName, 
        value: project.projectId.toString(),
        price: project.projectPrice
      }))
    }
  } catch (error) {
    console.error('获取项目和地区数据失败:', error)
    message.error('获取项目和地区数据失败')
  }
}

// 项目全选状态
const allProjectsSelected = ref(false)

// 监听项目选择变化
watch(() => uploadForm.value.projects, (newVal) => {
  // 如果选中的项目数量等于可选项目总数
  if (newVal.length === projectOptions.value.length) {
    allProjectsSelected.value = true
  } else {
    allProjectsSelected.value = false
  }
}, { deep: true })

// 项目全选功能
const handleAllProjectsChange = (val) => {
  if (val) {
    // 选中全部
    uploadForm.value.projects = projectOptions.value.map(item => item.value)
  } else {
    // 取消全部
    uploadForm.value.projects = []
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadProjectAndRegionData()
})

// 上传文件相关
const uploadedFiles = ref([]) // 已上传的文件列表
const fileStats = ref({
  count: 0,
  totalSize: 0
})

// 文件校验结果
const validationResults = ref({
  valid: [],
  invalid: []
})

// 上传状态
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadResult = ref(null)

// 校验文件内容格式
const validateFileContent = (content) => {
  // 手机号码格式正则表达式 - 以1开头的11位数字
  const phonePattern = /^1\d{10}$/

  // 预处理文本内容，移除不必要的字符
  const cleanedContent = content
    .replace(/[\r\n\t\s]+/g, ',') // 将换行、制表符、空格替换为逗号
    .replace(/,+/g, ',') // 将多个连续逗号替换为单个逗号
    .replace(/^,|,$/g, '') // 移除开头和结尾的逗号

  // 分割并过滤出所有潜在的手机号
  const phones = cleanedContent.split(',').filter((item) => item.trim() !== '')

  // 检查每个手机号是否符合格式
  const validPhones = []
  const invalidPhones = []

  phones.forEach((phone) => {
    // 清理电话号码中可能的非数字字符
    const cleanPhone = phone.replace(/[^\d]/g, '')

    // 检查是否符合手机号格式
    if (phonePattern.test(cleanPhone)) {
      validPhones.push(cleanPhone)
    } else {
      // 原始号码可能带有前缀或后缀，记录原始号码以便显示
      invalidPhones.push(phone)
    }
  })

  return {
    totalCount: phones.length,
    validCount: validPhones.length,
    invalidCount: invalidPhones.length,
    validPhones: validPhones,
    invalidPhones: invalidPhones,
    isValid: validPhones.length > 0
  }
}

// 更新文件统计信息
const updateFileStats = () => {
  fileStats.value.count = uploadedFiles.value.length
  fileStats.value.totalSize = uploadedFiles.value.reduce((total, file) => total + file.size, 0)
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 处理文件拖放
const handleDrop = (event) => {
  const files = event.dataTransfer.files
  console.log('文件已拖放:', files)

  // 遍历所有拖放的文件
  Array.from(files).forEach((file) => {
    // 检查文件扩展名
    if (!file.name.toLowerCase().endsWith('.txt')) {
      ElMessage.error(`文件 "${file.name}" 不是TXT格式，请上传TXT文件`)
      return
    }

    // 创建文件读取器
    const reader = new FileReader()

    // 设置读取完成事件
    reader.onload = (e) => {
      const content = e.target.result
      console.log(`文件名: ${file.name}, 文件内容:`, content)

      // 添加到文件列表
      uploadedFiles.value.push({
        id: Date.now() + Math.random(),
        name: file.name,
        size: file.size,
        content: content,
        status: 'success',
        progress: 100
      })

      // 更新文件统计
      updateFileStats()

      ElMessage.success(`文件 "${file.name}" 已准备上传`)
    }

    // 读取文件内容
    reader.readAsText(file)
  })
}

// 处理文件选择
const handleFileSelect = (event) => {
  const files = event.target.files
  console.log('选择文件:', files)

  // 遍历所有选择的文件
  Array.from(files).forEach((file) => {
    // 检查文件扩展名
    if (!file.name.toLowerCase().endsWith('.txt')) {
      ElMessage.error(`文件 "${file.name}" 不是TXT格式，请上传TXT文件`)
      return
    }

    // 创建文件读取器
    const reader = new FileReader()

    // 设置读取完成事件
    reader.onload = (e) => {
      const content = e.target.result
      console.log(`文件名: ${file.name}, 文件内容:`, content)

      // 添加到文件列表
      uploadedFiles.value.push({
        id: Date.now() + Math.random(),
        name: file.name,
        size: file.size,
        content: content,
        status: 'success',
        progress: 100
      })

      // 更新文件统计
      updateFileStats()

      ElMessage.success(`文件 "${file.name}" 已准备上传`)
    }

    // 读取文件内容
    reader.readAsText(file)
  })
}

// 删除文件
const handleDeleteFile = (fileId) => {
  const index = uploadedFiles.value.findIndex((file) => file.id === fileId)
  if (index !== -1) {
    uploadedFiles.value.splice(index, 1)
    updateFileStats()
    ElMessage.success('文件已删除')
  }
}

// 处理下一步
const handleNext = () => {
  if (currentStep.value === 1 && currentStep.value < 3) {
    // 验证国家和项目选择
    if (!uploadForm.value.country) {
      message.warning('请选择国家')
      // 聚焦到国家选择框并添加闪烁效果
      const countrySelect = document.querySelector('.form-item:first-child .el-select')
      if (countrySelect) {
        countrySelect.classList.add('shake-animation')
        setTimeout(() => {
          countrySelect.classList.remove('shake-animation')
        }, 600)
      }
      return
    }

    if (!uploadForm.value.projects.length) {
      message.warning('请至少选择一个项目')
      // 聚焦到项目选择框并添加闪烁效果
      const projectSelect = document.querySelector('.form-item:last-child .el-select')
      if (projectSelect) {
        projectSelect.classList.add('shake-animation')
        setTimeout(() => {
          projectSelect.classList.remove('shake-animation')
        }, 600)
      }
      return
    }

    // 准备进入步骤2，验证文件
    if (uploadedFiles.value.length === 0) {
      message.warning('请先上传至少一个文件')
      return
    }

    // 清空之前的验证结果
    validationResults.value.valid = []
    validationResults.value.invalid = []

    // 验证每个上传的文件
    uploadedFiles.value.forEach((file) => {
      const result = validateFileContent(file.content)

      // 更新文件的验证状态
      file.validationResult = result

      if (result.isValid) {
        validationResults.value.valid.push({
          ...file,
          validationResult: result
        })
      } else {
        validationResults.value.invalid.push({
          ...file,
          validationResult: result
        })
      }
    })

    // 更新文件统计
    updateFileStats()

    currentStep.value++
  } else if (currentStep.value === 2 && currentStep.value < 3) {
    // 从步骤2进入步骤3
    currentStep.value++
  } else {
    // 最后一步，如果已有上传结果则关闭对话框，否则提交数据
    if (uploadResult.value) {
      handleClose()
    } else {
      handleSubmit()
    }
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!uploadForm.value.country) {
    message.warning('请选择国家')
    return
  }

  if (!uploadForm.value.projects.length) {
    message.warning('请至少选择一个项目')
    return
  }

  // 只处理验证成功的文件
  if (validationResults.value.valid.length === 0) {
    message.warning('没有有效的数据可上传')
    return
  }

  // 设置上传状态
  uploading.value = true
  uploadProgress.value = 0
  uploadResult.value = null

  try {
    // 获取选中的国家和项目信息
    const selectedCountry = countryOptions.value.find(option => option.value === uploadForm.value.country)
    const selectedProjects = uploadForm.value.projects.map(projectId => {
      const project = projectOptions.value.find(option => option.value === projectId)
      return {
        projectId: Number(projectId),
        projectName: project ? project.label : ''
      }
    })

    // 构建上传数据
    const uploadData = {
      countryId: Number(uploadForm.value.country),
      countryName: selectedCountry ? selectedCountry.label : '',
      countryMark: selectedCountry ? selectedCountry.mark : '',
      projects: selectedProjects,
      files: validationResults.value.valid.map((file) => {
        return {
          fileName: file.name,
          phoneCount: file.validationResult.validCount,
          phoneNumbers: file.validationResult.validPhones
        }
      })
    }

    console.log('准备上传数据:', uploadData)

    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 300)

    // 调用上传API
    const response = await uploadPhoneNumbers(uploadData)

    // 完成上传
    clearInterval(progressInterval)
    uploadProgress.value = 100

    console.log('上传成功:', response)

    // 保存上传结果用于显示
    const resultData = response.data || {}
    uploadResult.value = {
      success: true,
      message: resultData.message || '成功上传数据',
      stats: {
        total: resultData.total || 0,
        added: resultData.added || 0,
        duplicate: resultData.duplicate || 0,
        invalid: resultData.invalid || 0
      }
    }
    // 上传完成，不再处于上传状态
    uploading.value = false
  } catch (error) {
    console.error('上传失败:', error)

    // 保存错误信息用于显示
    uploadResult.value = {
      success: false,
      message: error.message || '上传失败'
    }

    message.error('上传失败: ' + (error.message || '未知错误'))
    uploadProgress.value = 0
    uploading.value = false
  }
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
      <div class="step-line" :class="{ 'line-active': currentStep > 1 }"></div>
      <div class="step-item" :class="{ active: currentStep >= 2, 'step-done': currentStep > 2 }">
        <div class="step-circle">2</div>
        <div class="step-title">校验数据</div>
        <div class="step-desc">校验上传数据的格式</div>
      </div>
      <div class="step-line" :class="{ 'line-active': currentStep > 2 }"></div>
      <div class="step-item" :class="{ active: currentStep >= 3, 'step-done': currentStep > 3 }">
        <div class="step-circle">3</div>
        <div class="step-title">写入数据</div>
        <div class="step-desc">将有效数据导入系统</div>
      </div>
    </div>

    <!-- 表单内容 - 根据当前步骤显示不同内容 -->
    <div class="form-content">
      <!-- 步骤1：上传文件 -->
      <div v-if="currentStep === 1">
        <div class="form-row">
          <div class="form-item">
            <div class="form-label">国家：</div>
            <el-select v-model="uploadForm.country" placeholder="请选择国家" class="form-select">
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
              v-model="uploadForm.projects"
              placeholder="请选择项目"
              class="form-select"
              multiple
              collapse-tags
              collapse-tags-tooltip
            >
              <div style="padding: 5px 12px; border-bottom: 1px solid #EBEEF5">
                <el-checkbox 
                  v-model="allProjectsSelected"
                  @change="handleAllProjectsChange"
                  :indeterminate="uploadForm.projects.length > 0 && uploadForm.projects.length < projectOptions.length"
                >
                  全选
                </el-checkbox>
              </div>
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
              <img src="../assets/images/uploadFlier.png" alt="上传" class="folder-icon" />
            </div>
            <div class="upload-text">点击或拖拽TXT文件到此处上传</div>
            <div class="upload-format-hint">仅支持TXT格式</div>
            <input
              type="file"
              class="file-input"
              accept=".txt"
              multiple
              @change="handleFileSelect"
            />
          </div>
        </div>

        <!-- 已上传文件提示 -->
        <div class="upload-tip-text">
          共{{ fileStats.count }}个文件，共{{ formatFileSize(fileStats.totalSize) }}
        </div>

        <!-- 文件列表 -->
        <div class="file-list-container" v-if="uploadedFiles.length > 0">
          <div class="file-list">
            <div class="file-item" v-for="file in uploadedFiles" :key="file.id">
              <div class="file-icon">
                <img src="../assets/svg/folder.svg" alt="文件" class="doc-icon" />
              </div>
              <div class="file-info">
                <div class="file-name">{{ file.name }}</div>
                <div class="file-size">{{ formatFileSize(file.size) }}</div>
              </div>
              <div class="file-progress">
                <el-progress
                  :percentage="file.progress"
                  :status="
                    file.status === 'error'
                      ? 'exception'
                      : file.status === 'success'
                        ? 'success'
                        : ''
                  "
                  :stroke-width="6"
                  :show-text="false"
                ></el-progress>
              </div>
              <div class="file-actions">
                <el-button
                  v-if="file.status === 'success'"
                  type="text"
                  size="small"
                  class="success-text"
                  >成功
                </el-button>
                <el-button
                  v-else-if="file.status === 'error'"
                  type="text"
                  size="small"
                  class="error-text"
                  >重试
                </el-button>
                <el-button
                  type="text"
                  size="small"
                  class="delete-text"
                  @click="handleDeleteFile(file.id)"
                  >删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="compact-empty-list" v-else>
          <el-empty description="暂无上传文件" :image-size="40">
          </el-empty>
        </div>
      </div>

      <!-- 步骤2：校验数据 -->
      <div v-else-if="currentStep === 2" class="step-content">
        <div class="validation-summary">
          <div class="summary-title">数据校验结果</div>
          <div class="summary-content">
            <div class="summary-item success">
              <div class="summary-icon">✓</div>
              <div class="summary-text">
                共验证{{ uploadedFiles.length }}个文件，{{
                  validationResults.valid.length
                }}个校验成功
              </div>
            </div>
            <div class="summary-item error" v-if="validationResults.invalid.length > 0">
              <div class="summary-icon">!</div>
              <div class="summary-text">
                发现{{ validationResults.invalid.length }}个文件格式错误
              </div>
            </div>
          </div>

          <!-- 验证细节 -->
          <div class="validation-details">
            <div v-for="file in uploadedFiles" :key="file.id" class="validation-file-item">
              <div class="file-name">{{ file.name }}</div>
              <div
                :class="[
                  'validation-status',
                  file.validationResult?.isValid ? 'status-valid' : 'status-invalid'
                ]"
              >
                <span v-if="file.validationResult?.isValid">格式正确</span>
                <span v-else>格式错误</span>
              </div>
              <div class="validation-details-content">
                <div>
                  共{{ file.validationResult?.totalCount || 0 }}个手机号，{{
                    file.validationResult?.validCount || 0
                  }}个有效
                </div>
                <div v-if="file.validationResult?.invalidCount > 0" class="invalid-phones">
                  <div>无效手机号：</div>
                  <div class="invalid-list">
                    {{ file.validationResult?.invalidPhones.slice(0, 3).join(', ')
                    }}{{ file.validationResult?.invalidPhones.length > 3 ? '...' : '' }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 步骤3：写入数据 -->
      <div v-else-if="currentStep === 3" class="step-content">
        <div class="import-summary">
          <div class="summary-title">数据写入结果</div>

          <!-- 上传中状态 -->
          <div v-if="uploading" class="uploading-state">
            <div class="upload-progress">
              <el-progress
                :percentage="uploadProgress"
                :stroke-width="15"
                :show-text="true"
                :status="uploadProgress < 100 ? '' : 'success'"
              ></el-progress>
            </div>
            <div class="upload-status-text">
              {{ uploadProgress < 100 ? '正在上传数据...' : '上传完成，处理中...' }}
            </div>
          </div>

          <!-- 上传结果 -->
          <div v-else-if="uploadResult" class="summary-content">
            <div class="summary-item" :class="uploadResult.success ? 'success' : 'error'">
              <div class="summary-icon">{{ uploadResult.success ? '✓' : '!' }}</div>
              <div class="summary-text">{{ uploadResult.message }}</div>
            </div>

            <!-- 上传详细统计 -->
            <div v-if="uploadResult.success && uploadResult.stats" class="result-stats">
              <div class="stat-item">
                <div class="stat-label">处理总数：</div>
                <div class="stat-value">{{ uploadResult.stats.total }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">成功导入：</div>
                <div class="stat-value">{{ uploadResult.stats.added }}</div>
              </div>
              <div class="stat-item" v-if="uploadResult.stats.duplicate > 0">
                <div class="stat-label">重复跳过：</div>
                <div class="stat-value">{{ uploadResult.stats.duplicate }}</div>
              </div>
              <div class="stat-item" v-if="uploadResult.stats.invalid > 0">
                <div class="stat-label">无效号码：</div>
                <div class="stat-value">{{ uploadResult.stats.invalid }}</div>
              </div>
            </div>
          </div>

          <!-- 默认展示验证信息 -->
          <div v-else class="summary-content">
            <div class="summary-item success">
              <div class="summary-icon">✓</div>
              <div class="summary-text">
                准备导入{{
                  validationResults.valid.reduce(
                    (sum, file) => sum + (file.validationResult?.validCount || 0),
                    0
                  )
                }}个手机号
              </div>
            </div>
            <div
              class="summary-item error"
              v-if="
                validationResults.invalid.length > 0 ||
                validationResults.valid.some((file) => file.validationResult?.invalidCount > 0)
              "
            >
              <div class="summary-icon">!</div>
              <div class="summary-text">
                已过滤{{
                  validationResults.invalid.reduce(
                    (sum, file) => sum + (file.validationResult?.totalCount || 0),
                    0
                  ) +
                  validationResults.valid.reduce(
                    (sum, file) => sum + (file.validationResult?.invalidCount || 0),
                    0
                  )
                }}个无效手机号
              </div>
            </div>
          </div>

          <!-- 导入结果表格 -->
          <div class="import-details" v-if="!uploading">
            <div class="import-file-list-header">
              <span class="file-column">文件名</span>
              <span class="count-column">成功/总数</span>
              <span class="status-column">状态</span>
            </div>
            <div class="import-file-list">
              <div v-for="file in uploadedFiles" :key="file.id" class="import-file-item">
                <span class="file-column">{{ file.name }}</span>
                <span class="count-column"
                  >{{ file.validationResult?.validCount || 0 }}/{{
                    file.validationResult?.totalCount || 0
                  }}</span
                >
                <span
                  class="status-column"
                  :class="{
                    'status-success': file.validationResult?.isValid,
                    'status-partial':
                      !file.validationResult?.isValid && file.validationResult?.validCount > 0,
                    'status-failed': file.validationResult?.validCount === 0
                  }"
                >
                  {{
                    file.validationResult?.isValid
                      ? '全部有效'
                      : file.validationResult?.validCount > 0
                        ? '部分有效'
                        : '全部无效'
                  }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose" class="cancel-btn" :disabled="uploading">
          {{ currentStep < 3 ? '取消' : '关闭' }}
        </el-button>
        <el-button
          type="primary"
          @click="handleNext"
          class="next-btn"
          :loading="uploading"
          :disabled="uploading && uploadProgress >= 100"
        >
          <template v-if="uploading"> 上传中 {{ uploadProgress }}%</template>
          <template v-else>
            {{ currentStep < 3 ? '下一步' : uploadResult ? '关闭' : '提交' }}
          </template>
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="less" scoped>
.upload-dialog {
  :deep(.el-dialog__header) {
    padding: 8px 15px;
    border-bottom: 1px solid #ebeef5;
  }

  :deep(.el-dialog__body) {
    padding: 10px;
    max-height: 420px;
    overflow-y: auto;
  }

  :deep(.el-dialog__footer) {
    padding: 5px 15px;
  }
}

.steps-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  position: relative;
  margin-bottom: 12px;
  padding: 0 15px;
}

.step-line {
  width: 100px;
  height: 1px;
  background-color: #e6e8ec;
  align-self: flex-start;
  margin: 14px 0 0;
}

.step-line.line-active {
  background-color: #409eff;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  position: relative;
  z-index: 1;
  width: 100px;
}

.step-circle {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background-color: #e6e8ec;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  margin-bottom: 4px;
  border: 1px solid #e6e8ec;
  color: #909399;
}

.step-item.active .step-circle {
  background-color: #fff;
  border-color: #409eff;
  color: #409eff;
}

.step-item.step-done .step-circle {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
}

.step-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #606266;
}

.step-item.active .step-title,
.step-item.step-done .step-title {
  color: #409eff;
}

.step-desc {
  font-size: 12px;
  color: #909399;
  text-align: center;
  width: 100%;
  white-space: normal;
  word-break: break-word;
  line-height: 1.1;
}

.form-content {
  padding: 0;
}

.form-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
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

/* 步骤2和步骤3的内容样式 */
.step-content {
  min-height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 15px 0;
}

.validation-summary,
.import-summary {
  width: 100%;
  padding: 15px;
  border-radius: 6px;
  background-color: #f8f9fa;
  border: 1px solid #ebeef5;

  .summary-title {
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 15px;
    color: #333;
    text-align: center;
  }

  .summary-content {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .summary-item {
    display: flex;
    align-items: center;
    padding: 10px;
    border-radius: 4px;

    &.success {
      background-color: rgba(103, 194, 58, 0.1);
    }

    &.error {
      background-color: rgba(245, 108, 108, 0.1);
    }

    .summary-icon {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 10px;
      font-weight: bold;

      .success & {
        background-color: #67c23a;
        color: white;
      }

      .error & {
        background-color: #f56c6c;
        color: white;
      }
    }

    .summary-text {
      font-size: 14px;

      .success & {
        color: #67c23a;
      }

      .error & {
        color: #f56c6c;
      }
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
  padding-top: 0;

  .el-button {
    min-width: 80px;
    padding: 6px 12px;
  }

  .next-btn {
    background-color: #6366f1;
    border-color: #6366f1;

    &:hover,
    &:focus {
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
  padding: 10px 0;
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
    margin-bottom: 3px;
  }

  .folder-icon {
    width: 28px;
    height: 28px;
    color: #409eff;
  }

  .upload-text {
    font-size: 13px;
    color: #606266;
    margin-bottom: 0;
  }

  .upload-format-hint {
    font-size: 12px;
    color: #909399;
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

  .file-size {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
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

.empty-file-list {
  padding: 5px;
  text-align: center;
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;

  :deep(.el-empty__image) {
    margin-bottom: 5px;
  }

  :deep(.el-empty__description) {
    margin-top: 0;
    font-size: 12px;
  }
}

.compact-empty-list {
  padding: 0;
  text-align: center;
  min-height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;

  :deep(.el-empty__image) {
    margin-bottom: 2px;
    height: 40px;
  }

  :deep(.el-empty__description) {
    margin-top: 0;
    font-size: 12px;
    line-height: 1;
  }
}

.validation-details {
  margin-top: 15px;

  .validation-file-item {
    background-color: #f9f9f9;
    border-radius: 4px;
    padding: 10px;
    margin-bottom: 10px;

    .file-name {
      font-weight: 500;
      margin-bottom: 5px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .validation-status {
      display: inline-block;
      padding: 2px 6px;
      border-radius: 3px;
      font-size: 12px;
      margin-bottom: 5px;

      &.status-valid {
        background-color: rgba(103, 194, 58, 0.1);
        color: #67c23a;
      }

      &.status-invalid {
        background-color: rgba(245, 108, 108, 0.1);
        color: #f56c6c;
      }
    }

    .validation-details-content {
      font-size: 12px;
      color: #606266;
    }

    .invalid-phones {
      margin-top: 5px;

      .invalid-list {
        color: #f56c6c;
        word-break: break-all;
        background-color: rgba(245, 108, 108, 0.05);
        padding: 3px 5px;
        border-radius: 2px;
        font-family: monospace;
      }
    }
  }
}

.import-details {
  margin-top: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;

  .import-file-list-header {
    display: flex;
    padding: 8px 10px;
    background-color: #f5f7fa;
    font-weight: 500;
    border-bottom: 1px solid #ebeef5;
    font-size: 13px;

    .file-column {
      flex: 2;
    }

    .count-column {
      flex: 1;
      text-align: center;
    }

    .status-column {
      flex: 1;
      text-align: center;
    }
  }

  .import-file-list {
    max-height: 160px;
    overflow-y: auto;

    .import-file-item {
      display: flex;
      padding: 8px 10px;
      border-bottom: 1px solid #ebeef5;
      font-size: 13px;

      &:last-child {
        border-bottom: none;
      }

      .file-column {
        flex: 2;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .count-column {
        flex: 1;
        text-align: center;
      }

      .status-column {
        flex: 1;
        text-align: center;

        &.status-success {
          color: #67c23a;
        }

        &.status-partial {
          color: #e6a23c;
        }

        &.status-failed {
          color: #f56c6c;
        }
      }
    }
  }
}

/* 上传状态样式 */
.uploading-state {
  padding: 20px 10px;

  .upload-progress {
    margin-bottom: 15px;
  }

  .upload-status-text {
    text-align: center;
    font-size: 14px;
    color: #666;
  }
}

/* 修改一些现有样式以提高一致性 */
.import-summary {
  .summary-item {
    margin-bottom: 8px;
    border-radius: 6px;
  }
}

/* 上传结果统计样式 */
.result-stats {
  margin-top: 15px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #ebeef5;

  .stat-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;
    padding: 5px 0;

    &:not(:last-child) {
      border-bottom: 1px dashed #ebeef5;
    }

    .stat-label {
      font-weight: 500;
      color: #606266;
    }

    .stat-value {
      font-weight: 600;
      color: #409eff;
    }
  }
}

/* 必填项动画效果 */
@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  20%,
  60% {
    transform: translateX(-5px);
  }
  40%,
  80% {
    transform: translateX(5px);
  }
}

.shake-animation {
  animation: shake 0.6s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
  border-color: #f56c6c !important;
  box-shadow: 0 0 0 2px rgba(245, 108, 108, 0.2);
}

/* 全局消息样式修改，影响app.vue */
:deep(.highlight-message) {
  min-width: 200px !important;
  background-color: #fdf6ec !important;
  border: 1px solid #faecd8 !important;
  font-weight: bold !important;
  padding: 12px 15px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;

  .el-message__content {
    color: #e6a23c !important;
    font-size: 14px !important;
  }

  .el-message__icon {
    margin-right: 10px !important;
    font-size: 18px !important;
  }
}
</style>
