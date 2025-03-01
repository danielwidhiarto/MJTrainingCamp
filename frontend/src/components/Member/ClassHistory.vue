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
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="classItem in upcomingClasses" :key="classItem.id">
              <td>{{ classItem.className }}</td>
              <td>{{ formatDate(classItem.classDate) }}</td>
              <td>{{ classItem.classTime }}</td>
              <td>{{ classItem.classCapasity }} members</td>
              <td>
                <div class="action-buttons">
                  <button
                    class="btn btn-primary"
                    @click="goToClassDetail(classItem.idClass)"
                  >
                    View Detail
                  </button>
                  <button
                    class="btn btn-danger"
                    @click="cancelBooking(classItem)"
                  >
                    Cancel Booking
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Past Classes Table -->
      <div
        v-if="!loading && pastClasses.length"
        class="class-history-container"
      >
        <br />
        <h3 class="section-title">Past Classes</h3>
        <table class="class-history-table">
          <thead>
            <tr>
              <th>Class Name</th>
              <th>Date</th>
              <th>Time</th>
              <th>Capacity</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="classItem in pastClasses" :key="classItem.id">
              <td>{{ classItem.className }}</td>
              <td>{{ formatDate(classItem.classDate) }}</td>
              <td>{{ classItem.classTime }}</td>
              <td>{{ classItem.classCapasity }} members</td>
              <td>
                <button
                  class="btn btn-primary"
                  @click="goToClassDetail(classItem.idClass)"
                >
                  View Detail
                </button>
              </td>
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
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import dayjs from 'dayjs'
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter'

dayjs.extend(isSameOrAfter) // Aktifkan plugin

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

    const router = useRouter()

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
          { token },
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

    const splitClassesByDate = classes => {
      const now = dayjs().startOf('day') // Pastikan perbandingan mulai dari awal hari

      upcomingClasses.value = classes.filter(classItem =>
        dayjs(classItem.classDate).isSameOrAfter(now, 'day'),
      )

      pastClasses.value = classes.filter(classItem =>
        dayjs(classItem.classDate).isBefore(now, 'day'),
      )
    }

    const cancelBooking = async classItem => {
      const token = localStorage.getItem('token')
      if (!token) {
        Swal.fire({
          icon: 'info',
          title: 'Login Required',
          text: 'No authentication token found. Please log in.',
        })
        return
      }

      // Ensure classDate and classTime exist
      if (!classItem.classDate || !classItem.classTime) {
        Swal.fire({
          icon: 'error',
          title: 'Invalid Class Data',
          text: 'The class date or time is missing. Please check your schedule.',
        })
        return
      }

      // Convert `classDate` from ISO format to local date
      const classDateLocal = dayjs(classItem.classDate).format('YYYY-MM-DD')

      // Combine `classDateLocal` with `classTime`
      const classStart = dayjs(
        `${classDateLocal} ${classItem.classTime}`,
        'YYYY-MM-DD HH:mm:ss',
      )

      if (!classStart.isValid()) {
        Swal.fire({
          icon: 'error',
          title: 'Invalid Date Format',
          text: 'There was an issue parsing the class time. Please contact support.',
        })
        return
      }

      const now = dayjs()
      const hoursDiff = classStart.diff(now, 'hour', true)

      if (hoursDiff < 2) {
        Swal.fire({
          icon: 'error',
          title: 'Cannot Cancel',
          text: 'Cancellation is only allowed at least 2 hours before the class starts.',
        })
        return // Stop execution, no API call will be made
      }

      const confirmation = await Swal.fire({
        title: 'Are you sure?',
        text: 'Do you want to cancel this booking?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, cancel it!',
        cancelButtonText: 'No, keep it',
      })

      if (!confirmation.isConfirmed) return

      try {
        await axios.post(
          'https://mjtrainingcamp.my.id/api/v1/class/cancel',
          { idClass: classItem.idClass, token },
          {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          },
        )

        Swal.fire({
          icon: 'success',
          title: 'Cancelled!',
          text: 'Your booking has been successfully canceled.',
        })

        fetchClassHistory() // Refresh class list after successful cancellation
      } catch (err) {
        console.error('Cancellation Error:', err)
        Swal.fire({
          icon: 'error',
          title: 'Error!',
          text: err.response?.data?.message || 'An unexpected error occurred.',
        })
      }
    }

    const formatDate = dateString =>
      dateString ? dayjs(dateString).format('dddd, D MMMM YYYY') : 'N/A'
    const formatTime = time =>
      time ? dayjs(time, 'HH:mm:ss').format('h:mm A') : 'N/A'

    const goToClassDetail = idClass => {
      router.push({ name: 'ClassDetail', params: { id: idClass } })
    }

    onMounted(fetchClassHistory)

    return {
      classHistory,
      upcomingClasses,
      pastClasses,
      loading,
      error,
      formatDate,
      formatTime,
      cancelBooking,
      goToClassDetail,
    }
  },
}
</script>

<style scoped>
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

h1 {
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.welcome-text {
  font-size: 1.25rem;
  color: #555;
  margin-bottom: 40px;
}

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

.alert-danger {
  font-size: 1.3rem;
  font-weight: 600;
  color: #ff4500;
}

.class-history-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  overflow-x: auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.class-history-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
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

.no-history {
  margin-top: 20px;
  font-size: 1.25rem;
  color: #777;
}

.btn-danger {
  background-color: #ff4500;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-danger:hover {
  background-color: #e63900;
}

/* Add this to your existing styles */
.action-buttons {
  display: flex;
  gap: 10px; /* Adds space between the buttons */
  justify-content: center; /* Centers the buttons horizontally */
}

.btn {
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-danger {
  background-color: #ff4500;
  color: white;
  border: none;
}

.btn-danger:hover {
  background-color: #e63900;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  border: none;
}

.btn-primary:hover {
  background-color: #0056b3;
}
/* Add this to your existing styles */
.action-buttons {
  display: flex;
  gap: 10px; /* Adds space between the buttons */
  justify-content: center; /* Centers the buttons horizontally */
}

.btn {
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-danger {
  background-color: #ff4500;
  color: white;
  border: none;
}

.btn-danger:hover {
  background-color: #e63900;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  border: none;
}

.btn-primary:hover {
  background-color: #0056b3;
}

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
