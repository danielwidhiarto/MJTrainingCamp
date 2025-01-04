import { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue'
import Register from '../components/Register.vue'
import MemberDashboard from '../components/Member/Dashboard.vue'
import AdminDashboard from '../components/Admin/Dashboard.vue'
import TrainerDashboard from '../components/Trainer/Dashboard.vue'

// Define routes
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
    meta: { requiresAuth: true, roles: ['ROLE_USER'] },
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
  },
  {
    path: '/trainer/dashboard',
    name: 'TrainerDashboard',
    component: TrainerDashboard,
    meta: { requiresAuth: true, roles: ['ROLE_TRAINER'] },
  },

  // Member routes
  {
    path: '/member/buy-membership',
    name: 'BuyMembership',
    component: () => import('../components/Member/BuyMembership.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_USER'] },
  },
  {
    path: '/member/payment/:idPackage',
    name: 'Payment',
    component: () => import('../components/Member/Payment.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_USER'] },
  },
  {
    path: '/member/view-class',
    name: 'ViewClass',
    component: () => import('../components/Member/ViewClass.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_USER'] },
  },
  {
    path: '/member/class-detail/:id',
    name: 'ClassDetail',
    component: () => import('../components/Member/ClassDetail.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_USER'] },
  },
  {
    path: '/member/class-history',
    name: 'ClassHistory',
    component: () => import('../components/Member/ClassHistory.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_USER'] },
  },
  {
    path: '/member/transaction-history',
    name: 'TransactionHistory',
    component: () => import('../components/Member/TransactionHistory.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_USER'] },
  },
  {
    path: '/member/profile-page',
    name: 'ProfilePage',
    component: () => import('../components/Member/ProfilePage.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_USER'] },
  },

  // Admin routes
  {
    path: '/admin/manage-package',
    name: 'ManagePackage',
    component: () => import('../components/Admin/ManagePackage.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
  },
  {
    path: '/admin/manage-class',
    name: 'ManageClass',
    component: () => import('../components/Admin/ManageClass.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
  },
  {
    path: '/admin/manage-class/class-detail/:id',
    name: 'AdminClassDetail',
    component: () => import('../components/Admin/ClassDetail.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
  },
  {
    path: '/admin/manage-trainer',
    name: 'ManageTrainer',
    component: () => import('../components/Admin/ManageTrainer.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
  },
  {
    path: '/admin/verify-payment',
    name: 'VerifyPayment',
    component: () => import('../components/Admin/VerifyPayment.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
  },
  {
    path: '/admin/manage-member',
    name: 'ManageMember',
    component: () => import('../components/Admin/ManageMember.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
  },
  {
    path: '/admin/profile-page',
    name: 'AdminProfilePage',
    component: () => import('../components/Admin/ProfilePage.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
    props: true,
  },

  //Trainer
  {
    path: '/trainer/profile-page',
    name: 'TrainerProfilePage',
    component: () => import('../components/Trainer/ProfilePage.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_TRAINER'] },
  },
  {
    path: '/trainer/class-detail/:id',
    name: 'TrainerClassDetail',
    component: () => import('../components/Trainer/ClassDetail.vue'),
    meta: { requiresAuth: true, roles: ['ROLE_TRAINER'] },
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
  const tokenExpiredDate = localStorage.getItem('tokenExpiredDate')

  // If tokenExpiredDate exists, check if it's expired
  if (tokenExpiredDate) {
    const currentTime = new Date().toISOString()
    if (new Date(tokenExpiredDate) < new Date(currentTime)) {
      // Token is expired, remove stored data and redirect to login
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('idUser')
      localStorage.removeItem('name')
      localStorage.removeItem('email')
      localStorage.removeItem('phone')
      localStorage.removeItem('registrationDate')
      localStorage.removeItem('tokenExpiredDate')

      Swal.fire({
        title: 'Session Expired',
        text: 'Your session has expired. Please log in again.',
        icon: 'error',
        confirmButtonText: 'OK',
      })

      next({ name: 'Login' }) // Redirect to login
      return
    }
  }

  // Redirect logged-in users from login/register to the dashboard
  if (token && (to.name === 'Login' || to.name === 'Register')) {
    next({ name: 'MemberDashboard' }) // or any other default route for logged-in users
    return
  }

  // Handle protected routes
  if (to.meta.requiresAuth) {
    if (!token) {
      // If not logged in, redirect to Login
      next({ name: 'Login' })
    } else if (!to.meta.roles.includes(role)) {
      // If user doesn't have the required role, redirect accordingly
      if (role === 'ROLE_ADMIN') {
        next({ name: 'AdminDashboard' })
      } else if (role === 'ROLE_TRAINER') {
        next({ name: 'TrainerDashboard' })
      } else {
        next({ name: 'MemberDashboard' })
      }
    } else {
      next() // User is authenticated and authorized
    }
  } else {
    next() // Allow access to non-protected routes
  }
})

export default router
