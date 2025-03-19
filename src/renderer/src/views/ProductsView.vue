<script setup>
import { ref, onMounted } from 'vue'

// 商品数据
const products = ref([
  { id: 1, name: '游戏充值卡10元', type: '游戏卡', price: '9.5', stock: 100, status: '在售' },
  { id: 2, name: '游戏充值卡50元', type: '游戏卡', price: '48.0', stock: 80, status: '在售' },
  { id: 3, name: '游戏充值卡100元', type: '游戏卡', price: '95.0', stock: 50, status: '在售' },
  { id: 4, name: '话费充值卡50元', type: '话费卡', price: '49.5', stock: 200, status: '在售' },
  { id: 5, name: '话费充值卡100元', type: '话费卡', price: '98.5', stock: 150, status: '在售' },
  { id: 6, name: '购物卡200元', type: '购物卡', price: '196.0', stock: 30, status: '缺货' },
  { id: 7, name: '购物卡500元', type: '购物卡', price: '490.0', stock: 20, status: '在售' },
  { id: 8, name: '视频会员卡-月卡', type: '会员卡', price: '25.0', stock: 300, status: '在售' },
  { id: 9, name: '视频会员卡-年卡', type: '会员卡', price: '228.0', stock: 100, status: '在售' },
])

// 搜索条件
const searchForm = ref({
  name: '',
  type: '',
  status: ''
})

// 商品类型选项
const productTypes = [
  { label: '全部类型', value: '' },
  { label: '游戏卡', value: '游戏卡' },
  { label: '话费卡', value: '话费卡' },
  { label: '购物卡', value: '购物卡' },
  { label: '会员卡', value: '会员卡' }
]

// 商品状态选项
const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '在售', value: '在售' },
  { label: '缺货', value: '缺货' },
  { label: '下架', value: '下架' }
]

// 搜索方法
const handleSearch = () => {
  console.log('搜索条件：', searchForm.value)
  // 这里应该实现搜索逻辑
}

// 重置搜索条件
const resetSearch = () => {
  searchForm.value = {
    name: '',
    type: '',
    status: ''
  }
}

// 编辑商品
const editProduct = (row) => {
  console.log('编辑商品：', row)
  // 这里应该实现编辑逻辑
}

// 删除商品
const deleteProduct = (row) => {
  console.log('删除商品：', row)
  // 这里应该实现删除逻辑
}

// 添加商品
const addProduct = () => {
  console.log('添加商品')
  // 这里应该实现添加逻辑
}

onMounted(() => {
  // 组件挂载时，可以加载数据
})
</script>

<template>
  <div class="products-container">
    <!-- 头部 -->
    <div class="page-header">
      <div class="title">商品管理</div>
      <div class="actions">
        <i class="el-icon-refresh"></i>
        <i class="el-icon-setting"></i>
        <i class="el-icon-message"></i>
        <i class="el-icon-user"></i>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-form">
          <el-form :inline="true" :model="searchForm" class="form-inline">
            <el-form-item label="商品名称">
              <el-input v-model="searchForm.name" placeholder="请输入商品名称" size="small" />
            </el-form-item>
            <el-form-item label="商品类型">
              <el-select v-model="searchForm.type" placeholder="请选择类型" size="small">
                <el-option 
                  v-for="item in productTypes" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
            <el-form-item label="商品状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" size="small">
                <el-option 
                  v-for="item in statusOptions" 
                  :key="item.value" 
                  :label="item.label" 
                  :value="item.value" 
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch" size="small">查询</el-button>
              <el-button @click="resetSearch" size="small">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- 表格操作栏 -->
      <div class="table-actions">
        <div class="left-actions">
          <el-button type="primary" size="small" @click="addProduct">添加商品</el-button>
        </div>
        <div class="right-actions">
          <el-button type="primary" size="small" icon="Download">导出数据</el-button>
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-container">
        <el-table
          :data="products"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        >
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="name" label="商品名称" min-width="180" />
          <el-table-column prop="type" label="商品类型" width="120" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '在售' ? 'success' : scope.row.status === '缺货' ? 'warning' : 'info'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="text" size="small" @click="editProduct(scope.row)">编辑</el-button>
              <el-button type="text" size="small" style="color: #F56C6C" @click="deleteProduct(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="50"
          :page-size="10"
        />
      </div>
    </div>
  </div>
</template>

<style lang="less" scoped>
.products-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
}

.page-header {
  height: 50px;
  background-color: #4169E1;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

  .title {
    font-size: 18px;
    font-weight: 500;
  }

  .actions {
    display: flex;
    gap: 15px;

    i {
      font-size: 18px;
      cursor: pointer;
    }
  }
}

.main-content {
  flex: 1;
  padding: 15px;
  overflow: auto;
}

.search-section {
  background-color: #fff;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.table-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.table-container {
  margin-bottom: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}
</style> 