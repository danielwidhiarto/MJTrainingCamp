<template>
  <div>
    <div class="container mt-5">
      <h1 class="dashboard-title">Admin Dashboard</h1>
      <p class="dashboard-welcome">Welcome back, {{ adminName }}!</p>

      <!-- Key Statistics Section -->
      <div class="row mt-4">
        <div class="col-md-4 mb-4">
          <div class="card card-stat">
            <div class="card-body text-center">
              <h5 class="card-title">Total Users</h5>
              <p class="card-stat-number">{{ totalUsers }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-4">
          <div class="card card-stat">
            <div class="card-body text-center">
              <h5 class="card-title">Pending Transactions</h5>
              <p class="card-stat-number">{{ pendingTransactions }}</p>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-4">
          <div class="card card-stat">
            <div class="card-body text-center">
              <h5 class="card-title">Active Memberships</h5>
              <p class="card-stat-number">{{ activeMemberships }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Divider -->
      <hr class="section-divider" />

      <!-- Quick Links Section -->
      <div class="row mt-5">
        <div class="col-md-6 mb-4">
          <div class="card card-action">
            <div class="card-body text-center">
              <h5 class="card-title">User Management</h5>
              <p>Manage members and trainers in the system.</p>
              <button class="btn btn-primary" @click="navigateToUserManagement">
                Go to User Management
              </button>
            </div>
          </div>
        </div>
        <div class="col-md-6 mb-4">
          <div class="card card-action">
            <div class="card-body text-center">
              <h5 class="card-title">Transaction Overview</h5>
              <p>View and verify member transactions.</p>
              <button class="btn btn-primary" @click="navigateToTransactions">
                Go to Transaction Overview
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Divider -->
      <hr class="section-divider" />

      <!-- System Controls Section -->
      <div class="row mt-5">
        <div class="col-md-4 mb-4">
          <div class="card card-action">
            <div class="card-body text-center">
              <h5 class="card-title">Package Management</h5>
              <p>Create and edit membership or visit packages.</p>
              <button class="btn btn-primary" @click="navigateToPackages">
                Go to Package Management
              </button>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-4">
          <div class="card card-action">
            <div class="card-body text-center">
              <h5 class="card-title">Class Management</h5>
              <p>Create and edit class schedules.</p>
              <button class="btn btn-primary" @click="navigateToClasses">
                Go to Class Management
              </button>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-4">
          <div class="card card-action">
            <div class="card-body text-center">
              <h5 class="card-title">Trainer Management</h5>
              <p>Manage trainers and assign classes.</p>
              <button class="btn btn-primary" @click="navigateToTrainers">
                Go to Trainer Management
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Divider -->
      <hr class="section-divider" />

      <!-- Profile Settings Section -->
      <div class="row mt-5">
        <div class="col-md-12 mb-4">
          <div class="card card-action">
            <div class="card-body text-center">
              <h5 class="card-title">Profile Settings</h5>
              <p>Update your admin profile and system preferences.</p>
              <button
                class="btn btn-primary"
                @click="navigateToProfileSettings"
              >
                Go to Profile Settings
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '../Admin/Navbar.vue'

export default {
  name: 'AdminDashboard',
  components: {
    Navbar,
  },
  data() {
    return {
      adminName: 'Admin', // Replace with actual admin name fetched from user data
      totalUsers: 120, // Placeholder for total user count
      pendingTransactions: 15, // Placeholder for pending transactions count
      activeMemberships: 45, // Placeholder for active membership count
    }
  },
  methods: {
    navigateToUserManagement() {
      this.$router.push('/admin/manage-member') // Updated route for user management
    },
    navigateToTransactions() {
      this.$router.push('/admin/verify-payment') // Route for transaction overview
    },
    navigateToPackages() {
      this.$router.push('/admin/manage-package') // Updated route for package management
    },
    navigateToClasses() {
      this.$router.push('/admin/manage-class') // Updated route for class management
    },
    navigateToTrainers() {
      this.$router.push('/admin/manage-trainer') // Updated route for trainer management
    },
    navigateToProfileSettings() {
      this.$router.push('/admin/profile-page') // Route for admin profile settings
    },
  },
}
</script>

<style scoped>
.dashboard-title {
  font-size: 2.5rem;
  font-weight: bold;
  text-align: center;
  color: #ff4500; /* Orange-red color */
  margin-bottom: 20px;
}

.dashboard-welcome {
  font-size: 1.2rem;
  text-align: center;
  color: #555;
}

/* Divider */
.section-divider {
  margin: 40px 0;
  border-top: 2px solid #ddd;
}

.card-stat {
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  padding: 30px;
  background-color: #fff;
}

.card-stat .card-title {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 10px;
}

.card-stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  color: #ff4500;
}

.card-action {
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  padding: 30px;
  background-color: #fff;
}

.card-action .card-title {
  font-size: 1.5rem;
  color: #ff4500;
}

.btn-primary {
  background-color: #ff4500;
  border: none;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-primary:hover {
  background-color: #e03b00;
}
</style>
