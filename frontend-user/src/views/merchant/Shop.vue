<template>
  <div class="merchant-shop">
    <h2 class="page-title">店铺管理</h2>

    <a-spin :spinning="loading">
      <template v-if="shop">
        <div class="shop-status-card">
          <div class="status-left">
            <a-upload :show-upload-list="false" :before-upload="handleLogoUpload" accept="image/*">
              <div class="logo-wrapper">
                <img v-if="shop.logo" :src="shop.logo" alt="店铺Logo" />
                <div v-else class="logo-placeholder">🏪</div>
                <div class="logo-overlay"><CameraOutlined /></div>
              </div>
            </a-upload>
            <div class="shop-meta">
              <h3 class="shop-name">{{ shop.name }}</h3>
              <div class="status-row">
                <span :class="['status-badge', shop.status === 1 ? 'open' : 'closed']">
                  {{ shop.status === 1 ? '🟢 营业中' : '🔴 休息中' }}
                </span>
              </div>
            </div>
          </div>
          <div class="status-right">
            <span class="switch-label">店铺状态</span>
            <a-switch :checked="shop.status === 1" checked-children="营业" un-checked-children="休息" @change="handleStatusChange" />
          </div>
        </div>

        <div class="form-card card">
          <h3 class="card-title">店铺信息</h3>
          <a-form :model="form" :rules="rules" @finish="handleSubmit" layout="vertical">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item name="name" label="店铺名称">
                  <a-input v-model:value="form.name" placeholder="请输入店铺名称" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
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
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item name="phone" label="联系电话">
                  <a-input v-model:value="form.phone" placeholder="请输入联系电话" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item name="description" label="店铺描述">
              <a-textarea v-model:value="form.description" placeholder="请输入店铺描述" :rows="3" />
            </a-form-item>
            <RegionSelect
              v-model:province="form.province"
              v-model:city="form.city"
              v-model:district="form.district"
              :gutter="24"
            />
            <a-form-item name="address" label="详细地址">
              <a-input v-model:value="form.address" placeholder="请输入详细地址" />
            </a-form-item>
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item name="minPrice" label="起送价 (元)">
                  <a-input-number v-model:value="form.minPrice" :min="0" :precision="2" style="width: 100%" placeholder="0.00" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item name="deliveryFee" label="配送费 (元)">
                  <a-input-number v-model:value="form.deliveryFee" :min="0" :precision="2" style="width: 100%" placeholder="0.00" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item>
              <a-button type="primary" html-type="submit" :loading="saving" size="large">保存修改</a-button>
            </a-form-item>
          </a-form>
        </div>
      </template>
    </a-spin>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { message } from 'ant-design-vue'
import { CameraOutlined } from '@ant-design/icons-vue'
import RegionSelect from '@/components/RegionSelect.vue'

const loading = ref(false)
const saving = ref(false)
const shop = ref(null)

const form = reactive({ name: '', category: '', logo: '', description: '', phone: '', province: '', city: '', district: '', address: '', minPrice: 0, deliveryFee: 0 })

const rules = {
  name: [{ required: true, message: '请输入店铺名称' }],
  category: [{ required: true, message: '请选择店铺分类' }],
  province: [{ required: true, message: '请选择省' }],
  city: [{ required: true, message: '请选择市' }],
  district: [{ required: true, message: '请选择区' }],
  address: [{ required: true, message: '请输入详细地址' }],
  minPrice: [{ required: true, message: '请输入起送价' }],
  deliveryFee: [{ required: true, message: '请输入配送费' }]
}

async function fetchShop() {
  loading.value = true
  try {
    const res = await request.get('/shops/my')
    shop.value = res.data
    if (shop.value) {
      Object.assign(form, {
        name: shop.value.name, category: shop.value.category || '', logo: shop.value.logo || '', description: shop.value.description || '', phone: shop.value.phone || '',
        province: shop.value.province || '', city: shop.value.city || '', district: shop.value.district || '',
        address: shop.value.address || '', minPrice: shop.value.minPrice || 0, deliveryFee: shop.value.deliveryFee || 0
      })
    }
  } finally { loading.value = false }
}

async function handleLogoUpload(file) {
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await request.post('/files/upload', formData)
    form.logo = res.data
    shop.value.logo = res.data
    message.success('Logo上传成功')
  } catch (e) {}
  return false
}

async function handleStatusChange(checked) {
  await request.put('/shops/my/status', null, { params: { status: checked ? 1 : 2 } })
  shop.value.status = checked ? 1 : 2
  message.success(checked ? '店铺已开始营业' : '店铺已休息')
}

async function handleSubmit() {
  saving.value = true
  try {
    await request.put('/shops/my', form)
    message.success('保存成功')
    fetchShop()
  } finally { saving.value = false }
}

onMounted(() => { fetchShop() })
</script>

<style scoped>
.page-title { font-size: 20px; font-weight: 700; margin-bottom: 24px; color: var(--text-color); }

.shop-status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, var(--primary-light), #fff);
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 24px;
  border: 1px solid rgba(255,107,0,0.1);
}

.status-left { display: flex; gap: 20px; align-items: center; }

.logo-wrapper {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-md);
  overflow: hidden;
  position: relative;
  cursor: pointer;
  background: #fff;
  box-shadow: var(--card-shadow);
}

.logo-wrapper img { width: 100%; height: 100%; object-fit: cover; }
.logo-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 40px; }
.logo-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 24px; opacity: 0; transition: opacity var(--transition-normal); }
.logo-wrapper:hover .logo-overlay { opacity: 1; }

.shop-name { font-size: 20px; font-weight: 600; margin-bottom: 8px; color: var(--text-color); }
.status-badge { padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 500; }
.status-badge.open { background: var(--success-light); color: var(--success-color); }
.status-badge.closed { background: #f5f5f5; color: var(--text-light); }

.status-right { display: flex; flex-direction: column; align-items: flex-end; gap: 8px; }
.switch-label { font-size: 13px; color: var(--text-secondary); }

.form-card { padding: 24px; }
.card-title { font-size: 16px; font-weight: 600; margin-bottom: 20px; color: var(--text-color); }
</style>
