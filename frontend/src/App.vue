<template>
  <div id="app">
    <!-- <div class="background"></div> -->
    <!-- Blurred background -->
    <router-view class="form-container"></router-view>
    <!-- Form container -->
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style>
/* Set the background image for the entire app */
#app {
  position: relative; /* Relative positioning for background */
  min-height: 100vh; /* Ensure it takes up at least the full viewport height */
  overflow: hidden; /* Prevent overflow of the blurred background */
}

/* Style for the blurred background */
.background {
  position: absolute; /* Position it absolutely within #app */
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('./assets/background.jpg');
  background-size: cover; /* Cover the entire area */
  background-position: center; /* Center the image */
  filter: blur(5px); /* Adjust the blur amount here */
  z-index: 1; /* Set the z-index so it's behind other content */
}

/* Ensure the router-view is above the blurred background */
.form-container {
  position: relative; /* Position it relative */
  z-index: 2; /* Ensure it is above the blurred background */
}
</style>
