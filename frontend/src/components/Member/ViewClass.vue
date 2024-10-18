<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>ViewClass</h1>
      <p>Welcome to your ViewClass page!</p>
      <!-- Add more content specific to the user here -->
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component

export default {
  name: 'ViewClass',
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
