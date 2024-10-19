<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Manage Trainer</h1>
      <p>Welcome to your Manage Trainer page!</p>
      <!-- Add more content specific to the user here -->
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component

export default {
  name: 'ManageTrainer',
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
