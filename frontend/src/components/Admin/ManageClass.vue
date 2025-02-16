<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Classes</h1>

      <!-- Button to Trigger Add Class Modal -->
      <div class="d-flex justify-content-between mb-3">
        <h5>Available Classes</h5>
        <button class="btn add-class-button" @click="openAddModal">
          <i class="fas fa-plus"></i> Add New Class
        </button>
      </div>

      <!-- Class List -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white">Class List</div>
        <div class="card-body">
          <table class="table table-hover" v-if="classes.length > 0">
            <thead>
              <tr>
                <th @click="sort('className')">
                  Class Name
                  <i :class="getSortIcon('className')"></i>
                </th>
                <th @click="sort('classDate')">
                  Date
                  <i :class="getSortIcon('classDate')"></i>
                </th>
                <th @click="sort('classTime')">
                  Time
                  <i :class="getSortIcon('classTime')"></i>
                </th>
                <th @click="sort('classCapasity')">
                  Capacity
                  <i :class="getSortIcon('classCapasity')"></i>
                </th>
                <th @click="sort('trainerDetail.trainerName')">
                  Trainer
                  <i :class="getSortIcon('trainerDetail.trainerName')"></i>
                </th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="classItem in sortedClasses" :key="classItem.idClass">
                <td>{{ classItem.className }}</td>
                <td>{{ formatDate(classItem.classDate) }}</td>
                <td>{{ classItem.classTime }}</td>
                <td>
                  {{
                    classItem.classMembers ? classItem.classMembers.length : 0
                  }}/{{ classItem.classCapasity }}
                </td>
                <td>
                  {{
                    classItem.trainerDetail
                      ? classItem.trainerDetail.trainerName
                      : 'No Trainer Assigned'
                  }}
                </td>
                <td>
                  <button
                    class="btn btn-sm btn-primary me-2"
                    @click="goToClassDetail(classItem.idClass)"
                  >
                    View Detail
                  </button>
                  <button
                    class="btn btn-sm btn-primary me-2"
                    @click="editClass(classItem)"
                  >
                    <i class="fas fa-edit"></i> Edit
                  </button>
                  <button
                    class="btn btn-sm btn-danger"
                    @click="deleteClass(classItem.idClass)"
                  >
                    <i class="fas fa-trash-alt"></i> Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">
              No classes found. Add a new class to get started!
            </p>
          </div>
        </div>
      </div>

      <!-- Add/Edit Class Modal -->
      <CustomModal :visible="isModalOpen" @close="closeModal">
        <h5 class="modal-title mb-4 text-center">
          {{ isEditMode ? 'Edit Class' : 'Add New Class' }}
        </h5>
        <form @submit.prevent="isEditMode ? updateClass() : addClass()">
          <!-- First Row: Class Name & Class Date -->
          <div class="row mb-3">
            <div class="col-md-6">
              <label for="className" class="form-label">Class Name</label>
              <input
                type="text"
                v-model="currentClass.className"
                class="form-control"
                id="className"
                required
                placeholder="Enter class name"
                @input="sanitizeClassNameDebounced"
                aria-required="true"
                aria-describedby="classNameHelp"
              />
              <small id="classNameHelp" class="form-text text-muted">
                Please enter the name of the class.
              </small>
              <span v-if="errors.className" class="text-danger">{{
                errors.className
              }}</span>
            </div>
            <div class="col-md-6">
              <label for="classDate" class="form-label">Class Date</label>
              <input
                type="date"
                v-model="currentClass.classDate"
                class="form-control"
                id="classDate"
                required
                @input="sanitizeClassDateDebounced"
                aria-required="true"
                aria-describedby="classDateHelp"
              />
              <small id="classDateHelp" class="form-text text-muted">
                Select the date for the class.
              </small>
              <span v-if="errors.classDate" class="text-danger">{{
                errors.classDate
              }}</span>
            </div>
          </div>

          <!-- Second Row: Class Time & Class Capacity -->
          <div class="row mb-3">
            <div class="col-md-6">
              <label for="classTime" class="form-label">Class Time</label>
              <input
                type="time"
                v-model="currentClass.classTime"
                class="form-control"
                id="classTime"
                required
                @input="sanitizeClassTimeDebounced"
                aria-required="true"
                aria-describedby="classTimeHelp"
              />
              <small id="classTimeHelp" class="form-text text-muted">
                Select the time for the class.
              </small>
              <span v-if="errors.classTime" class="text-danger">{{
                errors.classTime
              }}</span>
            </div>
            <div class="col-md-6">
              <label for="classCapasity" class="form-label"
                >Class Capacity</label
              >
              <input
                type="number"
                v-model="currentClass.classCapasity"
                class="form-control"
                id="classCapasity"
                required
                placeholder="Enter class capacity"
                @input="sanitizeClassCapasityDebounced"
                aria-required="true"
                aria-describedby="classCapasityHelp"
              />
              <small id="classCapasityHelp" class="form-text text-muted">
                Enter the maximum number of participants.
              </small>
              <span v-if="errors.classCapasity" class="text-danger">{{
                errors.classCapasity
              }}</span>
            </div>
          </div>

          <!-- Third Row: Class Requirements & Select Trainer -->
          <div class="row mb-3">
            <div class="col-md-6">
              <label for="classRequirement" class="form-label"
                >Class Requirements</label
              >
              <textarea
                v-model="currentClass.classRequirement"
                class="form-control"
                id="classRequirement"
                placeholder="Enter class requirements"
                required
                @input="sanitizeClassRequirementDebounced"
                aria-required="true"
                aria-describedby="classRequirementHelp"
                rows="4"
              ></textarea>
              <small id="classRequirementHelp" class="form-text text-muted">
                Describe any requirements for the class.
              </small>
              <span v-if="errors.classRequirement" class="text-danger">{{
                errors.classRequirement
              }}</span>
            </div>
            <div class="col-md-6">
              <label for="idTrainer" class="form-label">Select Trainer</label>
              <select
                v-model="currentClass.idTrainer"
                class="form-select"
                required
                @input="sanitizeIdTrainerDebounced"
                aria-required="true"
                aria-describedby="idTrainerHelp"
              >
                <option value="" disabled>Select a Trainer</option>
                <option
                  v-for="trainer in trainers"
                  :key="trainer.idTrainer"
                  :value="trainer.idTrainer"
                >
                  {{ trainer.trainerName }}
                </option>
              </select>
              <small id="idTrainerHelp" class="form-text text-muted">
                Assign a trainer to the class.
              </small>
              <span v-if="errors.idTrainer" class="text-danger">{{
                errors.idTrainer
              }}</span>
            </div>
          </div>

          <!-- Submit Button -->
          <button type="submit" class="btn add-class-button w-100">
            {{ isEditMode ? 'Update Class' : 'Add Class' }}
          </button>
        </form>
      </CustomModal>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import Navbar from './Navbar.vue'
import CustomModal from '../CustomModal.vue'
import axios from 'axios'
import Swal from 'sweetalert2'
import DOMPurify from 'dompurify'
import debounce from 'lodash/debounce'

export default {
  name: 'ManageClass',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      classes: [],
      trainers: [],
      currentClass: {
        idClass: null,
        className: '',
        classDate: '',
        classTime: '',
        classCapasity: '',
        classRequirement: '',
        idTrainer: '',
      },
      token: null,
      isModalOpen: false, // Renamed from isAddModalOpen to isModalOpen
      isEditMode: false,
      sortKey: 'className',
      sortOrder: 'asc',
      errors: {}, // Object to hold validation error messages
    }
  },
  computed: {
    sortedClasses() {
      return [...this.classes].sort((a, b) => {
        const key = this.sortKey
        let aValue = this.getNestedValue(a, key)
        let bValue = this.getNestedValue(b, key)

        // For dates, convert to timestamps for accurate comparison
        if (key === 'classDate') {
          aValue = dayjs(aValue).unix()
          bValue = dayjs(bValue).unix()
        }

        // Handle nested properties like trainerDetail.trainerName
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
          'https://mjtrainingcamp.my.id/api/v1/trainer/getall',
          {
            headers: { Authorization: `Bearer ${this.token}` },
          },
        )
        this.trainers = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch trainers.', 'error')
      }
    },

    // Fetch Classes from API
    async fetchClasses() {
      try {
        const response = await axios.get(
          'https://mjtrainingcamp.my.id/api/v1/class/getClasses',
          {
            headers: { Authorization: `Bearer ${this.token}` },
          },
        )
        this.classes = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch classes.', 'error')
      }
    },

    // Open Add Class Modal
    openAddModal() {
      this.resetCurrentClass()
      this.isEditMode = false
      this.isModalOpen = true // Updated to isModalOpen
    },

    // Open Edit Class Modal
    editClass(classItem) {
      this.currentClass = { ...classItem }
      this.isEditMode = true
      this.isModalOpen = true // Updated to isModalOpen
    },
    goToClassDetail(idClass) {
      // Using Vue Router to navigate to the class detail page
      this.$router.push({ name: 'AdminClassDetail', params: { id: idClass } })
    },
    // Close Modal
    closeModal() {
      this.isModalOpen = false // Updated to isModalOpen
      this.resetErrors()
    },

    // Reset Current Class Data
    resetCurrentClass() {
      this.currentClass = {
        idClass: null,
        className: '',
        classDate: '',
        classTime: '',
        classCapasity: '',
        classRequirement: '',
        idTrainer: '',
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

    // Helper to get nested values (e.g., trainerDetail.trainerName)
    getNestedValue(obj, path) {
      return path.split('.').reduce((acc, part) => acc && acc[part], obj)
    },

    // Form Validation Methods
    validateForm() {
      this.resetErrors()

      // Class Name Validation
      this.currentClass.className = this.sanitizeInput(
        this.currentClass.className,
      )
      if (!this.currentClass.className.trim()) {
        this.errors.className = 'Class Name is required.'
      }

      // Class Date Validation
      this.currentClass.classDate = this.sanitizeInput(
        this.currentClass.classDate,
      )
      if (!this.currentClass.classDate) {
        this.errors.classDate = 'Class Date is required.'
      } else if (!dayjs(this.currentClass.classDate).isValid()) {
        this.errors.classDate = 'Invalid Class Date.'
      }

      // Class Time Validation
      this.currentClass.classTime = this.sanitizeInput(
        this.currentClass.classTime,
      )
      if (!this.currentClass.classTime) {
        this.errors.classTime = 'Class Time is required.'
      }

      // Class Capacity Validation
      this.currentClass.classCapasity = this.sanitizeInput(
        this.currentClass.classCapasity,
      )
      if (!this.currentClass.classCapasity) {
        this.errors.classCapasity = 'Class Capacity is required.'
      } else if (this.currentClass.classCapasity <= 0) {
        this.errors.classCapasity = 'Class Capacity must be a positive number.'
      }

      // Class Requirement Validation
      this.currentClass.classRequirement = this.sanitizeInput(
        this.currentClass.classRequirement,
      )
      if (!this.currentClass.classRequirement.trim()) {
        this.errors.classRequirement = 'Class Requirements are required.'
      }

      // Trainer Selection Validation
      this.currentClass.idTrainer = this.sanitizeInput(
        this.currentClass.idTrainer,
      )
      if (!this.currentClass.idTrainer) {
        this.errors.idTrainer = 'Please select a trainer.'
      }

      return Object.keys(this.errors).length === 0
    },

    // Add New Class
    async addClass() {
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
          'https://mjtrainingcamp.my.id/api/v1/class/add',
          this.currentClass,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        this.classes.push(response.data)

        Swal.fire({
          title: 'Class Added!',
          text: 'The new class has been added successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          this.closeModal()
          // Refresh the page to fetch and display the updated classes list
          window.location.reload()
        })
      } catch (error) {
        console.error(error)
        Swal.fire('Error!', 'Failed to add the class.', 'error')
      }
    },

    // Update Existing Class
    async updateClass() {
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
          `https://mjtrainingcamp.my.id/api/v1/class/update/${this.currentClass.idClass}`,
          this.currentClass,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        const index = this.classes.findIndex(
          cls => cls.idClass === this.currentClass.idClass,
        )
        if (index !== -1) {
          this.$set(this.classes, index, response.data)
        }

        Swal.fire({
          title: 'Class Updated!',
          text: 'The class has been updated successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          this.closeModal()
          // Refresh the page to fetch and display the updated classes list
          window.location.reload()
        })
      } catch (error) {
        console.error(error)
        Swal.fire('Error!', 'Failed to update the class.', 'error')
      }
    },

    // Delete Class
    async deleteClass(idClass) {
      Swal.fire({
        title: 'Are you sure?',
        text: 'Do you want to delete this class?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
      }).then(async result => {
        if (result.isConfirmed) {
          try {
            await axios.delete(
              `https://mjtrainingcamp.my.id/api/v1/class/delete/${idClass}`,
              {
                headers: {
                  Authorization: `Bearer ${this.token}`,
                },
              },
            )

            this.classes = this.classes.filter(cls => cls.idClass !== idClass)

            Swal.fire(
              'Deleted!',
              'The class has been deleted.',
              'success',
            ).then(() => {
              // Refresh the page to fetch and display the updated classes list
              window.location.reload()
            })
          } catch (error) {
            console.error(error)
            Swal.fire('Error!', 'Failed to delete the class.', 'error')
          }
        }
      })
    },

    // Format Date using dayjs
    formatDate(date) {
      return dayjs(date).format('dddd, DD MMM YYYY') // e.g., Friday, 31 Oct 2024
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
    sanitizeClassName() {
      this.currentClass.className = this.sanitizeInput(
        this.currentClass.className,
      )
    },
    sanitizeClassNameDebounced: debounce(function () {
      this.sanitizeClassName()
    }, 300),

    sanitizeClassDate() {
      this.currentClass.classDate = this.sanitizeInput(
        this.currentClass.classDate,
      )
    },
    sanitizeClassDateDebounced: debounce(function () {
      this.sanitizeClassDate()
    }, 300),

    sanitizeClassTime() {
      this.currentClass.classTime = this.sanitizeInput(
        this.currentClass.classTime,
      )
    },
    sanitizeClassTimeDebounced: debounce(function () {
      this.sanitizeClassTime()
    }, 300),

    sanitizeClassCapasity() {
      this.currentClass.classCapasity = this.sanitizeInput(
        this.currentClass.classCapasity,
      )
    },
    sanitizeClassCapasityDebounced: debounce(function () {
      this.sanitizeClassCapasity()
    }, 300),

    sanitizeClassRequirement() {
      this.currentClass.classRequirement = this.sanitizeInput(
        this.currentClass.classRequirement,
      )
    },
    sanitizeClassRequirementDebounced: debounce(function () {
      this.sanitizeClassRequirement()
    }, 300),

    sanitizeIdTrainer() {
      this.currentClass.idTrainer = this.sanitizeInput(
        this.currentClass.idTrainer,
      )
    },
    sanitizeIdTrainerDebounced: debounce(function () {
      this.sanitizeIdTrainer()
    }, 300),
  },
  created() {
    this.token = localStorage.getItem('token')

    if (this.token) {
      this.fetchTrainers()
      this.fetchClasses()
    } else {
      Swal.fire('Error', 'Token not found. Please log in.', 'error').then(
        () => {
          this.$router.push('/')
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

.add-class-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  transition: background-color 0.3s ease;
}

.add-class-button:hover {
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

.add-class-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  width: 100%;
  font-size: 1.1rem;
  transition: background-color 0.3s ease;
}

.add-class-button:hover {
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

  .add-class-button {
    padding: 8px 16px;
    font-size: 1rem;
  }
}
</style>
