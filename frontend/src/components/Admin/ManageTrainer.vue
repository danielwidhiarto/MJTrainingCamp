<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4">Manage Trainer</h1>
      <p>Welcome to your Manage Trainer page!</p>

      <!-- Add Trainer Form -->
      <div class="card mb-4">
        <div class="card-header">Add New Trainer</div>
        <div class="card-body">
          <form @submit.prevent="addTrainer">
            <div class="mb-3">
              <label for="trainerName" class="form-label">Trainer Name</label>
              <input
                type="text"
                v-model="newTrainer.trainerName"
                class="form-control"
                id="trainerName"
                required
              />
            </div>
            <div class="mb-3">
              <label for="trainerEmail" class="form-label">Trainer Email</label>
              <input
                type="email"
                v-model="newTrainer.email"
                class="form-control"
                id="trainerEmail"
                required
              />
            </div>
            <div class="mb-3">
              <label for="trainerPhone" class="form-label">Phone Number</label>
              <input
                type="text"
                v-model="newTrainer.pNumber"
                class="form-control"
                id="trainerPhone"
                required
              />
            </div>
            <div class="mb-3">
              <label for="trainerDescription" class="form-label"
                >Trainer Description</label
              >
              <textarea
                v-model="newTrainer.trainerDescription"
                class="form-control"
                id="trainerDescription"
              ></textarea>
            </div>
            <button type="submit" class="btn btn-success">Add Trainer</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component
import axios from 'axios' // Import axios for HTTP requests
import Swal from 'sweetalert2' // Import SweetAlert

export default {
  name: 'ManageTrainer',
  components: {
    Navbar,
  },
  data() {
    return {
      newTrainer: {
        trainerName: '',
        email: '',
        password: '',
        pNumber: '',
        trainerDescription: '',
      },
      token: null, // Token for authorization will be fetched from localStorage
    }
  },
  created() {
    // Fetch the token from localStorage
    this.token = localStorage.getItem('token')

    if (!this.token) {
      Swal.fire('Error', 'Token not found. Please log in.', 'error')
    }
  },
  methods: {
    async addTrainer() {
      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/trainer/add',
          this.newTrainer,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        // Clear the form fields after adding
        this.newTrainer = {
          trainerName: '',
          email: '',
          password: '',
          pNumber: '',
          trainerDescription: '',
        }

        // Show a success alert
        Swal.fire(
          'Trainer Added!',
          'The new trainer has been added successfully.',
          'success',
        )
      } catch (error) {
        console.error(error)
        Swal.fire(
          'Error!',
          'Failed to add the trainer. Please try again.',
          'error',
        )
      }
    },
  },
}
</script>

<style>
.container {
  padding: 20px;
}
</style>
