<template>
  <nav class="navbar">
    <div class="navbar-left">
      <img src="../assets/logo.jpg" alt="Logo" class="logo" />
    </div>
    <div class="navbar-center">
      <router-link to="/user-home">Home</router-link>
      <router-link to="/buy-membership">Buy Membership</router-link>
      <router-link to="/view-class">View Class</router-link>
      <router-link to="/history-class">History Class</router-link>
    </div>
    <div class="navbar-right">
      <img
        src="../assets/profile-icon.png"
        alt="Profile"
        class="profile-icon"
      />
    </div>
  </nav>
</template>

<script>
export default {
  name: 'Navbar',
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #557c56; /* Set your desired navbar background color */
  color: #ffffff; /* Set your desired text color */
}

.navbar-left .logo {
  height: 40px; /* Adjust logo size as needed */
}

.navbar-center {
  display: flex;
  gap: 20px; /* Space between nav items */
}

.navbar-center a {
  color: #ffffff; /* Link color */
  text-decoration: none; /* Remove underline */
}

.navbar-center a:hover {
  text-decoration: underline; /* Underline on hover */
}

.navbar-right .profile-icon {
  height: 40px; /* Adjust profile icon size as needed */
}
</style>
