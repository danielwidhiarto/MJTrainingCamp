<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Trainer Home</h1>
      <p class="welcome-text">Welcome Coach {{ coachName }}</p>

      <!-- Horizontal Line -->
      <hr class="separator" />

      <!-- Schedule Section -->
      <div class="schedule-container">
        <h2>Schedule</h2>
        <p>Select a date to view your classes.</p>

        <!-- Horizontal Date Scroll -->
        <div
          class="horizontal-date-scroll"
          ref="dateScrollContainer"
          aria-label="Available Dates"
        >
          <div
            v-for="date in availableDates"
            :key="date.date"
            :class="[
              'date-item',
              {
                selected: date.date === selectedDate,
                today: date.date === todayDate,
                disabled: date.disabled,
              },
            ]"
            @click="!date.disabled && debouncedSelectDate(date.date)"
            tabindex="0"
            @keydown.enter.space.prevent="
              !date.disabled && debouncedSelectDate(date.date)
            "
            role="button"
            :aria-pressed="date.date === selectedDate"
            :aria-disabled="date.disabled"
          >
            <div class="day-name">{{ date.dayName }}</div>
            <div class="day-number">{{ date.dayNumber }}</div>
            <div class="month-name">{{ date.monthName }}</div>
            <div
              v-if="date.hasClass"
              class="class-dot"
              aria-label="Class Available"
            ></div>
          </div>
        </div>

        <!-- Loading Indicator -->
        <div v-if="loading" class="loading-indicator" aria-live="polite">
          <div class="spinner"></div>
          <span>Loading classes...</span>
        </div>

        <!-- Display Class Schedule -->
        <div
          v-if="!loading && selectedDate && filteredClasses.length > 0"
          class="schedule-list"
        >
          <h3>Schedule for {{ formatDate(selectedDate) }}</h3>
          <div
            class="class-item"
            v-for="classItem in filteredClasses"
            :key="classItem.idClass"
          >
            <p><strong>Class:</strong> {{ classItem.className }}</p>
            <p><strong>Time:</strong> {{ classItem.classTime }}</p>
            <p>
              <strong>Capacity:</strong> {{ classItem.classCapasity }} members
            </p>
            <p>
              <strong>Requirements:</strong> {{ classItem.classRequirement }}
            </p>
            <p>
              <strong>Trainer:</strong>
              {{ classItem.trainerDetail.trainerName }}
            </p>

            <!-- Members List -->
            <p><strong>Members:</strong></p>
            <ul>
              <li v-for="member in classItem.classMembers" :key="member.idUser">
                {{ member.name }} ({{ member.email }})
              </li>
            </ul>
          </div>
        </div>

        <!-- No Classes Available -->
        <div
          v-else-if="!loading && selectedDate && filteredClasses.length === 0"
          class="no-classes"
        >
          <p>No classes available on {{ formatDate(selectedDate) }}.</p>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="error-text">
          <p>{{ error }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'
import axios from 'axios'
import dayjs from 'dayjs'

export default {
  name: 'TrainerDashboard',
  components: {
    Navbar,
  },
  data() {
    const today = dayjs()
    return {
      coachName: '', // Store coach's name
      classes: [],
      loading: false,
      error: null,
      selectedDate: today.format('YYYY-MM-DD'),
      todayDate: today.format('YYYY-MM-DD'),
      availableDates: [],
      debouncedSelectDate: null, // Placeholder for debounced method
    }
  },
  computed: {
    filteredClasses() {
      if (!this.selectedDate) return []
      return this.classes.filter(classItem =>
        dayjs(classItem.classDate).isSame(this.selectedDate, 'day'),
      )
    },
  },
  methods: {
    async fetchClasses() {
      this.loading = true
      const token = localStorage.getItem('token')

      if (!token) {
        this.error = 'Authentication token is missing. Please log in.'
        this.loading = false
        console.error('Token is missing in localStorage.')
        return
      }

      try {
        // Log the request data
        console.log('Sending API request to fetch trainer schedule:')
        console.log({
          token: token,
        })

        const response = await axios.post(
          'https://mjtrainingcamp.my.id/api/v1/trainer/getschedule',
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

        // Log the response data
        console.log('API Response:', response.data)

        this.classes = response.data // Assign the fetched data
        this.markDatesWithClasses() // Update the calendar with class dates
      } catch (err) {
        // Log the error details
        console.error('Error during API call:')
        console.error('Error message:', err.message)
        console.error(
          'Error response:',
          err.response ? err.response.data : null,
        )

        this.error =
          err.response?.data?.message ||
          'Failed to fetch classes. Please try again later.'
      } finally {
        this.loading = false
      }
    },

    formatDate(date) {
      return dayjs(date).format('dddd, D MMMM YYYY')
    },
    generateAvailableDates() {
      const today = dayjs()
      const endDate = today.add(1, 'month')
      let currentDate = today.startOf('day')
      this.availableDates = []

      while (
        currentDate.isBefore(endDate) ||
        currentDate.isSame(endDate, 'day')
      ) {
        this.availableDates.push({
          date: currentDate.format('YYYY-MM-DD'),
          dayName: currentDate.format('ddd'),
          dayNumber: currentDate.format('D'),
          monthName: currentDate.format('MMM'),
          disabled: currentDate.isBefore(today, 'day'),
          hasClass: false,
        })
        currentDate = currentDate.add(1, 'day')
      }
    },
    markDatesWithClasses() {
      this.availableDates.forEach(dateItem => {
        const hasClass = this.classes.some(classItem =>
          dayjs(classItem.classDate).isSame(dateItem.date, 'day'),
        )
        dateItem.hasClass = hasClass
      })
    },
    loadCoachName() {
      this.coachName = localStorage.getItem('name') || 'Coach' // Default to "Coach" if no name is found
    },
  },
  mounted() {
    this.loadCoachName()
    this.generateAvailableDates()
    this.fetchClasses()
  },
}
</script>

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  text-align: center;
}

h1 {
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 10px;
}

.welcome-text {
  font-size: 1.25rem;
  color: #555;
  margin-bottom: 20px;
}

.separator {
  border: none;
  border-top: 2px solid #ddd;
  margin: 20px 0;
}

.schedule-container {
  margin-top: 20px;
  text-align: left;
}

.horizontal-date-scroll {
  display: flex;
  overflow-x: auto;
  margin-bottom: 20px;
  padding: 10px 0;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.date-item {
  padding: 10px 15px;
  margin: 0 10px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s ease;
}

.date-item:hover {
  transform: translateY(-3px);
}

.date-item.selected {
  background-color: #ff4500;
  color: #fff;
}

.class-item {
  padding: 15px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.error-text {
  color: #ff4500;
}
</style>
