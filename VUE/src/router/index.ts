import { createRouter, createWebHistory } from 'vue-router'
import ProtocolStudio from '@/views/ProtocolStudio.vue'
import RoundtableLive from '@/views/RoundtableLive.vue'

const routes = [
  {
    path: '/',
    redirect: '/studio'
  },
  {
    path: '/studio',
    name: 'ProtocolStudio',
    component: ProtocolStudio
  },
  {
    path: '/live',
    name: 'RoundtableLive',
    component: RoundtableLive
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router