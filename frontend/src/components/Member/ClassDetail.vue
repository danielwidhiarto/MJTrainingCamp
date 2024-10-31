<template>
  <div>
    <Navbar />
    <div class="container">
      <button class="back-button" @click="goBack">← Back to Classes</button>

      <!-- Loading Indicator -->
      <div v-if="loading" class="loading-indicator">
        Loading class details...
      </div>

      <!-- Error Message -->
      <div v-if="error" class="error-text">
        <p>{{ error }}</p>
      </div>

      <!-- Class Details -->
      <div v-if="!loading && classDetail" class="class-detail-container">
        <h1>{{ classDetail.className || 'No Name Provided' }}</h1>
        <p><strong>Date:</strong> {{ formatDate(classDetail.classDate) }}</p>
        <p><strong>Time:</strong> {{ classDetail.classTime }}</p>
        <p>
          <strong>Capacity:</strong> {{ classDetail.classCapacity }} students
        </p>
        <p v-if="classDetail.classRequirement">
          <strong>Requirements:</strong> {{ classDetail.classRequirement }}
        </p>
        <p v-if="classDetail.trainerDetail">
          <strong>Trainer:</strong>
          {{ classDetail.trainerDetail.name || 'N/A' }}
        </p>

        <h2>Members Enrolled:</h2>
        <ul>
          <li v-for="member in classDetail.classMembers" :key="member.idUser">
            User ID: {{ member.idUser }}
          </li>
        </ul>

        <!-- Book Class Button -->
        <button
          class="book-button"
          @click="bookClass"
          :disabled="bookingLoading || bookingSuccess"
        >
          {{
            bookingLoading
              ? 'Booking...'
              : bookingSuccess
                ? 'Booked'
                : 'Book Class'
          }}
        </button>

        <!-- Booking Success Message -->
        <div v-if="bookingSuccess" class="success-message">
          Successfully booked the class!
        </div>

        <!-- Booking Error Message -->
        <div v-if="bookingError" class="booking-error-text">
          {{ bookingError }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import axios from 'axios'
import Navbar from './Navbar.vue'
import dayjs from 'dayjs'

export default {
  name: 'ClassDetail',
  components: { Navbar },
  data() {
    return {
      classDetail: null,
      loading: false,
      error: null,
      // Booking States
      bookingLoading: false,
      bookingError: null,
      bookingSuccess: false,
    }
  },
  methods: {
    async fetchClassDetail() {
      this.loading = true
      const token = localStorage.getItem('token')
      const classId = this.$route.params.id

      if (!token) {
        this.error = 'Authentication token is missing. Please log in.'
        this.loading = false
        return
      }

      try {
        const response = await axios.get(
          `http://localhost:8081/api/v1/class/getClasses?idClass=${classId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        if (response.data && response.data.length > 0) {
          this.classDetail = response.data[0]
        } else {
          this.error = 'Class details not found.'
        }
      } catch (err) {
        this.error = 'Failed to fetch class details. Please try again later.'
      } finally {
        this.loading = false
      }
    },
    formatDate(date) {
      return dayjs(date).format('dddd, D MMMM YYYY')
    },
    goBack() {
      this.$router.back()
    },
    async bookClass() {
      // Reset previous booking states
      this.bookingError = null
      this.bookingSuccess = false
      this.bookingLoading = true

      const token = localStorage.getItem('token')
      const classId = this.classDetail.idClass

      if (!token) {
        this.bookingError = 'Authentication token is missing. Please log in.'
        this.bookingLoading = false
        return
      }

      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/class/book',
          {
            idClass: classId,
            type: 'member',
            token: token, // Including token as per provided CURL command
          },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          },
        )

        // Assuming a successful booking returns a status or message
        if (response.status === 200) {
          this.bookingSuccess = true
          // Optionally, update the class members list
          // this.classDetail.classMembers.push({ idUser: /* User ID */ }) // Replace with actual user ID if available
        } else {
          this.bookingError = 'Failed to book the class. Please try again.'
        }
      } catch (err) {
        // Handle specific error messages if available
        if (err.response && err.response.data && err.response.data.message) {
          this.bookingError = err.response.data.message
        } else {
          this.bookingError = 'An unexpected error occurred. Please try again.'
        }
      } finally {
        this.bookingLoading = false
      }
    },
  },
  mounted() {
    this.fetchClassDetail()
  },
}
</script>
<style scoped>
/* General Layout */
.container {
  padding: 40px 20px;
  max-width: 800px; /* Adjusted for detailed view */
  margin: 40px auto 0 auto; /* Centered */
  text-align: left; /* Left-aligned text for readability */
  background-color: #fff; /* White background */
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1); /* Subtle shadow */
  border-radius: 16px; /* Rounded corners */
  font-family: 'Arial, sans-serif'; /* Consistent font family */
}

.back-button {
  background-color: transparent;
  border: none;
  color: #ff4500; /* Matching color scheme */
  font-size: 1rem;
  cursor: pointer;
  margin-bottom: 20px;
}

.back-button:hover {
  text-decoration: underline;
}

.loading-indicator {
  text-align: center;
  padding: 30px;
  font-size: 1.3rem;
  font-weight: 600;
  color: #666;
}

.error-text {
  text-align: center;
  padding: 30px;
  font-size: 1.3rem;
  font-weight: 600;
  color: #ff4500;
}

.class-detail-container {
  padding: 20px;
  border-radius: 16px; /* Rounded corners */
  background-color: #fefefe;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05); /* Light shadow */
}

.class-detail-container h1 {
  font-size: 2.5rem;
  color: #000;
  margin-bottom: 20px;
}

.class-detail-container p {
  font-size: 1.2rem;
  color: #333;
  margin: 10px 0;
}

.class-detail-container h2 {
  font-size: 2rem;
  color: #333;
  margin-top: 30px;
  margin-bottom: 10px;
}

.class-detail-container ul {
  list-style-type: disc;
  padding-left: 20px;
}

.class-detail-container ul li {
  font-size: 1.1rem;
  color: #444;
}

/* Book Button Styles */
.book-button {
  background-color: #28a745; /* Green color for booking */
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: background-color 0.3s ease;
  margin-top: 20px;
}

.book-button:hover {
  background-color: #218838; /* Darker green on hover */
}

.book-button:disabled {
  background-color: #6c757d; /* Gray color when disabled */
  cursor: not-allowed;
}

.success-message {
  margin-top: 15px;
  padding: 10px;
  background-color: #d4edda; /* Light green background */
  color: #155724; /* Dark green text */
  border: 1px solid #c3e6cb;
  border-radius: 4px;
}

.booking-error-text {
  margin-top: 15px;
  padding: 10px;
  background-color: #f8d7da; /* Light red background */
  color: #721c24; /* Dark red text */
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .container {
    padding: 30px 15px;
  }

  .class-detail-container h1 {
    font-size: 2rem;
  }

  .class-detail-container p {
    font-size: 1.1rem;
  }

  .class-detail-container h2 {
    font-size: 1.8rem;
  }

  .class-detail-container ul li {
    font-size: 1rem;
  }

  .book-button {
    padding: 10px 18px;
    font-size: 0.95rem;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 20px 10px;
  }

  .class-detail-container h1 {
    font-size: 1.8rem;
  }

  .class-detail-container p {
    font-size: 1rem;
  }

  .class-detail-container h2 {
    font-size: 1.6rem;
  }

  .class-detail-container ul li {
    font-size: 0.95rem;
  }

  .book-button {
    padding: 8px 14px;
    font-size: 0.9rem;
  }
}
</style>
