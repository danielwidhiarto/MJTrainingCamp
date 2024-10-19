<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Manage Class</h1>
      <p>Welcome to your Manage Class page!</p>
      <!-- Add more content specific to the user here -->
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component

export default {
  name: 'ManageClass',
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
