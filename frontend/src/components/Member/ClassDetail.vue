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
}
</style>
