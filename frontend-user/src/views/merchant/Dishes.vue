<template>
  <div class="merchant-dishes">
    <div class="page-header">
      <h2 class="page-title">菜品管理</h2>
      <div class="header-actions">
        <a-button @click="openCategoryModal()"><AppstoreOutlined /> 管理分类</a-button>
        <a-button type="primary" @click="openDishModal()"><PlusOutlined /> 添加菜品</a-button>
      </div>
    </div>

    <a-spin :spinning="loading">
      <div v-if="dishes.length" class="dish-grid">
        <div v-for="dish in dishes" :key="dish.id" class="dish-card">
          <div class="dish-image-wrapper">
            <img :src="dish.image || defaultImage" :alt="dish.name" class="dish-image" />
            <div class="dish-status" :class="dish.status === 1 ? 'on' : 'off'">
              {{ dish.status === 1 ? '已上架' : '已下架' }}
            </div>
          </div>
          <div class="dish-content">
            <h3 class="dish-name">{{ dish.name }}</h3>
            <p class="dish-desc">{{ dish.description || '暂无描述' }}</p>
            <div class="dish-meta">
              <span class="dish-price">¥{{ dish.price }}</span>
              <span class="dish-sales">销量 {{ dish.sales || 0 }}</span>
            </div>
          </div>
          <div class="dish-actions">
            <a-switch :checked="dish.status === 1" checked-children="上架" un-checked-children="下架" @change="(checked) => handleStatusChange(dish, checked)" />
            <a-button type="text" size="small" @click="openDishModal(dish)"><EditOutlined /></a-button>
            <a-popconfirm title="确定删除该菜品？" @confirm="handleDeleteDish(dish.id)">
              <a-button type="text" size="small" danger><DeleteOutlined /></a-button>
            </a-popconfirm>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <div class="empty-icon">🍽️</div>
        <p class="empty-text">暂无菜品</p>
        <a-button type="primary" @click="openDishModal()">添加第一个菜品</a-button>
      </div>
    </a-spin>

    <!-- 分类管理弹窗 -->
    <a-modal v-model:open="categoryModalVisible" title="分类管理" :footer="null" width="480px">
      <div class="category-add">
        <a-input-search v-model:value="newCategoryName" placeholder="输入分类名称" enter-button="添加" @search="handleAddCategory" />
      </div>
      <div class="category-list">
        <div v-for="cat in categories" :key="cat.id" class="category-item">
          <span class="category-name">{{ cat.name }}</span>
          <a-popconfirm title="确定删除该分类？" @confirm="handleDeleteCategory(cat.id)">
            <a-button type="text" size="small" danger><DeleteOutlined /></a-button>
          </a-popconfirm>
        </div>
        <div v-if="!categories.length" class="category-empty">暂无分类</div>
      </div>
    </a-modal>

    <!-- 菜品编辑弹窗 -->
    <a-modal v-model:open="dishModalVisible" :title="editingDish ? '编辑菜品' : '添加菜品'" @ok="handleSaveDish" :confirm-loading="savingDish" width="520px">
      <a-form :model="dishForm" layout="vertical">
        <a-form-item label="菜品图片">
          <a-upload :show-upload-list="false" :before-upload="handleDishImageUpload" accept="image/*">
            <div class="dish-upload">
              <img v-if="dishForm.image" :src="dishForm.image" />
              <div v-else class="upload-placeholder"><PlusOutlined /><span>上传图片</span></div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="菜品名称" required>
          <a-input v-model:value="dishForm.name" placeholder="请输入菜品名称" />
        </a-form-item>
        <a-form-item label="所属分类">
          <a-select v-model:value="dishForm.categoryId" placeholder="请选择分类" allow-clear>
            <a-select-option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="菜品描述">
          <a-textarea v-model:value="dishForm.description" placeholder="请输入菜品描述" :rows="2" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="价格" required>
              <a-input-number v-model:value="dishForm.price" :min="0" :precision="2" style="width: 100%" placeholder="0.00" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="原价">
              <a-input-number v-model:value="dishForm.originalPrice" :min="0" :precision="2" style="width: 100%" placeholder="0.00" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { message } from 'ant-design-vue'
import { PlusOutlined, AppstoreOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'

const loading = ref(false)
const dishes = ref([])
const categories = ref([])
const categoryModalVisible = ref(false)
const dishModalVisible = ref(false)
const newCategoryName = ref('')
const editingDish = ref(null)
const savingDish = ref(false)

const defaultImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2ZmZjhmMCIvPjx0ZXh0IHg9IjEwMCIgeT0iMTEwIiBmb250LXNpemU9IjYwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj7wn42dPC90ZXh0Pjwvc3ZnPg=='

const dishForm = reactive({ name: '', categoryId: null, image: '', description: '', price: null, originalPrice: null })

async function fetchData() {
  loading.value = true
  try {
    const [dishesRes, categoriesRes] = await Promise.all([request.get('/merchant/dishes'), request.get('/merchant/categories')])
    dishes.value = dishesRes.data
    categories.value = categoriesRes.data
  } finally { loading.value = false }
}

function openCategoryModal() { categoryModalVisible.value = true }

async function handleAddCategory() {
  if (!newCategoryName.value.trim()) return
  await request.post('/merchant/categories', { name: newCategoryName.value })
  message.success('分类添加成功')
  newCategoryName.value = ''
  const res = await request.get('/merchant/categories')
  categories.value = res.data
}

async function handleDeleteCategory(id) {
  await request.delete(`/merchant/categories/${id}`)
  message.success('分类已删除')
  const res = await request.get('/merchant/categories')
  categories.value = res.data
}

function openDishModal(dish = null) {
  editingDish.value = dish
  if (dish) {
    Object.assign(dishForm, { name: dish.name, categoryId: dish.categoryId, image: dish.image || '', description: dish.description || '', price: dish.price, originalPrice: dish.originalPrice })
  } else {
    Object.assign(dishForm, { name: '', categoryId: null, image: '', description: '', price: null, originalPrice: null })
  }
  dishModalVisible.value = true
}

async function handleDishImageUpload(file) {
  const formData = new FormData()
  formData.append('file', file)
  const res = await request.post('/files/upload', formData)
  dishForm.image = res.data
  return false
}

async function handleSaveDish() {
  if (!dishForm.name || dishForm.price === null) { message.warning('请填写菜品名称和价格'); return }
  savingDish.value = true
  try {
    if (editingDish.value) {
      await request.put(`/merchant/dishes/${editingDish.value.id}`, dishForm)
      message.success('菜品更新成功')
    } else {
      await request.post('/merchant/dishes', dishForm)
      message.success('菜品添加成功')
    }
    dishModalVisible.value = false
    fetchData()
  } finally { savingDish.value = false }
}

async function handleStatusChange(dish, checked) {
  await request.put(`/merchant/dishes/${dish.id}/status`, null, { params: { status: checked ? 1 : 0 } })
  dish.status = checked ? 1 : 0
  message.success(checked ? '菜品已上架' : '菜品已下架')
}

async function handleDeleteDish(id) {
  await request.delete(`/merchant/dishes/${id}`)
  message.success('菜品已删除')
  fetchData()
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-title { font-size: 20px; font-weight: 700; margin: 0; color: var(--text-color); }
.header-actions { display: flex; gap: 12px; }

.dish-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }

.dish-card {
  background: #fff;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--card-shadow);
  transition: all var(--transition-normal);
  border: 1px solid transparent;
}
.dish-card:hover { box-shadow: var(--card-shadow-hover); border-color: rgba(255,107,0,0.1); }

.dish-image-wrapper { position: relative; height: 180px; overflow: hidden; }
.dish-image { width: 100%; height: 100%; object-fit: cover; }
.dish-status { position: absolute; top: 12px; right: 12px; padding: 4px 10px; border-radius: 4px; font-size: 12px; font-weight: 500; }
.dish-status.on { background: var(--success-light); color: var(--success-color); }
.dish-status.off { background: #f5f5f5; color: var(--text-light); }

.dish-content { padding: 16px; }
.dish-name { font-size: 16px; font-weight: 600; margin-bottom: 8px; color: var(--text-color); }
.dish-desc { font-size: 13px; color: var(--text-light); margin-bottom: 12px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; min-height: 36px; }
.dish-meta { display: flex; justify-content: space-between; align-items: center; }
.dish-price { font-size: 20px; font-weight: 700; color: var(--primary-color); }
.dish-sales { font-size: 13px; color: var(--text-light); }

.dish-actions { display: flex; align-items: center; justify-content: space-between; padding: 12px 16px; border-top: 1px solid var(--border-light); }

.empty-state { text-align: center; padding: 80px 20px; background: #fff; border-radius: var(--radius-lg); }
.empty-icon { font-size: 80px; margin-bottom: 16px; }
.empty-text { font-size: 18px; color: var(--text-color); margin-bottom: 24px; }

.category-add { margin-bottom: 20px; }
.category-list { max-height: 300px; overflow-y: auto; }
.category-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-bottom: 1px solid var(--border-light); }
.category-name { font-weight: 500; }
.category-empty { text-align: center; padding: 40px; color: var(--text-light); }

.dish-upload { width: 140px; height: 140px; border: 2px dashed var(--border-color); border-radius: var(--radius-md); overflow: hidden; cursor: pointer; transition: border-color var(--transition-normal); }
.dish-upload:hover { border-color: var(--primary-color); }
.dish-upload img { width: 100%; height: 100%; object-fit: cover; }
.upload-placeholder { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; color: var(--text-light); gap: 8px; }
</style>
