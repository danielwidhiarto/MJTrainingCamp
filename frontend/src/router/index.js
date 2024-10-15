import { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue' // Import Login component
import Register from '../components/Register.vue' // Import Register component

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login,
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
