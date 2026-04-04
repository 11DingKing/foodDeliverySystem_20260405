import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const shopId = ref(null)
  const shopInfo = ref(null)

  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  function setShop(shop) {
    if (shopId.value !== shop.id) {
      items.value = []
    }
    shopId.value = shop.id
    shopInfo.value = shop
  }

  function addItem(dish) {
    const existing = items.value.find(item => item.dishId === dish.id)
    if (existing) {
      existing.quantity++
    } else {
      items.value.push({
        dishId: dish.id,
        name: dish.name,
        image: dish.image,
        price: dish.price,
        quantity: 1
      })
    }
  }

  function removeItem(dishId) {
    const index = items.value.findIndex(item => item.dishId === dishId)
    if (index > -1) {
      if (items.value[index].quantity > 1) {
        items.value[index].quantity--
      } else {
        items.value.splice(index, 1)
      }
    }
  }

  function getItemQuantity(dishId) {
    const item = items.value.find(item => item.dishId === dishId)
    return item ? item.quantity : 0
  }

  function clearCart() {
    items.value = []
    shopId.value = null
    shopInfo.value = null
  }

  return {
    items,
    shopId,
    shopInfo,
    totalCount,
    totalPrice,
    setShop,
    addItem,
    removeItem,
    getItemQuantity,
    clearCart
  }
})
