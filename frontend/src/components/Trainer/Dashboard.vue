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
            <!-- Yellow-Orange Dot for Schedules -->
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
        console.log('Sending API request to fetch trainer schedule:')
        console.log({ token: token })

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

        console.log('API Response:', response.data)

        this.classes = response.data
        this.markDatesWithClasses()
      } catch (err) {
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
    selectDate(date) {
      this.selectedDate = date
      this.scrollToSelectedDate()
    },
    scrollToSelectedDate() {
      const dateElements =
        this.$refs.dateScrollContainer.querySelectorAll('.date-item')
      for (let el of dateElements) {
        if (el.classList.contains('selected')) {
          el.scrollIntoView({
            behavior: 'smooth',
            inline: 'center',
            block: 'nearest',
          })
          break
        }
      }
    },
    debounce(func, wait) {
      let timeout
      return function (...args) {
        const later = () => {
          clearTimeout(timeout)
          func.apply(this, args)
        }
        clearTimeout(timeout)
        timeout = setTimeout(later, wait)
      }
    },
  },
  mounted() {
    this.loadCoachName()
    this.generateAvailableDates()
    this.fetchClasses()

    // Initialize the debounced selectDate method
    this.debouncedSelectDate = this.debounce(this.selectDate, 300)
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
  text-align: center;
}

/* Horizontal Date Scroll */
.horizontal-date-scroll {
  display: flex;
  overflow-x: auto;
  margin-bottom: 20px;
  padding: 10px 0;
  background-color: #f9f9f9;
  border-radius: 8px;
  scroll-behavior: smooth;
}

.date-item {
  flex: 0 0 auto;
  width: 80px; /* Adjusted to match the member version */
  text-align: center;
  padding: 10px 8px;
  margin: 0 6px;
  cursor: pointer;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition:
    background-color 0.3s,
    transform 0.3s,
    box-shadow 0.3s;
  border: 2px solid transparent;
}

.date-item:hover {
  background-color: #ffe5d9;
  transform: translateY(-3px);
}

.date-item.selected {
  background-color: #ff4500;
  color: #fff;
  font-weight: bold;
  border-color: #ff4500;
}

.date-item.today {
  border: 2px solid #007bff;
}

.date-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.date-item.disabled:hover {
  background-color: transparent;
  transform: none;
  box-shadow: none;
}

/* Dot for Schedules */
.class-dot {
  width: 8px;
  height: 8px;
  background-color: #ff4500; /* Match active orange color */
  border-radius: 50%;
  margin: 5px auto 0 auto;
}

/* Schedule List */
.schedule-list {
  display: flex;
  flex-direction: column;
  align-items: center; /* Center horizontally */
  justify-content: center; /* Center vertically */
  min-height: 200px; /* Ensure height for vertical alignment */
  text-align: center; /* Center text */
}

.schedule-list h3 {
  margin-bottom: 20px;
  font-size: 1.5rem;
  font-weight: 600;
  color: #000;
}

/* Class Item */
.class-item {
  display: flex;
  flex-direction: column; /* Stack the elements vertically */
  justify-content: center; /* Center content vertically */
  align-items: center; /* Center content horizontally */
  margin: 15px 0;
  padding: 20px;
  width: 300px;
  border-radius: 16px;
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.3s,
    box-shadow 0.3s;
  text-align: center; /* Center text */
}

.class-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.15);
}

.class-item p {
  margin: 10px 0;
  font-size: 1rem;
  color: #444;
}

.class-item strong {
  font-size: 1rem;
  color: #000;
}

.error-text {
  color: #ff4500;
}

/* Loading Indicator */
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

/* No Classes Available Message */
.no-classes {
  font-size: 1.25rem;
  color: #777;
  margin-top: 20px;
  text-align: center;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .date-item {
    width: 90px; /* Adjusted width for medium screens */
    padding: 12px 6px;
    margin: 0 6px;
  }

  .day-name {
    font-size: 0.95rem;
  }

  .day-number {
    font-size: 1.4rem;
  }

  .month-name {
    font-size: 0.85rem;
  }

  .schedule-container {
    padding: 20px;
  }

  .class-item {
    width: 280px; /* Adjusted width */
    padding: 20px; /* Adjusted padding */
  }

  h1 {
    font-size: 2.5rem;
  }

  h2 {
    font-size: 1.75rem;
  }

  .welcome-text {
    font-size: 1.2rem;
    margin-bottom: 30px;
  }

  .details-button {
    padding: 12px 18px; /* Adjusted padding for medium screens */
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .date-item {
    width: 80px; /* Further adjusted width for small screens */
    padding: 10px 5px;
    margin: 0 5px;
  }

  .day-name {
    font-size: 0.9rem;
  }

  .day-number {
    font-size: 1.2rem;
  }

  .month-name {
    font-size: 0.75rem;
  }

  .schedule-container {
    padding: 15px;
  }

  .class-item {
    width: 100%;
    max-width: 300px;
  }

  h1 {
    font-size: 2rem;
  }

  h2 {
    font-size: 1.5rem;
  }

  .welcome-text {
    font-size: 1rem;
    margin-bottom: 20px;
  }

  .details-button {
    padding: 10px 14px; /* Adjusted padding for small screens */
    font-size: 0.95rem;
  }
}
</style>
