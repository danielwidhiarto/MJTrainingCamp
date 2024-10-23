<template>
  <div>
    <Navbar />
    <div class="container">
      <h1 class="page-title">View Class</h1>
      <p class="welcome-text">
        Welcome to your View Class page! Use the date selector to view the
        schedule for a selected day.
      </p>

      <!-- Custom Date Picker -->
      <div class="custom-date-picker-container">
        <div class="custom-date-picker">
          <span>{{ formatDate(selectedDate) }}</span>
          <div class="date-buttons">
            <button @click="changeDate(-1)">&#9664;</button>
            <!-- Previous day -->
            <button @click="changeDate(1)">&#9654;</button>
            <!-- Next day -->
          </div>
        </div>
      </div>

      <!-- If no date is selected -->
      <div v-if="!selectedDate" class="info-text">
        <p>Please select a date to view available classes.</p>
      </div>

      <!-- Class Listing Section (filtered by selectedDate) -->
      <div
        v-if="selectedDate && filteredClasses.length > 0"
        class="schedule-container"
      >
        <h2>Schedule for {{ formatDate(selectedDate) }}:</h2>
        <div class="schedule-list">
          <div
            class="class-item"
            v-for="classItem in filteredClasses"
            :key="classItem.id"
          >
            <p><strong>Class:</strong> {{ classItem.className }}</p>
            <p><strong>Time:</strong> {{ classItem.classTime }}</p>
            <p>
              <strong>Capacity:</strong> {{ classItem.classCapasity }} students
            </p>
            <button class="details-button">Join Class</button>
          </div>
        </div>
      </div>

      <!-- No classes available message -->
      <div
        v-else-if="selectedDate && filteredClasses.length === 0"
        class="info-text"
      >
        <p>No classes available on {{ formatDate(selectedDate) }}.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import Navbar from './Navbar.vue'
import dayjs from 'dayjs'

export default {
  name: 'ViewClass',
  components: {
    Navbar,
  },
  data() {
    return {
      classes: [], // All classes from the API
      loading: false,
      error: null,
      selectedDate: dayjs().format('YYYY-MM-DD'), // Set default to today
    }
  },
  computed: {
    filteredClasses() {
      // Filter classes by selectedDate and sort by time
      if (!this.selectedDate) return []
      return this.classes
        .filter(classItem =>
          dayjs(classItem.classDate).isSame(this.selectedDate, 'day'),
        )
        .sort((a, b) => this.compareTime(a.classTime, b.classTime)) // Sort using compareTime method
    },
  },
  methods: {
    async fetchClasses() {
      this.loading = true
      const token = localStorage.getItem('token')

      if (!token) {
        this.error = 'Authentication token is missing. Please log in.'
        this.loading = false
        return
      }

      try {
        const response = await axios.get(
          'http://localhost:8081/api/v1/class/getClasses',
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        )
        this.classes = response.data
      } catch (err) {
        this.error = 'Failed to fetch classes. Please try again later.'
      } finally {
        this.loading = false
      }
    },
    // Compare time strings like '12:00' and '13:30'
    compareTime(timeA, timeB) {
      const [hoursA, minutesA] = timeA.split(':').map(Number)
      const [hoursB, minutesB] = timeB.split(':').map(Number)
      return hoursA === hoursB ? minutesA - minutesB : hoursA - hoursB
    },
    // Change date by adding or subtracting days
    changeDate(days) {
      this.selectedDate = dayjs(this.selectedDate)
        .add(days, 'day')
        .format('YYYY-MM-DD')
    },
    // Format date using Day.js
    formatDate(dateString) {
      return dayjs(dateString).format('dddd, D MMMM YYYY')
    },
  },
  created() {
    this.fetchClasses()
  },
}
</script>

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 800px;
  margin: auto;
  text-align: center;
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  margin-top: 40px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 600;
  color: #000;
  margin-bottom: 20px;
}

.welcome-text {
  font-size: 1.25rem;
  color: #555;
  margin-bottom: 40px;
}

.custom-date-picker-container {
  margin-bottom: 20px;
}

.custom-date-picker {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f8ff;
  padding: 10px;
  border-radius: 8px;
}

.custom-date-picker span {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0 15px;
}

.date-buttons {
  display: flex;
  align-items: center;
}

.date-buttons button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0 10px;
  transition: color 0.3s ease;
}

.date-buttons button:hover {
  color: #ff4500;
}

.schedule-container {
  text-align: left;
}

.schedule-list {
  margin-top: 20px;
  border-top: 1px solid #ddd;
  padding-top: 10px;
}

.class-item {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.class-item p {
  font-size: 1rem;
  margin: 5px 0;
}

.details-button {
  padding: 10px 20px;
  background-color: #ff4500;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s ease;
  margin-top: 10px;
}

.details-button:hover {
  background-color: #e03b00;
}
</style>
