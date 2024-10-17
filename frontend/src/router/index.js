import { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue'
import Register from '../components/Register.vue'
import MemberDashboard from '../components/Member/Dashboard.vue'
import AdminDashboard from '../components/Admin/Dashboard.vue'
import TrainerDashboard from '../components/Trainer/Dashboard.vue'

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
  {
    path: '/member-dashboard',
    name: 'MemberDashboard',
    component: MemberDashboard,
  },
  {
    path: '/admin-dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
  },
  {
    path: '/trainer-dashboard',
    name: 'TrainerDashboard',
    component: TrainerDashboard,
  },

  {
    path: '/buy-membership',
    name: 'BuyMembership',
    component: () => import('../components/Member/BuyMembership.vue'), // Create this component as needed
  },
  {
    path: '/view-class',
    name: 'ViewClass',
    component: () => import('../components/Member/ViewClass.vue'), // Create this component as needed
  },
  {
    path: '/history-class',
    name: 'HistoryClass',
    component: () => import('../components/Member/HistoryClass.vue'), // Create this component as needed
  },
]

// Create the router instance
const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation guard to protect routes
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.name === 'AdminDashboard' && role !== 'ROLE_ADMIN') {
    // If trying to access Admin Dashboard and user is not admin, redirect to User Home
    next({ name: 'MemberDashboard' })
  } else if (to.name === 'TrainerDashboard' && role !== 'ROLE_TRAINER') {
    // If trying to access Trainer Dashboard and user is not a trainer, redirect to User Home
    next({ name: 'MemberDashboard' })
  } else if (to.name !== 'Login' && !token) {
    // If trying to access a protected route and not logged in, redirect to Login
    next({ name: 'Login' })
  } else {
    next() // Allow access to the route
  }
})

export default router
