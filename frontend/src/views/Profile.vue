<template>
  <div class="profile-container">
    <h2>个人中心</h2>

    <el-card v-loading="loading" style="max-width:600px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>基本信息</span>
          <el-button v-if="!editing" type="primary" size="small" @click="startEdit" :icon="Edit">编辑</el-button>
        </div>
      </template>
      <el-form :model="form" label-width="80px" :disabled="!editing">
        <el-form-item label="用户名">
          <span v-if="!editing">{{ form.username || '-' }}</span>
          <el-input v-else v-model="form.username" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <span v-if="!editing">{{ form.realName || '-' }}</span>
          <el-input v-else v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <span v-if="!editing">{{ form.phone || '-' }}</span>
          <el-input v-else v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <span v-if="!editing">{{ form.email || '-' }}</span>
          <el-input v-else v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="form.role==='admin'?'danger':''">{{ form.role === 'admin' ? '管理员' : '普通用户' }}</el-tag>
        </el-form-item>
        <el-form-item v-if="editing">
          <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="max-width:600px;margin-top:20px">
      <template #header><span>修改密码</span></template>
      <el-form :model="passwordForm" label-width="100px" style="max-width:400px">
        <el-form-item label="旧密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="changing">确认修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/user'

const loading = ref(false)
const editing = ref(false)
const saving = ref(false)
const changing = ref(false)
const form = ref({ username: '', realName: '', phone: '', email: '', role: '' })
const originalForm = ref({})
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const fetchProfile = async () => {
  loading.value = true
  try {
    const res = await getUserInfo()
    form.value = res.data
  } catch {} finally { loading.value = false }
}

const startEdit = () => {
  originalForm.value = { ...form.value }
  editing.value = true
}

const cancelEdit = () => {
  form.value = { ...originalForm.value }
  editing.value = false
}

const saveProfile = async () => {
  saving.value = true
  try {
    await updateUserInfo({
      username: form.value.username,
      realName: form.value.realName,
      phone: form.value.phone,
      email: form.value.email
    })
    ElMessage.success('保存成功')
    editing.value = false
  } catch {} finally { saving.value = false }
}

const handleChangePassword = async () => {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  changing.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch {} finally { changing.value = false }
}

onMounted(fetchProfile)
</script>

<style scoped>
.profile-container { padding: 20px 0; }
</style>
