<template>
  <div class="profile-page">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">个人中心</h1>
        <p class="page-desc">管理您的账户信息</p>
      </div>

      <div class="profile-layout">
        <div class="profile-sidebar">
          <div class="user-card">
            <a-upload :show-upload-list="false" :before-upload="handleAvatarUpload" accept="image/*">
              <div class="avatar-wrapper">
                <a-avatar :src="userStore.userInfo?.avatar" :size="100" class="user-avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                </a-avatar>
                <div class="avatar-overlay"><CameraOutlined /></div>
              </div>
            </a-upload>
            <h3 class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h3>
            <p class="user-role">{{ getRoleText() }}</p>
          </div>
          <div class="quick-links">
            <router-link to="/orders" class="link-item"><OrderedListOutlined /> 我的订单</router-link>
            <router-link to="/addresses" class="link-item"><EnvironmentOutlined /> 地址管理</router-link>
          </div>
        </div>

        <div class="profile-main">
          <div class="section-card card">
            <h3 class="card-title"><UserOutlined /> 基本信息</h3>
            <a-form :model="form" :rules="rules" @finish="handleUpdateProfile" layout="vertical">
              <a-form-item label="用户名">
                <a-input :value="userStore.userInfo?.username" disabled />
              </a-form-item>
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item name="nickname" label="昵称">
                    <a-input v-model:value="form.nickname" placeholder="请输入昵称" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item name="phone" label="手机号">
                    <a-input v-model:value="form.phone" placeholder="请输入手机号" />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-form-item>
                <a-button type="primary" html-type="submit" :loading="saving">保存修改</a-button>
              </a-form-item>
            </a-form>
          </div>

          <div class="section-card card">
            <h3 class="card-title"><LockOutlined /> 修改密码</h3>
            <a-form :model="passwordForm" :rules="passwordRules" @finish="handleUpdatePassword" layout="vertical">
              <a-form-item name="oldPassword" label="原密码">
                <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入原密码" />
              </a-form-item>
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item name="newPassword" label="新密码">
                    <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item name="confirmPassword" label="确认密码">
                    <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-form-item>
                <a-button type="primary" html-type="submit" :loading="changingPassword">修改密码</a-button>
              </a-form-item>
            </a-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { message } from 'ant-design-vue'
import { CameraOutlined, UserOutlined, LockOutlined, OrderedListOutlined, EnvironmentOutlined } from '@ant-design/icons-vue'

const userStore = useUserStore()
const saving = ref(false)
const changingPassword = ref(false)

const form = reactive({ nickname: '', phone: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const rules = { nickname: [{ max: 50, message: '昵称最多50个字符' }] }

const validateConfirmPassword = async (rule, value) => {
  if (value && value !== passwordForm.newPassword) throw new Error('两次输入的密码不一致')
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码' }],
  newPassword: [{ required: true, message: '请输入新密码' }, { min: 6, max: 20, message: '密码长度6-20个字符' }],
  confirmPassword: [{ required: true, message: '请确认新密码' }, { validator: validateConfirmPassword }]
}

function getRoleText() {
  const roles = []
  if (userStore.isMerchant) roles.push('商家')
  if (userStore.isRider) roles.push('骑手')
  if (roles.length === 0) roles.push('普通用户')
  return roles.join(' / ')
}

async function handleAvatarUpload(file) {
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await request.post('/user/avatar', formData)
    // 重新获取用户信息以确保头像更新
    await userStore.fetchUserInfo()
    message.success('头像更新成功')
  } catch (e) {
    message.error('头像上传失败')
  }
  return false
}

async function handleUpdateProfile() {
  saving.value = true
  try {
    await request.put('/user/profile', form)
    userStore.updateUserInfo(form)
    message.success('保存成功')
  } finally {
    saving.value = false
  }
}

async function handleUpdatePassword() {
  changingPassword.value = true
  try {
    await request.put('/user/password', { oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword })
    message.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  form.nickname = userStore.userInfo?.nickname || ''
  form.phone = userStore.userInfo?.phone || ''
})
</script>

<style scoped>
.profile-page { padding: 24px 0; min-height: 100vh; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; color: var(--text-color); margin-bottom: 8px; }
.page-desc { font-size: 15px; color: var(--text-secondary); }

.profile-layout { display: flex; gap: 24px; }

.profile-sidebar { width: 280px; flex-shrink: 0; }

.user-card {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
  border-radius: var(--radius-xl);
  padding: 32px 24px;
  text-align: center;
  color: #fff;
  margin-bottom: 16px;
}

.avatar-wrapper { position: relative; display: inline-block; cursor: pointer; margin-bottom: 16px; }
.user-avatar { border: 4px solid rgba(255,255,255,0.3); }
.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  opacity: 0;
  transition: opacity var(--transition-normal);
}
.avatar-wrapper:hover .avatar-overlay { opacity: 1; }
.user-name { font-size: 20px; font-weight: 600; margin-bottom: 4px; }
.user-role { font-size: 14px; opacity: 0.8; }

.quick-links {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 8px;
  box-shadow: var(--card-shadow);
}

.link-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-weight: 500;
  transition: all var(--transition-normal);
}

.link-item:hover { background: var(--primary-light); color: var(--primary-color); }

.profile-main { flex: 1; min-width: 0; }

.section-card { margin-bottom: 20px; }
.card-title { font-size: 16px; font-weight: 600; margin-bottom: 20px; display: flex; align-items: center; gap: 8px; color: var(--text-color); }

@media (max-width: 768px) {
  .profile-layout { flex-direction: column; }
  .profile-sidebar { width: 100%; }
}
</style>
