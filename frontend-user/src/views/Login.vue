<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand-content">
        <div class="brand-logo">🍔</div>
        <h1 class="brand-title">美食外卖</h1>
        <p class="brand-desc">发现身边美食，享受便捷生活</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-icon">🚀</span>
            <span>30分钟极速送达</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🏪</span>
            <span>海量优质商家</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">💰</span>
            <span>天天优惠不停</span>
          </div>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-container">
        <div class="login-header">
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">登录您的账号，继续美食之旅</p>
        </div>
        <a-form :model="form" :rules="rules" @finish="handleLogin" layout="vertical" class="login-form">
          <a-form-item name="username" label="用户名">
            <a-input v-model:value="form.username" placeholder="请输入用户名" size="large">
              <template #prefix><UserOutlined class="input-icon" /></template>
            </a-input>
          </a-form-item>
          <a-form-item name="password" label="密码">
            <a-input-password v-model:value="form.password" placeholder="请输入密码" size="large">
              <template #prefix><LockOutlined class="input-icon" /></template>
            </a-input-password>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" :loading="loading" block size="large" class="login-btn">
              {{ loading ? '登录中...' : '登 录' }}
            </a-button>
          </a-form-item>
        </a-form>
        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

async function handleLogin() {
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    message.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #ff6b00 0%, #ff9248 50%, #ffb380 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  top: -200px;
  right: -200px;
}

.login-left::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: rgba(255,255,255,0.08);
  border-radius: 50%;
  bottom: -100px;
  left: -100px;
}

.brand-content {
  text-align: center;
  color: #fff;
  position: relative;
  z-index: 1;
}

.brand-logo {
  font-size: 80px;
  margin-bottom: 24px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.brand-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.brand-desc {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 48px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 16px;
  background: rgba(255,255,255,0.15);
  padding: 12px 24px;
  border-radius: 30px;
  backdrop-filter: blur(10px);
}

.feature-icon {
  font-size: 20px;
}

.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fff;
}

.login-container {
  width: 100%;
  max-width: 360px;
}

.login-header {
  margin-bottom: 40px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 15px;
  color: var(--text-secondary);
}

.login-form :deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: var(--text-color);
}

.input-icon {
  color: var(--text-light);
}

.login-btn {
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 8px;
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  color: var(--text-secondary);
}

.register-link {
  color: var(--primary-color);
  font-weight: 600;
  margin-left: 4px;
  transition: color var(--transition-fast);
}

.register-link:hover {
  color: var(--primary-hover);
}

@media (max-width: 900px) {
  .login-left { display: none; }
  .login-right { width: 100%; }
}
</style>
