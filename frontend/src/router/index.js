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
    path: '/member/dashboard',
    name: 'MemberDashboard',
    component: MemberDashboard,
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
  },
  {
    path: '/trainer/dashboard',
    name: 'TrainerDashboard',
    component: TrainerDashboard,
  },

  //Member
  {
    path: '/member/buy-membership',
    name: 'BuyMembership',
    component: () => import('../components/Member/BuyMembership.vue'), // Create this component as needed
  },
  {
    path: '/member/view-class',
    name: 'ViewClass',
    component: () => import('../components/Member/ViewClass.vue'), // Create this component as needed
  },
  {
    path: '/member/class-history',
    name: 'ClassHistory',
    component: () => import('../components/Member/ClassHistory.vue'), // Create this component as needed
  },
  {
    path: '/member/profile-page',
    name: 'ProfilePage',
    component: () => import('../components/Member/ProfilePage.vue'), // Create this component as needed
  },

  //Admin
  {
    path: '/admin/manage-package',
    name: 'ManagePackage',
    component: () => import('../components/Admin/ManagePackage.vue'), // Create this component as needed
  },
  {
    path: '/admin/manage-class',
    name: 'ManageClass',
    component: () => import('../components/Admin/ManageClass.vue'), // Create this component as needed
  },
  {
    path: '/admin/verify-payment',
    name: 'VerifyPayment',
    component: () => import('../components/Admin/VerifyPayment.vue'), // Create this component as needed
  },
  {
    path: '/admin/manage-member',
    name: 'ManageMember',
    component: () => import('../components/Admin/ManageMember.vue'), // Create this component as needed
  },
  {
    path: '/admin/profile-page',
    name: 'ProfilePage',
    component: () => import('../components/Admin/ProfilePage.vue'), // Create this component as needed
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

  // Allow access to login and register without token
  if (to.name === 'Login' || to.name === 'Register') {
    next() // Allow access to Login and Register routes
  } else if (to.name === 'AdminDashboard' && role !== 'ROLE_ADMIN') {
    // If trying to access Admin Dashboard and user is not admin, redirect to Member Dashboard
    next({ name: 'MemberDashboard' })
  } else if (to.name === 'TrainerDashboard' && role !== 'ROLE_TRAINER') {
    // If trying to access Trainer Dashboard and user is not a trainer, redirect to Member Dashboard
    next({ name: 'MemberDashboard' })
  } else if (!token) {
    // If trying to access a protected route and not logged in, redirect to Login
    next({ name: 'Login' })
  } else {
    next() // Allow access to the route
  }
})

export default router
