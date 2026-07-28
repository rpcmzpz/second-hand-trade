<template>
  <div class="orders-container">
    <h2>我的订单</h2>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="ALL" />
      <el-tab-pane label="待付款" name="PENDING" />
      <el-tab-pane label="待发货" name="PAID" />
      <el-tab-pane label="已发货" name="SHIPPED" />
      <el-tab-pane label="已完成" name="COMPLETED" />
      <el-tab-pane label="已取消" name="CANCELED" />
    </el-tabs>
    <el-table :data="orderList" v-loading="loading" style="width:100%">
      <el-table-column prop="orderId" label="订单号" width="180" />
      <el-table-column label="商品信息" min-width="200">
        <template #default="{ row }"><span>{{ row.productTitle || '商品ID:' + row.productId }}</span></template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="120"><template #default="{ row }">¥{{ row.amount }}</template></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status==='PENDING'" size="small" type="success" @click="handlePay(row)">付款</el-button>
          <el-button v-if="row.status==='PENDING'" size="small" type="danger" @click="handleCancel(row)">取消</el-button>
          <el-button v-if="row.status==='SHIPPED'" size="small" type="primary" @click="handleConfirm(row)">确认收货</el-button>
          <el-button v-if="row.status==='COMPLETED' && !row.reviewed" size="small" type="warning" @click="openReview(row)">评价</el-button>
          <el-button size="small" @click="$router.push('/order/'+row.orderId)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-if="total>0" style="margin-top:20px;text-align:center" v-model:current-page="current" v-model:page-size="size" :total="total" layout="total,prev,pager,next" @current-change="fetchOrders" />
    <el-empty v-if="orderList.length===0 && !loading" description="暂无订单" />

    <el-dialog v-model="reviewDialogVisible" title="评价订单" width="400px">
      <div style="text-align:center;margin-bottom:20px">
        <span>评分：</span><el-rate v-model="reviewForm.rating" />
      </div>
      <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="请输入评价内容..." />
      <template #footer>
        <el-button @click="reviewDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="reviewing">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, updateOrderStatus } from '@/api/order'
import { createReview, getReviewByOrder } from '@/api/review'

const activeTab = ref('ALL')
const orderList = ref([])
const loading = ref(false)
const current = ref(1)
const size = ref(10)
const total = ref(0)
const reviewDialogVisible = ref(false)
const reviewing = ref(false)
const reviewForm = ref({ orderId: null, rating: 5, content: '' })

const statusText = (s) => ({ PENDING:'待付款', PAID:'待发货', SHIPPED:'已发货', COMPLETED:'已完成', CANCELED:'已取消' }[s]||s)
const statusType = (s) => ({ PENDING:'warning', PAID:'', SHIPPED:'', COMPLETED:'success', CANCELED:'info' }[s]||'')

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: current.value, size: size.value }
    if (activeTab.value !== 'ALL') params.status = activeTab.value
    const res = await getOrderList(params)
    const records = res.data.records || []
    for (const r of records) {
      if (r.status === 'COMPLETED') {
        try { const revRes = await getReviewByOrder(r.orderId); r.reviewed = revRes.data && revRes.data.length > 0 } catch { r.reviewed = false }
      }
    }
    orderList.value = records
    total.value = res.data.total || 0
  } catch {} finally { loading.value = false }
}

const handleTabChange = () => { current.value = 1; fetchOrders() }

const handlePay = async (row) => {
  try { await updateOrderStatus(row.orderId, { status: 'PAID' }); ElMessage.success('付款成功'); fetchOrders() } catch {}
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
    await updateOrderStatus(row.orderId, { status: 'CANCELED' })
    ElMessage.success('已取消'); fetchOrders()
  } catch {}
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'warning' })
    await updateOrderStatus(row.orderId, { status: 'COMPLETED' })
    ElMessage.success('收货确认成功'); fetchOrders()
  } catch {}
}

const openReview = (row) => {
  reviewForm.value = { orderId: row.orderId, rating: 5, content: '' }
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  reviewing.value = true
  try {
    const order = orderList.value.find(o => o.orderId === reviewForm.value.orderId)
    await createReview({ 
      orderId: reviewForm.value.orderId, 
      rating: reviewForm.value.rating, 
      content: reviewForm.value.content,
      revieweeId: order ? order.sellerId : null
    })
    ElMessage.success('评价成功'); reviewDialogVisible.value = false; fetchOrders()
  } catch {} finally { reviewing.value = false }
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders-container { padding: 20px 0; }
</style>
