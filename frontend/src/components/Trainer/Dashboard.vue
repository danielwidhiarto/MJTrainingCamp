<template>
  <div>
    <LazyNavbar />
    <div class="container">
      <h1 class="mb-4">Trainer Dashboard</h1>
      <p class="welcome-text mb-6">
        Welcome, {{ trainerName }}! Here is your upcoming and past class
        schedule.
      </p>

      <!-- Loading Indicator -->
      <div v-if="loading" class="loading-indicator" aria-live="polite">
        <div class="spinner"></div>
        <span>Loading your schedule...</span>
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
            <tr v-for="classItem in upcomingClasses" :key="classItem.idClass">
              <td>{{ classItem.className }}</td>
              <td>{{ formatDate(classItem.classDate) }}</td>
              <td>{{ formatTime(classItem.classTime) }}</td>
              <td>{{ classItem.classCapasity }} members</td>
              <td>
                <button
                  class="btn btn-primary"
                  @click="viewClassDetail(classItem.idClass)"
                >
                  View Detail
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- No Upcoming Classes Message -->
      <div
        v-else-if="!loading && !upcomingClasses.length"
        class="no-history text-center"
      >
        You have no upcoming classes.
      </div>

      <!-- Past Classes Table -->
      <div
        v-if="!loading && pastClasses.length"
        class="class-history-container mt-5"
      >
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
            <tr v-for="classItem in pastClasses" :key="classItem.idClass">
              <td>{{ classItem.className }}</td>
              <td>{{ formatDate(classItem.classDate) }}</td>
              <td>{{ formatTime(classItem.classTime) }}</td>
              <td>{{ classItem.classCapasity }} members</td>
              <td>
                <button
                  class="btn btn-primary"
                  @click="viewClassDetail(classItem.idClass)"
                >
                  View Detail
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- No Past Classes Message -->
      <div
        v-else-if="!loading && !pastClasses.length"
        class="no-history text-center mt-5"
      >
        You have no past classes.
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import { useRouter } from 'vue-router'

// Extend dayjs with UTC and Timezone plugins
dayjs.extend(utc)
dayjs.extend(timezone)

export default {
  name: 'TrainerDashboard',
  setup() {
    const upcomingClasses = ref([])
    const pastClasses = ref([])
    const loading = ref(false)
    const error = ref(null)
    const trainerName = ref('') // New ref to store trainer's name
    const router = useRouter()

    // Fetch trainer's name from localStorage
    onMounted(() => {
      trainerName.value = localStorage.getItem('name') || 'Trainer' // Default to 'Trainer' if no name in localStorage
      fetchTrainerSchedule()
    })

    const fetchTrainerSchedule = async () => {
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
          'https://mjtrainingcamp.my.id/api/v1/trainer/getschedule',
          { token: token },
          {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          },
        )

        if (Array.isArray(response.data)) {
          const currentDate = dayjs().startOf('day')
          upcomingClasses.value = response.data.filter(classItem =>
            dayjs(classItem.classDate).isAfter(currentDate),
          )
          pastClasses.value = response.data.filter(classItem =>
            dayjs(classItem.classDate).isBefore(currentDate),
          )
        }
      } catch (err) {
        console.error('API Request Error:', err)
        error.value =
          err.response?.data?.message || 'An unexpected error occurred.'
        upcomingClasses.value = []
        pastClasses.value = []
      } finally {
        loading.value = false
      }
    }

    const viewClassDetail = classId => {
      router.push({
        name: 'TrainerClassDetail',
        params: { id: classId },
      })
    }

    const formatDate = dateString => {
      if (!dateString) return 'N/A'
      return dayjs(dateString).format('dddd, D MMMM YYYY')
    }

    const formatTime = time => {
      if (!time) return 'N/A'

      let parsedTime = dayjs(time, 'HH:mm:ss', true)
      if (!parsedTime.isValid()) {
        parsedTime = dayjs(time, 'HH:mm', true)
      }

      return parsedTime.isValid() ? parsedTime.format('h:mm A') : time
    }

    return {
      trainerName,
      upcomingClasses,
      pastClasses,
      loading,
      error,
      formatDate,
      formatTime,
      viewClassDetail,
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
  background-color: #f1f1f1;
  color: #333;
}

.class-history-table td {
  font-size: 1.1rem;
}

.class-history-table tr:hover {
  background-color: #f9f9f9;
}

.no-history {
  font-size: 1.3rem;
  color: #777;
}

.btn {
  padding: 10px 15px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  text-align: center;
}

.btn-primary {
  background-color: #ff4500;
  border-color: #ff4500;
}

.btn-primary:hover {
  background-color: #e03b00;
  border-color: #e03b00;
}
</style>
