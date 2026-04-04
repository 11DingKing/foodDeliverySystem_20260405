<template>
  <a-row :gutter="gutter">
    <a-col :span="colSpan">
      <a-form-item :name="provinceName" :label="showLabel ? '省' : undefined">
        <a-select
          v-model:value="innerProvince"
          placeholder="请选择省"
          :allow-clear="allowClear"
          show-search
          :filter-option="filterOption"
          @change="handleProvinceChange"
        >
          <a-select-option v-for="p in provinces" :key="p" :value="p">{{ p }}</a-select-option>
        </a-select>
      </a-form-item>
    </a-col>
    <a-col :span="colSpan">
      <a-form-item :name="cityName" :label="showLabel ? '市' : undefined">
        <a-select
          v-model:value="innerCity"
          placeholder="请选择市"
          :allow-clear="allowClear"
          show-search
          :filter-option="filterOption"
          @change="handleCityChange"
        >
          <a-select-option v-for="c in cities" :key="c" :value="c">{{ c }}</a-select-option>
        </a-select>
      </a-form-item>
    </a-col>
    <a-col :span="colSpan">
      <a-form-item :name="districtName" :label="showLabel ? '区' : undefined">
        <a-select
          v-model:value="innerDistrict"
          placeholder="请选择区"
          :allow-clear="allowClear"
          show-search
          :filter-option="filterOption"
          @change="handleDistrictChange"
        >
          <a-select-option v-for="d in districts" :key="d" :value="d">{{ d }}</a-select-option>
        </a-select>
      </a-form-item>
    </a-col>
  </a-row>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { getProvinces, getCities, getDistricts } from '@/utils/region-data'

const props = defineProps({
  province: { type: String, default: '' },
  city: { type: String, default: '' },
  district: { type: String, default: '' },
  provinceName: { type: String, default: 'province' },
  cityName: { type: String, default: 'city' },
  districtName: { type: String, default: 'district' },
  gutter: { type: Number, default: 16 },
  colSpan: { type: Number, default: 8 },
  showLabel: { type: Boolean, default: true },
  allowClear: { type: Boolean, default: true }
})

const emit = defineEmits(['update:province', 'update:city', 'update:district', 'change'])

const innerProvince = ref('')
const innerCity = ref('')
const innerDistrict = ref('')

const provinces = computed(() => getProvinces())
const cities = computed(() => getCities(innerProvince.value))
const districts = computed(() => getDistricts(innerProvince.value, innerCity.value))

const filterOption = (input, option) => {
  return option.value.toLowerCase().includes(input.toLowerCase())
}

function handleProvinceChange(val) {
  innerCity.value = ''
  innerDistrict.value = ''
  emit('update:province', val || '')
  emit('update:city', '')
  emit('update:district', '')
  emit('change', { province: val || '', city: '', district: '' })
}

function handleCityChange(val) {
  innerDistrict.value = ''
  emit('update:city', val || '')
  emit('update:district', '')
  emit('change', { province: innerProvince.value, city: val || '', district: '' })
}

function handleDistrictChange(val) {
  emit('update:district', val || '')
  emit('change', { province: innerProvince.value, city: innerCity.value, district: val || '' })
}

// 监听外部值变化
watch(() => props.province, (val) => {
  if (val !== innerProvince.value) {
    innerProvince.value = val
  }
}, { immediate: true })

watch(() => props.city, (val) => {
  if (val !== innerCity.value) {
    innerCity.value = val
  }
}, { immediate: true })

watch(() => props.district, (val) => {
  if (val !== innerDistrict.value) {
    innerDistrict.value = val
  }
}, { immediate: true })
</script>
