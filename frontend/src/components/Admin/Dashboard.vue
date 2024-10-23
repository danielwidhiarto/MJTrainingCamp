<template>
  <div>
    <Navbar />
    <div class="container">
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

      <!-- Analytics Overview Section -->
      <div class="row mt-4">
        <div class="col-md-6 mb-4">
          <div class="card card-analytics">
            <div class="card-body">
              <h5 class="card-title text-center">User Growth Over Time</h5>
              <apexchart
                type="line"
                :options="userGrowthOptions"
                :series="userGrowthSeries"
              />
            </div>
          </div>
        </div>

        <div class="col-md-6 mb-4">
          <div class="card card-analytics">
            <div class="card-body">
              <h5 class="card-title text-center">Class Attendance Trends</h5>
              <apexchart
                type="bar"
                :options="classAttendanceOptions"
                :series="classAttendanceSeries"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ApexCharts from 'vue3-apexcharts'
import Navbar from '../Admin/Navbar.vue'

export default {
  name: 'AdminDashboard',
  components: {
    Navbar,
    apexchart: ApexCharts, // Import ApexCharts
  },
  data() {
    return {
      adminName: 'Admin', // Replace with actual admin name fetched from user data
      totalUsers: 120, // Placeholder for total user count
      pendingTransactions: 15, // Placeholder for pending transactions count
      activeMemberships: 45, // Placeholder for active membership count

      // User Growth Chart Data
      userGrowthSeries: [
        {
          name: 'New Users',
          data: [10, 30, 20, 40, 60, 80, 100], // Example data
        },
      ],
      userGrowthOptions: {
        chart: {
          height: 350,
          type: 'line',
          zoom: { enabled: false },
        },
        stroke: { curve: 'smooth' },
        xaxis: {
          categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'], // Example labels
        },
        colors: ['#ff4500'],
      },

      // Class Attendance Chart Data
      classAttendanceSeries: [
        {
          name: 'Attendance',
          data: [30, 45, 25, 50, 40], // Example data
        },
      ],
      classAttendanceOptions: {
        chart: { height: 350, type: 'bar' },
        xaxis: {
          categories: ['Yoga', 'Pilates', 'CrossFit', 'Dance', 'Boxing'], // Example labels
        },
        colors: ['#ff4500'],
      },
    }
  },
}
</script>

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  border-radius: 16px;
}

.dashboard-title {
  font-size: 2.5rem;
  font-weight: bold;
  text-align: center;
  color: #ff4500;
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

.card-analytics {
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  padding: 30px;
  background-color: #fff;
  height: 400px;
}

.card-analytics .card-title {
  font-size: 1.5rem;
  color: #ff4500;
}
</style>
