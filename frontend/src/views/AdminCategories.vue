<template>
  <div class="admin-categories">
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="openAddDialog">添加分类</el-button>
    </div>

    <el-table :data="categories" v-loading="loading" border stripe style="width:100%">
      <el-table-column prop="categoryId" label="ID" width="80" />
      <el-table-column prop="icon" label="图标" width="80">
        <template #default="{ row }">
          <span style="font-size:24px">{{ row.icon || '📦' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类名称" min-width="150" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '添加分类'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="如 📚 💻 👔 🎮 👗 🔧" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '保存修改' : '确认添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminCategories, addCategory, updateCategory, deleteCategory } from '@/api/category'

const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const form = ref({ categoryName: '', icon: '', sortOrder: 99 })
let editingId = null

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getAdminCategories()
    categories.value = res.data || []
  } catch {} finally { loading.value = false }
}

const openAddDialog = () => {
  isEdit.value = false
  editingId = null
  form.value = { categoryName: '', icon: '', sortOrder: 99 }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  editingId = row.categoryId
  form.value = {
    categoryName: row.categoryName,
    icon: row.icon || '',
    sortOrder: row.sortOrder || 99
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.categoryName.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateCategory(editingId, form.value)
      ElMessage.success('更新成功')
    } else {
      await addCategory(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchCategories()
  } catch {} finally { submitting.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除分类「${row.categoryName}」？`, '提示', { type: 'warning' })
    await deleteCategory(row.categoryId)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch {}
}

onMounted(fetchCategories)
</script>

<style scoped>
.admin-categories { padding: 20px 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; }
</style>
