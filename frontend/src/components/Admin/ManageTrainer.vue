<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Trainers</h1>

      <!-- Button to Trigger Add Trainer Modal - Positioned Right -->
      <div class="d-flex justify-content-between mb-3">
        <h5></h5>
        <button class="btn btn-success" @click="isAddModalOpen = true">
          Add New Trainer
        </button>
      </div>

      <!-- Trainer List -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-primary text-white">Trainers List</div>
        <div class="card-body">
          <table class="table table-striped" v-if="trainers.length > 0">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone Number</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="trainer in trainers" :key="trainer.idTrainer">
                <td>{{ trainer.idTrainer }}</td>
                <!-- Use idTrainer instead of id -->
                <td>{{ trainer.trainerName }}</td>
                <td>{{ trainer.email }}</td>
                <!-- email is not in the response, so you can remove or adjust it -->
                <td>{{ trainer.pnumber }}</td>
                <!-- pNumber is also missing, adjust as needed -->
                <td>{{ trainer.trainerDescription }}</td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">No trainers found.</p>
          </div>
        </div>
      </div>

      <!-- Add Trainer Modal (Using the Custom Modal Component) -->
      <CustomModal :visible="isAddModalOpen" @close="isAddModalOpen = false">
        <h5 class="modal-title mb-4 text-center">Add New Trainer</h5>
        <form @submit.prevent="addTrainer">
          <div class="mb-3">
            <label for="trainerName" class="form-label">Trainer Name</label>
            <input
              type="text"
              v-model="newTrainer.trainerName"
              class="form-control"
              id="trainerName"
              required
              placeholder="Enter trainer name"
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
              placeholder="Enter trainer email"
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
              placeholder="Enter phone number"
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
              placeholder="Enter description"
            ></textarea>
          </div>
          <button type="submit" class="btn btn-success w-100">
            Add Trainer
          </button>
        </form>
      </CustomModal>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue' // Import the Navbar component
import CustomModal from '../CustomModal.vue' // Import the Custom Modal component
import axios from 'axios' // Import axios for HTTP requests
import Swal from 'sweetalert2' // Import SweetAlert

export default {
  name: 'ManageTrainer',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      trainers: [], // Trainers data fetched from API
      newTrainer: {
        trainerName: '',
        email: '',
        password: '',
        pNumber: '',
        trainerDescription: '',
      },
      token: null, // Token for authorization will be fetched from localStorage
      isAddModalOpen: false, // Control the visibility of the add modal
    }
  },
  created() {
    // Fetch the token from localStorage
    this.token = localStorage.getItem('token')

    if (this.token) {
      this.fetchTrainers() // Fetch trainers when component is created
    } else {
      Swal.fire('Error', 'Token not found. Please log in.', 'error')
    }
  },
  methods: {
    // Fetch trainers from API
    async fetchTrainers() {
      try {
        const response = await axios.get(
          'http://localhost:8081/api/v1/trainer/getall',
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
          },
        )
        this.trainers = response.data // Store fetched trainers in the trainers array
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch trainers.', 'error')
      }
    },
    // Add a new trainer
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

        // Add the new trainer to the list
        this.trainers.push(response.data)

        // Clear the form fields after adding
        this.newTrainer = {
          trainerName: '',
          email: '',
          password: '',
          pNumber: '',
          trainerDescription: '',
        }

        // Close the modal after adding the trainer
        this.isAddModalOpen = false

        // Show a success alert and refresh the page when the user presses "OK"
        Swal.fire({
          title: 'Trainer Added!',
          text: 'The new trainer has been added successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          // Refresh the page after pressing OK
          window.location.reload()
        })
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

.card {
  border-radius: 8px;
}

.btn-primary,
.btn-success {
  font-weight: bold;
}

h1 {
  font-family: 'Arial', sans-serif;
  color: #333;
}

/* Custom Modal Styling */
.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.custom-modal-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 50%;
  position: relative;
}

.btn-close {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}
</style>
