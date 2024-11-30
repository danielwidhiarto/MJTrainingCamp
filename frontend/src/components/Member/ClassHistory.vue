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

      <!-- Class History Table -->
      <div
        v-if="!loading && classHistory.length"
        class="class-history-container flex justify-center"
      >
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
            <tr v-for="classItem in classHistory" :key="classItem.id">
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
        v-else-if="!loading && !classHistory.length"
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

// Set default timezone if needed, e.g., 'America/New_York'
// dayjs.tz.setDefault("America/New_York")

// Lazy load Navbar component
const LazyNavbar = defineAsyncComponent(() => import('./Navbar.vue'))

export default {
  name: 'ClassHistory',
  components: { LazyNavbar },
  setup() {
    const classHistory = ref([])
    const loading = ref(false)
    const error = ref(null)

    /**
     * Fetches the class history for the logged-in user.
     */
    const fetchClassHistory = async () => {
      loading.value = true
      error.value = null

      try {
        // Step 1: Get token from localStorage
        const token = localStorage.getItem('token')
        if (!token) {
          // Token is missing
          Swal.fire({
            icon: 'info',
            title: 'Login Required',
            text: 'No authentication token found. Please log in.',
          })
          console.warn('Authentication token missing')
          return
        }

        // Step 2: Make the API request with the token in headers and body
        const response = await axios.post(
          'http://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/class/getHistory',
          {
            token: token, // Include token in the request body
          },
          {
            headers: {
              Authorization: `Bearer ${token}`, // Include token in headers
              'Content-Type': 'application/json',
            },
          },
        )

        // Step 3: Process the response
        console.log('API Response:', response.data) // Debug: log the entire response data

        // Set classHistory directly to the response data, assuming it's an array
        if (Array.isArray(response.data)) {
          classHistory.value = response.data
        } else {
          console.warn(
            'Expected an array in response.data but received:',
            response.data,
          )
          classHistory.value = [] // Fallback to an empty array if data is not in expected format
        }
      } catch (err) {
        console.error('API Request Error:', err) // Log the error details

        // Check if it's a 403 Forbidden error specifically
        if (err.response && err.response.status === 403) {
          Swal.fire({
            icon: 'error',
            title: 'Access Denied',
            text: 'You do not have permission to view this content.',
          })
          console.warn('403 Forbidden - Token may be invalid or expired')
        } else {
          // Other errors
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text:
              err.response?.data?.message || 'An unexpected error occurred.',
          })
        }

        classHistory.value = []
      } finally {
        loading.value = false
      }
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

      // Attempt parsing with 'HH:mm:ss' first
      let parsedTime = dayjs(time, 'HH:mm:ss', true)

      if (!parsedTime.isValid()) {
        // Attempt parsing with 'HH:mm'
        parsedTime = dayjs(time, 'HH:mm', true)
      }

      if (parsedTime.isValid()) {
        return parsedTime.format('h:mm A') // e.g., "2:30 PM"
      } else {
        console.warn('Invalid time format:', time)
        return time // Return original string if parsing fails
      }
    }

    onMounted(() => {
      fetchClassHistory()
    })

    return {
      classHistory,
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
  font-size: 3rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #000; /* Black text to match the logo's accent */
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
  width: 100%;
  overflow-x: auto;
  display: flex;
  justify-content: center; /* Center the table horizontally */
}

.class-history-table {
  width: 100%;
  max-width: 1000px; /* Increased max-width for more columns */
  border-collapse: collapse;
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
  h1 {
    font-size: 2.5rem;
  }

  .welcome-text {
    font-size: 1.2rem;
    margin-bottom: 30px;
  }

  .class-history-table th,
  .class-history-table td {
    padding: 10px 12px;
  }

  .class-history-table {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 2rem;
  }

  .welcome-text {
    font-size: 1rem;
    margin-bottom: 20px;
  }

  .class-history-table th,
  .class-history-table td {
    padding: 8px 10px;
    font-size: 0.95rem;
  }

  .no-history {
    font-size: 1rem;
  }
}
</style>
