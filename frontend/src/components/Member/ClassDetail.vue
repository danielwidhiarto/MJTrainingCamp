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

        <!-- Join Class Button -->
        <div class="text-center">
          <button
            class="btn btn-primary btn-lg"
            @click="debouncedHandleJoinClass"
            :disabled="joinButtonDisabled"
          >
            Join Class
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { defineAsyncComponent } from 'vue'
import dayjs from 'dayjs'
import customParseFormat from 'dayjs/plugin/customParseFormat' // Import plugin
import Swal from 'sweetalert2'

dayjs.extend(customParseFormat) // Extend dayjs with the plugin

// Lazy load Navbar component
const LazyNavbar = defineAsyncComponent(() => import('./Navbar.vue'))

export default {
  name: 'ClassDetail',
  components: { LazyNavbar },
  data() {
    return {
      classDetails: null,
      loading: false,
      error: null,
      joinButtonDisabled: false, // To disable button during join process
    }
  },
  methods: {
    /**
     * Navigates back to the ViewClass page.
     */
    goBack() {
      this.$router.push({ name: 'ViewClass' })
      // Alternatively, use router.go(-1) to go back to the previous history entry:
      // this.$router.go(-1)
    },

    /**
     * Fetches the class details based on the ID from the route parameters.
     */
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
          `https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/class/getClasses?id=${idClass}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )

        if (response.data && response.data.length > 0) {
          this.classDetails = response.data[0]
          console.log('Class Details:', this.classDetails) // For debugging
        } else {
          this.error = 'Class not found.'
        }
      } catch (err) {
        this.error = 'Failed to fetch class details. Please try again later.'
      } finally {
        this.loading = false
      }
    },

    /**
     * Formats the date to a more readable format.
     * @param {String} date - The date string.
     * @returns {String} - Formatted date.
     */
    formatDate(date) {
      return dayjs(date).format('dddd, D MMMM YYYY')
    },

    /**
     * Formats the time to a more readable format.
     * @param {String} time - The time string.
     * @returns {String} - Formatted time or original string if invalid.
     */
    formatTime(time) {
      // Attempt parsing with 'HH:mm:ss' first
      let parsedTime = dayjs(time, 'HH:mm:ss', true)

      if (!parsedTime.isValid()) {
        // Attempt parsing with 'HH:mm'
        parsedTime = dayjs(time, 'HH:mm', true)
      }

      if (parsedTime.isValid()) {
        return parsedTime.format('h:mm A') // e.g., "2:30 PM"
      } else {
        console.warn('Invalid time format:', time)
        return time // Return original string if parsing fails
      }
    },

    /**
     * Handles the join class action with eligibility validation and confirmation dialog.
     */
    async handleJoinClass() {
      const token = localStorage.getItem('token')
      const idClass = this.classDetails.idClass

      if (!token) {
        Swal.fire('Error', 'You need to log in to join a class.', 'error')
        return
      }

      this.joinButtonDisabled = true // Disable button to prevent multiple clicks

      try {
        // Show loading indicator
        Swal.fire({
          title: 'Checking Eligibility...',
          allowOutsideClick: false,
          didOpen: () => {
            Swal.showLoading()
          },
        })

        // Call eligibility API
        const eligibilityResponse = await axios.post(
          'https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/class/check-eligibility',
          {
            idClass: idClass,
            token: token,
          },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          },
        )

        Swal.close() // Close the loading indicator

        const { validMember, validVisit, alreadyBooked } =
          eligibilityResponse.data

        // Check if already booked
        if (alreadyBooked) {
          Swal.fire(
            'Already Booked',
            'You have already booked this class.',
            'info',
          )
          this.joinButtonDisabled = false
          return
        }

        // Check membership/visit validity
        if (!validMember && !validVisit) {
          Swal.fire({
            title: 'Membership Required',
            text: 'You need to purchase a membership or visit package to join this class.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Buy Membership',
            cancelButtonText: 'Cancel',
          }).then(result => {
            if (result.isConfirmed) {
              // Navigate to the membership purchase page
              this.$router.push({ name: 'BuyMembership' })
            }
            this.joinButtonDisabled = false
          })
          return
        }

        // Proceed to select type
        this.promptJoinType(validMember, validVisit)
      } catch (err) {
        Swal.close() // Ensure the loading indicator is closed in case of error
        // Handle specific error messages if available
        if (err.response && err.response.data && err.response.data.message) {
          Swal.fire('Error', err.response.data.message, 'error')
        } else {
          Swal.fire(
            'Error',
            'Failed to check eligibility. Please try again later.',
            'error',
          )
        }
        this.joinButtonDisabled = false
      }
    },

    /**
     * Prompts the user to select the type of participation based on eligibility.
     * @param {Boolean} validMember - Indicates if the user has a valid member package.
     * @param {Boolean} validVisit - Indicates if the user has a valid visit package.
     */
    promptJoinType(validMember, validVisit) {
      // Determine available types
      const availableTypes = []
      if (validMember) availableTypes.push('member')
      if (validVisit) availableTypes.push('visit')

      // Create HTML for type selection
      let htmlContent = '<p>Select the type of participation:</p>'
      htmlContent += '<div class="d-flex justify-content-center">'
      availableTypes.forEach(type => {
        const btnClass =
          type === 'member'
            ? 'btn-outline-primary me-2'
            : 'btn-outline-primary me-2'
        const capitalizedType = type.charAt(0).toUpperCase() + type.slice(1)
        htmlContent += `<button id="${type}-btn" class="btn ${btnClass}">${capitalizedType}</button>`
      })
      htmlContent += '</div>'

      Swal.fire({
        title: 'Join Class',
        html: htmlContent,
        showConfirmButton: false,
        showCancelButton: true,
        cancelButtonText: 'Cancel',
        didOpen: () => {
          availableTypes.forEach(type => {
            const btn = Swal.getPopup().querySelector(`#${type}-btn`)
            btn.addEventListener('click', () => {
              Swal.close()
              this.confirmJoinClass(type)
            })
          })
        },
      })
    },

    /**
     * Confirms the join class action with the selected type.
     * @param {String} type - The type of participation ('member' or 'visit').
     */
    async confirmJoinClass(type) {
      Swal.fire({
        title: 'Confirm Join',
        text: `Do you want to join this class as a ${type}?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: `Yes, join as ${type}`,
        cancelButtonText: 'Cancel',
      }).then(async result => {
        if (result.isConfirmed) {
          try {
            const token = localStorage.getItem('token')
            if (!token) {
              Swal.fire('Error', 'You need to log in to join a class.', 'error')
              return
            }

            // Show loading indicator
            Swal.fire({
              title: 'Joining Class...',
              allowOutsideClick: false,
              didOpen: () => {
                Swal.showLoading()
              },
            })

            // API call to book the class
            await axios.post(
              'https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/class/book',
              {
                idClass: this.classDetails.idClass,
                type: type,
                token: token,
              },
              {
                headers: {
                  'Content-Type': 'application/json',
                  Authorization: `Bearer ${token}`,
                },
              },
            )

            Swal.fire(
              'Joined!',
              `You have successfully joined the class as a ${type}.`,
              'success',
            ).then(() => {
              // Optionally, refresh the class details to show the new member
              this.fetchClassDetail()
            })
          } catch (err) {
            Swal.close() // Ensure the loading indicator is closed in case of error
            // Handle specific error messages if available
            if (
              err.response &&
              err.response.data &&
              err.response.data.message
            ) {
              Swal.fire('Error', err.response.data.message, 'error')
            } else {
              Swal.fire(
                'Error',
                'Failed to join the class. Please try again later.',
                'error',
              )
            }
          } finally {
            this.joinButtonDisabled = false
          }
        }
      })
    },

    /**
     * Debounce function to limit the rate at which a function can fire.
     * @param {Function} func - The function to debounce.
     * @param {Number} wait - The delay in milliseconds.
     * @returns {Function} - The debounced function.
     */
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
    // Initialize the debounced methods
    this.debouncedHandleJoinClass = this.debounce(this.handleJoinClass, 300)
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

/* Join Button Disabled State */
.btn[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
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
