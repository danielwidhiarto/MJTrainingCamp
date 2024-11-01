<template>
  <div>
    <Navbar />
    <div class="container">
      <button class="back-button" @click="$router.back()">
        ← Back to Classes
      </button>

      <!-- Loading Indicator -->
      <div v-if="loading" class="loading-indicator">
        Loading class details...
      </div>

      <!-- Error Message -->
      <div v-if="error" class="error-text">
        <p>{{ error }}</p>
      </div>

      <!-- Class Details -->
      <div v-if="!loading && classDetails" class="class-detail-container">
        <h1>{{ classDetails.className || 'Unnamed Class' }}</h1>

        <div class="class-info">
          <p><strong>Date:</strong> {{ formatDate(classDetails.classDate) }}</p>
          <p><strong>Time:</strong> {{ formatTime(classDetails.classTime) }}</p>
          <p>
            <strong>Capacity:</strong> {{ classDetails.classCapasity }} members
          </p>
          <p v-if="classDetails.classRequirement">
            <strong>Requirements:</strong> {{ classDetails.classRequirement }}
          </p>
          <p v-else><strong>Requirements:</strong> None</p>
        </div>

        <!-- Trainer Details -->
        <div v-if="classDetails.trainerDetail" class="trainer-info">
          <h2>Trainer Information</h2>
          <p>
            <strong>Name:</strong>
            {{ classDetails.trainerDetail.name || 'N/A' }}
          </p>
          <p>
            <strong>Email:</strong>
            {{ classDetails.trainerDetail.email || 'N/A' }}
          </p>
          <!-- Add more trainer details as needed -->
        </div>
        <div v-else class="trainer-info">
          <h2>Trainer Information</h2>
          <p>No trainer assigned to this class.</p>
        </div>

        <!-- Class Members -->
        <div class="members-section">
          <h2>Registered Members ({{ classDetails.classMembers.length }})</h2>
          <ul>
            <li
              v-for="member in classDetails.classMembers"
              :key="member.idUser"
            >
              User ID: {{ member.idUser }}
              <!-- If you have more member details, display them here -->
            </li>
          </ul>
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
      classDetails: null,
      loading: false,
      error: null,
    }
  },
  methods: {
    async fetchClassDetails() {
      this.loading = true
      this.error = null
      this.classDetails = null // Reset previous class details

      const token = localStorage.getItem('token')
      if (!token) {
        this.error = 'Authentication token is missing. Please log in.'
        this.loading = false
        return
      }

      const idClass = this.$route.params.id
      if (!idClass) {
        this.error = 'Class ID is missing from the route.'
        this.loading = false
        return
      }

      try {
        const response = await axios.get(
          `http://localhost:8081/api/v1/class/getClasses?id=${idClass}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )

        if (response.data && response.data.length > 0) {
          this.classDetails = response.data[0]
        } else {
          this.error = 'No class details found for the provided ID.'
        }
      } catch (err) {
        console.error(err)
        this.error =
          err.response?.data?.message ||
          'Failed to fetch class details. Please try again later.'
      } finally {
        this.loading = false
      }
    },
    formatDate(date) {
      return dayjs(date).format('dddd, D MMMM YYYY')
    },
    formatTime(time) {
      return dayjs(time, 'HH:mm:ss').format('hh:mm A')
    },
  },
  mounted() {
    this.fetchClassDetails()
  },
  watch: {
    '$route.params.id': {
      immediate: false,
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.fetchClassDetails()
        }
      },
    },
  },
  beforeRouteUpdate(to, from, next) {
    if (to.params.id !== from.params.id) {
      this.fetchClassDetails()
    }
    next()
  },
}
</script>

<style scoped>
/* General Layout */
.container {
  padding: 40px 20px;
  max-width: 800px;
  margin: 40px auto 0 auto;
  text-align: left;
  background-color: #fff;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
  font-family: 'Arial, sans-serif';
}

/* Back Button */
.back-button {
  background-color: transparent;
  border: none;
  color: #ff4500;
  font-size: 1rem;
  cursor: pointer;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.back-button:hover {
  text-decoration: underline;
}

/* Loading Indicator */
.loading-indicator {
  text-align: center;
  padding: 30px;
  font-size: 1.3rem;
  font-weight: 600;
  color: #666;
}

/* Error Message */
.error-text {
  text-align: center;
  padding: 30px;
  font-size: 1.3rem;
  font-weight: 600;
  color: #ff4500;
}

/* Class Detail Container */
.class-detail-container {
  padding: 20px;
  border-radius: 16px;
  background-color: #f9f9f9;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
}

.class-detail-container h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 20px;
}

.class-info p {
  font-size: 1.2rem;
  color: #555;
  margin: 8px 0;
}

.trainer-info {
  margin-top: 30px;
  padding: 15px;
  border-top: 1px solid #ddd;
}

.trainer-info h2 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 10px;
}

.trainer-info p {
  font-size: 1.1rem;
  color: #555;
  margin: 5px 0;
}

.members-section {
  margin-top: 30px;
}

.members-section h2 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 15px;
}

.members-section ul {
  list-style-type: disc;
  padding-left: 20px;
}

.members-section li {
  font-size: 1.1rem;
  color: #555;
  margin: 5px 0;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .container {
    padding: 30px 15px;
  }

  .class-detail-container h1 {
    font-size: 2rem;
  }

  .class-info p,
  .trainer-info p,
  .members-section li {
    font-size: 1rem;
  }

  .trainer-info h2,
  .members-section h2 {
    font-size: 1.5rem;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 20px 10px;
  }

  .class-detail-container h1 {
    font-size: 1.8rem;
  }

  .class-info p,
  .trainer-info p,
  .members-section li {
    font-size: 0.95rem;
  }

  .trainer-info h2,
  .members-section h2 {
    font-size: 1.3rem;
  }
}
</style>
