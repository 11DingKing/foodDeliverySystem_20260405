<template>
  <div class="orders-page">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">我的订单</h1>
        <p class="page-desc">查看和管理您的所有订单</p>
      </div>

      <div class="orders-tabs">
        <div
          v-for="tab in tabs"
          :key="tab.key"
          :class="['tab-item', { active: activeStatus === tab.key }]"
          @click="handleTabChange(tab.key)"
        >
          <span class="tab-name">{{ tab.name }}</span>
          <span v-if="tab.count" class="tab-count">{{ tab.count }}</span>
        </div>
      </div>

      <a-spin :spinning="loading">
        <div v-if="orders.length" class="order-list">
          <div v-for="order in orders" :key="order.id" class="order-card" @click="goDetail(order.id)">
            <div class="order-header">
              <div class="order-shop">
                <ShopOutlined class="shop-icon" />
                <span class="shop-name">{{ order.shopName || '店铺' }}</span>
              </div>
              <div :class="['order-status', `status-${order.status}`]">
                {{ getStatusText(order.status) }}
              </div>
            </div>
            <div class="order-body">
              <div class="order-info">
                <p class="order-no">订单号: {{ order.orderNo }}</p>
                <p class="order-time">
                  <ClockCircleOutlined /> {{ formatTime(order.createdAt) }}
                </p>
                <p class="order-address">
                  <EnvironmentOutlined /> {{ order.deliveryAddress }}
                </p>
              </div>
              <div class="order-amount">
                <span class="amount-label">实付</span>
                <span class="amount-value">¥{{ order.payAmount }}</span>
              </div>
            </div>
            <div class="order-footer" @click.stop>
              <a-button v-if="order.status === 0" type="primary" @click="handlePay(order)">
                <WalletOutlined /> 去支付
              </a-button>
              <a-button v-if="order.status <= 1" danger @click="handleCancel(order)">
                取消订单
              </a-button>
              <a-button type="link" @click="goDetail(order.id)">
                查看详情 <RightOutlined />
              </a-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <div class="empty-icon">📋</div>
          <p class="empty-text">暂无订单</p>
          <p class="empty-hint">快去下单吧~</p>
          <a-button type="primary" @click="$router.push('/')">去逛逛</a-button>
        </div>
      </a-spin>

      <div v-if="total > pageSize" class="pagination">
        <a-pagination v-model:current="page" :total="total" :page-size="pageSize" @change="fetchOrders" show-less-items />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { message, Modal } from 'ant-design-vue'
import { ShopOutlined, ClockCircleOutlined, EnvironmentOutlined, WalletOutlined, RightOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const activeStatus = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const tabs = [
  { key: '', name: '全部' },
  { key: '0', name: '待支付' },
  { key: '1', name: '待接单' },
  { key: '2', name: '备餐中' },
  { key: '4', name: '配送中' },
  { key: '5', name: '已完成' },
  { key: '6', name: '已取消' }
]

const statusMap = {
  0: '待支付', 1: '待接单', 2: '备餐中', 3: '待取餐', 4: '配送中', 5: '已完成', 6: '已取消'
}

function getStatusText(status) { return statusMap[status] || '未知' }
function formatTime(time) { return dayjs(time).format('YYYY-MM-DD HH:mm') }

async function fetchOrders() {
  loading.value = true
  try {
    const res = await request.get('/orders', { params: { page: page.value, size: pageSize.value, status: activeStatus.value || undefined } })
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleTabChange(key) {
  activeStatus.value = key
  page.value = 1
  fetchOrders()
}

function goDetail(id) { router.push(`/order/${id}`) }

async function handlePay(order) {
  try {
    await request.post(`/orders/${order.id}/pay`)
    message.success('支付成功')
    fetchOrders()
  } catch (e) {}
}

function handleCancel(order) {
  Modal.confirm({
    title: '确认取消订单？',
    content: '取消后订单将无法恢复',
    okText: '确认取消',
    cancelText: '再想想',
    okButtonProps: { danger: true },
    onOk: async () => {
      await request.post(`/orders/${order.id}/cancel`, null, { params: { reason: '用户取消' } })
      message.success('订单已取消')
      fetchOrders()
    }
  })
}

onMounted(() => { fetchOrders() })
</script>

<style scoped>
.orders-page { padding: 24px 0; min-height: 100vh; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; color: var(--text-color); margin-bottom: 8px; }
.page-desc { font-size: 15px; color: var(--text-secondary); }

.orders-tabs {
  display: flex;
  gap: 8px;
  padding: 16px;
  background: #fff;
  border-radius: var(--radius-lg);
  margin-bottom: 20px;
  overflow-x: auto;
  box-shadow: var(--card-shadow);
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  transition: all var(--transition-normal);
  white-space: nowrap;
  font-weight: 500;
  color: var(--text-secondary);
}

.tab-item:hover { background: var(--primary-light); color: var(--primary-color); }
.tab-item.active { background: var(--primary-color); color: #fff; }
.tab-count { font-size: 12px; background: rgba(255,255,255,0.2); padding: 2px 8px; border-radius: 10px; }

.order-list { display: flex; flex-direction: column; gap: 16px; }

.order-card {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 20px;
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 1px solid transparent;
  box-shadow: var(--card-shadow);
}

.order-card:hover { transform: translateY(-2px); box-shadow: var(--card-shadow-hover); border-color: rgba(255,107,0,0.1); }

.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding-bottom: 16px; border-bottom: 1px solid var(--border-light); }
.order-shop { display: flex; align-items: center; gap: 8px; }
.shop-icon { font-size: 18px; color: var(--primary-color); }
.shop-name { font-size: 16px; font-weight: 600; color: var(--text-color); }

.order-status { padding: 4px 12px; border-radius: 4px; font-size: 13px; font-weight: 500; }
.status-0 { background: var(--warning-light); color: var(--warning-color); }
.status-1, .status-2, .status-3 { background: #e6f7ff; color: #1890ff; }
.status-4 { background: #e6fffb; color: #13c2c2; }
.status-5 { background: var(--success-light); color: var(--success-color); }
.status-6 { background: #f5f5f5; color: var(--text-light); }

.order-body { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.order-info { flex: 1; }
.order-no { font-size: 13px; color: var(--text-light); margin-bottom: 8px; }
.order-time, .order-address { font-size: 14px; color: var(--text-secondary); margin-bottom: 4px; display: flex; align-items: center; gap: 6px; }
.order-amount { text-align: right; }
.amount-label { font-size: 13px; color: var(--text-light); display: block; margin-bottom: 4px; }
.amount-value { font-size: 24px; font-weight: 700; color: var(--primary-color); }

.order-footer { display: flex; justify-content: flex-end; gap: 12px; padding-top: 16px; border-top: 1px solid var(--border-light); }

.empty-state { text-align: center; padding: 80px 20px; background: #fff; border-radius: var(--radius-lg); }
.empty-icon { font-size: 80px; margin-bottom: 16px; }
.empty-text { font-size: 18px; color: var(--text-color); margin-bottom: 8px; }
.empty-hint { font-size: 14px; color: var(--text-light); margin-bottom: 24px; }

.pagination { margin-top: 32px; text-align: center; }
</style>
