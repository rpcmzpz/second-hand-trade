<template>
  <div class="order-detail-container">
    <el-button @click="$router.back()" :icon="ArrowLeft" style="margin-bottom:20px">返回</el-button>
    <h2>订单详情</h2>
    <el-card v-loading="loading" style="max-width:800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(order.status)">{{ statusText(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="商品信息">{{ order.productTitle || '商品ID:' + order.productId }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ order.amount }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ order.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-if="review" style="max-width:800px;margin-top:20px">
      <template #header><span>我的评价</span></template>
      <div>
        <el-rate v-model="review.rating" disabled show-score />
        <p style="margin-top:10px;color:#666">{{ review.content }}</p>
      </div>
    </el-card>

    <el-empty v-if="!order.orderId && !loading" description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getOrderDetail } from '@/api/order'
import { getReviewByOrder } from '@/api/review'

const route = useRoute()
const order = ref({})
const review = ref(null)
const loading = ref(false)

const statusText = (s) => ({ PENDING:'待付款', PAID:'待发货', SHIPPED:'已发货', COMPLETED:'已完成', CANCELED:'已取消' }[s]||s)
const statusType = (s) => ({ PENDING:'warning', PAID:'', SHIPPED:'', COMPLETED:'success', CANCELED:'info' }[s]||'')

const fetchOrder = async () => {
  loading.value = true
  try {
    const orderId = route.params.id
    const res = await getOrderDetail(orderId)
    order.value = res.data
    if (order.value.status === 'COMPLETED') {
      try {
        const revRes = await getReviewByOrder(orderId)
        if (revRes.data && revRes.data.length > 0) {
          review.value = revRes.data[0]
        }
      } catch { review.value = null }
    }
  } catch {} finally { loading.value = false }
}

onMounted(fetchOrder)
</script>

<style scoped>
.order-detail-container { padding: 20px 0; }
</style>
