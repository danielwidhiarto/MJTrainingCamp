<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="page-title text-center">Admin Profile</h1>

      <!-- Profile Card -->
      <div class="profile-card">
        <img
          :src="user.profilePicture"
          alt="Admin Profile Picture"
          class="profile-picture"
        />
        <h2 class="user-name">{{ user.name }}</h2>
        <p class="profile-info"><strong>Email:</strong> {{ user.email }}</p>
        <p class="profile-info"><strong>Role:</strong> {{ user.role }}</p>
        <p class="profile-info">
          <strong>Joined:</strong> {{ formatDate(user.joinDate) }}
        </p>
        <button class="logout-button" @click="logout">Logout</button>
      </div>
      <!-- Admin Settings Section -->
      <div class="card mt-5">
        <div class="card-body">
          <h3 class="card-title">Admin Settings</h3>
          <button class="btn btn-outline-secondary mt-2">
            Change Password
          </button>
          <button class="btn btn-outline-secondary mt-2">
            Manage Notifications
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Swal from 'sweetalert2' // Import SweetAlert2
import Navbar from './Navbar.vue' // Import the Navbar component
import dayjs from 'dayjs' // Import Day.js for date formatting

export default {
  name: 'AdminProfilePage',
  components: {
    Navbar,
  },
  data() {
    return {
      user: {
        name: 'John Doe',
        email: 'admin@example.com',
        profilePicture: 'https://via.placeholder.com/150', // Placeholder image, replace with actual
        role: 'Administrator',
        joinDate: '2023-01-15',
      },
    }
  },
  methods: {
    formatDate(dateString) {
      return dayjs(dateString).format('MMMM D, YYYY') // Format date to 'Month Day, Year'
    },
    logout() {
      // Notify the user using SweetAlert2
      Swal.fire({
        title: 'Logged Out!',
        text: 'You have been logged out successfully.',
        icon: 'success',
        confirmButtonColor: '#ff4500', // Orange-red
      }).then(() => {
        // Clear localStorage and redirect
        localStorage.removeItem('token')
        localStorage.removeItem('role')
        localStorage.removeItem('idUser')
        this.$router.push('/') // Redirect to home page
      })
    },
  },
}
</script>

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 800px;
  margin: auto;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #ff4500; /* Orange-red color */
  margin-bottom: 30px;
}

.profile-card {
  background-color: #fff;
  border-radius: 16px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.profile-picture {
  border-radius: 50%;
  width: 150px;
  height: 150px;
  object-fit: cover;
  margin-bottom: 20px;
}

.user-name {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.profile-info {
  font-size: 1.1rem;
  color: #555;
  margin-bottom: 10px;
}

.logout-button {
  background-color: #ff4500; /* Orange-red for logout button */
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.logout-button:hover {
  background-color: #e03b00;
}

.card {
  margin-top: 30px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
}

.card-title {
  font-size: 1.5rem;
  color: #ff4500;
  margin-bottom: 15px;
}

.activity-list {
  list-style-type: none;
  padding-left: 0;
}

.activity-list li {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

button.btn {
  font-size: 1rem;
  padding: 10px 15px;
}
</style>
