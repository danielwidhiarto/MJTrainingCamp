<template>
  <div>
    <LazyNavbar />
    <div class="container">
      <h1 class="mb-4">Class History</h1>
      <p class="welcome-text mb-6">Here is the history of your classes.</p>

      <!-- Loading Indicator -->
      <div v-if="loading" class="loading-indicator" aria-live="polite">
        <div class="spinner"></div>
        <span>Loading class history...</span>
      </div>

      <!-- Error Message -->
      <div
        v-if="error"
        class="alert alert-danger text-center my-5"
        role="alert"
      >
        {{ error }}
      </div>

      <!-- Upcoming Classes Table -->
      <div
        v-if="!loading && upcomingClasses.length"
        class="class-history-container"
      >
        <h3 class="section-title">Upcoming Classes</h3>
        <table class="class-history-table">
          <thead>
            <tr>
              <th>Class Name</th>
              <th>Date</th>
              <th>Time</th>
              <th>Capacity</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="classItem in upcomingClasses" :key="classItem.id">
              <td>{{ classItem.className }}</td>
              <td>{{ formatDate(classItem.classDate) }}</td>
              <td>{{ formatTime(classItem.classTime) }}</td>
              <td>{{ classItem.classCapasity }} members</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Past / Ongoing Classes Table -->
      <div
        v-if="!loading && pastClasses.length"
        class="class-history-container"
      >
        <br />
        <h3 class="section-title">Past / Ongoing Classes</h3>
        <table class="class-history-table">
          <thead>
            <tr>
              <th>Class Name</th>
              <th>Date</th>
              <th>Time</th>
              <th>Capacity</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="classItem in pastClasses" :key="classItem.id">
              <td>{{ classItem.className }}</td>
              <td>{{ formatDate(classItem.classDate) }}</td>
              <td>{{ formatTime(classItem.classTime) }}</td>
              <td>{{ classItem.classCapasity }} members</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- No History Message -->
      <div
        v-else-if="!loading && !upcomingClasses.length && !pastClasses.length"
        class="no-history text-center"
      >
        You have no class history.
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { defineAsyncComponent, onMounted, ref } from 'vue'
import Swal from 'sweetalert2'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'

// Extend dayjs with UTC and Timezone plugins
dayjs.extend(utc)
dayjs.extend(timezone)

// Lazy load Navbar component
const LazyNavbar = defineAsyncComponent(() => import('./Navbar.vue'))

export default {
  name: 'ClassHistory',
  components: { LazyNavbar },
  setup() {
    const classHistory = ref([])
    const upcomingClasses = ref([])
    const pastClasses = ref([])
    const loading = ref(false)
    const error = ref(null)

    /**
     * Fetches the class history for the logged-in user.
     */
    const fetchClassHistory = async () => {
      loading.value = true
      error.value = null

      try {
        const token = localStorage.getItem('token')
        if (!token) {
          Swal.fire({
            icon: 'info',
            title: 'Login Required',
            text: 'No authentication token found. Please log in.',
          })
          return
        }

        const response = await axios.post(
          'https://mjtrainingcamp.my.id/api/v1/class/getHistory',
          {
            token: token,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          },
        )

        if (Array.isArray(response.data)) {
          classHistory.value = response.data
          splitClassesByDate(response.data)
        }
      } catch (err) {
        console.error('API Request Error:', err)
        error.value =
          err.response?.data?.message || 'An unexpected error occurred.'
        classHistory.value = []
      } finally {
        loading.value = false
      }
    }

    /**
     * Splits classes into upcoming and past/ongoing based on current date.
     */
    const splitClassesByDate = classes => {
      const now = dayjs()
      upcomingClasses.value = classes.filter(classItem =>
        dayjs(classItem.classDate).isAfter(now, 'day'),
      )
      pastClasses.value = classes.filter(classItem =>
        dayjs(classItem.classDate).isBefore(now, 'day'),
      )
    }

    /**
     * Formats the date to "Monday, 4 November 2024".
     * @param {String} dateString - The date string.
     * @returns {String} - Formatted date.
     */
    const formatDate = dateString => {
      if (!dateString) return 'N/A'
      return dayjs(dateString).format('dddd, D MMMM YYYY')
    }

    /**
     * Formats the time to "1:30 PM".
     * @param {String} time - The time string.
     * @returns {String} - Formatted time.
     */
    const formatTime = time => {
      if (!time) return 'N/A'

      let parsedTime = dayjs(time, 'HH:mm:ss', true)
      if (!parsedTime.isValid()) {
        parsedTime = dayjs(time, 'HH:mm', true)
      }

      return parsedTime.isValid() ? parsedTime.format('h:mm A') : time
    }

    onMounted(() => {
      fetchClassHistory()
    })

    return {
      classHistory,
      upcomingClasses,
      pastClasses,
      loading,
      error,
      formatDate,
      formatTime,
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
  text-align: center;
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  margin-top: 40px;
}

/* Heading Styles */
h1 {
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

/* Welcome Text */
.welcome-text {
  font-size: 1.25rem;
  color: #555;
  margin-bottom: 40px;
}

/* Loading Indicator Styling */
.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  font-size: 1.3rem;
  font-weight: 600;
  color: #666;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #ff4500;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-right: 10px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Error Message Styling */
.alert-danger {
  font-size: 1.3rem;
  font-weight: 600;
  color: #ff4500;
}

/* Class History Table */
.class-history-container {
  width: 100%; /* Ensure it takes full width of the container */
  max-width: 800px; /* Set a maximum width for the table container */
  margin: 0 auto; /* Center the container horizontally */
  overflow-x: auto; /* Enable horizontal scrolling if content overflows */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center; /* Center the table */
}

.class-history-table {
  width: 100%; /* Make the table take full width of its container */
  border-collapse: collapse; /* Remove space between table cells */
  text-align: left; /* Align text to the left */
}

.section-title {
  font-size: 1.75rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.class-history-table th,
.class-history-table td {
  padding: 12px 20px;
  border: 1px solid #ddd;
  text-align: left;
}

.class-history-table th {
  background-color: #f4f4f4;
  color: #333;
  font-size: 1.1rem;
}

.class-history-table tbody tr:nth-child(even) {
  background-color: #f9f9f9;
}

.class-history-table tbody tr:hover {
  background-color: #ffe5d9;
}

/* No History Message */
.no-history {
  margin-top: 20px;
  font-size: 1.25rem;
  color: #777;
  text-align: center;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .section-title {
    font-size: 1.5rem;
  }

  .class-history-table th,
  .class-history-table td {
    padding: 10px;
    font-size: 1rem;
  }
}
</style>
