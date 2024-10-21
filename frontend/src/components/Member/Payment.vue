<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Payment Page</h1>
      <p>Welcome to your Payment Page!</p>
      <div v-if="packageDetails">
        <h2>Package Details</h2>
        <p><strong>Package Name:</strong> {{ packageDetails.packageName }}</p>
        <p><strong>Price:</strong> {{ packageDetails.price }}</p>
        <p><strong>Description:</strong> {{ packageDetails.description }}</p>
        <!-- Add payment form or button here -->
        <button @click="processPayment">Proceed to Payment</button>
      </div>
      <div v-else>
        <p>Loading package details...</p>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component
import axios from 'axios' // Import axios for HTTP requests
import { onMounted } from 'vue' // Import onMounted lifecycle hook
import { useRoute } from 'vue-router' // Import useRoute to access route parameters

export default {
  name: 'PaymentPage',
  components: {
    Navbar,
  },
  data() {
    return {
      packageDetails: null, // Initialize package details
    }
  },
  setup() {
    const route = useRoute() // Access the current route
    return { route }
  },
  mounted() {
    this.fetchPackageDetails() // Fetch package details when the component is mounted
  },
  methods: {
    async fetchPackageDetails() {
      const packageId = this.route.params.packageId // Get the packageId from route parameters
      try {
        const token = localStorage.getItem('token') // Get the token from local storage
        const response = await axios.get(
          `http://localhost:8081/api/v1/package/get/${packageId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`, // Include the token in the headers
            },
          },
        )
        this.packageDetails = response.data // Set the package details
      } catch (error) {
        console.error(error)
        // Handle error (e.g., show an alert)
      }
    },
    processPayment() {
      // Implement payment processing logic here
      alert('Payment processing...')
    },
  },
}
</script>

<style>
.container {
  padding: 20px;
}
</style>
