<template>
  <div class="home-page">
    <!-- Hero Banner -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">美味即刻送达</h1>
        <p class="hero-subtitle">发现身边好店，享受便捷外卖服务</p>
        <div class="search-box">
          <a-input-search
            v-model:value="keyword"
            placeholder="搜索店铺或美食"
            size="large"
            enter-button="搜索"
            @search="handleSearch"
            class="search-input"
          >
            <template #prefix>
              <SearchOutlined class="search-icon" />
            </template>
          </a-input-search>
        </div>
      </div>
      <div class="hero-decoration">
        <span class="deco-emoji deco-1">🍔</span>
        <span class="deco-emoji deco-2">🍕</span>
        <span class="deco-emoji deco-3">🍜</span>
        <span class="deco-emoji deco-4">🍣</span>
        <span class="deco-emoji deco-5">🥗</span>
      </div>
    </div>

    <div class="page-container">
      <!-- 快捷分类 -->
      <div class="category-section">
        <div class="category-grid">
          <div v-for="cat in categories" :key="cat.name" class="category-item" @click="filterByCategory(cat.name)">
            <span class="category-icon">{{ cat.icon }}</span>
            <span class="category-name">{{ cat.name }}</span>
          </div>
        </div>
      </div>

      <!-- 店铺列表 -->
      <div class="shop-section">
        <div class="section-header">
          <h2 class="section-title">
            <FireOutlined class="title-icon" />
            附近热门店铺
          </h2>
          <span class="shop-count">共 {{ total }} 家店铺</span>
        </div>

        <a-spin :spinning="loading">
          <div v-if="shops.length" class="shop-grid">
            <div
              v-for="(shop, index) in shops"
              :key="shop.id"
              class="shop-card"
              :style="{ animationDelay: `${index * 0.05}s` }"
              @click="goToShop(shop.id)"
            >
              <div class="shop-image-wrapper">
                <img :src="shop.logo || defaultShopImage" :alt="shop.name" class="shop-image" />
                <div class="shop-badge" v-if="shop.isNew">
                  <span>新店</span>
                </div>
                <div class="shop-discount" v-if="shop.discount">
                  <ThunderboltOutlined /> {{ shop.discount }}
                </div>
              </div>
              <div class="shop-content">
                <h3 class="shop-name">{{ shop.name }}</h3>
                <div class="shop-rating-row">
                  <div class="rating-badge">
                    <StarFilled class="star-icon" />
                    <span class="rating-value">{{ shop.rating?.toFixed(1) || '5.0' }}</span>
                  </div>
                  <span class="sales-text">月售{{ formatSales(shop.monthlySales) }}</span>
                  <span class="distance" v-if="shop.distance">{{ shop.distance }}km</span>
                </div>
                <div class="shop-info-row">
                  <span class="info-item">
                    <WalletOutlined />
                    ¥{{ shop.minPrice }}起送
                  </span>
                  <span class="info-divider"></span>
                  <span class="info-item">
                    <CarOutlined />
                    配送¥{{ shop.deliveryFee }}
                  </span>
                </div>
                <div class="shop-tags" v-if="shop.tags">
                  <span v-for="tag in shop.tags?.slice(0, 3)" :key="tag" class="shop-tag">{{ tag }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-icon">🏪</div>
            <p class="empty-text">暂无店铺</p>
            <p class="empty-hint">换个关键词试试吧</p>
          </div>
        </a-spin>

        <!-- 分页 -->
        <div v-if="total > pageSize" class="pagination">
          <a-pagination
            v-model:current="page"
            :total="total"
            :page-size="pageSize"
            @change="fetchShops"
            show-less-items
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { 
  SearchOutlined, 
  StarFilled, 
  FireOutlined,
  ThunderboltOutlined,
  WalletOutlined,
  CarOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const loading = ref(false)
const shops = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const categories = [
  { name: '美食', icon: '🍜' },
  { name: '快餐', icon: '🍔' },
  { name: '甜点', icon: '🍰' },
  { name: '饮品', icon: '🧋' },
  { name: '小吃', icon: '🍢' },
  { name: '火锅', icon: '🍲' },
  { name: '烧烤', icon: '🍖' },
  { name: '日料', icon: '🍣' },
  { name: '西餐', icon: '🍝' },
  { name: '其他', icon: '📋' }
]

const defaultShopImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2ZmZjhmMCIvPjx0ZXh0IHg9IjEwMCIgeT0iMTEwIiBmb250LXNpemU9IjYwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj7wn42UPC90ZXh0Pjwvc3ZnPg=='

function formatSales(sales) {
  if (!sales) return '0'
  if (sales >= 10000) return (sales / 10000).toFixed(1) + '万'
  if (sales >= 1000) return (sales / 1000).toFixed(1) + 'k'
  return sales
}

async function fetchShops() {
  loading.value = true
  try {
    const res = await request.get('/shops', {
      params: {
        page: page.value,
        size: pageSize.value,
        keyword: keyword.value || undefined
      }
    })
    shops.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchShops()
}

function filterByCategory(name) {
  if (name === '其他') return
  // 用分类名搜索店铺
  keyword.value = name
  handleSearch()
}

function goToShop(id) {
  router.push(`/shop/${id}`)
}

onMounted(() => {
  fetchShops()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #ff6b00 0%, #ff9248 50%, #ffb380 100%);
  padding: 48px 20px 64px;
  position: relative;
  overflow: hidden;
}

.hero-content {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
  position: relative;
  z-index: 2;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 12px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.hero-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 32px;
}

.search-box {
  max-width: 500px;
  margin: 0 auto;
}

.search-input {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border-radius: 12px;
  overflow: hidden;
}

.search-input :deep(.ant-input-affix-wrapper) {
  border: none;
  padding: 12px 16px;
  background: #fff;
}

.search-input :deep(.ant-input-group-addon) {
  border: none;
}

.search-input :deep(.ant-btn) {
  height: 50px;
  padding: 0 24px;
  font-size: 15px;
}

.search-icon {
  color: var(--text-light);
  font-size: 18px;
}

.hero-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-emoji {
  position: absolute;
  font-size: 40px;
  opacity: 0.3;
  animation: float 6s ease-in-out infinite;
}

.deco-1 { top: 20%; left: 10%; animation-delay: 0s; }
.deco-2 { top: 60%; left: 5%; animation-delay: 1s; }
.deco-3 { top: 30%; right: 15%; animation-delay: 2s; }
.deco-4 { top: 70%; right: 10%; animation-delay: 3s; }
.deco-5 { top: 15%; right: 25%; animation-delay: 4s; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

/* Category Section */
.category-section {
  margin: -32px 0 32px;
  position: relative;
  z-index: 10;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 12px;
  background: #fff;
  padding: 24px;
  border-radius: var(--radius-lg);
  box-shadow: var(--card-shadow-hover);
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px 8px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.category-item:hover {
  background: var(--primary-light);
  transform: translateY(-2px);
}

.category-icon {
  font-size: 32px;
  line-height: 1;
}

.category-name {
  font-size: 13px;
  color: var(--text-color);
  font-weight: 500;
}

/* Shop Section */
.shop-section {
  margin-top: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-color);
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.title-icon {
  color: var(--primary-color);
}

.shop-count {
  font-size: 14px;
  color: var(--text-light);
}

.shop-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.shop-card {
  background: #fff;
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
  animation: slideUp 0.4s ease backwards;
  border: 1px solid transparent;
}

.shop-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--card-shadow-hover);
  border-color: rgba(255, 107, 0, 0.1);
}

.shop-image-wrapper {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.shop-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.shop-card:hover .shop-image {
  transform: scale(1.05);
}

.shop-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #ff6b00, #ff9248);
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.shop-discount {
  position: absolute;
  bottom: 12px;
  left: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.shop-content {
  padding: 16px;
}

.shop-name {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.shop-rating-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.rating-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  background: var(--warning-light);
  padding: 3px 8px;
  border-radius: 4px;
}

.star-icon {
  color: var(--warning-color);
  font-size: 12px;
}

.rating-value {
  font-size: 13px;
  font-weight: 600;
  color: var(--warning-color);
}

.sales-text {
  font-size: 13px;
  color: var(--text-secondary);
}

.distance {
  font-size: 13px;
  color: var(--text-light);
}

.shop-info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 13px;
  color: var(--text-secondary);
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.info-divider {
  width: 1px;
  height: 12px;
  background: var(--border-color);
}

.shop-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.shop-tag {
  font-size: 11px;
  color: var(--primary-color);
  background: var(--primary-light);
  padding: 2px 8px;
  border-radius: 4px;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 64px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 16px;
  opacity: 0.8;
}

.empty-text {
  font-size: 18px;
  color: var(--text-color);
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 14px;
  color: var(--text-light);
}

/* Pagination */
.pagination {
  margin-top: 40px;
  text-align: center;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-title {
    font-size: 28px;
  }
  
  .category-grid {
    grid-template-columns: repeat(5, 1fr);
    padding: 16px;
  }
  
  .category-icon {
    font-size: 28px;
  }
  
  .shop-grid {
    grid-template-columns: 1fr;
  }
  
  .deco-emoji {
    display: none;
  }
}
</style>
