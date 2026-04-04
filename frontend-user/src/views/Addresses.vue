<template>
  <div class="addresses-page">
    <div class="page-container">
      <div class="page-header">
        <div class="header-left">
          <h1 class="page-title">地址管理</h1>
          <p class="page-desc">管理您的收货地址</p>
        </div>
        <a-button type="primary" size="large" @click="openModal()">
          <PlusOutlined /> 新增地址
        </a-button>
      </div>

      <a-spin :spinning="loading">
        <div v-if="addresses.length" class="address-list">
          <div v-for="addr in addresses" :key="addr.id" class="address-card">
            <div class="address-main">
              <div class="address-header">
                <div class="contact-info">
                  <span class="contact-name">{{ addr.contactName }}</span>
                  <span class="contact-phone">{{ addr.contactPhone }}</span>
                </div>
                <a-tag v-if="addr.isDefault" color="orange" class="default-tag">默认</a-tag>
              </div>
              <p class="address-detail">
                <EnvironmentOutlined class="address-icon" />
                {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
              </p>
            </div>
            <div class="address-actions">
              <a-button type="text" @click="openModal(addr)"><EditOutlined /> 编辑</a-button>
              <a-button v-if="!addr.isDefault" type="text" @click="handleSetDefault(addr)"><CheckOutlined /> 设为默认</a-button>
              <a-popconfirm title="确定删除该地址？" @confirm="handleDelete(addr.id)">
                <a-button type="text" danger><DeleteOutlined /> 删除</a-button>
              </a-popconfirm>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <div class="empty-icon">📍</div>
          <p class="empty-text">暂无收货地址</p>
          <p class="empty-hint">添加地址后可快速下单</p>
          <a-button type="primary" @click="openModal()">添加地址</a-button>
        </div>
      </a-spin>

      <a-modal v-model:open="modalVisible" :title="editingId ? '编辑地址' : '新增地址'" @ok="handleSubmit" :confirm-loading="submitting" width="520px">
        <a-form :model="form" :rules="rules" ref="formRef" layout="vertical" class="address-form">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item name="contactName" label="联系人">
                <a-input v-model:value="form.contactName" placeholder="请输入联系人姓名" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item name="contactPhone" label="联系电话">
                <a-input v-model:value="form.contactPhone" placeholder="请输入联系电话" />
              </a-form-item>
            </a-col>
          </a-row>
          <RegionSelect
            v-model:province="form.province"
            v-model:city="form.city"
            v-model:district="form.district"
          />
          <a-form-item name="detail" label="详细地址">
            <a-textarea v-model:value="form.detail" placeholder="请输入详细地址，如街道、门牌号等" :rows="2" />
          </a-form-item>
          <a-form-item name="isDefault">
            <a-checkbox v-model:checked="form.isDefault">设为默认地址</a-checkbox>
          </a-form-item>
        </a-form>
      </a-modal>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { message } from 'ant-design-vue'
import { PlusOutlined, EnvironmentOutlined, EditOutlined, CheckOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import RegionSelect from '@/components/RegionSelect.vue'

const loading = ref(false)
const addresses = ref([])
const modalVisible = ref(false)
const submitting = ref(false)
const editingId = ref(null)
const formRef = ref(null)

const form = reactive({ contactName: '', contactPhone: '', province: '', city: '', district: '', detail: '', isDefault: false })

const rules = {
  contactName: [{ required: true, message: '请输入联系人' }],
  contactPhone: [{ required: true, message: '请输入联系电话' }],
  province: [{ required: true, message: '请选择省' }],
  city: [{ required: true, message: '请选择市' }],
  district: [{ required: true, message: '请选择区' }],
  detail: [{ required: true, message: '请输入详细地址' }]
}

async function fetchAddresses() {
  loading.value = true
  try {
    const res = await request.get('/addresses')
    addresses.value = res.data
  } finally {
    loading.value = false
  }
}

function openModal(addr = null) {
  if (addr) {
    editingId.value = addr.id
    Object.assign(form, { contactName: addr.contactName, contactPhone: addr.contactPhone, province: addr.province || '', city: addr.city || '', district: addr.district || '', detail: addr.detail, isDefault: addr.isDefault === 1 })
  } else {
    editingId.value = null
    Object.assign(form, { contactName: '', contactPhone: '', province: '', city: '', district: '', detail: '', isDefault: false })
  }
  modalVisible.value = true
}

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    if (editingId.value) {
      await request.put(`/addresses/${editingId.value}`, form)
      message.success('地址更新成功')
    } else {
      await request.post('/addresses', form)
      message.success('地址添加成功')
    }
    modalVisible.value = false
    fetchAddresses()
  } finally {
    submitting.value = false
  }
}

async function handleSetDefault(addr) {
  await request.put(`/addresses/${addr.id}/default`)
  message.success('已设为默认地址')
  fetchAddresses()
}

async function handleDelete(id) {
  await request.delete(`/addresses/${id}`)
  message.success('地址已删除')
  fetchAddresses()
}

onMounted(() => { fetchAddresses() })
</script>

<style scoped>
.addresses-page { padding: 24px 0; min-height: 100vh; }

.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.page-title { font-size: 28px; font-weight: 700; color: var(--text-color); margin-bottom: 8px; }
.page-desc { font-size: 15px; color: var(--text-secondary); }

.address-list { display: flex; flex-direction: column; gap: 16px; }

.address-card {
  background: #fff;
  border-radius: var(--radius-lg);
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  box-shadow: var(--card-shadow);
  transition: all var(--transition-normal);
  border: 1px solid transparent;
}

.address-card:hover { box-shadow: var(--card-shadow-hover); border-color: rgba(255,107,0,0.1); }

.address-main { flex: 1; }
.address-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.contact-info { display: flex; align-items: center; gap: 16px; }
.contact-name { font-size: 17px; font-weight: 600; color: var(--text-color); }
.contact-phone { font-size: 15px; color: var(--text-secondary); }
.default-tag { margin-left: 8px; }

.address-detail { display: flex; align-items: flex-start; gap: 8px; color: var(--text-secondary); font-size: 14px; line-height: 1.6; margin: 0; }
.address-icon { color: var(--primary-color); margin-top: 3px; }

.address-actions { display: flex; gap: 4px; flex-shrink: 0; }

.empty-state { text-align: center; padding: 80px 20px; background: #fff; border-radius: var(--radius-lg); }
.empty-icon { font-size: 80px; margin-bottom: 16px; }
.empty-text { font-size: 18px; color: var(--text-color); margin-bottom: 8px; }
.empty-hint { font-size: 14px; color: var(--text-light); margin-bottom: 24px; }
</style>
