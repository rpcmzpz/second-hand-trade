<template>
  <div class="admin-dashboard">
    <h2>管理员仪表盘</h2>
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="40" color="#409eff"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ data.totalUsers ?? '-' }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="40" color="#67c23a"><Goods /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ data.totalProducts ?? '-' }}</div>
              <div class="stat-label">总商品数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="40" color="#e6a23c"><Document /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ data.totalOrders ?? '-' }}</div>
              <div class="stat-label">总订单数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="40" color="#f56c6c"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ data.todayNewUsers ?? '-' }}</div>
              <div class="stat-label">今日新增</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div class="quick-links" style="margin-top:30px">
      <el-card shadow="hover">
        <template #header>快捷操作</template>
        <el-space wrap>
          <el-button type="primary" @click="$router.push('/admin/users')">
            <el-icon><User /></el-icon> 用户管理
          </el-button>
          <el-button type="success" @click="$router.push('/admin/categories')">
            <el-icon><FolderOpened /></el-icon> 分类管理
          </el-button>
          <el-button type="warning" @click="$router.push('/publish')">
            <el-icon><Plus /></el-icon> 发布商品
          </el-button>
        </el-space>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Goods, Document, TrendCharts, FolderOpened, Plus } from '@element-plus/icons-vue'
import { getDashboard } from '@/api/admin'

const loading = ref(false)
const data = ref({ totalUsers: 0, totalProducts: 0, totalOrders: 0, todayNewUsers: 0 })

const fetchDashboard = async () => {
  loading.value = true
  try {
    const res = await getDashboard()
    data.value = res.data
  } catch {} finally { loading.value = false }
}

onMounted(fetchDashboard)
</script>

<style scoped>
.admin-dashboard { padding: 20px 0; }
.stat-card { display:flex;align-items:center;gap:20px;padding:10px 0 }
.stat-info { display:flex;flex-direction:column;gap:4px }
.stat-value { font-size:28px;font-weight:bold;color:#303133 }
.stat-label { font-size:14px;color:#909399 }
</style>
