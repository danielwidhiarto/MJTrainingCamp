<template>
  <div>
    <Navbar />
    <div class="container">
      <h1 class="page-title">Profile Page</h1>

      <div class="profile-card">
        <!-- Profile Section -->
        <img
          :src="user.profilePicture"
          alt="User Profile Picture"
          class="profile-picture"
        />
        <h2 class="user-name">{{ user.name }}</h2>
        <p class="profile-info"><strong>Email:</strong> {{ user.email }}</p>
        <p class="profile-info">
          <strong>Membership:</strong> {{ user.membershipStatus }}
        </p>
        <p class="profile-info">
          <strong>Joined:</strong> {{ formatDate(user.joinDate) }}
        </p>
        <button class="logout-button" @click="logout">Logout</button>
      </div>

      <!-- Membership Plan Section -->
      <div class="card mt-5">
        <div class="card-body">
          <h3 class="card-title">Your Membership Plan</h3>
          <p><strong>Plan:</strong> Gold Member</p>
          <p><strong>Status:</strong> Active until January 31, 2024</p>
          <button class="btn btn-primary">Manage Membership</button>
        </div>
      </div>

      <!-- Activity Summary Section -->
      <div class="card mt-5">
        <div class="card-body">
          <h3 class="card-title">Activity Summary</h3>
          <ul class="activity-list">
            <li><strong>Yoga Class:</strong> Attended on October 1, 2024</li>
            <li>
              <strong>Pilates Class:</strong> Attended on September 28, 2024
            </li>
            <li><strong>CrossFit:</strong> Booked for October 5, 2024</li>
          </ul>
        </div>
      </div>

      <!-- Settings Section -->
      <div class="card mt-5">
        <div class="card-body">
          <h3 class="card-title">Settings</h3>
          <button class="btn btn-outline-secondary mt-2">
            Change Password
          </button>
          <button class="btn btn-outline-secondary mt-2">
            Manage Notifications
          </button>
        </div>
      </div>

      <!-- Preferences Section -->
      <div class="card mt-5">
        <div class="card-body">
          <h3 class="card-title">Preferences</h3>
          <label>
            <input type="checkbox" v-model="userPreferences.darkMode" />
            Dark Mode
          </label>
          <label class="mt-3">
            <input type="checkbox" v-model="userPreferences.emailUpdates" />
            Receive Email Updates
          </label>
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
  name: 'ProfilePage',
  components: {
    Navbar,
  },
  data() {
    return {
      user: {
        name: 'John Doe',
        email: 'johndoe@example.com',
        profilePicture: 'https://via.placeholder.com/150', // Placeholder image, replace with actual
        membershipStatus: 'Active Member',
        joinDate: '2023-01-15',
      },
      userPreferences: {
        darkMode: false,
        emailUpdates: true,
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
        confirmButtonColor: '#ff4500', // Update to orange-red
      }).then(() => {
        // Clear localStorage and redirect
        localStorage.removeItem('token')
        localStorage.removeItem('role')
        localStorage.removeItem('idUser')
        this.$router.push('/') // Redirect to the home page
      })
    },
  },
}
</script>

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 600px;
  margin: 0 auto;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  text-align: center;
  color: #ff4500; /* Orange-red color for title */
  margin-bottom: 20px;
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
  background-color: #e03b00; /* Darker shade on hover */
}

/* Card Styles */
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

/* Settings and Preferences */
label {
  display: block;
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 10px;
}

input[type='checkbox'] {
  margin-right: 10px;
}

button.btn {
  font-size: 1rem;
  padding: 10px 15px;
}
</style>
