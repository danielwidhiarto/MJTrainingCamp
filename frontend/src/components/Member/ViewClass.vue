<template>
  <div>
    <Navbar />
    <div class="container">
      <h1 class="page-title">View Class</h1>
      <p class="welcome-text">
        Welcome to your View Class page! Choose a date to view available
        classes.
      </p>

      <!-- Calendar Section -->
      <vue-cal
        :events="calendarEvents"
        @cell-click="handleDateClick"
        class="calendar-view"
        :hide-weekdays="false"
        active-view="month"
        :disable-views="['years', 'year', 'week', 'day']"
        :time="false"
        default-view="month"
        events-count-on-year-view
      />

      <!-- If no date is selected -->
      <div v-if="!selectedDate" class="info-text">
        <p>Select a date to see available classes.</p>
      </div>

      <!-- Class Listing Section (filtered by selectedDate) -->
      <div
        v-if="selectedDate && filteredClasses.length > 0"
        class="card-container"
      >
        <div
          class="class-card"
          v-for="classItem in filteredClasses"
          :key="classItem.id"
        >
          <h2>{{ classItem.className }}</h2>
          <p><strong>Date:</strong> {{ formatDate(classItem.classDate) }}</p>
          <p><strong>Time:</strong> {{ classItem.classTime }}</p>
          <p>
            <strong>Capacity:</strong> {{ classItem.classCapasity }} students
          </p>
          <button class="details-button">Join Class</button>
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
import vueCal from 'vue-cal' // Calendar component import
import dayjs from 'dayjs' // Import Day.js for date formatting
import 'vue-cal/dist/vuecal.css' // Import vue-cal CSS

export default {
  name: 'ViewClass',
  components: {
    Navbar,
    vueCal, // Register vue-cal component
  },
  data() {
    return {
      classes: [], // All classes from the API
      loading: false,
      error: null,
      selectedDate: null, // Date selected in the calendar
      calendarEvents: [], // Events to show in the calendar
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

        // Populate calendar events with dots to indicate available classes
        this.calendarEvents = this.classes.map(classItem => ({
          start: classItem.classDate,
          end: classItem.classDate,
          title: classItem.className,
          class: 'class-available-count', // Add a class for the count indicator
        }))
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
    // Handle date click on calendar
    handleDateClick(day) {
      this.selectedDate = day
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
  max-width: 1200px;
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

.loading-text,
.error-text,
.info-text {
  font-size: 1.2rem;
  color: #ff4500;
}

.card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.class-card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
  width: calc(33% - 20px);
  text-align: left;
  transition:
    transform 0.3s,
    box-shadow 0.3s;
}

.class-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.1);
}

.class-card h2 {
  font-size: 1.5rem;
  color: #000;
  margin-bottom: 10px;
}

.class-card p {
  font-size: 1rem;
  color: #555;
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

/* Calendar styling */
.calendar-view {
  margin-bottom: 40px;
}

/* Style for the event count indicator */
.vuecal__cell-events-count {
  background-color: #ff4500;
  color: white;
  border-radius: 50%;
  padding: 5px 10px;
  font-size: 0.9rem;
  font-weight: bold;
  min-width: 25px;
  text-align: center;
  margin-top: 5px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .class-card {
    width: calc(50% - 20px);
  }

  .page-title {
    font-size: 2rem;
  }

  .welcome-text {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .class-card {
    width: 100%;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .welcome-text {
    font-size: 0.9rem;
  }
}
</style>
