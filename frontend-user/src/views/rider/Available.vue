<template>
  <div class="rider-available">
    <div class="page-header">
      <h2 class="page-title">待接订单</h2>
      <p class="page-desc">以下订单等待骑手接单配送</p>
    </div>

    <a-spin :spinning="loading">
      <div v-if="orders.length" class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-no">#{{ order.orderNo }}</span>
            <span class="order-status">待取餐</span>
          </div>
          <div class="order-body">
            <div class="location-info">
              <div class="location-item pickup">
                <div class="location-icon">🏪</div>
                <div class="location-detail">
                  <span class="location-label">取餐地址</span>
                  <span class="location-text">{{ order.shopName || '店铺' }}</span>
                  <span class="location-address">{{ order.shopAddress }}</span>
                </div>
              </div>
              <div class="location-line"></div>
              <div class="location-item delivery">
                <div class="location-icon">📍</div>
                <div class="location-detail">
                  <span class="location-label">送达地址</span>
                  <span class="location-text">{{ order.deliveryAddress }}</span>
                </div>
              </div>
            </div>
            <div class="order-meta">
              <div class="meta-item">
                <UserOutlined />
                <span>{{ order.contactName }} {{ order.contactPhone }}</span>
              </div>
              <div class="meta-item">
                <ClockCircleOutlined />
                <span>{{ formatTime(order.createdAt) }}</span>
              </div>
            </div>
          </div>
          <div class="order-footer">
            <div class="delivery-fee">
              <span class="fee-label">配送费</span>
              <span class="fee-value">¥{{ order.deliveryFee }}</span>
            </div>
            <a-button type="primary" size="large" @click="handlePickup(order)">
              <CarOutlined /> 接单配送
            </a-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <div class="empty-icon">🛵</div>
        <p class="empty-text">暂无待接订单</p>
        <p class="empty-hint">休息一下，等待新订单</p>
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
import { UserOutlined, ClockCircleOutlined, CarOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const loading = ref(false)
const orders = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

function formatTime(time) { return dayjs(time).format('MM-DD HH:mm') }

async function fetchOrders() {
  loading.value = true
  try {
    const res = await request.get('/orders/available', { params: { page: page.value, size: pageSize.value } })
    orders.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function handlePickup(order) {
  await request.post(`/orders/${order.id}/pickup`)
  message.success('接单成功，请尽快取餐配送')
  fetchOrders()
}

onMounted(() => { fetchOrders() })
</script>

<style scoped>
.page-header { margin-bottom: 24px; }
.page-title { font-size: 20px; font-weight: 700; margin-bottom: 8px; color: var(--text-color); }
.page-desc { font-size: 14px; color: var(--text-secondary); }

.order-list { display: flex; flex-direction: column; gap: 16px; }

.order-card {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--card-shadow);
  transition: all var(--transition-normal);
  border: 2px solid transparent;
}
.order-card:hover { box-shadow: var(--card-shadow-hover); border-color: rgba(255,107,0,0.2); }

.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.order-no { font-weight: 600; color: var(--text-color); }
.order-status { padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; background: #e6f7ff; color: #1890ff; }

.location-info { background: var(--bg-color); border-radius: var(--radius-md); padding: 16px; margin-bottom: 16px; }
.location-item { display: flex; gap: 12px; align-items: flex-start; }
.location-icon { font-size: 24px; }
.location-detail { display: flex; flex-direction: column; gap: 4px; }
.location-label { font-size: 12px; color: var(--text-light); }
.location-text { font-size: 14px; color: var(--text-color); font-weight: 500; }
.location-address { font-size: 13px; color: var(--text-secondary); }
.location-line { width: 2px; height: 24px; background: var(--border-color); margin: 8px 0 8px 11px; }

.order-meta { display: flex; gap: 24px; }
.meta-item { display: flex; align-items: center; gap: 8px; font-size: 14px; color: var(--text-secondary); }

.order-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 16px; border-top: 1px solid var(--border-light); margin-top: 16px; }
.fee-label { font-size: 13px; color: var(--text-light); display: block; }
.fee-value { font-size: 24px; font-weight: 700; color: var(--primary-color); }

.empty-state { text-align: center; padding: 80px 20px; background: #fff; border-radius: var(--radius-lg); }
.empty-icon { font-size: 80px; margin-bottom: 16px; }
.empty-text { font-size: 18px; color: var(--text-color); margin-bottom: 8px; }
.empty-hint { font-size: 14px; color: var(--text-light); }

.pagination { margin-top: 24px; text-align: center; }
</style>
