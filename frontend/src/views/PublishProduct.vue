<template>
  <div class="publish-page">
    <div class="publish-card">
      <h2 class="title">发布商品</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="商品标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入商品标题" size="large" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" size="large" style="width:100%">
                <el-option v-for="cat in categories" :key="cat.categoryId" :label="cat.icon + ' ' + cat.categoryName" :value="cat.categoryId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="价格 (¥)" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :controls="true" placeholder="售价" size="large" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原价 (¥)" prop="originalPrice">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :controls="true" placeholder="选填" size="large" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="成色" prop="condition">
              <el-select v-model="form.condition" placeholder="请选择成色" size="large" style="width:100%">
                <el-option label="全新" value="NEW" />
                <el-option label="几乎全新" value="LIKE_NEW" />
                <el-option label="良好" value="GOOD" />
                <el-option label="一般" value="FAIR" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="交易地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入交易地点，如：教学楼A栋" size="large" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="5" placeholder="请详细描述商品状况、使用时长等信息" />
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            :action="uploadUrl"
            list-type="picture-card"
            :headers="uploadHeaders"
            :on-preview="handlePreview"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :file-list="fileList"
            :limit="9"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">发布商品</el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-dialog v-model="previewVisible" title="图片预览">
      <img :src="previewUrl" style="width:100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { createProduct, getCategoryList } from '@/api/product'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const categories = ref([])
const fileList = ref([])
const previewVisible = ref(false)
const previewUrl = ref('')

const uploadUrl = '/api/upload'
const uploadHeaders = {}

const form = reactive({
  title: '',
  categoryId: null,
  price: null,
  originalPrice: null,
  condition: '',
  location: '',
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  condition: [{ required: true, message: '请选择成色', trigger: 'change' }],
  location: [{ required: true, message: '请输入交易地点', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }]
}

const handlePreview = (file) => {
  previewUrl.value = file.url
  previewVisible.value = true
}

const handleUploadSuccess = (response, file) => {
  file.url = response.data ? response.data.url : response.url
}

const handleRemove = (file) => {
  const idx = fileList.value.findIndex((f) => f.uid === file.uid)
  if (idx > -1) fileList.value.splice(idx, 1)
}

const fetchCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch {}
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const images = fileList.value
      .filter((f) => f.status === 'success' || f.url)
      .map((f) => f.url || f.response?.data)
    await createProduct({
      title: form.title,
      categoryId: form.categoryId,
      price: form.price,
      originalPrice: form.originalPrice,
      condition: form.condition,
      location: form.location,
      description: form.description,
      images: JSON.stringify(images)
    })
    ElMessage.success('发布成功')
    router.push('/home')
  } catch {
  } finally {
    loading.value = false
  }
}

onMounted(fetchCategories)
</script>

<style scoped>
.publish-page { padding: 30px 0; max-width: 800px; margin: 0 auto; }
.publish-card { background: #fff; border-radius: 12px; padding: 40px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.title { text-align: center; color: #303133; margin-bottom: 30px; font-size: 22px; }
</style>
