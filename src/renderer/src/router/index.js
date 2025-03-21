import { createRouter, createWebHashHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import MainLayout from '@/layout/MainLayout.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: 'home',
        name: 'Home',
        component: HomeView
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/ProfileView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
