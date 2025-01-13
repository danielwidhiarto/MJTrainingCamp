<template>
  <div>
    <Navbar />
    <div class="container mt-5">
      <h1 class="page-title text-center">Trainer Profile</h1>

      <!-- Profile Card -->
      <div class="profile-card">
        <div class="profile-header">
          <img
            src="@/assets/TrainerProfile.png"
            alt="Admin Profile Picture"
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
        </div>
        <button class="logout-button" @click="logout">Logout</button>
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
        phone: 'Not provided',
        profilePicture: 'https://via.placeholder.com/150', // Placeholder image, replace with actual
        role: 'Administrator',
        joinDate: '2023-01-15',
      },
    }
  },
  created() {
    // Fetch user data from localStorage
    this.user.name = localStorage.getItem('name') || 'Admin 1'
    this.user.email = localStorage.getItem('email') || 'admin@example.com'
    this.user.phone = localStorage.getItem('phone') || 'Not provided'
    this.user.joinDate =
      localStorage.getItem('registrationDate') || '2023-01-15'
    this.user.profilePicture =
      localStorage.getItem('profilePicture') ||
      'https://via.placeholder.com/150'
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
        this.$router.push('/') // Redirect to home page
      })
    },
  },
}
</script>

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 900px;
  margin: auto;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #ff4500; /* Orange-red color */
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

.profile-role {
  font-size: 1.2rem;
  color: #777;
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

.card {
  margin-top: 50px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 16px;
}

.card-title {
  font-size: 1.8rem;
  color: #ff4500;
  margin-bottom: 20px;
}

.settings-buttons {
  display: flex;
  flex-direction: column;
}

.settings-buttons .btn {
  width: 100%;
  font-size: 1rem;
  padding: 12px 20px;
  margin-bottom: 15px;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
}

.settings-buttons .btn:hover {
  background-color: #ff4500;
  color: #fff;
}

@media (min-width: 768px) {
  .profile-header {
    flex-direction: row;
    align-items: center;
    text-align: left;
  }

  .user-info {
    margin-left: 30px;
  }

  .settings-buttons {
    flex-direction: row;
    gap: 20px;
  }

  .settings-buttons .btn {
    width: auto;
    flex: 1;
    margin-bottom: 0;
  }
}
</style>
