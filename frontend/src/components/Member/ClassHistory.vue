<template>
  <div>
    <Navbar />
    <div class="container mx-auto p-4">
      <h1 class="text-3xl font-bold mb-4">Class History</h1>
      <p class="welcome-text mb-6">Here is the history of your classes.</p>

      <div v-if="classHistory.length" class="class-history-container">
        <table class="class-history-table">
          <thead>
            <tr>
              <th>Class Number</th>
              <th>Class Name</th>
              <th>Date</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="classItem in classHistory" :key="classItem.id">
              <td>{{ classItem.id }}</td>
              <td>{{ classItem.className }}</td>
              <td>{{ formatDate(classItem.date) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="no-history">You have no class history.</div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import { onMounted, ref } from 'vue'

export default {
  components: { Navbar },
  setup() {
    const classHistory = ref([])

    const fetchClassHistory = async () => {
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

        // Step 2: Attempt the API request with the token in both headers and body
        const response = await axios.post(
          'http://localhost:8081/api/v1/class/getHistory',
          {
            token: token, // Include token in the request body
          },
          {
            headers: {
              Authorization: `Bearer ${token}`, // Include token in the headers as well
            },
          },
        )

        // Step 3: Process the response based on the new data structure
        console.log('API Response:', response.data) // Debug: log the entire response data

        // Set classHistory directly to the response data, since it's already an array
        if (Array.isArray(response.data)) {
          classHistory.value = response.data
        } else {
          console.warn(
            'Expected an array in response.data but received:',
            response.data,
          )
          classHistory.value = [] // Fallback to an empty array if data is not in expected format
        }
      } catch (error) {
        console.error('API Request Error:', error) // Log the error details

        // Check if it's a 403 Forbidden error specifically
        if (error.response && error.response.status === 403) {
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
              error.response?.data?.message || 'An unexpected error occurred.',
          })
        }
      }
    }

    // Function to format date
    const formatDate = dateString => {
      const options = { year: 'numeric', month: 'long', day: 'numeric' }
      return new Date(dateString).toLocaleDateString(undefined, options)
    }

    // Call the fetch function on component mount
    onMounted(fetchClassHistory)

    return {
      classHistory,
      formatDate,
    }
  },
}
</script>

<style scoped>
/* General Styles */
.container {
  max-width: 1200px;
}

h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 20px;
}

.welcome-text {
  font-size: 1.2rem;
  color: #555;
  margin-bottom: 40px;
}

/* Class History Table */
.class-history-container {
  width: 100%;
  overflow-x: auto;
}

.class-history-table {
  width: 100%;
  border-collapse: collapse;
}

.class-history-table th,
.class-history-table td {
  padding: 12px 20px;
  border: 1px solid #ddd;
}

.class-history-table th {
  background-color: #f4f4f4;
  color: #333;
  text-align: left;
}

.class-history-table tr:nth-child(even) {
  background-color: #f9f9f9;
}

.status-completed {
  color: #28a745;
  font-weight: bold;
}

.status-upcoming {
  color: #007bff;
  font-weight: bold;
}

.status-canceled {
  color: #dc3545;
  font-weight: bold;
}

.status-default {
  color: #6c757d;
  font-weight: bold;
}

/* No History Message */
.no-history {
  margin-top: 20px;
  font-size: 1.2rem;
  color: #777;
}

/* Responsive Styles */
@media (max-width: 768px) {
  h1 {
    font-size: 2.5rem;
  }

  .welcome-text {
    font-size: 1rem;
  }

  .class-history-table th,
  .class-history-table td {
    padding: 10px 12px;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 2rem;
  }

  .welcome-text {
    font-size: 0.9rem;
  }

  .class-history-table th,
  .class-history-table td {
    padding: 8px 10px;
  }
}
</style>
