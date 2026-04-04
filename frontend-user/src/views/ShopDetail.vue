<template>
  <div class="shop-detail-page">
    <div class="page-container">
      <a-spin :spinning="loading">
        <!-- 店铺信息 -->
        <div v-if="shop" class="shop-header card">
          <div class="shop-logo">
            <img :src="shop.logo || defaultImage" :alt="shop.name" />
          </div>
          <div class="shop-info">
            <h1 class="shop-name">{{ shop.name }}</h1>
            <div class="shop-meta">
              <span class="rating">
                <StarFilled class="star-icon" />
                {{ shop.rating?.toFixed(1) || '5.0' }}
              </span>
              <span>月售{{ shop.monthlySales || 0 }}</span>
              <span>¥{{ shop.minPrice }}起送</span>
              <span>配送费¥{{ shop.deliveryFee }}</span>
            </div>
            <p class="shop-desc">{{ shop.description || '暂无描述' }}</p>
            <p class="shop-address">
              <EnvironmentOutlined /> {{ shop.address }}
            </p>
          </div>
        </div>

        <!-- 菜品列表 -->
        <div class="menu-section">
          <div class="menu-layout">
            <!-- 分类侧边栏 -->
            <div class="category-sidebar card">
              <div
                v-for="cat in categories"
                :key="cat.id"
                :class="['category-item', { active: activeCategory === cat.id }]"
                @click="activeCategory = cat.id"
              >
                {{ cat.name }}
              </div>
              <div
                :class="['category-item', { active: activeCategory === 0 }]"
                @click="activeCategory = 0"
              >
                全部菜品
              </div>
            </div>

            <!-- 菜品列表 -->
            <div class="dish-list card">
              <div v-if="filteredDishes.length" class="dishes">
                <div v-for="dish in filteredDishes" :key="dish.id" class="dish-item">
                  <div class="dish-image">
                    <img :src="dish.image || defaultDishImage" :alt="dish.name" />
                  </div>
                  <div class="dish-info">
                    <h3 class="dish-name">{{ dish.name }}</h3>
                    <p class="dish-desc">{{ dish.description || '暂无描述' }}</p>
                    <div class="dish-footer">
                      <div class="dish-price">
                        <span class="price">¥{{ dish.price }}</span>
                        <span v-if="dish.originalPrice" class="original-price">¥{{ dish.originalPrice }}</span>
                      </div>
                      <div class="dish-actions">
                        <a-button
                          v-if="cartStore.getItemQuantity(dish.id) > 0"
                          shape="circle"
                          size="small"
                          @click="handleRemove(dish)"
                        >
                          <MinusOutlined />
                        </a-button>
                        <span v-if="cartStore.getItemQuantity(dish.id) > 0" class="quantity">
                          {{ cartStore.getItemQuantity(dish.id) }}
                        </span>
                        <a-button
                          type="primary"
                          shape="circle"
                          size="small"
                          @click="handleAdd(dish)"
                        >
                          <PlusOutlined />
                        </a-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <a-empty v-else description="暂无菜品" />
            </div>
          </div>
        </div>
      </a-spin>

      <!-- 购物车悬浮栏 -->
      <div v-if="cartStore.totalCount > 0" class="cart-bar">
        <div class="cart-info">
          <div class="cart-icon" @click="showCartDrawer = true">
            <ShoppingCartOutlined />
            <span class="cart-badge">{{ cartStore.totalCount }}</span>
          </div>
          <div class="cart-price">
            <span class="total">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
            <span class="delivery">另需配送费¥{{ shop?.deliveryFee || 0 }}</span>
          </div>
        </div>
        <a-button
          type="primary"
          size="large"
          :disabled="cartStore.totalPrice < (shop?.minPrice || 0)"
          @click="goCheckout"
        >
          {{ cartStore.totalPrice < (shop?.minPrice || 0) ? `还差¥${((shop?.minPrice || 0) - cartStore.totalPrice).toFixed(2)}起送` : '去结算' }}
        </a-button>
      </div>

      <!-- 购物车抽屉 -->
      <a-drawer
        v-model:open="showCartDrawer"
        title="购物车"
        placement="bottom"
        :height="400"
      >
        <div class="cart-items">
          <div v-for="item in cartStore.items" :key="item.dishId" class="cart-item">
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-price">¥{{ item.price }}</span>
            </div>
            <div class="item-actions">
              <a-button shape="circle" size="small" @click="cartStore.removeItem(item.dishId)">
                <MinusOutlined />
              </a-button>
              <span class="item-quantity">{{ item.quantity }}</span>
              <a-button type="primary" shape="circle" size="small" @click="cartStore.addItem({ id: item.dishId, name: item.name, price: item.price, image: item.image })">
                <PlusOutlined />
              </a-button>
            </div>
          </div>
        </div>
        <div class="cart-footer">
          <a-button @click="cartStore.clearCart">清空购物车</a-button>
          <div class="cart-total">
            合计: <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
          </div>
        </div>
      </a-drawer>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { message } from 'ant-design-vue'
import {
  StarFilled,
  EnvironmentOutlined,
  PlusOutlined,
  MinusOutlined,
  ShoppingCartOutlined
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const loading = ref(false)
const shop = ref(null)
const categories = ref([])
const dishes = ref([])
const activeCategory = ref(0)
const showCartDrawer = ref(false)

const defaultImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2ZmZjVmMCIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1zaXplPSI0MCIgdGV4dC1hbmNob3I9Im1pZGRsZSI+8J+NlDwvdGV4dD48L3N2Zz4='
const defaultDishImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2ZmZjVmMCIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1zaXplPSI0MCIgdGV4dC1hbmNob3I9Im1pZGRsZSI+8J+NnTwvdGV4dD48L3N2Zz4='

const filteredDishes = computed(() => {
  if (activeCategory.value === 0) return dishes.value
  return dishes.value.filter(d => d.categoryId === activeCategory.value)
})

async function fetchData() {
  loading.value = true
  try {
    const shopId = route.params.id
    const [shopRes, categoriesRes, dishesRes] = await Promise.all([
      request.get(`/shops/${shopId}`),
      request.get(`/shops/${shopId}/categories`),
      request.get(`/shops/${shopId}/dishes`)
    ])
    shop.value = shopRes.data
    categories.value = categoriesRes.data
    dishes.value = dishesRes.data
    cartStore.setShop(shop.value)
  } finally {
    loading.value = false
  }
}

function handleAdd(dish) {
  cartStore.addItem(dish)
}

function handleRemove(dish) {
  cartStore.removeItem(dish.id)
}

function goCheckout() {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  router.push('/checkout')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.shop-detail-page {
  padding-bottom: 80px;
}

.shop-header {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.shop-logo {
  width: 120px;
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
}

.shop-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-info {
  flex: 1;
}

.shop-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 12px;
}

.shop-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
  color: var(--text-secondary);
  font-size: 14px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--primary-color);
}

.star-icon {
  color: #faad14;
}

.shop-desc {
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.shop-address {
  color: var(--text-light);
  font-size: 13px;
}

.menu-layout {
  display: flex;
  gap: 20px;
}

.category-sidebar {
  width: 120px;
  flex-shrink: 0;
  padding: 12px;
  position: sticky;
  top: 84px;
  height: fit-content;
}

.category-item {
  padding: 12px;
  cursor: pointer;
  border-radius: 8px;
  text-align: center;
  transition: all 0.3s;
  font-size: 14px;
}

.category-item:hover {
  background: var(--primary-light);
}

.category-item.active {
  background: var(--primary-color);
  color: #fff;
}

.dish-list {
  flex: 1;
}

.dish-item {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
}

.dish-item:last-child {
  border-bottom: none;
}

.dish-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.dish-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.dish-desc {
  color: var(--text-light);
  font-size: 13px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.dish-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.dish-price .price {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color);
}

.dish-price .original-price {
  font-size: 12px;
  color: var(--text-light);
  text-decoration: line-through;
  margin-left: 8px;
}

.dish-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity {
  min-width: 24px;
  text-align: center;
  font-weight: 500;
}

.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #333;
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 100;
}

.cart-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.cart-icon {
  position: relative;
  width: 48px;
  height: 48px;
  background: var(--primary-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  cursor: pointer;
  margin-top: -24px;
}

.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ff4d4f;
  color: #fff;
  font-size: 12px;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-price .total {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.cart-price .delivery {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.cart-items {
  max-height: 280px;
  overflow-y: auto;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-color);
}

.item-info {
  display: flex;
  gap: 12px;
}

.item-name {
  font-weight: 500;
}

.item-price {
  color: var(--primary-color);
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-quantity {
  min-width: 24px;
  text-align: center;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
  margin-top: 16px;
}

.total-price {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
}
</style>
