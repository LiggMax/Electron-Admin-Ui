<template>
  <div class="phone-import-container">
    <h2>批量导入手机号</h2>
    
    <el-form :model="importForm" label-width="100px">
      <!-- 选择地区 -->
      <el-form-item label="选择地区">
        <el-select v-model="importForm.regionId" placeholder="请选择地区">
          <el-option
            v-for="item in regions"
            :key="item.regionId"
            :label="item.regionName"
            :value="item.regionId"
          />
        </el-select>
      </el-form-item>
      
      <!-- 选择项目 -->
      <el-form-item label="选择项目">
        <el-select v-model="importForm.projectIds" multiple placeholder="请选择项目">
          <el-option
            v-for="item in projects"
            :key="item.projectId"
            :label="item.projectName"
            :value="item.projectId"
          />
        </el-select>
      </el-form-item>
      
      <!-- 文件上传 -->
      <el-form-item label="上传文件">
        <el-upload
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :file-list="fileList"
          multiple
        >
          <el-button type="primary">选择文件</el-button>
          <template #tip>
            <div class="el-upload__tip">请上传txt格式文件，每行一个手机号</div>
          </template>
        </el-upload>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitImport" :loading="loading">开始导入</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 导入结果展示 -->
    <div v-if="importResult" class="import-result">
      <h3>导入结果</h3>
      <p>总处理数: {{ importResult.total }}</p>
      <p>成功添加: {{ importResult.added }}</p>
      <p>重复号码: {{ importResult.duplicate }}</p>
      <p>无效号码: {{ importResult.invalid }}</p>
      <p>{{ importResult.message }}</p>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { uploadPhoneNumbers, getProjectAndRegionData } from '../api/phone'
import { ElMessage } from 'element-plus'

export default {
  name: 'PhoneImport',
  setup() {
    const projects = ref([])
    const regions = ref([])
    const fileList = ref([])
    const loading = ref(false)
    const importResult = ref(null)
    
    const importForm = reactive({
      regionId: null,
      projectIds: [],
      files: []
    })
    
    onMounted(async () => {
      try {
        const { data } = await getProjectAndRegionData()
        projects.value = data.projects || []
        regions.value = data.regions || []
      } catch (error) {
        ElMessage.error('获取项目和地区数据失败')
        console.error(error)
      }
    })
    
    const handleFileChange = (file) => {
      // 读取文件内容
      const reader = new FileReader()
      reader.onload = (e) => {
        const content = e.target.result
        const phoneNumbers = content.split('\n')
          .map(line => line.trim())
          .filter(line => line !== '')
        
        importForm.files.push({
          name: file.name,
          phoneNumbers
        })
      }
      reader.readAsText(file.raw)
    }
    
    const submitImport = async () => {
      if (!importForm.regionId) {
        ElMessage.warning('请选择地区')
        return
      }
      
      if (importForm.projectIds.length === 0) {
        ElMessage.warning('请选择至少一个项目')
        return
      }
      
      if (importForm.files.length === 0) {
        ElMessage.warning('请上传至少一个文件')
        return
      }
      
      loading.value = true
      try {
        const { data } = await uploadPhoneNumbers(importForm)
        importResult.value = data
        ElMessage.success(data.message || '导入成功')
      } catch (error) {
        ElMessage.error('导入失败：' + (error.message || '未知错误'))
        console.error(error)
      } finally {
        loading.value = false
      }
    }
    
    return {
      projects,
      regions,
      fileList,
      importForm,
      loading,
      importResult,
      handleFileChange,
      submitImport
    }
  }
}
</script>

<style scoped>
.phone-import-container {
  padding: 20px;
}
.import-result {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 5px;
  background-color: #f9f9f9;
}
</style> 