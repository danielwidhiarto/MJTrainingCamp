<template>
  <div>
    <Navbar />
    <div class="container">
      <h1 class="dashboard-title">Admin Dashboard</h1>
      <p class="dashboard-welcome">Welcome back, {{ adminName }}!</p>

      <!-- Loading Indicator -->
      <div v-if="isLoading" class="loading-container">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <span>Loading dashboard data...</span>
      </div>

      <!-- Key Statistics Section -->
      <div v-else>
        <div class="row mt-4">
          <div
            class="col-md-4 mb-4"
            v-for="(value, key) in statistics"
            :key="key"
          >
            <div class="card card-stat">
              <div class="card-body text-center">
                <h5 class="card-title">{{ statisticsTitles[key] }}</h5>
                <p class="card-stat-number">
                  {{ formatNumber(value) }}
                </p>
                <p class="card-trend" :class="trendClass(key)">
                  <i :class="trendIcon(key)"></i> {{ trendValue(key) }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Divider -->
        <hr class="section-divider" />

        <!-- Analytics Overview Section -->
        <div class="row mt-4">
          <!-- User Growth Over Time Chart -->
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

          <!-- Transaction Status Distribution Chart -->
          <div class="col-md-6 mb-4">
            <div class="card card-analytics">
              <div class="card-body">
                <h5 class="card-title text-center">
                  Transaction Status Distribution
                </h5>
                <apexchart
                  type="donut"
                  :options="transactionStatusOptions"
                  :series="transactionStatusSeries"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- Optional: User Engagement Metrics Chart -->
        <!-- Uncomment and implement if historical data becomes available -->
        <!--
        <div class="row mt-4">
          <div class="col-md-12 mb-4">
            <div class="card card-analytics">
              <div class="card-body">
                <h5 class="card-title text-center">Active Members Over Time</h5>
                <apexchart
                  type="area"
                  :options="userEngagementOptions"
                  :series="userEngagementSeries"
                />
              </div>
            </div>
          </div>
        </div>
        -->
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import Swal from 'sweetalert2'
import Navbar from '../Admin/Navbar.vue'
import ApexCharts from 'vue3-apexcharts'
import { ref, onMounted, computed } from 'vue'
import dayjs from 'dayjs'

export default {
  name: 'AdminDashboard',
  components: {
    Navbar,
    apexchart: ApexCharts,
  },
  setup() {
    const adminName = ref('Admin') // Replace with actual admin name fetched from user data
    const dashboardData = ref({
      totalUser: 0,
      activeMemberThisMonth: 0,
      totalSuccesTransactionRevenue: 0,
      pendingTransaction: 0,
      countTransactionSuccess: 0,
      thisMonthTransaction: 0,
    })
    const isLoading = ref(true)

    // Mapping of dashboard data keys to display titles
    const statisticsTitles = {
      totalUser: 'Total Users',
      activeMemberThisMonth: 'Active Memberships',
      totalSuccesTransactionRevenue: 'Total Revenue This Month(IDR)',
      pendingTransaction: 'Pending Transactions',
      countTransactionSuccess: 'Successful Transactions',
      thisMonthTransaction: 'This Month Transactions',
    }

    // Computed property for statistics to display
    const statistics = computed(() => ({
      totalUser: dashboardData.value.totalUser,
      activeMemberThisMonth: dashboardData.value.activeMemberThisMonth,
      totalSuccesTransactionRevenue:
        dashboardData.value.totalSuccesTransactionRevenue,
      pendingTransaction: dashboardData.value.pendingTransaction,
      countTransactionSuccess: dashboardData.value.countTransactionSuccess,
      thisMonthTransaction: dashboardData.value.thisMonthTransaction,
    }))

    // Placeholder for previous period data for trend comparison
    // Ideally, fetch this data from the API
    const previousData = {
      totalUser: 4,
      activeMemberThisMonth: 2,
      thisMonthTransaction: 10,
      pendingTransaction: 5,
      countTransactionSuccess: 3,
      totalSuccesTransactionRevenue: 250000,
    }

    // Format numbers with thousand separators
    const formatNumber = num => {
      return new Intl.NumberFormat('id-ID').format(num)
    }

    // Trend Comparison Functions
    const trendValue = key => {
      const current = statistics.value[key]
      const previous = previousData[key]
      if (current > previous) return 'Increase'
      if (current < previous) return 'Decrease'
      return 'No Change'
    }

    const trendIcon = key => {
      const trend = trendValue(key)
      if (trend === 'Increase') return 'fas fa-arrow-up text-success'
      if (trend === 'Decrease') return 'fas fa-arrow-down text-danger'
      return 'fas fa-minus text-secondary'
    }

    const trendClass = key => {
      const trend = trendValue(key)
      if (trend === 'Increase') return 'text-success'
      if (trend === 'Decrease') return 'text-danger'
      return 'text-secondary'
    }

    // Chart Data References
    const userGrowthSeries = ref([])
    const userGrowthOptions = ref({})
    const transactionStatusSeries = ref([])
    const transactionStatusOptions = ref({})
    const revenueSeries = ref([])
    const revenueOptions = ref({})
    const revenueVsTransactionsSeries = ref([])
    const revenueVsTransactionsOptions = ref({})

    // Fetch Dashboard Data from API
    const fetchDashboardData = async () => {
      isLoading.value = true
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          Swal.fire(
            'Authentication Required',
            'Please log in to access the admin dashboard.',
            'warning',
          ).then(() => {
            window.location.href = '/login' // Redirect to login
          })
          return
        }

        const response = await axios.get(
          'https://mjtrainingcamp.my.id/api/v1/transaction/adminDashBoard',
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )

        // Validate and Assign Data
        if (response && response.data) {
          dashboardData.value = response.data
          setupCharts(response.data)
        } else {
          throw new Error('Invalid data format received from API.')
        }
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch dashboard data.', 'error')
      } finally {
        isLoading.value = false
      }
    }

    // Setup Charts with Fetched Data
    const setupCharts = data => {
      // User Growth Over Time
      // Since the API doesn't provide historical data, we'll simulate it based on current data
      // Replace this logic with actual historical data from the API when available

      // Example: Assume user growth over the last 6 months
      const months = [
        dayjs().subtract(5, 'month').format('MMM'),
        dayjs().subtract(4, 'month').format('MMM'),
        dayjs().subtract(3, 'month').format('MMM'),
        dayjs().subtract(2, 'month').format('MMM'),
        dayjs().subtract(1, 'month').format('MMM'),
        dayjs().format('MMM'),
      ]

      // Simulate user growth data
      const newUsersPerMonth = [
        Math.max(data.totalUser - 10, 0),
        Math.max(data.totalUser - 8, 0),
        Math.max(data.totalUser - 5, 0),
        Math.max(data.totalUser - 3, 0),
        Math.max(data.totalUser - 1, 0),
        data.totalUser,
      ]

      userGrowthSeries.value = [
        {
          name: 'Total Users',
          data: newUsersPerMonth,
        },
      ]
      userGrowthOptions.value = {
        chart: {
          height: 350,
          type: 'line',
          zoom: { enabled: false },
          toolbar: { show: false },
        },
        stroke: { curve: 'smooth' },
        xaxis: {
          categories: months,
          title: {
            text: 'Month',
          },
        },
        yaxis: {
          title: {
            text: 'Number of Users',
          },
        },
        colors: ['#ff4500'],
        dataLabels: {
          enabled: true,
        },
      }

      // Transaction Status Distribution
      transactionStatusSeries.value = [
        data.countTransactionSuccess,
        data.pendingTransaction,
      ]
      transactionStatusOptions.value = {
        labels: ['Successful Transactions', 'Pending Transactions'],
        colors: ['#28a745', '#ffc107'],
        responsive: [
          {
            breakpoint: 480,
            options: {
              chart: {
                width: 200,
              },
              legend: {
                position: 'bottom',
              },
            },
          },
        ],
        legend: {
          position: 'right',
          offsetY: 0,
          height: 230,
        },
        title: {
          text: 'Transaction Status',
          align: 'center',
          style: {
            fontSize: '16px',
            color: '#333',
          },
        },
      }
    }

    // Handle Token Expiration Globally (Optional)
    axios.interceptors.response.use(
      response => response,
      error => {
        if (error.response && error.response.status === 401) {
          Swal.fire('Session Expired', 'Please log in again.', 'warning').then(
            () => {
              window.location.href = '/login'
            },
          )
        }
        return Promise.reject(error)
      },
    )

    onMounted(() => {
      fetchDashboardData()
    })

    return {
      adminName,
      dashboardData,
      isLoading,
      statistics,
      statisticsTitles,
      formatNumber,
      trendValue,
      trendIcon,
      trendClass,
      userGrowthSeries,
      userGrowthOptions,
      transactionStatusSeries,
      transactionStatusOptions,
      revenueSeries,
      revenueOptions,
      revenueVsTransactionsSeries,
      revenueVsTransactionsOptions,
    }
  },
}
</script>

<style scoped>
/* Container Styling */
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  border-radius: 16px;
  background-color: #f8f9fa;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* Dashboard Title */
.dashboard-title {
  font-size: 2.5rem;
  font-weight: bold;
  text-align: center;
  color: #ff4500;
  margin-bottom: 20px;
}

/* Welcome Message */
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

/* Statistics Cards */
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

.card-trend {
  font-size: 0.9rem;
  margin-top: 5px;
}

/* Trend Indicators */
.card-trend i {
  margin-right: 5px;
}

/* Analytics Cards */
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
  margin-bottom: 20px;
}

/* Loading Indicator */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
}

.loading-container .spinner-border {
  width: 3rem;
  height: 3rem;
  margin-bottom: 15px;
  color: #ff4500;
}

.loading-container span {
  font-size: 1.2rem;
  color: #555;
}

/* Responsive Design */
@media (max-width: 768px) {
  .container {
    padding: 30px 15px;
  }

  .dashboard-title {
    font-size: 2rem;
  }

  .card-stat,
  .card-analytics {
    padding: 20px;
  }

  .card-stat-number {
    font-size: 2rem;
  }

  .card-analytics {
    height: 350px;
  }
}

@media (max-width: 480px) {
  .dashboard-title {
    font-size: 1.75rem;
  }

  .card-stat,
  .card-analytics {
    padding: 15px;
  }

  .card-stat-number {
    font-size: 1.75rem;
  }

  .card-analytics {
    height: 300px;
  }

  .card-analytics .card-title {
    font-size: 1.2rem;
  }
}
</style>
