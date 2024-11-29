<template>
  <div>
    <LazyNavbar />
    <div class="container">
      <h1>View Class</h1>
      <p class="welcome-text">
        Welcome to your View Class page! Use the date selector to view the
        schedule for a selected day.
      </p>

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
          <!-- Orange Dot Indicator -->
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
        class="schedule-container"
      >
        <h2>Schedule for {{ formatDate(selectedDate) }}:</h2>
        <div class="schedule-list">
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

            <!-- Navigate to Class Detail -->
            <button
              class="details-button"
              @click="goToClassDetail(classItem.idClass)"
              aria-label="Join {{ classItem.className }} Class"
            >
              Join Class
            </button>
          </div>
        </div>
      </div>

      <!-- No Classes Available Message -->
      <div
        v-else-if="!loading && selectedDate && filteredClasses.length === 0"
        class="info-text"
      >
        <p>No classes available on {{ formatDate(selectedDate) }}.</p>
      </div>

      <!-- Error Message -->
      <div v-if="error" class="error-text" role="alert">
        <p>{{ error }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { defineAsyncComponent } from 'vue'
import dayjs from 'dayjs'

// Lazy load Navbar component
const LazyNavbar = defineAsyncComponent(() => import('./Navbar.vue'))

export default {
  name: 'ViewClass',
  components: { LazyNavbar },
  data() {
    const today = dayjs()
    return {
      classes: [],
      loading: false,
      error: null,
      selectedDate: today.format('YYYY-MM-DD'),
      todayDate: today.format('YYYY-MM-DD'),
      availableDates: [],
      debouncedSelectDate: null, // Placeholder for the debounced method
    }
  },
  computed: {
    filteredClasses() {
      if (!this.selectedDate) return []
      return this.classes
        .filter(classItem =>
          dayjs(classItem.classDate).isSame(this.selectedDate, 'day'),
        )
        .sort((a, b) => this.compareTime(a.classTime, b.classTime))
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
          'http://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/class/getClasses',
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        this.classes = response.data
        this.markDatesWithClasses()
      } catch (err) {
        console.error(err)
        this.error = 'Failed to fetch classes. Please try again later.'
      } finally {
        this.loading = false
      }
    },
    selectDate(date) {
      this.selectedDate = date
      this.scrollToSelectedDate()
    },
    formatDate(date) {
      return dayjs(date).format('dddd, D MMMM YYYY')
    },
    compareTime(timeA, timeB) {
      const [hoursA, minutesA] = timeA.split(':').map(Number)
      const [hoursB, minutesB] = timeB.split(':').map(Number)
      return hoursA === hoursB ? minutesA - minutesB : hoursA - hoursB
    },
    goToClassDetail(idClass) {
      this.$router.push({ name: 'ClassDetail', params: { id: idClass } })
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
          disabled:
            currentDate.isBefore(today, 'day') &&
            !currentDate.isSame(today, 'day'),
          hasClass: false, // Initialize hasClass as false
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
    scrollToSelectedDate() {
      // Find the date item element
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
    goToToday() {
      const today = dayjs()
      this.selectedDate = today.format('YYYY-MM-DD')
      this.$nextTick(() => {
        this.scrollToSelectedDate()
      })
    },
    // Debounce function to limit the rate at which a function can fire.
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
    this.generateAvailableDates()
    this.fetchClasses()
    // Scroll to the selected date after initial render
    this.$nextTick(() => {
      this.scrollToSelectedDate()
    })
    // Initialize the debounced selectDate method
    this.debouncedSelectDate = this.debounce(this.selectDate, 300)
  },
}
</script>

<style scoped>
/* General Layout */
.container {
  padding: 40px 20px;
  max-width: 1200px; /* Increased to match Choose Your Plan */
  margin: 40px auto 0 auto; /* Added top margin */
  text-align: center;
  background-color: #fff; /* White background */
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1); /* Similar shadow */
  border-radius: 16px; /* Rounded corners */
}

/* Headings */
h1 {
  font-size: 3rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #000; /* Black text to match the logo's accent */
}

h2 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #000;
}

/* Paragraphs */
.welcome-text {
  font-size: 1.25rem;
  color: #555;
  margin-bottom: 40px;
}

/* Horizontal Date Scroll */
.horizontal-date-scroll {
  display: flex;
  overflow-x: auto;
  margin-bottom: 30px;
  padding: 10px 0;
  border-radius: 16px; /* Rounded corners */
  background-color: #ffffff;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1); /* Similar shadow */
  scroll-behavior: smooth;
}

.horizontal-date-scroll::-webkit-scrollbar {
  height: 8px;
}

.horizontal-date-scroll::-webkit-scrollbar-thumb {
  background-color: #ff4500;
  border-radius: 4px;
}

/* Date Item Styles */
.date-item {
  flex: 0 0 auto;
  width: 100px; /* Reduced width for smaller date picker */
  text-align: center;
  padding: 15px 8px; /* Reduced padding */
  margin: 0 8px; /* Adjusted margin for spacing */
  cursor: pointer;
  border-radius: 16px; /* Rounded corners */
  background-color: #fff; /* White background */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* Subtle shadow */
  transition:
    background-color 0.3s,
    transform 0.3s,
    box-shadow 0.3s;
  border: 2px solid transparent; /* Default border */
}

.date-item:hover {
  background-color: #ffe5d9;
  transform: translateY(-4px);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
}

.date-item.selected {
  background-color: #ff4500;
  color: #fff;
  font-weight: bold;
  border-color: #ff4500; /* Matching border */
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

/* Date Content */
.day-name {
  font-size: 1rem; /* Increased font size */
  margin-bottom: 8px;
  color: #555;
}

.day-number {
  font-size: 1.5rem; /* Increased font size */
  font-weight: 600;
}

.month-name {
  font-size: 0.9rem; /* Adjusted font size */
  color: #777;
  margin-top: 5px;
}

/* Orange Dot Indicator */
.class-dot {
  width: 8px;
  height: 8px;
  background-color: #ff4500; /* Orange color */
  border-radius: 50%;
  margin: 5px auto 0 auto; /* Center the dot */
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

/* Error Message */
.error-text {
  text-align: center;
  padding: 30px;
  font-size: 1.3rem;
  font-weight: 600;
  color: #ff4500;
}

/* No Classes Available Message */
.info-text {
  text-align: center;
  padding: 30px;
  font-size: 1.25rem; /* Increased font size */
  color: #555;
}

/* Schedule Container */
.schedule-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 25px;
  border-radius: 16px; /* Rounded corners */
  background-color: #fefefe;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1); /* Similar shadow */
  margin-top: 30px; /* Added margin */
}

.schedule-container h2 {
  font-size: 2rem; /* Increased font size */
  margin-bottom: 20px;
  color: #000; /* Ensuring consistent color */
}

/* Schedule List */
.schedule-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.class-item {
  margin: 15px;
  padding: 25px; /* Increased padding */
  width: 320px; /* Increased width for better visibility */
  border-radius: 16px; /* Rounded corners */
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1); /* Similar shadow */
  transition:
    transform 0.3s,
    box-shadow 0.3s;
}

.class-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.15);
}

.class-item p {
  margin: 10px 0;
  font-size: 1.2rem; /* Increased font size */
  color: #444;
}

.class-item p strong {
  font-size: 1.2rem; /* Ensure bold text is also larger */
}

/* Details Button */
.details-button {
  background-color: #ff4500; /* Matching the BuyMembership button color */
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 14px 20px; /* Increased padding for a larger button */
  cursor: pointer;
  font-size: 1.1rem; /* Increased font size */
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.details-button:hover {
  background-color: #e03b00; /* Darker shade on hover */
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
