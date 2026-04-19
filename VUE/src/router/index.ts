import { createRouter, createWebHistory } from 'vue-router'
import ProtocolStudio from '@/views/ProtocolStudio.vue'
import RoundtableLive from '@/views/RoundtableLive.vue'
import PyramidDemoView from '@/views/PyramidDemoView.vue'
import PptWebView from '@/views/PptWebView.vue'

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
  },
  {
    path: '/pyramid-demo',
    name: 'PyramidDemo',
    component: PyramidDemoView
  },
  {
    path: '/ppt',
    name: 'PptWeb',
    component: PptWebView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router