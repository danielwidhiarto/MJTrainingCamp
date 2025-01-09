<template>
  <div>
    <Navbar />

    <!-- Image Carousel -->
    <div
      id="trainingCarousel"
      class="carousel slide mt-4"
      data-bs-ride="carousel"
    >
      <div class="carousel-indicators">
        <button
          type="button"
          data-bs-target="#trainingCarousel"
          data-bs-slide-to="0"
          class="active"
          aria-current="true"
          aria-label="Slide 1"
        ></button>
        <button
          type="button"
          data-bs-target="#trainingCarousel"
          data-bs-slide-to="1"
          aria-label="Slide 2"
        ></button>
        <button
          type="button"
          data-bs-target="#trainingCarousel"
          data-bs-slide-to="2"
          aria-label="Slide 3"
        ></button>
        <button
          type="button"
          data-bs-target="#trainingCarousel"
          data-bs-slide-to="3"
          aria-label="Slide 4"
        ></button>
      </div>
      <div class="carousel-inner">
        <div class="carousel-item active">
          <img
            src="../../assets/img4.jpeg"
            class="d-block w-100 carousel-image"
            alt="MJ Fight Team"
          />
          <div class="carousel-caption d-none d-md-block">
            <h5>MJ Fight Team</h5>
          </div>
        </div>
        <div class="carousel-item">
          <img
            src="../../assets/img1.jpeg"
            class="d-block w-100 carousel-image"
            alt="Mitra, Head Coach at Byon 3"
          />
          <div class="carousel-caption d-none d-md-block">
            <h5>Mitra, Head Coach at Byon 3</h5>
          </div>
        </div>
        <div class="carousel-item">
          <img
            src="../../assets/img2.jpeg"
            class="d-block w-100 carousel-image"
            alt="Mitra, Head Coach at Byon 3"
          />
          <div class="carousel-caption d-none d-md-block">
            <h5>Mitra, Head Coach at Byon 3</h5>
          </div>
        </div>
        <div class="carousel-item">
          <img
            src="../../assets/img3.jpeg"
            class="d-block w-100 carousel-image"
            alt="Hadirman Waruwu at HSS"
          />
          <div class="carousel-caption d-none d-md-block">
            <h5>Hadirman Waruwu at HSS</h5>
          </div>
        </div>
      </div>
      <button
        class="carousel-control-prev"
        type="button"
        data-bs-target="#trainingCarousel"
        data-bs-slide="prev"
      >
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Previous</span>
      </button>
      <button
        class="carousel-control-next"
        type="button"
        data-bs-target="#trainingCarousel"
        data-bs-slide="next"
      >
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
        <span class="visually-hidden">Next</span>
      </button>
    </div>

    <div class="container mt-5">
      <h1 class="mb-5 text-center primary-text">
        Welcome to MJ Training Camp, {{ userName }} !
      </h1>

      <!-- Overview of MJ Training Camp -->
      <div class="card mb-5 overview-card">
        <div class="card-body">
          <h2 class="card-title">About MJ Training Camp</h2>
          <p class="card-text">
            MJ Training Camp is your premier fitness destination dedicated to
            helping you achieve your fitness goals. We offer a wide range of
            classes from beginner to advanced levels, ensuring that there's
            something for everyone.
          </p>
        </div>
      </div>

      <!-- Upcoming Classes and Membership Details -->
      <div class="row">
        <!-- Upcoming Classes Section -->
        <div class="col-lg-6 mb-4">
          <div class="card h-100 shadow-sm class-history-card">
            <div class="card-body">
              <h3 class="card-title">Upcoming Classes</h3>

              <!-- Loading Indicator -->
              <div
                v-if="loadingClasses"
                class="loading-indicator"
                aria-live="polite"
              >
                <div class="spinner"></div>
                <span>Loading upcoming classes...</span>
              </div>

              <!-- Error Message -->
              <div
                v-if="classesError"
                class="alert alert-danger text-center my-3"
                role="alert"
              >
                {{ classesError }}
              </div>

              <!-- Upcoming Classes Table -->
              <div
                v-if="!loadingClasses && upcomingClasses.length"
                class="class-history-container"
              >
                <table class="class-history-table">
                  <thead>
                    <tr>
                      <th>Class Name</th>
                      <th>Date</th>
                      <th>Time</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="classItem in upcomingClasses"
                      :key="classItem.idClass"
                    >
                      <td>{{ classItem.className }}</td>
                      <td>{{ formatDate(classItem.classDate) }}</td>
                      <td>{{ formatTime(classItem.classTime) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <!-- No Upcoming Classes Message -->
              <div
                v-else-if="!loadingClasses && !upcomingClasses.length"
                class="no-history text-center"
              >
                You have no upcoming classes.
              </div>
              <button class="btn btn-primary mt-3" @click="goToClassDetail">
                View More Details
              </button>
            </div>
          </div>
        </div>

        <!-- Enhanced Membership Details -->
        <div class="col-lg-6 mb-4">
          <div class="card h-100 shadow-sm membership-card">
            <div class="card-body">
              <h3 class="card-title">Your Membership Details</h3>

              <!-- Current Memberships -->
              <div v-if="currentMemberships.length > 0" class="mb-4">
                <h5 class="membership-section-title">
                  <i class="fas fa-clipboard-list me-2 text-success"></i>
                  Current Memberships
                </h5>
                <ul class="list-group list-group-flush">
                  <li
                    v-for="membership in currentMemberships"
                    :key="membership.idMember"
                    class="list-group-item membership-item d-flex justify-content-between align-items-center"
                  >
                    <div>
                      <h6 class="mb-1">{{ membership.packageName }}</h6>
                      <p class="mb-0">
                        Duration: {{ membership.duration }} Months
                      </p>
                      <p class="mb-0">
                        Ends On: {{ formatDate(membership.endDate) }}
                      </p>
                    </div>
                    <span class="badge bg-success rounded-pill">Active</span>
                  </li>
                </ul>
              </div>
              <div v-else class="mb-4">
                <p class="no-membership">
                  You do not have any active memberships.
                </p>
              </div>

              <!-- Upcoming Memberships -->
              <div v-if="upcomingMemberships.length > 0" class="mb-4">
                <h5 class="membership-section-title">
                  <i class="fas fa-calendar-alt me-2 text-primary"></i>
                  Upcoming Memberships
                </h5>
                <ul class="list-group list-group-flush">
                  <li
                    v-for="membership in upcomingMemberships"
                    :key="membership.idMember"
                    class="list-group-item membership-item d-flex justify-content-between align-items-center"
                  >
                    <div>
                      <h6 class="mb-1">{{ membership.packageName }}</h6>
                      <p class="mb-0">
                        Duration: {{ membership.duration }} Months
                      </p>
                      <p class="mb-0">
                        Starts On: {{ formatDate(membership.startDate) }}
                      </p>
                    </div>
                    <span class="badge bg-info rounded-pill">Upcoming</span>
                  </li>
                </ul>
              </div>
              <div v-else class="mb-4">
                <p class="no-membership">
                  You do not have any upcoming memberships.
                </p>
              </div>

              <!-- Visits Left -->
              <div class="mb-4">
                <h5 class="membership-section-title">
                  <i class="fas fa-calendar-check me-2 text-warning"></i>
                  Visits Left
                </h5>
                <div class="d-flex align-items-center">
                  <span class="visits-number">{{ visitsLeft }}</span>
                  <div class="progress flex-grow-1 ms-3">
                    <div
                      class="progress-bar bg-warning"
                      role="progressbar"
                      :style="{ width: visitsProgress + '%' }"
                      aria-valuenow="visitsLeft"
                      aria-valuemin="0"
                      aria-valuemax="100"
                    ></div>
                  </div>
                </div>
              </div>

              <!-- View Membership Button -->
              <button class="btn btn-primary mt-3" @click="navigateToProfile">
                View More Details
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'
import Swal from 'sweetalert2' // Import SweetAlert2
import dayjs from 'dayjs' // Import Day.js for date formatting
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'
import axios from 'axios' // Import Axios for HTTP requests

// Extend dayjs with UTC and Timezone plugins
dayjs.extend(utc)
dayjs.extend(timezone)

export default {
  name: 'MemberDashboard',
  components: {
    Navbar,
  },
  data() {
    return {
      userName: '', // Menyimpan username yang diambil dari localStorage

      // Membership Related Data
      currentMemberships: [], // Array to store current memberships
      upcomingMemberships: [], // Array to store upcoming memberships
      visitsLeft: 0, // To store number of visits left
      totalVisits: 100, // Assuming total visits for progress bar

      // Upcoming Classes Related Data
      classHistory: [], // All classes from history
      upcomingClasses: [], // Filtered upcoming classes
      pastClasses: [], // Filtered past classes
      loadingClasses: false, // Loading state for classes
      classesError: null, // Error state for classes
    }
  },
  computed: {
    visitsProgress() {
      // Calculate progress percentage based on visits left
      if (this.totalVisits === 0) return 0
      return ((this.visitsLeft / this.totalVisits) * 100).toFixed(0)
    },
  },
  created() {
    this.userName = localStorage.getItem('name') || 'Guest' // Default ke 'Guest' jika username tidak ditemukan

    // Fetch membership and class data when component is created
    this.fetchMembershipDetails()
    this.fetchClassHistory()
  },
  methods: {
    // --- Membership Methods ---

    /**
     * Formats the date to "Month Day, Year".
     * @param {String} dateString - The date string.
     * @returns {String} - Formatted date.
     */
    formatDate(dateString) {
      return dayjs(dateString).format('MMMM D, YYYY') // Format date to 'Month Day, Year'
    },

    /**
     * Fetches membership details from the API.
     */
    async fetchMembershipDetails() {
      const token = localStorage.getItem('token')
      if (!token) {
        console.warn(
          'No token found. Membership information cannot be fetched.',
        )
        return
      }

      try {
        const response = await axios.post(
          'https://mjtrainingcamp.my.id/api/v1/class/checkMembership',
          {
            token: token,
          },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          },
        )

        const data = response.data

        // Check if memberships exist
        if (data.memberships && data.memberships.length > 0) {
          const today = dayjs()

          data.memberships.forEach(membership => {
            const startDate = dayjs(membership.startDate)
            const endDate = dayjs(membership.endDate)

            if (endDate.isAfter(today) && startDate.isBefore(today)) {
              // Current Membership
              this.currentMemberships.push({
                idMember: membership.idMember,
                duration: membership.duration, // Duration in months
                endDate: membership.endDate, // End date as string
                packageName:
                  membership.packageName || `Package ${membership.idMember}`, // Replace with actual package name if available
              })
            } else if (startDate.isAfter(today)) {
              // Upcoming Membership
              this.upcomingMemberships.push({
                idMember: membership.idMember,
                duration: membership.duration,
                startDate: membership.startDate,
                endDate: membership.endDate,
                packageName:
                  membership.packageName || `Package ${membership.idMember}`, // Replace with actual package name if available
              })
            }
          })

          // Calculate visits left
          if (data.visitDetails && data.visitDetails.length > 0) {
            // Sum up all visits left from visitDetails
            this.visitsLeft = data.visitDetails.reduce((acc, visit) => {
              return acc + (visit.visitNumber - visit.visitUsed)
            }, 0)
          } else {
            this.visitsLeft = 0
          }
        } else {
          this.currentMemberships = []
          this.upcomingMemberships = []
          this.visitsLeft = 0
        }
      } catch (error) {
        console.error('Error fetching membership data:', error)
        Swal.fire({
          title: 'Error',
          text: 'Failed to fetch membership information.',
          icon: 'error',
          confirmButtonColor: '#ff4500',
        })
      }
    },

    /**
     * Navigates to the Profile page for more detailed information.
     */
    navigateToProfile() {
      this.$router.push('/member/profile-page')
    },

    // --- Upcoming Classes Methods ---

    /**
     * Fetches class history from the API.
     */
    async fetchClassHistory() {
      this.loadingClasses = true
      this.classesError = null

      try {
        const token = localStorage.getItem('token')
        if (!token) {
          Swal.fire({
            icon: 'info',
            title: 'Login Required',
            text: 'No authentication token found. Please log in.',
          })
          return
        }

        const response = await axios.post(
          'https://mjtrainingcamp.my.id/api/v1/class/getHistory',
          {
            token: token,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          },
        )

        if (Array.isArray(response.data)) {
          this.classHistory = response.data
          this.splitClassesByDate(this.classHistory)
        } else {
          this.classHistory = []
          this.upcomingClasses = []
          this.pastClasses = []
          this.classesError = 'Invalid data format received from server.'
        }
      } catch (err) {
        console.error('API Request Error:', err)
        this.classesError =
          err.response?.data?.message || 'An unexpected error occurred.'
        this.classHistory = []
        this.upcomingClasses = []
        this.pastClasses = []
      } finally {
        this.loadingClasses = false
      }
    },

    /**
     * Splits classes into upcoming and past based on the current date.
     * @param {Array} classes - Array of class objects.
     */
    splitClassesByDate(classes) {
      const now = dayjs()
      this.upcomingClasses = classes.filter(classItem =>
        dayjs(classItem.classDate).isAfter(now, 'day'),
      )
      this.pastClasses = classes.filter(classItem =>
        dayjs(classItem.classDate).isBefore(now, 'day'),
      )
    },

    /**
     * Cancels a booking for a class.
     * @param {String} classId - The ID of the class to cancel.
     */
    async cancelBooking(classId) {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          Swal.fire({
            icon: 'info',
            title: 'Login Required',
            text: 'No authentication token found. Please log in.',
          })
          return
        }

        const confirmation = await Swal.fire({
          title: 'Are you sure?',
          text: 'Do you want to cancel this booking?',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonText: 'Yes, cancel it!',
          cancelButtonText: 'No, keep it',
        })

        if (!confirmation.isConfirmed) return

        const response = await axios.post(
          'https://mjtrainingcamp.my.id/api/v1/class/cancel',
          {
            idClass: classId,
            token: token,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          },
        )

        Swal.fire({
          icon: 'success',
          title: 'Cancelled!',
          text: 'Your booking has been successfully canceled.',
        })

        // Refresh the class history after cancellation
        this.fetchClassHistory()
      } catch (err) {
        console.error('Cancellation Error:', err)
        Swal.fire({
          icon: 'error',
          title: 'Error!',
          text: err.response?.data?.message || 'An unexpected error occurred.',
        })
      }
    },

    /**
     * Navigates to the class detail page.
     * @param {String} idClass - The ID of the class to view details.
     */
    goToClassDetail(idClass) {
      this.$router.push({ name: 'ClassDetail', params: { id: idClass } })
    },

    /**
     * Formats the time to "h:mm A".
     * @param {String} time - The time string.
     * @returns {String} - Formatted time.
     */
    formatTime(time) {
      if (!time) return 'N/A'

      let parsedTime = dayjs(time, 'HH:mm:ss', true)
      if (!parsedTime.isValid()) {
        parsedTime = dayjs(time, 'HH:mm', true)
      }

      return parsedTime.isValid() ? parsedTime.format('h:mm A') : time
    },
  },
}
</script>

<style scoped>
/* General Container Styling */
.container {
  background-color: #f4f7f6; /* Light background for contrast */
  border-radius: 8px;
  padding: 20px;
}

/* Primary Text Color */
.primary-text {
  color: #ff4500; /* Orange-red color for consistency */
}

/* Overview Card Styling */
.overview-card {
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.overview-card:hover {
  transform: translateY(-5px); /* Lift effect on hover */
}

.overview-card .card-title {
  color: #ff4500; /* Orange-red color for consistency */
  font-weight: bold;
}

/* Membership Card Styling */
.membership-card .card-title {
  color: #28a745; /* Bootstrap success color */
  font-weight: bold;
}

.membership-section-title {
  font-weight: bold;
  margin-bottom: 15px;
}

.membership-section-title i {
  font-size: 1.2em;
}

.membership-item {
  padding: 15px;
  border: none;
  border-bottom: 1px solid #e9ecef;
  transition:
    background-color 0.3s,
    border-left-color 0.3s;
}

.membership-item:last-child {
  border-bottom: none;
}

.membership-item:hover {
  background-color: #f8f9fa;
}

.membership-item h6 {
  margin-bottom: 5px;
  color: #333;
}

.membership-item p {
  margin-bottom: 3px;
  color: #555;
}

.no-membership {
  color: #888;
  font-style: italic;
}

/* Visits Left Styling */
.visits-number {
  font-size: 1.5rem;
  font-weight: bold;
  color: #ffc107; /* Bootstrap warning color */
}

.progress {
  height: 20px;
}

.progress-bar {
  transition: width 0.6s ease;
}

/* Buttons Styling */
.btn-primary {
  background-color: #ff4500; /* Orange-red for buttons */
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.btn-primary:hover {
  background-color: #e03b00; /* Darker shade on hover */
}

.btn-secondary {
  background-color: #6c757d; /* Bootstrap secondary color */
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.btn-secondary:hover {
  background-color: #5a6268; /* Darker shade on hover */
}

/* Class History Card Styling */
.class-history-card .card-title {
  color: #007bff; /* Bootstrap primary color */
  font-weight: bold;
}

.class-history-card .section-title {
  font-size: 1.75rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.class-history-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  overflow-x: auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.class-history-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.class-history-table th,
.class-history-table td {
  padding: 12px 20px;
  border: 1px solid #ddd;
}

.class-history-table th {
  background-color: #f4f4f4;
  color: #333;
  font-size: 1.1rem;
}

.class-history-table tbody tr:nth-child(even) {
  background-color: #f9f9f9;
}

.class-history-table tbody tr:hover {
  background-color: #ffe5d9;
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

/* No History Message Styling */
.no-history {
  margin-top: 20px;
  font-size: 1.25rem;
  color: #777;
}

/* Action Buttons Styling */
.action-buttons {
  display: flex;
  gap: 10px; /* Adds space between the buttons */
  justify-content: center; /* Centers the buttons horizontally */
}

.action-buttons .btn {
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.action-buttons .btn-danger {
  background-color: #ff4500;
  color: white;
  border: none;
}

.action-buttons .btn-danger:hover {
  background-color: #e63900;
}

.action-buttons .btn-primary {
  background-color: #007bff;
  color: white;
  border: none;
}

.action-buttons .btn-primary:hover {
  background-color: #0056b3;
}

/* Carousel Image Styling */
.carousel-image {
  height: 400px;
  object-fit: contain; /* Changed from cover to contain */
  background-color: #000; /* Optional: Adds a background to fill empty space */
}

.carousel-caption {
  background: rgba(
    0,
    0,
    0,
    0.5
  ); /* Transparent black background for text visibility */
  padding: 10px 20px;
  border-radius: 5px;
}

/* Responsive Design Adjustments */
@media (max-width: 992px) {
  .carousel-image {
    height: 350px;
  }

  .visits-number {
    font-size: 1.3rem;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 15px;
  }

  .carousel-image {
    height: 300px;
  }

  .membership-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .badge {
    margin-top: 10px;
  }

  .class-history-table th,
  .class-history-table td {
    padding: 10px;
    font-size: 1rem;
  }
}

@media (max-width: 576px) {
  .carousel-image {
    height: 250px;
  }

  .visits-number {
    font-size: 1.2rem;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    text-align: center;
  }

  .action-buttons {
    flex-direction: column;
  }

  .class-history-table th,
  .class-history-table td {
    padding: 8px 10px;
    font-size: 0.9rem;
  }
}
</style>
