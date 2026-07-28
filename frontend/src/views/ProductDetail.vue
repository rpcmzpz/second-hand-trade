<template>
  <div class="product-detail-page" v-loading="loading">
    <div class="detail-container" v-if="product">
      <div class="image-section">
        <el-image
          :src="currentImage"
          :preview-src-list="imageList"
          :preview-teleported="true"
          fit="cover"
          class="main-image"
          lazy
        />
        <div class="thumb-list" v-if="imageList.length > 1">
          <div
            v-for="(img, i) in imageList"
            :key="i"
            class="thumb-item"
            :class="{ active: currentImage === img }"
            @click="currentImage = img"
          >
            <el-image :src="img" fit="cover" class="thumb-image" lazy />
          </div>
        </div>
      </div>
      <div class="info-section">
        <h1 class="product-title">{{ product.title }}</h1>
        <div class="product-price-row">
          <span class="price">¥{{ product.price }}</span>
          <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
        </div>
        <el-divider />
        <div class="info-grid">
          <div class="info-item">
            <span class="label">成色</span>
            <el-tag :type="conditionTagType(product.condition)" size="small">{{ conditionLabel(product.condition) }}</el-tag>
          </div>
          <div class="info-item">
            <span class="label">交易地点</span>
            <span class="value">{{ product.location || '未指定' }}</span>
          </div>
          <div class="info-item">
            <span class="label">卖家</span>
            <span class="value">{{ product.sellerUsername || '未知' }}</span>
          </div>
          <div class="info-item">
            <span class="label">浏览量</span>
            <span class="value"><el-icon :size="14"><View /></el-icon> {{ product.viewCount || 0 }}</span>
          </div>
          <div class="info-item">
            <span class="label">收藏</span>
            <span class="value"><el-icon :size="14"><Star /></el-icon> {{ product.favoriteCount || 0 }}</span>
          </div>
        </div>
        <el-divider />
        <div class="description-section">
          <h3>商品描述</h3>
          <p class="description-text">{{ product.description || '暂无描述' }}</p>
        </div>
        <el-divider />
        <div class="action-section">
          <el-button type="primary" :icon="ChatDotRound" size="large" @click="contactSeller">联系卖家</el-button>
          <el-button :icon="Star" size="large" @click="toggleFavorite" :loading="favLoading" :type="isFavorited ? 'warning' : 'default'">
            {{ isFavorited ? '已收藏' : '收藏' }}
          </el-button>
          <el-button type="success" :icon="ShoppingCart" size="large" @click="showOrderDialog = true">我想要</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="!product && !loading" description="商品不存在" />

    <el-dialog v-model="showOrderDialog" title="确认下单" width="420px" :close-on-click-modal="false">
      <div class="order-confirm">
        <div class="order-product">
          <el-image :src="currentImage" fit="cover" style="width:80px;height:80px;border-radius:8px" />
          <div class="order-product-info">
            <div class="order-product-title">{{ product?.title }}</div>
            <div class="order-product-price">¥{{ product?.price }}</div>
          </div>
        </div>
        <el-form :model="orderForm" :rules="orderRules" ref="orderFormRef" style="margin-top:20px">
          <el-form-item label="收货地址" prop="address">
            <el-input v-model="orderForm.address" placeholder="请输入收货地址" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="orderForm.remark" type="textarea" :rows="2" placeholder="给卖家留言（选填）" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showOrderDialog = false">取消</el-button>
        <el-button type="success" :loading="orderLoading" @click="submitOrder">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { View, Star, ChatDotRound, ShoppingCart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '@/api/product'
import { createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(false)
const currentImage = ref('')
const isFavorited = ref(false)
const favLoading = ref(false)
const showOrderDialog = ref(false)
const orderLoading = ref(false)
const orderFormRef = ref(null)

const orderForm = reactive({
  address: '',
  remark: ''
})

const orderRules = {
  address: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
}

const imageList = computed(() => {
  if (!product.value?.images) return ['https://via.placeholder.com/500x500?text=No+Image']
  try {
    let parsed = JSON.parse(product.value.images)
    if (Array.isArray(parsed) && parsed.length > 0) return parsed
    if (parsed && typeof parsed === 'object' && parsed.images && parsed.images.length > 0) return parsed.images
  } catch {}
  return ['https://via.placeholder.com/500x500?text=No+Image']
})

const conditionLabel = (condition) => {
  const map = { NEW: '全新', LIKE_NEW: '几乎全新', GOOD: '良好', FAIR: '一般' }
  return map[condition] || condition || '未知'
}

const conditionTagType = (condition) => {
  const map = { NEW: 'success', LIKE_NEW: 'primary', GOOD: 'warning', FAIR: 'info' }
  return map[condition] || 'info'
}

const fetchProductDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getProductDetail(id)
    product.value = res.data
    currentImage.value = imageList.value[0] || ''
  } catch {
    product.value = null
  } finally {
    loading.value = false
  }
}

const toggleFavorite = () => {
  isFavorited.value = !isFavorited.value
  ElMessage.success(isFavorited.value ? '已收藏' : '已取消收藏')
}

const contactSeller = () => {
  router.push('/messages?userId=' + product.value?.sellerId)
}

const submitOrder = async () => {
  const valid = await orderFormRef.value.validate().catch(() => false)
  if (!valid) return
  orderLoading.value = true
  try {
    await createOrder({
      productId: product.value.productId,
      address: orderForm.address,
      remark: orderForm.remark
    })
    ElMessage.success('下单成功')
    showOrderDialog.value = false
    orderForm.address = ''
    orderForm.remark = ''
  } catch {
  } finally {
    orderLoading.value = false
  }
}

onMounted(fetchProductDetail)
</script>

<style scoped>
.product-detail-page { padding: 30px 0; max-width: 1100px; margin: 0 auto; }
.detail-container { display: flex; gap: 40px; background: #fff; border-radius: 12px; padding: 30px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.image-section { flex: 0 0 450px; }
.main-image { width: 100%; height: 400px; border-radius: 8px; }
.thumb-list { display: flex; gap: 10px; margin-top: 12px; }
.thumb-item { width: 72px; height: 72px; border-radius: 6px; overflow: hidden; cursor: pointer; border: 2px solid transparent; transition: border-color .3s; }
.thumb-item.active { border-color: #409eff; }
.thumb-image { width: 100%; height: 100%; }
.info-section { flex: 1; }
.product-title { font-size: 22px; color: #303133; margin: 0 0 16px 0; }
.product-price-row { margin-bottom: 8px; }
.price { font-size: 28px; color: #f56c6c; font-weight: bold; }
.original-price { font-size: 16px; color: #909399; text-decoration: line-through; margin-left: 12px; }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.info-item { display: flex; align-items: center; }
.info-item .label { color: #909399; font-size: 14px; min-width: 70px; }
.info-item .value { color: #303133; font-size: 14px; display: flex; align-items: center; gap: 4px; }
.description-section h3 { font-size: 16px; color: #303133; margin-bottom: 12px; }
.description-text { color: #606266; line-height: 1.8; font-size: 14px; white-space: pre-wrap; }
.action-section { display: flex; gap: 12px; }
.order-product { display: flex; gap: 16px; align-items: center; padding: 16px; background: #f5f7fa; border-radius: 8px; }
.order-product-title { font-size: 15px; color: #303133; margin-bottom: 6px; }
.order-product-price { font-size: 20px; color: #f56c6c; font-weight: bold; }
</style>
