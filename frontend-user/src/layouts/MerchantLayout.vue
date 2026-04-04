<template>
  <a-layout class="merchant-layout">
    <a-layout-sider v-model:collapsed="collapsed" :trigger="null" collapsible theme="light" class="sider">
      <div class="logo">
        <span class="logo-icon">🏪</span>
        <span v-if="!collapsed" class="logo-text">商家中心</span>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" mode="inline">
        <a-menu-item key="shop" @click="router.push('/merchant/shop')">
          <ShopOutlined />
          <span>店铺管理</span>
        </a-menu-item>
        <a-menu-item key="dishes" @click="router.push('/merchant/dishes')">
          <CoffeeOutlined />
          <span>菜品管理</span>
        </a-menu-item>
        <a-menu-item key="orders" @click="router.push('/merchant/orders')">
          <OrderedListOutlined />
          <span>订单管理</span>
        </a-menu-item>
        <a-menu-divider />
        <a-menu-item key="home" @click="router.push('/')">
          <HomeOutlined />
          <span>返回首页</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="header">
        <menu-unfold-outlined v-if="collapsed" class="trigger" @click="collapsed = !collapsed" />
        <menu-fold-outlined v-else class="trigger" @click="collapsed = !collapsed" />
        <div class="header-right">
          <a-dropdown>
            <div class="user-info">
              <a-avatar :src="userStore.userInfo?.avatar" :size="32">
                {{ userStore.userInfo?.nickname?.charAt(0) || 'M' }}
              </a-avatar>
              <span>{{ userStore.userInfo?.nickname }}</span>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="router.push('/')">返回首页</a-menu-item>
                <a-menu-item @click="handleLogout">退出登录</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { message } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  ShopOutlined,
  CoffeeOutlined,
  OrderedListOutlined,
  HomeOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const collapsed = ref(false)
const selectedKeys = ref(['shop'])

watch(() => route.path, (path) => {
  if (path.includes('shop')) selectedKeys.value = ['shop']
  else if (path.includes('dishes')) selectedKeys.value = ['dishes']
  else if (path.includes('orders')) selectedKeys.value = ['orders']
}, { immediate: true })

function handleLogout() {
  userStore.logout()
  message.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.merchant-layout {
  min-height: 100vh;
}

.sider {
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-color);
}

.header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: var(--primary-color);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 64px - 48px);
}
</style>
