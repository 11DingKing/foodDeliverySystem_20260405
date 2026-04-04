<template>
  <a-layout class="main-layout">
    <a-layout-header class="header">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <span class="logo-icon">🍔</span>
          <span class="logo-text">美食外卖</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">
            <HomeOutlined /> 首页
          </router-link>
          <router-link v-if="userStore.isLoggedIn" to="/orders" class="nav-item" :class="{ active: $route.path.startsWith('/order') }">
            <OrderedListOutlined /> 订单
          </router-link>
        </nav>
        <div class="header-right">
          <template v-if="userStore.isLoggedIn">
            <a-dropdown placement="bottomRight">
              <div class="user-info">
                <a-avatar :src="userStore.userInfo?.avatar" :size="36" class="user-avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                </a-avatar>
                <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
                <DownOutlined class="dropdown-icon" />
              </div>
              <template #overlay>
                <a-menu class="user-menu">
                  <a-menu-item key="profile" @click="router.push('/profile')">
                    <UserOutlined /> 个人中心
                  </a-menu-item>
                  <a-menu-item key="addresses" @click="router.push('/addresses')">
                    <EnvironmentOutlined /> 地址管理
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item v-if="userStore.isUser" key="apply-merchant" @click="router.push('/apply-merchant')">
                    <ShopOutlined /> 申请开店
                  </a-menu-item>
                  <a-menu-item v-if="userStore.isUser" key="apply-rider" @click="router.push('/apply-rider')">
                    <CarOutlined /> 成为骑手
                  </a-menu-item>
                  <a-menu-item v-if="userStore.isMerchant" key="merchant" @click="router.push('/merchant')">
                    <ShopOutlined /> 商家中心
                  </a-menu-item>
                  <a-menu-item v-if="userStore.isRider" key="rider" @click="router.push('/rider')">
                    <CarOutlined /> 骑手中心
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout" @click="handleLogout" class="logout-item">
                    <LogoutOutlined /> 退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
          <template v-else>
            <a-button type="text" @click="router.push('/login')" class="login-btn">登录</a-button>
            <a-button type="primary" @click="router.push('/register')" class="register-btn">注册</a-button>
          </template>
        </div>
      </div>
    </a-layout-header>
    <a-layout-content class="content">
      <router-view />
    </a-layout-content>
    <a-layout-footer class="footer">
      <div class="footer-content">
        <p>© 2024 美食外卖 - 发现身边美食，享受便捷生活</p>
      </div>
    </a-layout-footer>
  </a-layout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { message } from 'ant-design-vue'
import { HomeOutlined, OrderedListOutlined, UserOutlined, EnvironmentOutlined, ShopOutlined, CarOutlined, LogoutOutlined, DownOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

function handleLogout() {
  userStore.logout()
  cartStore.clearCart()
  message.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.main-layout { min-height: 100vh; display: flex; flex-direction: column; }

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 0;
  height: 64px;
  overflow: hidden;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 64px;
  gap: 40px;
  overflow: hidden;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 10px;
  transition: transform var(--transition-fast);
}

.logo:hover { transform: scale(1.02); }
.logo-icon { font-size: 32px; }
.logo-text { font-size: 22px; font-weight: 700; color: var(--primary-color); }

.nav-menu { display: flex; gap: 8px; flex: 1; }

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  color: var(--text-secondary);
  font-weight: 500;
  transition: all var(--transition-normal);
  white-space: nowrap;
}

.nav-item:hover { background: var(--primary-light); color: var(--primary-color); }
.nav-item.active { background: var(--primary-color); color: #fff; }

.header-right { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px 6px 6px;
  border-radius: 24px;
  transition: all var(--transition-normal);
  max-height: 48px;
}

.user-info:hover { background: var(--bg-color); }
.user-avatar { border: 2px solid var(--primary-light); flex-shrink: 0; }
.username { font-weight: 500; color: var(--text-color); max-width: 80px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.dropdown-icon { font-size: 10px; color: var(--text-light); }

.user-menu { min-width: 160px; padding: 8px; }
.user-menu :deep(.ant-dropdown-menu-item) { border-radius: 6px; padding: 10px 16px; }
.logout-item { color: var(--error-color); }

.login-btn { font-weight: 500; color: var(--text-secondary); }
.login-btn:hover { color: var(--primary-color); }
.register-btn { font-weight: 500; }

.content { margin-top: 64px; flex: 1; background: var(--bg-color); }

.footer { background: #1a1a1a; padding: 24px 20px; text-align: center; }
.footer-content p { color: rgba(255,255,255,0.5); font-size: 13px; margin: 0; }

@media (max-width: 768px) {
  .nav-menu { display: none; }
  .username { display: none; }
}
</style>
