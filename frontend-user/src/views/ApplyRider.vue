<template>
  <div class="apply-page">
    <div class="page-container">
      <div class="apply-card card">
        <h1 class="page-title">申请成为骑手</h1>
        <p class="page-desc">填写个人信息，成为骑手开始配送</p>

        <a-form :model="form" :rules="rules" @finish="handleSubmit" layout="vertical">
          <a-form-item name="realName" label="真实姓名">
            <a-input v-model:value="form.realName" placeholder="请输入真实姓名" />
          </a-form-item>
          <a-form-item name="idCard" label="身份证号">
            <a-input v-model:value="form.idCard" placeholder="请输入身份证号" />
          </a-form-item>
          <a-form-item name="phone" label="联系电话">
            <a-input v-model:value="form.phone" placeholder="请输入联系电话" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" html-type="submit" :loading="submitting" block size="large">
              提交申请
            </a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { message } from 'ant-design-vue'

const router = useRouter()
const userStore = useUserStore()
const submitting = ref(false)

const form = reactive({
  realName: '',
  idCard: '',
  phone: ''
})

const rules = {
  realName: [{ required: true, message: '请输入真实姓名' }],
  idCard: [
    { required: true, message: '请输入身份证号' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '身份证号格式不正确' }
  ],
  phone: [{ required: true, message: '请输入联系电话' }]
}

async function handleSubmit() {
  submitting.value = true
  try {
    await request.post('/rider/apply', form)
    await userStore.fetchUserInfo()
    message.success('申请成功，您已成为骑手')
    router.push('/rider')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.apply-page {
  padding: 40px 0;
}

.apply-card {
  max-width: 500px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  text-align: center;
}

.page-desc {
  text-align: center;
  color: var(--text-secondary);
  margin-bottom: 32px;
}
</style>
