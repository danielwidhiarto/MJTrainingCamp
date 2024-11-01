<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <!-- Back Button -->
      <button class="btn btn-link back-button" @click="goBack">
        <i class="bi bi-arrow-left-circle-fill"></i> Back to Classes
      </button>

      <!-- Loading Indicator -->
      <div v-if="loading" class="text-center my-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3">Loading class details...</p>
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
                <strong>Time:</strong> {{ formatTime(classDetails.classTime) }}
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
          <button class="btn btn-primary btn-lg" @click="handleJoinClass">
            Join Class
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import Navbar from './Navbar.vue'
import dayjs from 'dayjs'
import Swal from 'sweetalert2'

export default {
  name: 'ClassDetail',
  components: { Navbar },
  data() {
    return {
      classDetails: null,
      loading: false,
      error: null,
    }
  },
  methods: {
    /**
     * Navigates back to the previous page or to the ViewClass page.
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
          `http://localhost:8081/api/v1/class/getClasses?id=${idClass}`,
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
     * @returns {String} - Formatted time.
     */
    formatTime(time) {
      return dayjs(time, 'HH:mm:ss').format('h:mm A')
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
          'http://localhost:8081/api/v1/class/check-eligibility',
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
            : 'btn-outline-secondary'
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
              'http://localhost:8081/api/v1/class/book',
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
          }
        }
      })
    },
  },
  mounted() {
    this.fetchClassDetail()
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
  display: flex;
  align-items: center;
  font-size: 1.1rem;
  color: #ff4500;
  margin-bottom: 20px;
}

.back-button i {
  font-size: 1.5rem;
  margin-right: 8px;
}

/* Heading Styles */
h1 {
  font-size: 2.5rem;
  color: #ff4500;
}

h4 {
  margin-bottom: 20px;
  color: #333;
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

/* Responsive Adjustments */
@media (max-width: 768px) {
  h1 {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 1.8rem;
  }

  .card-title {
    font-size: 1.25rem;
  }

  .card-text {
    font-size: 1rem;
  }
}
</style>
