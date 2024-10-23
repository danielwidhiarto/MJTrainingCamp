<template>
  <div>
    <Navbar />
    <div class="container">
      <h1 class="page-title">View Class</h1>
      <p class="welcome-text">Welcome to your View Class page!</p>

      <div v-if="loading" class="loading-text">Loading classes...</div>
      <div v-else-if="error" class="error-text">{{ error }}</div>
      <div v-else class="card-container">
        <div
          class="class-card"
          v-for="classItem in classes"
          :key="classItem.id"
        >
          <h2>{{ classItem.className }}</h2>
          <p><strong>Date:</strong> {{ formatDate(classItem.classDate) }}</p>
          <p><strong>Time:</strong> {{ classItem.classTime }}</p>
          <p>
            <strong>Capacity:</strong> {{ classItem.classCapasity }} students
          </p>
          <button class="details-button">View Details</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import Navbar from './Navbar.vue'
import dayjs from 'dayjs' // Import Day.js for date formatting

export default {
  name: 'ViewClass',
  components: {
    Navbar,
  },
  data() {
    return {
      classes: [],
      loading: false,
      error: null,
    }
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
/* General container styling */
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

/* Page title and text styling */
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

.loading-text {
  font-size: 1.2rem;
  color: #ff4500;
}

.error-text {
  font-size: 1.2rem;
  color: red;
}

/* Card container styling */
.card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

/* Individual class card styling */
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

/* Title inside the card */
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

/* Button styling */
.details-button {
  display: inline-block;
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
