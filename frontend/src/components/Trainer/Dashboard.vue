<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Trainer Home</h1>
      <p>Welcome to your home page!</p>
      <!-- Add more content specific to the user here -->
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component

export default {
  name: 'TrainerDashboard',
  components: {
    Navbar,
  },
}
</script>

<style>
.container {
  padding: 20px;
}
</style>
