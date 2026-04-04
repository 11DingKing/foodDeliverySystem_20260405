<template>
  <div class="register-page">
    <div class="register-left">
      <div class="brand-content">
        <div class="brand-logo">🍔</div>
        <h1 class="brand-title">美食外卖</h1>
        <p class="brand-desc">加入我们，开启美食新体验</p>
      </div>
    </div>
    <div class="register-right">
      <div class="register-container">
        <div class="register-header">
          <h2 class="register-title">创建账号</h2>
          <p class="register-subtitle">填写信息，开始您的美食之旅</p>
        </div>
        <a-form :model="form" :rules="rules" @finish="handleRegister" layout="vertical" class="register-form">
          <a-form-item name="username" label="用户名">
            <a-input v-model:value="form.username" placeholder="3-20个字符" size="large">
              <template #prefix><UserOutlined class="input-icon" /></template>
            </a-input>
          </a-form-item>
          <a-form-item name="password" label="密码">
            <a-input-password v-model:value="form.password" placeholder="6-20个字符" size="large">
              <template #prefix><LockOutlined class="input-icon" /></template>
            </a-input-password>
          </a-form-item>
          <a-form-item name="confirmPassword" label="确认密码">
            <a-input-password v-model:value="form.confirmPassword" placeholder="再次输入密码" size="large">
              <template #prefix><LockOutlined class="input-icon" /></template>
            </a-input-password>
          </a-form-item>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item name="nickname" label="昵称">
                <a-input v-model:value="form.nickname" placeholder="可选" size="large">
                  <template #prefix><SmileOutlined class="input-icon" /></template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item name="phone" label="手机号">
                <a-input v-model:value="form.phone" placeholder="可选" size="large">
                  <template #prefix><PhoneOutlined class="input-icon" /></template>
                </a-input>
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item>
            <a-button type="primary" html-type="submit" :loading="loading" block size="large" class="register-btn">
              {{ loading ? '注册中...' : '立即注册' }}
            </a-button>
          </a-form-item>
        </a-form>
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
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
import { UserOutlined, LockOutlined, SmileOutlined, PhoneOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({ username: '', password: '', confirmPassword: '', nickname: '', phone: '' })

const validateConfirmPassword = async (rule, value) => {
  if (value && value !== form.password) throw new Error('两次输入的密码不一致')
}

const rules = {
  username: [{ required: true, message: '请输入用户名' }, { min: 3, max: 20, message: '用户名长度3-20个字符' }],
  password: [{ required: true, message: '请输入密码' }, { min: 6, max: 20, message: '密码长度6-20个字符' }],
  confirmPassword: [{ required: true, message: '请确认密码' }, { validator: validateConfirmPassword }]
}

async function handleRegister() {
  loading.value = true
  try {
    await userStore.register({ username: form.username, password: form.password, nickname: form.nickname || undefined, phone: form.phone || undefined })
    message.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page { min-height: 100vh; display: flex; }

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #ff6b00 0%, #ff9248 50%, #ffb380 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.register-left::before {
  content: '';
  position: absolute;
  width: 600px;
  height: 600px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  top: -200px;
  right: -200px;
}

.brand-content { text-align: center; color: #fff; position: relative; z-index: 1; }
.brand-logo { font-size: 80px; margin-bottom: 24px; }
.brand-title { font-size: 42px; font-weight: 700; margin-bottom: 16px; }
.brand-desc { font-size: 18px; opacity: 0.9; }

.register-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fff;
}

.register-container { width: 100%; max-width: 400px; }
.register-header { margin-bottom: 32px; }
.register-title { font-size: 28px; font-weight: 700; color: var(--text-color); margin-bottom: 8px; }
.register-subtitle { font-size: 15px; color: var(--text-secondary); }
.input-icon { color: var(--text-light); }
.register-btn { height: 50px; font-size: 16px; font-weight: 600; margin-top: 8px; }
.register-footer { text-align: center; margin-top: 24px; color: var(--text-secondary); }
.login-link { color: var(--primary-color); font-weight: 600; margin-left: 4px; }
.login-link:hover { color: var(--primary-hover); }

@media (max-width: 900px) {
  .register-left { display: none; }
  .register-right { width: 100%; }
}
</style>
