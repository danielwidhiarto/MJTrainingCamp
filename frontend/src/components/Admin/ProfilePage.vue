<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Profile Page</h1>
      <div class="profile-card">
        <img
          :src="user.profilePicture"
          alt="Admin Profile Picture"
          class="profile-picture"
        />
        <h2>{{ user.name }}</h2>
        <p><strong>Email:</strong> {{ user.email }}</p>
        <p><strong>Membership:</strong> {{ user.membershipStatus }}</p>
        <p><strong>Joined:</strong> {{ user.joinDate }}</p>
        <button class="logout-button" @click="logout">Logout</button>
      </div>
    </div>
  </div>
</template>

<script>
import Swal from 'sweetalert2' // Import SweetAlert2
import Navbar from './Navbar.vue' // Import the Navbar component

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
    }
  },
  methods: {
    logout() {
      // Notify the user using SweetAlert2
      Swal.fire({
        title: 'Logged Out!',
        text: 'You have been logged out successfully.',
        icon: 'success',
        confirmButtonColor: '#557c56',
      }).then(() => {
        // Once the user presses OK, clear localStorage and redirect
        localStorage.removeItem('token')
        localStorage.removeItem('role')
        localStorage.removeItem('idUser')

        // Redirect to login or home page
        this.$router.push('/login') // Assuming '/login' is your login route
      })
    },
  },
}
</script>

<style scoped>
.container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto; /* Center the profile card */
}

.profile-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 2px 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-picture {
  border-radius: 50%;
  width: 150px;
  height: 150px;
  object-fit: cover;
  margin-bottom: 20px;
}

.logout-button {
  background-color: #557c56;
  color: #ffe5cf;
  border: none;
  border-radius: 5px;
  padding: 10px 15px;
  cursor: pointer;
  font-size: 16px;
}

.logout-button:hover {
  background-color: #446c45;
}
</style>
