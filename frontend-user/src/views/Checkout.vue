<template>
  <div class="checkout-page">
    <div class="page-container">
      <h1 class="page-title">确认订单</h1>

      <div class="checkout-layout">
        <div class="checkout-main">
          <!-- 配送地址 -->
          <div class="section-card card">
            <h2 class="section-title"><EnvironmentOutlined /> 配送地址</h2>
            <div v-if="selectedAddress" class="address-selected" @click="showAddressModal = true">
              <div class="address-content">
                <div class="address-header">
                  <span class="contact-name">{{ selectedAddress.contactName }}</span>
                  <span class="contact-phone">{{ selectedAddress.contactPhone }}</span>
                  <a-tag v-if="selectedAddress.isDefault" color="orange" size="small">默认</a-tag>
                </div>
                <p class="address-detail">{{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detail }}</p>
              </div>
              <RightOutlined class="arrow-icon" />
            </div>
            <a-button v-else type="dashed" block size="large" @click="showAddressModal = true">
              <PlusOutlined /> 选择配送地址
            </a-button>
          </div>

          <!-- 商品信息 -->
          <div class="section-card card">
            <h2 class="section-title"><ShopOutlined /> {{ cartStore.shopInfo?.name }}</h2>
            <div class="order-items">
              <div v-for="item in cartStore.items" :key="item.dishId" class="order-item">
                <img :src="item.image || defaultImage" :alt="item.name" class="item-image" />
                <div class="item-info">
                  <span class="item-name">{{ item.name }}</span>
                  <span class="item-price">¥{{ item.price }} × {{ item.quantity }}</span>
                </div>
                <span class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </div>
            </div>
          </div>

          <!-- 备注 -->
          <div class="section-card card">
            <h2 class="section-title"><EditOutlined /> 订单备注</h2>
            <a-textarea v-model:value="remark" placeholder="如需备注请输入，如：少放辣、不要香菜等" :rows="2" :maxlength="200" show-count />
          </div>
        </div>

        <!-- 订单汇总 -->
        <div class="checkout-sidebar">
          <div class="summary-card card">
            <h3 class="summary-title">订单汇总</h3>
            <div class="summary-list">
              <div class="summary-item"><span>商品金额</span><span>¥{{ cartStore.totalPrice.toFixed(2) }}</span></div>
              <div class="summary-item"><span>配送费</span><span>¥{{ cartStore.shopInfo?.deliveryFee?.toFixed(2) || '0.00' }}</span></div>
              <div class="summary-item discount" v-if="discount > 0"><span>优惠</span><span>-¥{{ discount.toFixed(2) }}</span></div>
            </div>
            <div class="summary-total">
              <span>实付金额</span>
              <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
            </div>
            <a-button type="primary" size="large" block :loading="submitting" :disabled="!selectedAddress" @click="handleSubmit" class="submit-btn">
              {{ submitting ? '提交中...' : `提交订单 ¥${totalAmount.toFixed(2)}` }}
            </a-button>
            <p v-if="!selectedAddress" class="submit-hint">请先选择配送地址</p>
          </div>
        </div>
      </div>

      <!-- 地址选择弹窗 -->
      <a-modal v-model:open="showAddressModal" title="选择配送地址" :footer="null" width="520px">
        <a-spin :spinning="loadingAddresses">
          <div v-if="addresses.length" class="address-list">
            <div v-for="addr in addresses" :key="addr.id" :class="['address-option', { selected: selectedAddress?.id === addr.id }]" @click="selectAddress(addr)">
              <div class="option-content">
                <div class="option-header">
                  <span class="contact-name">{{ addr.contactName }}</span>
                  <span class="contact-phone">{{ addr.contactPhone }}</span>
                  <a-tag v-if="addr.isDefault" color="orange" size="small">默认</a-tag>
                </div>
                <p class="option-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</p>
              </div>
              <CheckOutlined v-if="selectedAddress?.id === addr.id" class="check-icon" />
            </div>
          </div>
          <div v-else class="empty-address">
            <p>暂无地址</p>
            <a-button type="primary" @click="$router.push('/addresses')">去添加地址</a-button>
          </div>
        </a-spin>
      </a-modal>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import request from '@/utils/request'
import { message } from 'ant-design-vue'
import { EnvironmentOutlined, ShopOutlined, EditOutlined, PlusOutlined, RightOutlined, CheckOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const cartStore = useCartStore()

const addresses = ref([])
const selectedAddress = ref(null)
const remark = ref('')
const showAddressModal = ref(false)
const loadingAddresses = ref(false)
const submitting = ref(false)
const discount = ref(0)

const defaultImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MCIgaGVpZ2h0PSI2MCI+PHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZmZmOGYwIi8+PHRleHQgeD0iMzAiIHk9IjM1IiBmb250LXNpemU9IjI0IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj7wn42dPC90ZXh0Pjwvc3ZnPg=='

const totalAmount = computed(() => cartStore.totalPrice + (cartStore.shopInfo?.deliveryFee || 0) - discount.value)

async function fetchAddresses() {
  loadingAddresses.value = true
  try {
    const res = await request.get('/addresses')
    addresses.value = res.data
    const defaultAddr = addresses.value.find(a => a.isDefault)
    if (defaultAddr) selectedAddress.value = defaultAddr
    else if (addresses.value.length) selectedAddress.value = addresses.value[0]
  } finally {
    loadingAddresses.value = false
  }
}

function selectAddress(addr) {
  selectedAddress.value = addr
  showAddressModal.value = false
}

async function handleSubmit() {
  if (!selectedAddress.value) { message.warning('请选择配送地址'); return }
  if (!cartStore.items.length) { message.warning('购物车为空'); return }

  submitting.value = true
  try {
    const res = await request.post('/orders', {
      shopId: cartStore.shopId,
      addressId: selectedAddress.value.id,
      items: cartStore.items.map(item => ({ dishId: item.dishId, quantity: item.quantity })),
      remark: remark.value || undefined
    })
    await request.post(`/orders/${res.data.id}/pay`)
    message.success('下单成功')
    cartStore.clearCart()
    router.push(`/order/${res.data.id}`)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!cartStore.items.length) { message.warning('购物车为空'); router.push('/'); return }
  fetchAddresses()
})
</script>

<style scoped>
.checkout-page { padding: 24px 0; min-height: 100vh; }
.page-title { font-size: 28px; font-weight: 700; color: var(--text-color); margin-bottom: 24px; }

.checkout-layout { display: flex; gap: 24px; align-items: flex-start; }
.checkout-main { flex: 1; min-width: 0; }
.checkout-sidebar { width: 360px; flex-shrink: 0; position: sticky; top: 88px; }

.section-card { margin-bottom: 16px; }
.section-title { font-size: 16px; font-weight: 600; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; color: var(--text-color); }

.address-selected {
  display: flex;
  align-items: center;
  padding: 16px;
  background: var(--primary-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-normal);
}
.address-selected:hover { background: #ffedd5; }
.address-content { flex: 1; }
.address-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.contact-name { font-weight: 600; color: var(--text-color); }
.contact-phone { color: var(--text-secondary); }
.address-detail { color: var(--text-secondary); font-size: 14px; margin: 0; }
.arrow-icon { color: var(--text-light); }

.order-items { border-top: 1px solid var(--border-light); padding-top: 16px; }
.order-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--border-light); }
.order-item:last-child { border-bottom: none; }
.item-image { width: 56px; height: 56px; border-radius: var(--radius-sm); object-fit: cover; }
.item-info { flex: 1; display: flex; flex-direction: column; gap: 4px; }
.item-name { font-weight: 500; color: var(--text-color); }
.item-price { font-size: 13px; color: var(--text-light); }
.item-subtotal { font-weight: 600; color: var(--primary-color); }

.summary-card { padding: 24px; }
.summary-title { font-size: 18px; font-weight: 600; margin-bottom: 20px; color: var(--text-color); }
.summary-list { display: flex; flex-direction: column; gap: 12px; margin-bottom: 16px; }
.summary-item { display: flex; justify-content: space-between; color: var(--text-secondary); }
.summary-item.discount { color: var(--success-color); }
.summary-total { display: flex; justify-content: space-between; padding-top: 16px; border-top: 1px solid var(--border-light); font-weight: 500; color: var(--text-color); margin-bottom: 20px; }
.total-price { font-size: 24px; font-weight: 700; color: var(--primary-color); }
.submit-btn { height: 52px; font-size: 16px; font-weight: 600; }
.submit-hint { text-align: center; font-size: 13px; color: var(--text-light); margin-top: 12px; }

.address-list { max-height: 400px; overflow-y: auto; }
.address-option {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  margin-bottom: 12px;
  cursor: pointer;
  transition: all var(--transition-normal);
}
.address-option:hover { border-color: var(--primary-color); }
.address-option.selected { border-color: var(--primary-color); background: var(--primary-light); }
.option-content { flex: 1; }
.option-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.option-detail { color: var(--text-secondary); font-size: 14px; margin: 0; }
.check-icon { color: var(--primary-color); font-size: 18px; }
.empty-address { text-align: center; padding: 40px; color: var(--text-light); }

@media (max-width: 900px) {
  .checkout-layout { flex-direction: column; }
  .checkout-sidebar { width: 100%; position: static; }
}
</style>
