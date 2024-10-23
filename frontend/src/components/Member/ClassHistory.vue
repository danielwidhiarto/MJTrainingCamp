<template>
  <div>
    <Navbar />
    <div class="container">
      <h1 class="page-title">Class History</h1>
      <p class="welcome-text">Welcome to your Class History page!</p>
      <!-- Add more content specific to the user here -->
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component

export default {
  name: 'ClassHistory',
  components: {
    Navbar,
  },
}
</script>

<style scoped>
/* Container Styling */
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  text-align: center; /* Center content */
  background-color: #fff; /* White background for content clarity */
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1); /* Subtle shadow for a modern look */
  border-radius: 16px; /* Rounded corners */
  margin-top: 40px; /* Space from the top */
}

/* Title Styling */
.page-title {
  font-size: 2.5rem;
  font-weight: 600;
  color: #000; /* Black text color */
  margin-bottom: 20px;
}

/* Welcome Text Styling */
.welcome-text {
  font-size: 1.25rem;
  color: #555;
  margin-bottom: 40px;
  line-height: 1.6; /* Better readability */
}

/* Responsive Design */
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem; /* Slightly smaller on smaller screens */
  }

  .welcome-text {
    font-size: 1rem;
  }

  .container {
    padding: 20px 15px; /* Less padding on smaller screens */
  }
}
</style>
