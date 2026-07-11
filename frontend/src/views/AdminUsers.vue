<template>
  <div class="admin-users-container">
    <h2>用户管理</h2>
    <div style="margin-bottom:16px;display:flex;gap:12px">
      <el-input v-model="searchKeyword" placeholder="搜索用户名..." clearable style="width:240px" @keydown.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    <el-table :data="userList" v-loading="loading" style="width:100%">
      <el-table-column prop="userId" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="140" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role==='admin'?'danger':''">{{ row.role === 'admin' ? '管理员' : '普通用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status===1?'success':'info'">{{ row.status===1?'启用':'禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 1"
            size="small"
            type="danger"
            @click="handleToggleStatus(row)"
            :loading="togglingId === row.userId"
          >禁用</el-button>
          <el-button
            v-else
            size="small"
            type="success"
            @click="handleToggleStatus(row)"
            :loading="togglingId === row.userId"
          >启用</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="total>0"
      style="margin-top:20px;text-align:center"
      v-model:current-page="current"
      v-model:page-size="size"
      :total="total"
      layout="total,prev,pager,next"
      @current-change="fetchUsers"
    />
    <el-empty v-if="userList.length===0 && !loading" description="暂无用户" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminUsers, updateUserStatus } from '@/api/admin'

const searchKeyword = ref('')
const userList = ref([])
const loading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const togglingId = ref(null)

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = { page: current.value, size: size.value }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    const res = await getAdminUsers(params)
    userList.value = res.data.records || res.data || []
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

const handleSearch = () => {
  current.value = 1
  fetchUsers()
}

const resetSearch = () => {
  searchKeyword.value = ''
  current.value = 1
  fetchUsers()
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定${actionText}该用户？`, '提示', { type: 'warning' })
    togglingId.value = row.userId
    await updateUserStatus(row.userId, { status: newStatus })
    ElMessage.success(`${actionText}成功`)
    fetchUsers()
  } catch {} finally { togglingId.value = null }
}

onMounted(fetchUsers)
</script>

<style scoped>
.admin-users-container { padding: 20px 0; }
</style>
