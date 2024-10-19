<template>
  <nav class="navbar">
    <div class="navbar-left">
      <img src="../../assets/logo.jpg" alt="Logo" class="logo" />
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
      <router-link to="/admin/verify-payment">Verify Payment</router-link>
      <router-link to="/admin/manage-member">Manage Member</router-link>
    </div>

    <!-- Profile icon redirects to Profile Page -->
    <div class="navbar-right">
      <router-link to="/admin/profile-page">
        <img
          src="../assets/profile-icon.png"
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
  background-color: #557c56;
  color: #ffffff;
}

.navbar-left .logo {
  height: 40px;
}

.navbar-center {
  display: flex;
  gap: 20px;
}

.navbar-center a {
  color: #ffffff;
  text-decoration: none;
}

.navbar-center a:hover {
  text-decoration: underline;
}

.navbar-right .profile-icon {
  height: 40px;
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
  background-color: #ffffff;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .navbar-center {
    display: none; /* Hide links by default on mobile */
    position: absolute;
    top: 60px;
    left: 0;
    width: 100%;
    background-color: #557c56;
    flex-direction: column;
    text-align: center;
    gap: 15px;
    padding: 20px 0;
  }

  /* Show navbar links when the menu is active */
  .navbar-center-active {
    display: flex;
  }

  .navbar-hamburger {
    display: flex; /* Show hamburger menu on mobile */
  }

  /* Keep profile icon always visible */
  .navbar-right {
    display: flex; /* Profile icon stays visible on all screen sizes */
  }
}
</style>
