<template>
  <div>
    <LazyNavbar />
    <div class="container mt-4">
      <!-- Back Button -->
      <button class="back-button" @click="debouncedGoBack">
        ← Back to Classes
      </button>

      <!-- Loading Indicator -->
      <div v-if="loading" class="loading-indicator" aria-live="polite">
        <div class="spinner"></div>
        <span>Loading class details...</span>
      </div>

      <!-- Error Message -->
      <div
        v-if="error"
        class="alert alert-danger text-center my-5"
        role="alert"
      >
        {{ error }}
      </div>

      <!-- Class Details -->
      <div v-if="!loading && classDetails" class="class-detail">
        <h1 class="mb-4">{{ classDetails.className }}</h1>
        <div class="row mb-4">
          <!-- Class Information -->
          <div class="col-md-6 mb-4">
            <h4>Class Information</h4>
            <ul class="list-group list-group-flush">
              <li class="list-group-item">
                <strong>Date:</strong> {{ formatDate(classDetails.classDate) }}
              </li>
              <li class="list-group-item">
                <strong>Time:</strong>
                {{
                  classDetails.classTime
                    ? formatTime(classDetails.classTime)
                    : 'N/A'
                }}
              </li>
              <li class="list-group-item">
                <strong>Capacity:</strong>
                {{ classDetails.classCapasity }} members
              </li>
              <li class="list-group-item">
                <strong>Requirements:</strong>
                {{ classDetails.classRequirement }}
              </li>
            </ul>
          </div>

          <!-- Trainer Details -->
          <div class="col-md-6 mb-4">
            <h4>Trainer Details</h4>
            <div class="card">
              <div class="card-body">
                <h5 class="card-title">
                  {{ classDetails.trainerDetail.trainerName }}
                </h5>
                <p class="card-text">
                  {{ classDetails.trainerDetail.trainerDescription }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Class Members -->
        <div class="mb-4">
          <h4>Class Members</h4>
          <table class="table table-striped">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone Number</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="member in classDetails.classMembers"
                :key="member.idUser"
              >
                <td>{{ member.name }}</td>
                <td>{{ member.email }}</td>
                <td>{{ member.pnumber }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Add Notes Button (Visible for past classes only) -->
        <div v-if="isPastClass" class="text-center">
          <button class="btn btn-warning" @click="showAddNotesModal">
            Add Notes
          </button>
        </div>
      </div>

      <!-- Add Notes Modal -->
      <CustomModal :visible="showModal" @close="closeModal">
        <h5 class="modal-title mb-4 text-center">Add Notes</h5>
        <textarea
          v-model="noteContent"
          placeholder="Write your notes here..."
          class="form-control mb-3"
          rows="5"
        ></textarea>
        <button class="btn btn-primary w-100" @click="submitNotes">
          Submit Notes
        </button>
      </CustomModal>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { defineAsyncComponent } from 'vue'
import dayjs from 'dayjs'
import customParseFormat from 'dayjs/plugin/customParseFormat'
import CustomModal from '../CustomModal.vue' // Import CustomModal
import Swal from 'sweetalert2' // Import SweetAlert

dayjs.extend(customParseFormat) // Extend dayjs with the plugin

// Lazy load Navbar component
const LazyNavbar = defineAsyncComponent(() => import('./Navbar.vue'))

export default {
  name: 'ClassDetail',
  components: { LazyNavbar, CustomModal },
  data() {
    return {
      classDetails: null,
      loading: false,
      error: null,
      showModal: false, // Control visibility of the modal
      noteContent: '', // Note content for the modal
    }
  },
  computed: {
    isPastClass() {
      // Check if the class is past the current date
      const classDate = dayjs(this.classDetails.classDate)
      return classDate.isBefore(dayjs(), 'day') // Past class if the date is before today
    },
  },
  methods: {
    goBack() {
      this.$router.push({ name: 'TrainerDashboard' })
    },

    showAddNotesModal() {
      this.showModal = true // Open the modal
    },

    closeModal() {
      this.showModal = false // Close the modal
      this.noteContent = '' // Reset note content
    },

    async submitNotes() {
      const token = localStorage.getItem('token')

      if (!this.noteContent.trim()) {
        Swal.fire({
          icon: 'warning',
          title: 'Empty Notes',
          text: 'Please write some notes before submitting.',
        })
        return
      }

      try {
        const response = await axios.post(
          'https://mjtrainingcamp.my.id/api/v1/class/addNotes',
          {
            notes: this.noteContent,
            idClass: this.classDetails.idClass,
          },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          },
        )

        Swal.fire({
          icon: 'success',
          title: 'Notes Submitted',
          text: 'Your notes have been added successfully!',
        }).then(() => {
          this.closeModal()
        })
      } catch (error) {
        console.error('Failed to submit notes:', error)
        Swal.fire({
          icon: 'error',
          title: 'Submission Failed',
          text: 'An error occurred while submitting your notes. Please try again.',
        })
      }
    },

    async fetchClassDetail() {
      this.loading = true
      const token = localStorage.getItem('token')

      if (!token) {
        this.error = 'Authentication token is missing. Please log in.'
        this.loading = false
        return
      }

      const idClass = this.$route.params.id

      try {
        const response = await axios.get(
          `https://mjtrainingcamp.my.id/api/v1/class/getClasses?id=${idClass}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )

        if (response.data && response.data.length > 0) {
          this.classDetails = response.data[0]
        } else {
          this.error = 'Class not found.'
        }
      } catch (err) {
        this.error = 'Failed to fetch class details. Please try again later.'
      } finally {
        this.loading = false
      }
    },

    formatDate(date) {
      return dayjs(date).format('dddd, D MMMM YYYY')
    },

    formatTime(time) {
      let parsedTime = dayjs(time, 'HH:mm:ss', true)
      if (!parsedTime.isValid()) {
        parsedTime = dayjs(time, 'HH:mm', true)
      }
      return parsedTime.isValid() ? parsedTime.format('h:mm A') : time
    },

    debounce(func, wait) {
      let timeout
      return function (...args) {
        const later = () => {
          clearTimeout(timeout)
          func.apply(this, args)
        }
        clearTimeout(timeout)
        timeout = setTimeout(later, wait)
      }
    },
  },
  mounted() {
    this.fetchClassDetail()
    this.debouncedGoBack = this.debounce(this.goBack, 300)
  },
}
</script>

<style scoped>
/* Container Styling */
.container {
  background-color: #fff;
  padding: 40px 20px;
  border-radius: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* Back Button Styling */
.back-button {
  background-color: transparent;
  border: none;
  color: #ff4500; /* Matching color scheme */
  font-size: 1rem;
  cursor: pointer;
  margin-bottom: 20px;
}

.back-button:hover {
  text-decoration: underline;
}

/* Heading Styles */
h1 {
  font-size: 3rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #000; /* Black text to match the logo's accent */
}

h4 {
  margin-bottom: 20px;
  color: #000;
}

/* Card Styling */
.card {
  border: none;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* Table Styling */
.table th,
.table td {
  vertical-align: middle;
}

/* Button Styling */
.btn-primary {
  background-color: #ff4500;
  border-color: #ff4500;
}

.btn-primary:hover {
  background-color: #e03b00;
  border-color: #e03b00;
}

/* Loading Indicator Styling */
.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  font-size: 1.3rem;
  font-weight: 600;
  color: #666;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #ff4500;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-right: 10px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Error Message Styling */
.alert-danger {
  font-size: 1.3rem;
  font-weight: 600;
  color: #ff4500;
}

/* SweetAlert Custom Styles */
.swal2-popup {
  font-size: 1.1rem !important;
}

.swal2-confirm {
  background-color: #ff4500 !important;
  border-color: #ff4500 !important;
}

.swal2-confirm:hover {
  background-color: #e03b00 !important;
  border-color: #e03b00 !important;
}

.swal2-cancel {
  background-color: #6c757d !important;
  border-color: #6c757d !important;
}

.swal2-cancel:hover {
  background-color: #5a6268 !important;
  border-color: #545b62 !important;
}

/* Typography Consistency */
h1,
h4 {
  color: #000;
  font-weight: 600;
}

p {
  color: #555;
  font-size: 1.25rem;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  h1 {
    font-size: 2.5rem;
  }

  h4 {
    font-size: 1.75rem;
  }

  .back-button {
    font-size: 0.95rem;
  }

  p {
    font-size: 1.2rem;
    margin-bottom: 30px;
  }

  .btn-primary {
    padding: 12px 18px; /* Adjusted padding for medium screens */
    font-size: 1rem;
  }

  textarea {
    width: 100%;
    height: 150px;
    margin-bottom: 20px;
    padding: 10px;
    font-size: 1rem;
    border: 1px solid #ccc;
    border-radius: 5px;
  }

  button {
    padding: 10px 20px;
    font-size: 1rem;
    cursor: pointer;
  }

  button.btn-primary {
    background-color: #ff4500;
    color: white;
    border: none;
  }

  button.btn-secondary {
    background-color: #6c757d;
    color: white;
    border: none;
  }

  button:hover {
    opacity: 0.8;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 2rem;
  }

  h4 {
    font-size: 1.5rem;
  }

  .back-button {
    font-size: 0.9rem;
  }

  p {
    font-size: 1rem;
  }

  .btn-primary {
    padding: 10px 14px; /* Adjusted padding for small screens */
    font-size: 0.95rem;
  }

  .card-title {
    font-size: 1.25rem;
  }

  .card-text {
    font-size: 1rem;
  }
}
</style>
