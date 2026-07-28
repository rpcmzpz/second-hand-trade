<template>
  <div class="home-container">
    <div class="search-section">
      <el-input v-model="keyword" placeholder="搜索商品..." size="large" clearable @keyup.enter="handleSearch" class="search-input">
        <template #append><el-button :icon="Search" @click="handleSearch">搜索</el-button></template>
      </el-input>
    </div>
    <div class="category-section">
      <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
        <el-radio-button :value="null">全部</el-radio-button>
        <el-radio-button v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">{{ cat.icon }} {{ cat.categoryName }}</el-radio-button>
      </el-radio-group>
    </div>
    <div class="product-list" v-loading="loading">
      <el-row :gutter="20">
        <el-col v-for="product in productList" :key="product.productId" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="product-card" shadow="hover" @click="goToDetail(product.productId)">
            <el-image :src="getProductImage(product)" class="product-image" lazy />
            <div class="product-info">
              <div class="product-title">{{ product.title }}</div>
              <div class="product-price">
                <span class="price">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
              </div>
              <div class="product-meta">
                <span><el-icon :size="14"><View /></el-icon> {{ product.viewCount }}</span>
                <span class="location">{{ product.location }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="pagination" v-if="total > 0">
        <el-pagination v-model:current-page="current" v-model:page-size="size" :page-sizes="[8,16,24,32]" :total="total" layout="total,sizes,prev,pager,next,jumper" @size-change="fetchProductList" @current-change="fetchProductList" />
      </div>
      <el-empty v-if="productList.length===0 && !loading" description="暂无商品" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View } from '@element-plus/icons-vue'
import { getProductList, getCategoryList } from '@/api/product'

const router = useRouter()
const keyword = ref('')
const selectedCategory = ref(null)
const categories = ref([])
const productList = ref([])
const loading = ref(false)
const current = ref(1)
const size = ref(8)
const total = ref(0)

const getProductImage = (product) => {
  if (product.images) {
    try {
      let parsed = JSON.parse(product.images);
      if (Array.isArray(parsed) && parsed.length > 0) return parsed[0];
      if (parsed && typeof parsed === 'object' && parsed.images && parsed.images.length > 0) return parsed.images[0];
    } catch {}
  }
  return 'https://via.placeholder.com/300x300?text=No+Image'
}

const fetchProductList = async () => {
  loading.value = true
  try {
    const res = await getProductList({ current: current.value, size: size.value, categoryId: selectedCategory.value, keyword: keyword.value })
    productList.value = res.data.records
    total.value = res.data.total
  } catch {} finally {
    loading.value = false
  }
}

const handleSearch = () => { current.value = 1; fetchProductList() }
const handleCategoryChange = () => { current.value = 1; fetchProductList() }
const fetchCategories = async () => { try { const res = await getCategoryList(); categories.value = res.data } catch {} }
const goToDetail = (id) => router.push('/product/' + id)

onMounted(() => { fetchCategories(); fetchProductList() })
</script>

<style scoped>
.home-container { padding: 20px 0; }
.search-section { margin-bottom: 30px; }
.search-input { max-width: 600px; margin: 0 auto; }
.category-section { margin-bottom: 30px; text-align: center; }
.product-list { margin-top: 30px; }
.product-card { margin-bottom: 20px; cursor: pointer; }
.product-image { width: 100%; height: 200px; object-fit: cover; }
.product-info { padding: 10px 0; }
.product-title { font-size: 14px; color: #333; margin-bottom: 10px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { margin-bottom: 10px; }
.price { font-size: 20px; color: #f56c6c; font-weight: bold; }
.original-price { font-size: 12px; color: #909399; text-decoration: line-through; margin-left: 8px; }
.product-meta { display: flex; justify-content: space-between; font-size: 12px; color: #909399; }
.pagination { margin-top: 30px; text-align: center; }
</style>
