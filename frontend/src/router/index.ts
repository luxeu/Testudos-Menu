import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

import LandingPage from '../views/LandingPage.vue'
import Yahentamitsi from '../views/Yahentamitsi.vue'
import NorthCampus from '../views/North.vue'
import SouthCampus from '../views/SouthCampus.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: LandingPage
  },
  {
    path: '/yahentamitsi',
    name: 'Yahentamitsi',
    component: Yahentamitsi
  },
  {
    path: '/251-north',
    name: 'NorthCampus',
    component: NorthCampus
  },
  {
    path: '/south-campus',
    name: 'SouthCampus',
    component: SouthCampus
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})


export default router
