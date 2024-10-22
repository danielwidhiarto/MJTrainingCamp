<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>ViewClass</h1>
      <p>Welcome to your ViewClass page!</p>
      <div v-if="loading">Loading classes...</div>
      <div v-else-if="error">{{ error }}</div>
      <div v-else>
        <table>
          <thead>
            <tr>
              <th>Class Name</th>
              <th>Date</th>
              <th>Time</th>
              <th>Capacity</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="classItem in classes" :key="classItem.id">
              <td>{{ classItem.className }}</td>
              <td>{{ classItem.classDate }}</td>
              <td>{{ classItem.classTime }}</td>
              <td>{{ classItem.classCapasity }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import Navbar from './Navbar.vue'

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
  },
  created() {
    this.fetchClasses()
  },
}
</script>

<style>
.container {
  padding: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th,
td {
  padding: 10px;
  text-align: left;
  border: 1px solid #ddd;
}

th {
  background-color: #f4f4f4;
}

tr:nth-child(even) {
  background-color: #f9f9f9;
}
</style>
