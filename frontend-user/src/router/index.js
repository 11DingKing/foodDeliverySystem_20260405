import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'shop/:id',
        name: 'ShopDetail',
        component: () => import('@/views/ShopDetail.vue')
      },
      {
        path: 'checkout',
        name: 'Checkout',
        component: () => import('@/views/Checkout.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/Orders.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/OrderDetail.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'addresses',
        name: 'Addresses',
        component: () => import('@/views/Addresses.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/merchant',
    component: () => import('@/layouts/MerchantLayout.vue'),
    meta: { requiresAuth: true, role: 1 },
    children: [
      {
        path: '',
        redirect: '/merchant/shop'
      },
      {
        path: 'shop',
        name: 'MerchantShop',
        component: () => import('@/views/merchant/Shop.vue')
      },
      {
        path: 'dishes',
        name: 'MerchantDishes',
        component: () => import('@/views/merchant/Dishes.vue')
      },
      {
        path: 'orders',
        name: 'MerchantOrders',
        component: () => import('@/views/merchant/Orders.vue')
      }
    ]
  },
  {
    path: '/rider',
    component: () => import('@/layouts/RiderLayout.vue'),
    meta: { requiresAuth: true, role: 2 },
    children: [
      {
        path: '',
        redirect: '/rider/available'
      },
      {
        path: 'available',
        name: 'RiderAvailable',
        component: () => import('@/views/rider/Available.vue')
      },
      {
        path: 'orders',
        name: 'RiderOrders',
        component: () => import('@/views/rider/Orders.vue')
      }
    ]
  },
  {
    path: '/apply-merchant',
    name: 'ApplyMerchant',
    component: () => import('@/views/ApplyMerchant.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/apply-rider',
    name: 'ApplyRider',
    component: () => import('@/views/ApplyRider.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.guest && userStore.isLoggedIn) {
    return next('/')
  }
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return next('/login')
  }
  
  if (to.meta.role !== undefined && userStore.userInfo?.role !== to.meta.role) {
    return next('/')
  }
  
  next()
})

export default router
