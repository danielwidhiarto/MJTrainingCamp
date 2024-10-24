<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Trainers</h1>

      <!-- Button to Trigger Add Trainer Modal -->
      <div class="d-flex justify-content-between mb-3">
        <h5></h5>
        <button class="btn add-trainer-button" @click="isAddModalOpen = true">
          Add New Trainer
        </button>
      </div>

      <!-- Trainer List -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white">Trainers List</div>
        <div class="card-body">
          <table class="table table-hover" v-if="trainers.length > 0">
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
                <td>{{ trainer.trainerName }}</td>
                <td>{{ trainer.email }}</td>
                <td>{{ trainer.pnumber }}</td>
                <td>{{ trainer.trainerDescription }}</td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">No trainers found.</p>
          </div>
        </div>
      </div>

      <!-- Add Trainer Modal -->
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
            <label for="trainerPassword" class="form-label">Password</label>
            <input
              type="password"
              v-model="newTrainer.password"
              class="form-control"
              id="trainerPassword"
              required
              placeholder="Enter password"
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
          <button type="submit" class="btn add-trainer-button w-100">
            Add Trainer
          </button>
        </form>
      </CustomModal>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'
import CustomModal from '../CustomModal.vue'
import axios from 'axios'
import Swal from 'sweetalert2'

export default {
  name: 'ManageTrainer',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      trainers: [],
      newTrainer: {
        trainerName: '',
        email: '',
        password: '',
        pNumber: '',
        trainerDescription: '',
      },
      token: null,
      isAddModalOpen: false,
    }
  },
  created() {
    this.token = localStorage.getItem('token')

    if (this.token) {
      this.fetchTrainers()
    } else {
      Swal.fire('Error', 'Token not found. Please log in.', 'error')
    }
  },
  methods: {
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
        this.trainers = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch trainers.', 'error')
      }
    },
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

        this.trainers.push(response.data)

        this.newTrainer = {
          trainerName: '',
          email: '',
          password: '',
          pNumber: '',
          trainerDescription: '',
        }

        this.isAddModalOpen = false

        Swal.fire({
          title: 'Trainer Added!',
          text: 'The new trainer has been added successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
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

<style scoped>
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  background-color: #f8f9fa;
  border-radius: 16px;
}

h1 {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #ff4500;
}

.card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 1.2rem;
  font-weight: bold;
}

.add-trainer-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  transition: background-color 0.3s ease;
}

.add-trainer-button:hover {
  background-color: #e03b00;
}

.table-hover tbody tr:hover {
  background-color: #f0f9f0;
}

/* Modal Styling */
.modal-title {
  color: #ff4500;
  font-weight: bold;
  text-align: center;
}

.form-control {
  border-radius: 8px;
  padding: 12px;
  transition: border-color 0.3s ease;
}

.form-control:focus {
  border-color: #ff4500;
  box-shadow: 0px 0px 5px rgba(255, 69, 0, 0.5);
}

/* Responsive Design */
@media (max-width: 768px) {
  .container {
    padding: 20px;
  }

  h1 {
    font-size: 2rem;
  }

  .card-header {
    font-size: 1.1rem;
  }
}
</style>
