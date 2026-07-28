<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-left">
        <h2 @click="$router.push('/home')" style="cursor:pointer">🏫 校园二手交易平台</h2>
      </div>
      <div class="header-right">
        <template v-if="userInfo">
          <el-button link @click="$router.push('/home')">首页</el-button>
          <el-button link @click="$router.push('/publish')">发布商品</el-button>
          <el-button link @click="$router.push('/orders')">我的订单</el-button>
          <el-button link @click="$router.push('/messages')">
            站内信
            <el-badge v-if="unreadCount > 0" :value="unreadCount" style="margin-left: 6px" />
          </el-button>
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">{{ userInfo.username }} <el-icon><ArrowDown /></el-icon></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="userInfo.role === 'admin'" command="dashboard">📊 仪表盘</el-dropdown-item>
                <el-dropdown-item v-if="userInfo.role === 'admin'" command="adminUsers">👥 用户管理</el-dropdown-item>
                <el-dropdown-item v-if="userInfo.role === 'admin'" command="adminCategories">📂 分类管理</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button link @click="$router.push('/login')">登录</el-button>
          <el-button link @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { getUserInfo } from '@/api/user'
import { getUnreadCount } from '@/api/message'

const router = useRouter()
const userInfo = ref(null)
const unreadCount = ref(0)
let unreadTimer = null

const loadUserInfo = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return
    const res = await getUserInfo()
    userInfo.value = res.data
  } catch (e) {
    userInfo.value = null
  }
}

const fetchUnread = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch {}
}

const startPolling = () => {
  if (localStorage.getItem('token')) {
    fetchUnread()
    unreadTimer = setInterval(fetchUnread, 15000)
  }
}

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    localStorage.removeItem('token')
    userInfo.value = null
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'dashboard') {
    router.push('/admin/dashboard')
  } else if (cmd === 'adminUsers') {
    router.push('/admin/users')
  } else if (cmd === 'adminCategories') {
    router.push('/admin/categories')
  }
}

onMounted(() => {
  loadUserInfo()
  startPolling()
})

onUnmounted(() => {
  if (unreadTimer) clearInterval(unreadTimer)
})
</script>

<style scoped>
.layout-container { min-height: 100vh; background: #f5f7fa; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.06); padding: 0 30px; }
.header-left h2 { margin: 0; color: #409eff; font-size: 20px; }
.header-right { display: flex; align-items: center; gap: 10px; }
.user-dropdown { cursor: pointer; color: #409eff; display: flex; align-items: center; gap: 4px; }
</style>
