<template>
  <div class="merchant-orders">
    <h2 class="page-title">订单管理</h2>

    <div class="stats-row">
      <div class="stat-card">
        <span class="stat-icon">📋</span>
        <div class="stat-info">
          <span class="stat-value">{{ stats.pending || 0 }}</span>
          <span class="stat-label">待接单</span>
        </div>
      </div>
      <div class="stat-card">
        <span class="stat-icon">👨‍🍳</span>
        <div class="stat-info">
          <span class="stat-value">{{ stats.preparing || 0 }}</span>
          <span class="stat-label">备餐中</span>
        </div>
      </div>
      <div class="stat-card">
        <span class="stat-icon">✅</span>
        <div class="stat-info">
          <span class="stat-value">{{ stats.completed || 0 }}</span>
          <span class="stat-label">已完成</span>
        </div>
      </div>
    </div>

    <div class="orders-tabs">
      <div v-for="tab in tabs" :key="tab.key" :class="['tab-item', { active: activeStatus === tab.key }]" @click="handleTabChange(tab.key)">
        {{ tab.name }}
      </div>
    </div>

    <a-spin :spinning="loading">
      <div v-if="orders.length" class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-no">#{{ order.orderNo }}</span>
            <span :class="['order-status', `status-${order.status}`]">{{ getStatusText(order.status) }}</span>
          </div>
          <div class="order-body">
            <div class="customer-info">
              <p class="customer-name"><UserOutlined /> {{ order.contactName }} <span class="phone">{{ order.contactPhone }}</span></p>
              <p class="customer-address"><EnvironmentOutlined /> {{ order.deliveryAddress }}</p>
              <p class="order-time"><ClockCircleOutlined /> {{ formatTime(order.createdAt) }}</p>
            </div>
            <div class="order-amount">
              <span class="amount-value">¥{{ order.payAmount }}</span>
            </div>
          </div>
          <div class="order-actions">
            <a-button v-if="order.status === 1" type="primary" @click="handleAccept(order)">
              <CheckOutlined /> 接单
            </a-button>
            <a-button v-if="order.status === 2" type="primary" @click="handleReady(order)">
              <SendOutlined /> 备餐完成
            </a-button>
            <a-button type="text" @click="viewDetail(order)">查看详情</a-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <div class="empty-icon">📋</div>
        <p>暂无订单</p>
      </div>
    </a-spin>

    <div v-if="total > pageSize" class="pagination">
      <a-pagination v-model:current="page" :total="total" :page-size="pageSize" @change="fetchOrders" />
    </div>

    <!-- 订单详情弹窗 -->
    <a-modal v-model:open="detailVisible" title="订单详情" :footer="null" width="600px">
      <template v-if="currentOrder">
        <div class="detail-section">
          <h4>订单信息</h4>
          <div class="detail-row"><span>订单号</span><span>{{ currentOrder.orderNo }}</span></div>
          <div class="detail-row"><span>下单时间</span><span>{{ formatTime(currentOrder.createdAt) }}</span></div>
          <div class="detail-row"><span>备注</span><span>{{ currentOrder.remark || '无' }}</span></div>
        </div>
        <div class="detail-section">
          <h4>配送信息</h4>
          <div class="detail-row"><span>联系人</span><span>{{ currentOrder.contactName }} {{ currentOrder.contactPhone }}</span></div>
          <div class="detail-row"><span>地址</span><span>{{ currentOrder.deliveryAddress }}</span></div>
        </div>
        <div class="detail-section">
          <h4>商品明细</h4>
          <div v-for="item in orderItems" :key="item.id" class="detail-item">
            <span>{{ item.dishName }} × {{ item.quantity }}</span>
            <span>¥{{ item.subtotal }}</span>
          </div>
        </div>
        <div class="detail-total">
          <div class="total-row"><span>商品金额</span><span>¥{{ currentOrder.totalAmount }}</span></div>
          <div class="total-row"><span>配送费</span><span>¥{{ currentOrder.deliveryFee }}</span></div>
          <div class="total-row final"><span>实付金额</span><span>¥{{ currentOrder.payAmount }}</span></div>
        </div>
      </template>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { message } from 'ant-design-vue'
import { UserOutlined, EnvironmentOutlined, ClockCircleOutlined, CheckOutlined, SendOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)
const orderItems = ref([])
const stats = reactive({ pending: 0, preparing: 0, completed: 0 })

const tabs = [
  { key: '', name: '全部' },
  { key: '1', name: '待接单' },
  { key: '2', name: '备餐中' },
  { key: '3', name: '待取餐' },
  { key: '4', name: '配送中' },
  { key: '5', name: '已完成' }
]

const statusMap = { 1: '待接单', 2: '备餐中', 3: '待取餐', 4: '配送中', 5: '已完成', 6: '已取消' }
function getStatusText(status) { return statusMap[status] || '未知' }
function formatTime(time) { return dayjs(time).format('MM-DD HH:mm') }

async function fetchOrders() {
  loading.value = true
  try {
    const [ordersRes, statsRes] = await Promise.all([
      request.get('/orders/shop', { params: { page: page.value, size: pageSize.value, status: activeStatus.value || undefined } }),
      request.get('/orders/shop/stats')
    ])
    orders.value = ordersRes.data.records
    total.value = ordersRes.data.total
    Object.assign(stats, statsRes.data)
  } finally { loading.value = false }
}

function handleTabChange(key) {
  activeStatus.value = key
  page.value = 1
  fetchOrders()
}

async function handleAccept(order) {
  await request.post(`/orders/${order.id}/accept`)
  message.success('已接单')
  fetchOrders()
}

async function handleReady(order) {
  await request.post(`/orders/${order.id}/ready`)
  message.success('已通知骑手取餐')
  fetchOrders()
}

async function viewDetail(order) {
  currentOrder.value = order
  const res = await request.get(`/orders/${order.id}/items`)
  orderItems.value = res.data
  detailVisible.value = true
}

onMounted(() => { fetchOrders() })
</script>

<style scoped>
.page-title { font-size: 20px; font-weight: 700; margin-bottom: 24px; color: var(--text-color); }

.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { display: flex; align-items: center; gap: 16px; background: #fff; padding: 20px; border-radius: var(--radius-lg); box-shadow: var(--card-shadow); }
.stat-icon { font-size: 36px; }
.stat-value { font-size: 28px; font-weight: 700; color: var(--text-color); display: block; }
.stat-label { font-size: 14px; color: var(--text-secondary); }

.orders-tabs { display: flex; gap: 8px; margin-bottom: 20px; background: #fff; padding: 12px; border-radius: var(--radius-lg); box-shadow: var(--card-shadow); }
.tab-item { padding: 10px 20px; border-radius: 20px; cursor: pointer; font-weight: 500; color: var(--text-secondary); transition: all var(--transition-normal); }
.tab-item:hover { background: var(--primary-light); color: var(--primary-color); }
.tab-item.active { background: var(--primary-color); color: #fff; }

.order-list { display: flex; flex-direction: column; gap: 16px; }
.order-card { background: #fff; border-radius: var(--radius-lg); padding: 20px; box-shadow: var(--card-shadow); transition: all var(--transition-normal); }
.order-card:hover { box-shadow: var(--card-shadow-hover); }

.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding-bottom: 16px; border-bottom: 1px solid var(--border-light); }
.order-no { font-weight: 600; color: var(--text-color); }
.order-status { padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; }
.status-1 { background: var(--warning-light); color: var(--warning-color); }
.status-2, .status-3 { background: #e6f7ff; color: #1890ff; }
.status-4 { background: #e6fffb; color: #13c2c2; }
.status-5 { background: var(--success-light); color: var(--success-color); }
.status-6 { background: #f5f5f5; color: var(--text-light); }

.order-body { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.customer-info p { margin: 6px 0; font-size: 14px; color: var(--text-secondary); display: flex; align-items: center; gap: 8px; }
.customer-name { font-weight: 500; color: var(--text-color) !important; }
.phone { font-weight: 400; color: var(--text-secondary); }
.amount-value { font-size: 24px; font-weight: 700; color: var(--primary-color); }

.order-actions { display: flex; gap: 12px; padding-top: 16px; border-top: 1px solid var(--border-light); }

.empty-state { text-align: center; padding: 80px 20px; background: #fff; border-radius: var(--radius-lg); }
.empty-icon { font-size: 64px; margin-bottom: 16px; }
.empty-state p { color: var(--text-light); }

.pagination { margin-top: 24px; text-align: center; }

.detail-section { margin-bottom: 20px; }
.detail-section h4 { font-size: 14px; font-weight: 600; color: var(--text-color); margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid var(--border-light); }
.detail-row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 14px; }
.detail-row span:first-child { color: var(--text-light); }
.detail-item { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border-light); }
.detail-total { margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border-color); }
.total-row { display: flex; justify-content: space-between; padding: 6px 0; color: var(--text-secondary); }
.total-row.final { font-weight: 600; color: var(--text-color); font-size: 16px; }
.total-row.final span:last-child { color: var(--primary-color); font-size: 20px; }
</style>
