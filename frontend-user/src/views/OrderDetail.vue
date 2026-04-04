<template>
  <div class="order-detail-page">
    <div class="page-container">
      <a-spin :spinning="loading">
        <template v-if="order">
          <!-- 订单状态卡片 -->
          <div :class="['status-card', { 'status-cancelled': order.status === 6 }]">
            <div class="status-icon">{{ getStatusIcon(order.status) }}</div>
            <div class="status-info">
              <h2 class="status-text">{{ getStatusText(order.status) }}</h2>
              <p class="status-desc">{{ getStatusDesc(order.status) }}</p>
            </div>
            <div class="order-no">订单号: {{ order.orderNo }}</div>
          </div>

          <!-- 订单进度 -->
          <div class="progress-card card">
            <h3 class="card-title">订单进度</h3>
            <div class="progress-steps">
              <div v-for="(step, index) in steps" :key="index" :class="['step-item', { active: index <= currentStep, done: index < currentStep }]">
                <div class="step-dot">
                  <CheckOutlined v-if="index < currentStep" />
                  <span v-else>{{ index + 1 }}</span>
                </div>
                <div class="step-content">
                  <span class="step-title">{{ step.title }}</span>
                  <span class="step-time" v-if="step.time">{{ step.time }}</span>
                </div>
                <div v-if="index < steps.length - 1" class="step-line"></div>
              </div>
            </div>
          </div>

          <!-- 配送信息 -->
          <div class="section-card card">
            <h3 class="card-title"><EnvironmentOutlined /> 配送信息</h3>
            <div class="delivery-info">
              <div class="contact-row">
                <span class="contact-name">{{ order.contactName }}</span>
                <span class="contact-phone">{{ order.contactPhone }}</span>
              </div>
              <p class="address">{{ order.deliveryAddress }}</p>
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="section-card card">
            <h3 class="card-title"><ShoppingOutlined /> 商品信息</h3>
            <div class="order-items">
              <div v-for="item in items" :key="item.id" class="order-item">
                <img :src="item.dishImage || defaultImage" class="item-image" />
                <div class="item-info">
                  <span class="item-name">{{ item.dishName }}</span>
                  <span class="item-spec">¥{{ item.price }} × {{ item.quantity }}</span>
                </div>
                <span class="item-subtotal">¥{{ item.subtotal }}</span>
              </div>
            </div>
          </div>

          <!-- 费用明细 -->
          <div class="section-card card">
            <h3 class="card-title">费用明细</h3>
            <div class="fee-list">
              <div class="fee-item"><span>商品金额</span><span>¥{{ order.totalAmount }}</span></div>
              <div class="fee-item"><span>配送费</span><span>¥{{ order.deliveryFee }}</span></div>
              <div class="fee-item total"><span>实付金额</span><span class="total-price">¥{{ order.payAmount }}</span></div>
            </div>
          </div>

          <!-- 订单信息 -->
          <div class="section-card card">
            <h3 class="card-title">订单信息</h3>
            <div class="info-list">
              <div class="info-item"><span class="label">下单时间</span><span>{{ formatTime(order.createdAt) }}</span></div>
              <div v-if="order.payTime" class="info-item"><span class="label">支付时间</span><span>{{ formatTime(order.payTime) }}</span></div>
              <div v-if="order.deliverTime" class="info-item"><span class="label">送达时间</span><span>{{ formatTime(order.deliverTime) }}</span></div>
              <div v-if="order.remark" class="info-item"><span class="label">备注</span><span>{{ order.remark }}</span></div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div v-if="order.status === 0 || order.status <= 1" class="actions-section">
            <a-button v-if="order.status === 0" type="primary" size="large" @click="handlePay">
              <WalletOutlined /> 去支付
            </a-button>
            <a-button v-if="order.status <= 1" size="large" danger @click="handleCancel">取消订单</a-button>
          </div>
        </template>
      </a-spin>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'
import { message, Modal } from 'ant-design-vue'
import { EnvironmentOutlined, ShoppingOutlined, WalletOutlined, CheckOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const loading = ref(false)
const order = ref(null)
const items = ref([])

const defaultImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MCIgaGVpZ2h0PSI2MCI+PHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmOGYwIi8+PHRleHQgeD0iMzAiIHk9IjM1IiBmb250LXNpemU9IjI0IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj7wn42dPC90ZXh0Pjwvc3ZnPg=='

const statusConfig = {
  0: { text: '待支付', icon: '💳', desc: '请尽快完成支付' },
  1: { text: '待接单', icon: '⏳', desc: '商家正在确认订单' },
  2: { text: '备餐中', icon: '👨‍🍳', desc: '商家正在准备您的美食' },
  3: { text: '待取餐', icon: '📦', desc: '等待骑手取餐' },
  4: { text: '配送中', icon: '🛵', desc: '骑手正在飞速赶来' },
  5: { text: '已完成', icon: '✅', desc: '感谢您的订购' },
  6: { text: '已取消', icon: '🚫', desc: '订单已取消' }
}

function getStatusText(status) { return statusConfig[status]?.text || '未知' }
function getStatusIcon(status) { return statusConfig[status]?.icon || '📋' }
function getStatusDesc(status) { return statusConfig[status]?.desc || '' }
function formatTime(time) { return dayjs(time).format('YYYY-MM-DD HH:mm:ss') }

const steps = [
  { title: '已下单' },
  { title: '商家接单' },
  { title: '备餐完成' },
  { title: '配送中' },
  { title: '已送达' }
]

const currentStep = computed(() => {
  if (!order.value) return 0
  const s = order.value.status
  if (s === 6) return -1
  if (s === 0) return 0
  if (s === 1) return 1
  if (s === 2 || s === 3) return 2
  if (s === 4) return 3
  if (s === 5) return 4
  return 0
})

async function fetchOrder() {
  loading.value = true
  try {
    const [orderRes, itemsRes] = await Promise.all([
      request.get(`/orders/${route.params.id}`),
      request.get(`/orders/${route.params.id}/items`)
    ])
    order.value = orderRes.data
    items.value = itemsRes.data
  } finally {
    loading.value = false
  }
}

async function handlePay() {
  await request.post(`/orders/${order.value.id}/pay`)
  message.success('支付成功')
  fetchOrder()
}

function handleCancel() {
  Modal.confirm({
    title: '确认取消订单？',
    okButtonProps: { danger: true },
    onOk: async () => {
      await request.post(`/orders/${order.value.id}/cancel`, null, { params: { reason: '用户取消' } })
      message.success('订单已取消')
      fetchOrder()
    }
  })
}

onMounted(() => { fetchOrder() })
</script>

<style scoped>
.order-detail-page { padding: 24px 0; min-height: 100vh; }

.status-card {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
  border-radius: var(--radius-xl);
  padding: 32px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  position: relative;
}

.status-card.status-cancelled {
  background: linear-gradient(135deg, #8c8c8c, #595959);
}

.status-icon { font-size: 56px; line-height: 1; display: flex; align-items: center; }
.status-info { flex: 1; display: flex; flex-direction: column; justify-content: center; }
.status-text { font-size: 24px; font-weight: 700; margin: 0 0 4px 0; }
.status-desc { font-size: 14px; opacity: 0.9; margin: 0; }
.order-no { font-size: 13px; opacity: 0.8; position: absolute; top: 16px; right: 20px; }

.card { background: #fff; border-radius: var(--radius-lg); padding: 20px; margin-bottom: 16px; box-shadow: var(--card-shadow); }
.card-title { font-size: 16px; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; color: var(--text-color); }

.progress-steps { display: flex; flex-direction: column; gap: 0; }
.step-item { display: flex; align-items: flex-start; gap: 16px; position: relative; padding-bottom: 24px; }
.step-item:last-child { padding-bottom: 0; }
.step-item:last-child .step-line { display: none; }

.step-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--text-light);
  flex-shrink: 0;
  z-index: 1;
}

.step-item.active .step-dot { background: var(--primary-color); color: #fff; }
.step-item.done .step-dot { background: var(--success-color); color: #fff; }

.step-line {
  position: absolute;
  left: 13px;
  top: 28px;
  width: 2px;
  height: calc(100% - 28px);
  background: var(--border-color);
}

.step-item.done .step-line { background: var(--success-color); }

.step-content { flex: 1; }
.step-title { font-size: 14px; color: var(--text-secondary); }
.step-item.active .step-title { color: var(--text-color); font-weight: 500; }
.step-time { font-size: 12px; color: var(--text-light); display: block; margin-top: 4px; }

.delivery-info { background: var(--bg-color); padding: 16px; border-radius: var(--radius-md); }
.contact-row { display: flex; gap: 16px; margin-bottom: 8px; }
.contact-name { font-weight: 600; color: var(--text-color); }
.contact-phone { color: var(--text-secondary); }
.address { color: var(--text-secondary); font-size: 14px; margin: 0; }

.order-items { border-top: 1px solid var(--border-light); padding-top: 16px; }
.order-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--border-light); }
.order-item:last-child { border-bottom: none; }
.item-image { width: 56px; height: 56px; border-radius: var(--radius-sm); object-fit: cover; }
.item-info { flex: 1; display: flex; flex-direction: column; gap: 4px; }
.item-name { font-weight: 500; color: var(--text-color); }
.item-spec { font-size: 13px; color: var(--text-light); }
.item-subtotal { font-weight: 600; color: var(--primary-color); }

.fee-list { display: flex; flex-direction: column; gap: 12px; }
.fee-item { display: flex; justify-content: space-between; color: var(--text-secondary); }
.fee-item.total { border-top: 1px solid var(--border-light); padding-top: 12px; margin-top: 4px; font-weight: 500; color: var(--text-color); }
.total-price { font-size: 20px; font-weight: 700; color: var(--primary-color); }

.info-list { display: flex; flex-direction: column; gap: 12px; }
.info-item { display: flex; justify-content: space-between; font-size: 14px; }
.info-item .label { color: var(--text-light); }

.actions-section { display: flex; gap: 16px; justify-content: center; margin-top: 24px; }
</style>
