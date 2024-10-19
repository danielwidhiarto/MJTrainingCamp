<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Classes</h1>

      <!-- Button to Trigger Add Class Modal -->
      <div class="d-flex justify-content-between mb-3">
        <h5></h5>
        <button class="btn btn-success" @click="isAddModalOpen = true">
          Add New Class
        </button>
      </div>

      <!-- Class List (For future implementation) -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-primary text-white">Class List</div>
        <div class="card-body">
          <p class="text-center">
            No classes found. Add a new class to get started!
          </p>
        </div>
      </div>

      <!-- Add Class Modal -->
      <CustomModal :visible="isAddModalOpen" @close="isAddModalOpen = false">
        <h5 class="modal-title mb-4 text-center">Add New Class</h5>
        <form @submit.prevent="addClass">
          <div class="mb-3">
            <label for="className" class="form-label">Class Name</label>
            <input
              type="text"
              v-model="newClass.className"
              class="form-control"
              id="className"
              required
              placeholder="Enter class name"
            />
          </div>
          <div class="mb-3">
            <label for="classDate" class="form-label">Class Date</label>
            <input
              type="date"
              v-model="newClass.classDate"
              class="form-control"
              id="classDate"
              required
            />
          </div>
          <div class="mb-3">
            <label for="classTime" class="form-label">Class Time</label>
            <input
              type="time"
              v-model="newClass.classTime"
              class="form-control"
              id="classTime"
              required
            />
          </div>
          <div class="mb-3">
            <label for="classCapasity" class="form-label">Class Capacity</label>
            <input
              type="number"
              v-model="newClass.classCapasity"
              class="form-control"
              id="classCapasity"
              required
              placeholder="Enter class capacity"
            />
          </div>
          <div class="mb-3">
            <label for="classRequirement" class="form-label"
              >Class Requirements</label
            >
            <textarea
              v-model="newClass.classRequirement"
              class="form-control"
              id="classRequirement"
              placeholder="Enter class requirements"
              required
            ></textarea>
          </div>
          <!-- Trainer Dropdown -->
          <div class="mb-3">
            <label for="idTrainer" class="form-label">Select Trainer</label>
            <select v-model="newClass.idTrainer" class="form-select" required>
              <option value="" disabled>Select a Trainer</option>
              <option
                v-for="trainer in trainers"
                :key="trainer.id"
                :value="trainer.id"
              >
                {{ trainer.idTrainer }} - {{ trainer.trainerName }}
              </option>
            </select>
          </div>
          <button type="submit" class="btn btn-success w-100">Add Class</button>
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
  name: 'ManageClass',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      newClass: {
        classDate: '',
        classTime: '',
        classCapasity: '',
        classRequirement: '',
        idTrainer: '',
        className: '',
      },
      trainers: [], // Store the list of trainers
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
    // Add a new class
    async addClass() {
      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/class/add',
          this.newClass,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        // Clear the form fields after adding
        this.newClass = {
          classDate: '',
          classTime: '',
          classCapasity: '',
          classRequirement: '',
          idTrainer: '',
          className: '',
        }

        // Close the modal after adding the class
        this.isAddModalOpen = false

        // Show a success alert
        Swal.fire({
          title: 'Class Added!',
          text: 'The new class has been added successfully.',
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
          'Failed to add the class. Please try again.',
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

/* Custom Modal Styling (In case it's needed here as well) */
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
