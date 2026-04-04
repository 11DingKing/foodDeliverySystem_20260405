<template>
  <div class="apply-page">
    <div class="page-container">
      <div class="apply-card card">
        <h1 class="page-title">申请开店</h1>
        <p class="page-desc">填写店铺信息，成为商家开始经营</p>

        <a-form :model="form" :rules="rules" @finish="handleSubmit" layout="vertical" ref="formRef">
          <a-form-item name="name" label="店铺名称">
            <a-input v-model:value="form.name" placeholder="请输入店铺名称" />
          </a-form-item>
          <a-form-item name="category" label="店铺分类">
            <a-select v-model:value="form.category" placeholder="请选择店铺分类">
              <a-select-option value="美食">美食</a-select-option>
              <a-select-option value="快餐">快餐</a-select-option>
              <a-select-option value="甜点">甜点</a-select-option>
              <a-select-option value="饮品">饮品</a-select-option>
              <a-select-option value="小吃">小吃</a-select-option>
              <a-select-option value="火锅">火锅</a-select-option>
              <a-select-option value="烧烤">烧烤</a-select-option>
              <a-select-option value="日料">日料</a-select-option>
              <a-select-option value="西餐">西餐</a-select-option>
              <a-select-option value="其他">其他</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item name="description" label="店铺描述">
            <a-textarea v-model:value="form.description" placeholder="请输入店铺描述" :rows="3" />
          </a-form-item>
          <a-form-item name="phone" label="联系电话">
            <a-input v-model:value="form.phone" placeholder="请输入联系电话" />
          </a-form-item>
          <RegionSelect
            v-model:province="form.province"
            v-model:city="form.city"
            v-model:district="form.district"
          />
          <a-form-item name="address" label="详细地址">
            <a-input v-model:value="form.address" placeholder="请输入详细地址" />
          </a-form-item>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item name="minPrice" label="起送价">
                <a-input-number v-model:value="form.minPrice" :min="0" :precision="2" style="width: 100%" placeholder="0.00" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item name="deliveryFee" label="配送费">
                <a-input-number v-model:value="form.deliveryFee" :min="0" :precision="2" style="width: 100%" placeholder="0.00" />
              </a-form-item>
            </a-col>
          </a-row>
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
import RegionSelect from '@/components/RegionSelect.vue'

const router = useRouter()
const userStore = useUserStore()
const submitting = ref(false)

const form = reactive({
  name: '',
  category: '',
  description: '新鲜食材，用心烹饪，为您提供美味可口的餐品，欢迎光临！',
  phone: '',
  province: '',
  city: '',
  district: '',
  address: '',
  minPrice: 0,
  deliveryFee: 0
})

const rules = {
  name: [{ required: true, message: '请输入店铺名称' }],
  category: [{ required: true, message: '请选择店铺分类' }],
  phone: [{ required: true, message: '请输入联系电话' }],
  province: [{ required: true, message: '请选择省' }],
  city: [{ required: true, message: '请选择市' }],
  district: [{ required: true, message: '请选择区' }],
  address: [{ required: true, message: '请输入详细地址' }],
  minPrice: [{ required: true, message: '请输入起送价' }],
  deliveryFee: [{ required: true, message: '请输入配送费' }]
}

async function handleSubmit() {
  submitting.value = true
  try {
    await request.post('/shops/apply', form)
    await userStore.fetchUserInfo()
    message.success('申请成功，您已成为商家')
    router.push('/merchant')
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
  max-width: 600px;
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
