<template>
  <div class="rider-orders">
    <div class="page-header">
      <h2 class="page-title">我的配送</h2>
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
            <span :class="['order-status', order.status === 4 ? 'delivering' : 'done']">
              {{ order.status === 4 ? '🛵 配送中' : '✅ 已完成' }}
            </span>
          </div>
          <div class="order-body">
            <div class="delivery-info">
              <div class="shop-info">
                <ShopOutlined class="shop-icon" />
                <div class="shop-detail">
                  <span class="shop-name">{{ order.shopName }}</span>
                  <span class="shop-address">{{ order.shopAddress }}</span>
                </div>
              </div>
              <div class="info-row">
                <UserOutlined />
                <span class="contact">{{ order.contactName }}</span>
                <span class="phone">{{ order.contactPhone }}</span>
              </div>
              <div class="info-row">
                <EnvironmentOutlined />
                <span class="address">{{ order.deliveryAddress }}</span>
              </div>
              <div class="info-row">
                <ClockCircleOutlined />
                <span class="time">{{ formatTime(order.createdAt) }}</span>
              </div>
            </div>
            <div class="order-fee">
              <span class="fee-label">配送费</span>
              <span class="fee-value">¥{{ order.deliveryFee }}</span>
            </div>
          </div>
          <div v-if="order.status === 4" class="order-actions">
            <a-button type="primary" size="large" block @click="handleDeliver(order)">
              <CheckCircleOutlined /> 确认送达
            </a-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <div class="empty-icon">📦</div>
        <p class="empty-text">暂无配送记录</p>
        <p class="empty-hint">去接单页面接单吧</p>
      </div>
    </a-spin>

    <div v-if="total > pageSize" class="pagination">
      <a-pagination v-model:current="page" :total="total" :page-size="pageSize" @change="fetchOrders" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { message } from 'ant-design-vue'
import { UserOutlined, EnvironmentOutlined, ClockCircleOutlined, CheckCircleOutlined, ShopOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const tabs = [
  { key: '', name: '全部' },
  { key: '4', name: '配送中' },
  { key: '5', name: '已完成' }
]

function formatTime(time) { return dayjs(time).format('MM-DD HH:mm') }

async function fetchOrders() {
  loading.value = true
  try {
    const res = await request.get('/orders/rider', { params: { page: page.value, size: pageSize.value, status: activeStatus.value || undefined } })
    orders.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleTabChange(key) {
  activeStatus.value = key
  page.value = 1
  fetchOrders()
}

async function handleDeliver(order) {
  await request.post(`/orders/${order.id}/deliver`)
  message.success('订单已送达')
  fetchOrders()
}

onMounted(() => { fetchOrders() })
</script>

<style scoped>
.page-header { margin-bottom: 24px; }
.page-title { font-size: 20px; font-weight: 700; color: var(--text-color); }

.orders-tabs { display: flex; gap: 8px; margin-bottom: 20px; background: #fff; padding: 12px; border-radius: var(--radius-lg); box-shadow: var(--card-shadow); }
.tab-item { padding: 10px 24px; border-radius: 20px; cursor: pointer; font-weight: 500; color: var(--text-secondary); transition: all var(--transition-normal); }
.tab-item:hover { background: var(--primary-light); color: var(--primary-color); }
.tab-item.active { background: var(--primary-color); color: #fff; }

.order-list { display: flex; flex-direction: column; gap: 16px; }

.order-card {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--card-shadow);
  transition: all var(--transition-normal);
}
.order-card:hover { box-shadow: var(--card-shadow-hover); }

.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding-bottom: 16px; border-bottom: 1px solid var(--border-light); }
.order-no { font-weight: 600; color: var(--text-color); }
.order-status { padding: 6px 14px; border-radius: 20px; font-size: 13px; font-weight: 500; }
.order-status.delivering { background: #e6f7ff; color: #1890ff; }
.order-status.done { background: var(--success-light); color: var(--success-color); }

.order-body { display: flex; justify-content: space-between; align-items: flex-start; }

.delivery-info { flex: 1; }
.shop-info { display: flex; align-items: flex-start; gap: 10px; background: var(--primary-light); padding: 12px; border-radius: var(--radius-sm); margin-bottom: 12px; }
.shop-icon { color: var(--primary-color); font-size: 16px; min-width: 16px; margin-top: 2px; }
.shop-detail { display: flex; flex-direction: column; gap: 4px; }
.shop-name { font-weight: 600; color: var(--primary-color); font-size: 15px; }
.shop-address { font-size: 13px; color: var(--text-secondary); }
.info-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; font-size: 14px; color: var(--text-secondary); padding-left: 12px; }
.info-row:last-child { margin-bottom: 0; }
.info-row :deep(.anticon) { min-width: 16px; font-size: 16px; }
.contact { font-weight: 600; color: var(--text-color); }
.address { flex: 1; }

.order-fee { text-align: right; }
.fee-label { font-size: 12px; color: var(--text-light); display: block; margin-bottom: 4px; }
.fee-value { font-size: 24px; font-weight: 700; color: var(--primary-color); }

.order-actions { margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border-light); }

.empty-state { text-align: center; padding: 80px 20px; background: #fff; border-radius: var(--radius-lg); }
.empty-icon { font-size: 80px; margin-bottom: 16px; }
.empty-text { font-size: 18px; color: var(--text-color); margin-bottom: 8px; }
.empty-hint { font-size: 14px; color: var(--text-light); }

.pagination { margin-top: 24px; text-align: center; }
</style>
