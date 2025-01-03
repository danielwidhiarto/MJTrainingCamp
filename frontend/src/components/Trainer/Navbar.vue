<template>
  <nav class="navbar">
    <div class="navbar-left">
      <router-link to="/trainer/dashboard">
        <img src="../../assets/logo.jpg" alt="Logo" class="logo" />
      </router-link>
    </div>

    <!-- Profile icon redirects to Profile Page -->
    <div class="navbar-right">
      <router-link to="/trainer/profile-page">
        <img
          src="../../assets/profile-icon.png"
          alt="Profile"
          class="profile-icon"
        />
      </router-link>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'TrainerNavbar',
}
</script>

<style scoped>
/* General Navbar Styling */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: black; /* Black background */
  color: #ffffff;
  position: relative;
  z-index: 10;
}

.navbar-left .logo {
  height: 45px; /* Adjust logo size */
  object-fit: contain;
}

.profile-icon {
  height: 45px;
}

.profile-icon:hover {
  opacity: 0.8; /* Slight opacity change on hover */
}

/* Responsive Styles */
@media (max-width: 768px) {
  .navbar-left .logo {
    height: 35px; /* Adjust logo size for smaller screens */
  }

  .profile-icon {
    height: 35px; /* Adjust icon size for smaller screens */
  }
}
</style>
