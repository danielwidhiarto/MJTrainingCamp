<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Trainers</h1>

      <!-- Button to Trigger Add/Edit Trainer Modal -->
      <div class="d-flex justify-content-between mb-3">
        <h5>Trainers List</h5>
        <button class="btn add-trainer-button" @click="openAddModal">
          <i class="fas fa-plus"></i> Add New Trainer
        </button>
      </div>

      <!-- Trainers List -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white">Trainers List</div>
        <div class="card-body">
          <table class="table table-hover" v-if="trainers.length > 0">
            <thead>
              <tr>
                <th @click="sort('idTrainer')">
                  ID
                  <i :class="getSortIcon('idTrainer')"></i>
                </th>
                <th @click="sort('trainerName')">
                  Name
                  <i :class="getSortIcon('trainerName')"></i>
                </th>
                <th @click="sort('email')">
                  Email
                  <i :class="getSortIcon('email')"></i>
                </th>
                <th @click="sort('pnumber')">
                  Phone Number
                  <i :class="getSortIcon('pnumber')"></i>
                </th>
                <th @click="sort('trainerDescription')">
                  Description
                  <i :class="getSortIcon('trainerDescription')"></i>
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="trainer in sortedTrainers" :key="trainer.idTrainer">
                <td>{{ trainer.idTrainer }}</td>
                <td>{{ trainer.trainerName }}</td>
                <td>{{ trainer.email }}</td>
                <td>{{ trainer.pnumber }}</td>
                <td>{{ trainer.trainerDescription }}</td>
                <td>
                  <button
                    class="btn btn-sm btn-primary me-2"
                    @click="editTrainer(trainer)"
                  >
                    <i class="fas fa-edit"></i> Edit
                  </button>
                  <button
                    class="btn btn-sm btn-danger"
                    @click="deleteTrainer(trainer.idTrainer)"
                  >
                    <i class="fas fa-trash-alt"></i> Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">No trainers found.</p>
          </div>
        </div>
      </div>

      <!-- Add/Edit Trainer Modal -->
      <CustomModal :visible="isModalOpen" @close="closeModal">
        <h5 class="modal-title mb-4 text-center">
          {{ isEditMode ? 'Edit Trainer' : 'Add New Trainer' }}
        </h5>
        <form @submit.prevent="isEditMode ? updateTrainer() : addTrainer()">
          <div class="mb-3">
            <label for="trainerName" class="form-label">Trainer Name</label>
            <input
              type="text"
              v-model="currentTrainer.trainerName"
              class="form-control"
              id="trainerName"
              required
              placeholder="Enter trainer name"
              @input="sanitizeTrainerNameDebounced"
              aria-required="true"
              aria-describedby="trainerNameHelp"
            />
            <small id="trainerNameHelp" class="form-text text-muted">
              Please enter the trainer's name.
            </small>
            <span v-if="errors.trainerName" class="text-danger">{{
              errors.trainerName
            }}</span>
          </div>
          <div class="mb-3">
            <label for="trainerEmail" class="form-label">Trainer Email</label>
            <input
              type="email"
              v-model="currentTrainer.email"
              class="form-control"
              id="trainerEmail"
              required
              placeholder="Enter trainer email"
              @input="sanitizeEmailDebounced"
              aria-required="true"
              aria-describedby="trainerEmailHelp"
            />
            <small id="trainerEmailHelp" class="form-text text-muted">
              Please enter a valid email address.
            </small>
            <span v-if="errors.email" class="text-danger">{{
              errors.email
            }}</span>
          </div>
          <div class="mb-3" v-if="!isEditMode">
            <label for="trainerPassword" class="form-label">Password</label>
            <input
              type="password"
              v-model="currentTrainer.password"
              class="form-control"
              id="trainerPassword"
              required
              placeholder="Enter password"
              @input="sanitizePasswordDebounced"
              aria-required="true"
              aria-describedby="trainerPasswordHelp"
            />
            <small id="trainerPasswordHelp" class="form-text text-muted">
              Please enter a secure password.
            </small>
            <span v-if="errors.password" class="text-danger">{{
              errors.password
            }}</span>
          </div>
          <div class="mb-3">
            <label for="trainerPhone" class="form-label">Phone Number</label>
            <input
              type="text"
              v-model="currentTrainer.pnumber"
              class="form-control"
              id="trainerPhone"
              required
              placeholder="Enter phone number"
              @input="sanitizePhoneDebounced"
              aria-required="true"
              aria-describedby="trainerPhoneHelp"
            />
            <small id="trainerPhoneHelp" class="form-text text-muted">
              Please enter a valid phone number (10-15 digits, optional '+'
              prefix).
            </small>
            <span v-if="errors.pnumber" class="text-danger">{{
              errors.pnumber
            }}</span>
          </div>
          <div class="mb-3">
            <label for="trainerDescription" class="form-label"
              >Trainer Description</label
            >
            <textarea
              v-model="currentTrainer.trainerDescription"
              class="form-control"
              id="trainerDescription"
              placeholder="Enter description"
              @input="sanitizeDescriptionDebounced"
              aria-describedby="trainerDescriptionHelp"
            ></textarea>
            <small id="trainerDescriptionHelp" class="form-text text-muted">
              Describe the trainer's expertise and background.
            </small>
            <span v-if="errors.trainerDescription" class="text-danger">{{
              errors.trainerDescription
            }}</span>
          </div>
          <button type="submit" class="btn add-trainer-button w-100">
            {{ isEditMode ? 'Update Trainer' : 'Add Trainer' }}
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
import DOMPurify from 'dompurify'
import debounce from 'lodash/debounce'

export default {
  name: 'ManageTrainer',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      trainers: [],
      currentTrainer: {
        idTrainer: null,
        trainerName: '',
        email: '',
        password: '',
        pnumber: '',
        trainerDescription: '',
      },
      token: null,
      isModalOpen: false,
      isEditMode: false,
      sortKey: 'idTrainer',
      sortOrder: 'asc',
      errors: {}, // Object to hold validation error messages
    }
  },
  computed: {
    sortedTrainers() {
      return [...this.trainers].sort((a, b) => {
        let aValue = this.getNestedValue(a, this.sortKey)
        let bValue = this.getNestedValue(b, this.sortKey)

        // For strings, compare case-insensitively
        if (typeof aValue === 'string') {
          aValue = aValue.toLowerCase()
          bValue = bValue.toLowerCase()
        }

        if (aValue < bValue) return this.sortOrder === 'asc' ? -1 : 1
        if (aValue > bValue) return this.sortOrder === 'asc' ? 1 : -1
        return 0
      })
    },
  },
  methods: {
    // Fetch Trainers from API
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

    // Open Add Trainer Modal
    openAddModal() {
      this.resetCurrentTrainer()
      this.isEditMode = false
      this.isModalOpen = true
    },

    // Open Edit Trainer Modal
    editTrainer(trainer) {
      this.currentTrainer = { ...trainer }
      this.isEditMode = true
      this.isModalOpen = true
    },

    // Close Modal
    closeModal() {
      this.isModalOpen = false
      this.resetErrors()
    },

    // Reset Current Trainer Data
    resetCurrentTrainer() {
      this.currentTrainer = {
        idTrainer: null,
        trainerName: '',
        email: '',
        password: '',
        pnumber: '',
        trainerDescription: '',
      }
    },

    // Reset Errors
    resetErrors() {
      this.errors = {}
    },

    // Sanitize Inputs using DOMPurify
    sanitizeInput(input) {
      return DOMPurify.sanitize(input)
    },

    // Helper to get nested values (if needed in future)
    getNestedValue(obj, path) {
      return path.split('.').reduce((acc, part) => acc && acc[part], obj)
    },

    // Form Validation
    validateForm() {
      this.resetErrors()

      // Trainer Name Validation
      this.currentTrainer.trainerName = this.sanitizeInput(
        this.currentTrainer.trainerName,
      )
      if (!this.currentTrainer.trainerName.trim()) {
        this.errors.trainerName = 'Trainer Name is required.'
      }

      // Email Validation
      this.currentTrainer.email = this.sanitizeInput(this.currentTrainer.email)
      if (!this.currentTrainer.email.trim()) {
        this.errors.email = 'Email is required.'
      } else if (!this.validateEmailFormat(this.currentTrainer.email)) {
        this.errors.email = 'Invalid email format.'
      }

      // Password Validation (only when adding)
      if (!this.isEditMode) {
        this.currentTrainer.password = this.sanitizeInput(
          this.currentTrainer.password,
        )
        if (!this.currentTrainer.password) {
          this.errors.password = 'Password is required.'
        } else if (this.currentTrainer.password.length < 6) {
          this.errors.password = 'Password must be at least 6 characters.'
        }
      }

      // Phone Number Validation
      this.currentTrainer.pnumber = this.sanitizeInput(
        this.currentTrainer.pnumber,
      )
      if (!this.currentTrainer.pnumber.trim()) {
        this.errors.pnumber = 'Phone Number is required.'
      } else if (!/^\+?\d{10,15}$/.test(this.currentTrainer.pnumber)) {
        this.errors.pnumber = 'Invalid phone number format.'
      }

      // Description Validation
      this.currentTrainer.trainerDescription = this.sanitizeInput(
        this.currentTrainer.trainerDescription,
      )
      if (!this.currentTrainer.trainerDescription.trim()) {
        this.errors.trainerDescription = 'Trainer Description is required.'
      }

      return Object.keys(this.errors).length === 0
    },

    // Validate Email Format
    validateEmailFormat(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return emailRegex.test(email)
    },

    // Add New Trainer
    async addTrainer() {
      if (!this.validateForm()) {
        Swal.fire({
          title: 'Invalid Input',
          text: 'Please correct the errors in the form before submitting.',
          icon: 'error',
          confirmButtonText: 'OK',
        })
        return
      }

      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/trainer/add',
          this.currentTrainer,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        // Option 1: Push the new trainer to the trainers array
        // Ensure that response.data contains all necessary fields
        this.trainers.push(response.data)

        // Option 2: Refetch the trainers list (Uncomment if Option 1 doesn't work)
        // await this.fetchTrainers()

        // Reset form and close modal
        this.resetCurrentTrainer()
        this.isModalOpen = false

        Swal.fire({
          title: 'Trainer Added!',
          text: 'The new trainer has been added successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          // Refresh the page to fetch and display the updated trainers list
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

    // Update Existing Trainer
    async updateTrainer() {
      if (!this.validateForm()) {
        Swal.fire({
          title: 'Invalid Input',
          text: 'Please correct the errors in the form before submitting.',
          icon: 'error',
          confirmButtonText: 'OK',
        })
        return
      }

      try {
        const response = await axios.put(
          `http://localhost:8081/api/v1/trainer/update/${this.currentTrainer.idTrainer}`,
          this.currentTrainer,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        const index = this.trainers.findIndex(
          trainer => trainer.idTrainer === this.currentTrainer.idTrainer,
        )
        if (index !== -1) {
          this.$set(this.trainers, index, response.data)
        }

        Swal.fire({
          title: 'Trainer Updated!',
          text: 'The trainer has been updated successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          this.closeModal()
        })
      } catch (error) {
        console.error(error)
        Swal.fire(
          'Error!',
          'Failed to update the trainer. Please try again.',
          'error',
        )
      }
    },

    // Delete Trainer
    async deleteTrainer(idTrainer) {
      Swal.fire({
        title: 'Are you sure?',
        text: 'Do you want to delete this trainer?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
      }).then(async result => {
        if (result.isConfirmed) {
          try {
            await axios.delete(
              `http://localhost:8081/api/v1/trainer/delete/${idTrainer}`,
              {
                headers: {
                  Authorization: `Bearer ${this.token}`,
                },
              },
            )

            this.trainers = this.trainers.filter(
              trainer => trainer.idTrainer !== idTrainer,
            )

            Swal.fire('Deleted!', 'The trainer has been deleted.', 'success')
          } catch (error) {
            console.error(error)
            Swal.fire(
              'Error!',
              'Failed to delete the trainer. Please try again.',
              'error',
            )
          }
        }
      })
    },

    // Sorting Logic
    sort(key) {
      if (this.sortKey === key) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortKey = key
        this.sortOrder = 'asc'
      }
    },

    getSortIcon(key) {
      if (this.sortKey !== key) return 'fas fa-sort'
      return this.sortOrder === 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down'
    },

    // Sanitize and Debounce Methods
    sanitizeTrainerName() {
      this.currentTrainer.trainerName = this.sanitizeInput(
        this.currentTrainer.trainerName,
      )
    },
    sanitizeTrainerNameDebounced: debounce(function () {
      this.sanitizeTrainerName()
    }, 300),

    sanitizeEmail() {
      this.currentTrainer.email = this.sanitizeInput(this.currentTrainer.email)
    },
    sanitizeEmailDebounced: debounce(function () {
      this.sanitizeEmail()
    }, 300),

    sanitizePassword() {
      this.currentTrainer.password = this.sanitizeInput(
        this.currentTrainer.password,
      )
    },
    sanitizePasswordDebounced: debounce(function () {
      this.sanitizePassword()
    }, 300),

    sanitizePhone() {
      this.currentTrainer.pnumber = this.sanitizeInput(
        this.currentTrainer.pnumber,
      )
    },
    sanitizePhoneDebounced: debounce(function () {
      this.sanitizePhone()
    }, 300),

    sanitizeDescription() {
      this.currentTrainer.trainerDescription = this.sanitizeInput(
        this.currentTrainer.trainerDescription,
      )
    },
    sanitizeDescriptionDebounced: debounce(function () {
      this.sanitizeDescription()
    }, 300),
  },
  created() {
    this.token = localStorage.getItem('token')

    if (this.token) {
      this.fetchTrainers()
    } else {
      Swal.fire('Error', 'Token not found. Please log in.', 'error').then(
        () => {
          this.$router.push('/login')
        },
      )
    }
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

.add-trainer-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  width: 100%;
  font-size: 1.1rem;
  transition: background-color 0.3s ease;
}

.add-trainer-button:hover {
  background-color: #e03b00;
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

  .add-trainer-button {
    padding: 8px 16px;
    font-size: 1rem;
  }
}
</style>
