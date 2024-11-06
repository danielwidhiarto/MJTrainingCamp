<template>
  <div>
    <Navbar />
    <div class="container">
      <h1 class="page-title">Profile Page</h1>

      <!-- Profile Card -->
      <div class="profile-card">
        <div class="profile-header">
          <img
            :src="user.profilePicture"
            alt="User Profile Picture"
            class="profile-picture"
          />
          <div class="user-info">
            <h2 class="user-name">{{ user.name }}</h2>
          </div>
        </div>
        <div class="profile-details">
          <p class="profile-info"><strong>Email:</strong> {{ user.email }}</p>
          <p class="profile-info"><strong>Phone:</strong> {{ user.phone }}</p>
          <p class="profile-info">
            <strong>Joined:</strong> {{ formatDate(user.joinDate) }}
          </p>
          <button class="logout-button" @click="logout">Logout</button>
        </div>
      </div>

      <!-- Membership and Visits Section -->
      <div class="membership-visits-section">
        <!-- Current Memberships -->
        <div class="card membership-card" v-if="currentMemberships.length > 0">
          <div class="card-body">
            <h3 class="card-title">Current Memberships</h3>
            <ul class="membership-list">
              <li
                v-for="membership in currentMemberships"
                :key="membership.idMember"
              >
                <p>
                  <strong>Duration:</strong> {{ membership.duration }} Months
                </p>
                <p>
                  <strong>Starts On:</strong>
                  {{ formatDate(membership.startDate) }}
                </p>
                <p>
                  <strong>Ends On:</strong> {{ formatDate(membership.endDate) }}
                </p>
              </li>
            </ul>
          </div>
        </div>
        <div class="card membership-card" v-else>
          <div class="card-body">
            <h3 class="card-title">Current Memberships</h3>
            <p>You do not have any active memberships.</p>
          </div>
        </div>

        <!-- Upcoming Memberships -->
        <div class="card visits-card" v-if="upcomingMemberships.length > 0">
          <div class="card-body">
            <h3 class="card-title">Upcoming Memberships</h3>
            <ul class="membership-list">
              <li
                v-for="membership in upcomingMemberships"
                :key="membership.idMember"
              >
                <p>
                  <strong>Duration:</strong> {{ membership.duration }} Months
                </p>
                <p>
                  <strong>Starts On:</strong>
                  {{ formatDate(membership.startDate) }}
                </p>
                <p>
                  <strong>Ends On:</strong> {{ formatDate(membership.endDate) }}
                </p>
              </li>
            </ul>
          </div>
        </div>
        <div class="card visits-card" v-else>
          <div class="card-body">
            <h3 class="card-title">Upcoming Memberships</h3>
            <p>You do not have any upcoming memberships.</p>
          </div>
        </div>
      </div>

      <!-- Visits Left Section -->
      <div class="card mt-5">
        <div class="card-body">
          <h3 class="card-title">Visits Left</h3>
          <p><strong>Remaining Visits:</strong> {{ visitsLeft }}</p>
          <button class="btn btn-secondary">View Visit History</button>
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
          <div class="settings-buttons">
            <button class="btn btn-outline-secondary mt-2">
              Change Password
            </button>
            <button class="btn btn-outline-secondary mt-2">
              Manage Notifications
            </button>
          </div>
        </div>
      </div>

      <!-- Preferences Section -->
      <div class="card mt-5">
        <div class="card-body">
          <h3 class="card-title">Preferences</h3>
          <div class="preferences">
            <label class="preference-item">
              <input type="checkbox" v-model="userPreferences.darkMode" />
              Dark Mode
            </label>
            <label class="preference-item">
              <input type="checkbox" v-model="userPreferences.emailUpdates" />
              Receive Email Updates
            </label>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Swal from 'sweetalert2' // Import SweetAlert2
import Navbar from './Navbar.vue' // Import the Navbar component
import dayjs from 'dayjs' // Import Day.js for date formatting
import axios from 'axios' // Import Axios for HTTP requests

export default {
  name: 'ProfilePage',
  components: {
    Navbar,
  },
  data() {
    return {
      user: {
        name: '',
        email: '',
        phone: '',
        profilePicture: 'https://via.placeholder.com/150', // Placeholder image, replace with actual if needed
        joinDate: '',
      },
      userPreferences: {
        darkMode: false,
        emailUpdates: true,
      },
      currentMemberships: [], // Array to store current memberships
      upcomingMemberships: [], // Array to store upcoming memberships
      visitsLeft: 0, // To store number of visits left
    }
  },
  created() {
    // Fetch user data from localStorage
    this.user.name = localStorage.getItem('name') || 'User Name'
    this.user.email = localStorage.getItem('email') || 'user@example.com'
    this.user.phone = localStorage.getItem('phone') || 'Not provided'
    this.user.joinDate =
      localStorage.getItem('registrationDate') || '2024-11-01'
    this.user.profilePicture =
      localStorage.getItem('profilePicture') ||
      'https://via.placeholder.com/150'

    // Fetch membership data from API
    this.checkMembership()
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
        localStorage.removeItem('name')
        localStorage.removeItem('email')
        localStorage.removeItem('phone')
        localStorage.removeItem('registrationDate')
        localStorage.removeItem('profilePicture')
        this.$router.push('/') // Redirect to the home page
      })
    },
    async checkMembership() {
      const token = localStorage.getItem('token')
      if (!token) {
        console.warn(
          'No token found. Membership information cannot be fetched.',
        )
        return
      }

      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/class/checkMembership',
          {
            token: token,
          },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          },
        )

        const data = response.data

        // Check if memberships exist
        if (data.memberships && data.memberships.length > 0) {
          const today = dayjs()

          data.memberships.forEach(membership => {
            const startDate = dayjs(membership.startDate)
            const endDate = dayjs(membership.endDate)

            if (endDate.isAfter(today) && startDate.isBefore(today)) {
              // Current Membership
              this.currentMemberships.push({
                idMember: membership.idMember,
                duration: membership.duration, // Duration in months
                endDate: membership.endDate, // End date as string
                packageName: `Package ${membership.idMember}`, // Replace with actual package name if available
              })
            } else if (startDate.isAfter(today)) {
              // Upcoming Membership
              this.upcomingMemberships.push({
                idMember: membership.idMember,
                duration: membership.duration,
                startDate: membership.startDate,
                endDate: membership.endDate,
                packageName: `Package ${membership.idMember}`, // Replace with actual package name if available
              })
            }
          })

          // Calculate visits left
          if (data.visitDetails && data.visitDetails.length > 0) {
            // Sum up all visits left from visitDetails
            this.visitsLeft = data.visitDetails.reduce((acc, visit) => {
              return acc + (visit.visitNumber - visit.visitUsed)
            }, 0)
          } else {
            this.visitsLeft = 0
          }
        } else {
          this.currentMemberships = []
          this.upcomingMemberships = []
          this.visitsLeft = 0
        }
      } catch (error) {
        console.error('Error fetching membership data:', error)
        Swal.fire({
          title: 'Error',
          text: 'Failed to fetch membership information.',
          icon: 'error',
          confirmButtonColor: '#ff4500',
        })
      }
    },
  },
}
</script>

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #ff4500; /* Orange-red color */
  text-align: center;
  margin-bottom: 40px;
}

.profile-card {
  background-color: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.profile-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.profile-header {
  display: flex;
  align-items: center;
  flex-direction: column;
}

.profile-picture {
  border-radius: 50%;
  width: 160px;
  height: 160px;
  object-fit: cover;
  margin-bottom: 20px;
  border: 4px solid #ff4500;
}

.user-info {
  text-align: center;
}

.user-name {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 5px;
  color: #333;
}

.profile-details {
  margin-top: 20px;
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
  padding: 12px 25px;
  cursor: pointer;
  font-size: 16px;
  transition:
    background-color 0.3s ease,
    transform 0.2s ease;
  margin-top: 20px;
}

.logout-button:hover {
  background-color: #e03b00;
  transform: scale(1.05);
}

/* Membership and Visits Section */
.membership-visits-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 50px;
}

.membership-card,
.visits-card {
  flex: 1;
}

/* Responsive Layout */
@media (min-width: 768px) {
  .membership-visits-section {
    flex-direction: row;
  }

  .membership-card,
  .visits-card {
    width: 48%;
  }
}

/* Card Styles */
.card {
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
}

.card-title {
  font-size: 1.8rem;
  color: #ff4500;
  margin-bottom: 20px;
}

.membership-list {
  list-style-type: none;
  padding-left: 0;
}

.membership-list li {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.membership-list li:last-child {
  border-bottom: none;
}

.visits-card .card-title {
  /* Specific styles for Visits Left card if needed */
}

.activity-list {
  list-style-type: none;
  padding-left: 0;
}

.activity-list li {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.activity-list li:last-child {
  border-bottom: none;
}

/* Settings and Preferences */
.settings-buttons,
.preferences {
  display: flex;
  flex-direction: column;
}

.settings-buttons .btn,
.preferences .preference-item {
  width: 100%;
  font-size: 1rem;
  padding: 12px 20px;
  margin-bottom: 15px;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
}

.settings-buttons .btn:hover,
.preferences .preference-item:hover {
  background-color: #ff4500;
  color: #fff;
}

.preferences .preference-item {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.preferences .preference-item input {
  margin-right: 10px;
}

/* Additional Styling for Buttons */
.btn-primary {
  background-color: #007bff;
  border: none;
  color: #fff;
  border-radius: 8px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  border: none;
  color: #fff;
  border-radius: 8px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-secondary:hover {
  background-color: #5a6268;
}
</style>
