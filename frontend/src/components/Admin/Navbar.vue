<template>
  <nav class="navbar">
    <div class="navbar-left">
      <router-link to="/member/dashboard">
        <img src="../../assets/logo.jpg" alt="Logo" class="logo" />
      </router-link>
    </div>

    <!-- Hamburger Menu for Mobile -->
    <div class="navbar-hamburger" @click="toggleMenu">
      <span class="bar"></span>
      <span class="bar"></span>
      <span class="bar"></span>
    </div>

    <!-- Center links for navigation -->
    <div :class="['navbar-center', { 'navbar-center-active': isMenuOpen }]">
      <router-link to="/admin/manage-package">Manage Package</router-link>
      <router-link to="/admin/manage-class">Manage Class</router-link>
      <router-link to="/admin/manage-trainer">Manage Trainer</router-link>
      <router-link to="/admin/verify-payment">Verify Payment</router-link>
      <router-link to="/admin/manage-member">Manage Member</router-link>
    </div>

    <!-- Profile icon redirects to Profile Page -->
    <div class="navbar-right">
      <router-link to="/admin/profile-page">
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
  name: 'Navbar',
  data() {
    return {
      isMenuOpen: false, // Controls the mobile menu state
    }
  },
  methods: {
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen
    },
  },
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
  height: 45px; /* Reduce logo size */
  object-fit: contain;
}

/* Center navigation links */
.navbar-center {
  display: flex;
  gap: 30px;
}

.navbar-center a {
  color: #ff4500; /* Orange-red color */
  font-size: 1rem;
  font-weight: 500;
  text-transform: uppercase;
  text-decoration: none;
  transition: color 0.3s;
}

.navbar-center a:hover {
  color: #e03b00; /* Darker shade on hover */
}

/* Profile icon from Bootstrap */
.profile-icon {
  height: 45px;
}

.profile-icon:hover {
  color: #e03b00; /* Darker shade on hover */
}

/* Hamburger Menu for Mobile */
.navbar-hamburger {
  display: none;
  flex-direction: column;
  cursor: pointer;
  gap: 5px;
}

.navbar-hamburger .bar {
  width: 25px;
  height: 3px;
  background-color: #ff4500;
  transition: background-color 0.3s;
}

.navbar-hamburger:hover .bar {
  background-color: #e03b00;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .navbar-center {
    display: none;
    position: absolute;
    top: 60px;
    left: 0;
    width: 100%;
    background-color: #000;
    flex-direction: column;
    text-align: center;
    gap: 20px;
    padding: 20px 0;
    z-index: 9;
  }

  .navbar-center-active {
    display: flex;
  }

  .navbar-hamburger {
    display: flex;
  }

  .navbar-right {
    display: flex;
  }
}

@media (max-width: 480px) {
  .navbar-left .logo {
    height: 35px; /* Adjust logo size for smaller screens */
  }

  .navbar-center a {
    font-size: 0.9rem;
  }

  .profile-icon {
    font-size: 1.3rem; /* Adjust icon size for smaller screens */
  }
}
</style>
