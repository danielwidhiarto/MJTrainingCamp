<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Packages</h1>

      <!-- Button to Trigger Add Package Modal - Positioned Right -->
      <div class="d-flex justify-content-between mb-3">
        <h5></h5>
        <button class="btn btn-success" @click="isAddModalOpen = true">
          Add New Package
        </button>
      </div>

      <!-- Membership Packages Table -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-primary text-white">Membership Packages</div>
        <div class="card-body">
          <table
            class="table table-striped"
            v-if="membershipPackages.length > 0"
          >
            <thead>
              <tr>
                <th @click="sort('packageName')">Package Name</th>
                <th @click="sort('price')">Price</th>
                <th>Duration (months)</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="packageItem in sortedMembershipPackages"
                :key="packageItem.id"
              >
                <td>{{ packageItem.packageName }}</td>
                <td>{{ packageItem.price }}</td>
                <td>{{ packageItem.duration }}</td>
                <td>{{ packageItem.status }}</td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">No membership packages found.</p>
          </div>
        </div>
      </div>

      <!-- Visit Packages Table -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-primary text-white">Visit Packages</div>
        <div class="card-body">
          <table class="table table-striped" v-if="visitPackages.length > 0">
            <thead>
              <tr>
                <th @click="sort('packageName')">Package Name</th>
                <th @click="sort('price')">Price</th>
                <th>Visit Number</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="packageItem in sortedVisitPackages"
                :key="packageItem.id"
              >
                <td>{{ packageItem.packageName }}</td>
                <td>{{ packageItem.price }}</td>
                <td>{{ packageItem.visitNumber }}</td>
                <td>{{ packageItem.status }}</td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">No visit packages found.</p>
          </div>
        </div>
      </div>

      <!-- Add Package Modal -->
      <CustomModal :visible="isAddModalOpen" @close="isAddModalOpen = false">
        <h5 class="modal-title mb-4 text-center">Add New Package</h5>
        <form @submit.prevent="addPackage">
          <div class="mb-3">
            <label for="packageName" class="form-label">Package Name</label>
            <input
              type="text"
              v-model="newPackage.packageName"
              class="form-control"
              id="packageName"
              required
              placeholder="Enter package name"
            />
          </div>
          <div class="mb-3">
            <label for="price" class="form-label">Price</label>
            <input
              type="number"
              v-model="newPackage.price"
              class="form-control"
              id="price"
              required
              placeholder="Enter package price"
            />
          </div>
          <div class="mb-3">
            <label for="type" class="form-label">Type</label>
            <select
              v-model="newPackage.type"
              class="form-select"
              @change="onTypeChange"
              required
            >
              <option value="" disabled>Select Package Type</option>
              <option value="Visit">Visit</option>
              <option value="Membership">Membership</option>
            </select>
          </div>
          <div v-if="newPackage.type === 'Visit'" class="mb-3">
            <label for="visitNumber" class="form-label">Visit Number</label>
            <input
              type="number"
              v-model="newPackage.visitNumber"
              class="form-control"
              id="visitNumber"
              required
              placeholder="Enter number of visits"
            />
          </div>
          <div v-if="newPackage.type === 'Membership'" class="mb-3">
            <label for="duration" class="form-label">Duration (months)</label>
            <select v-model="newPackage.duration" class="form-select" required>
              <option value="" disabled>Select Duration</option>
              <option v-for="month in 12" :key="month" :value="month">
                {{ month }} months
              </option>
            </select>
          </div>
          <button type="submit" class="btn btn-success w-100">
            Add Package
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
  name: 'ManagePackage',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      packages: [], // Store the list of packages
      newPackage: {
        packageName: '',
        price: '',
        type: '',
        visitNumber: '',
        duration: '',
        status: 'available', // Set status automatically
      },
      token: null, // Token for authorization will be fetched from localStorage
      isAddModalOpen: false, // Control the visibility of the add modal
      sortKey: 'packageName', // Sort key for sorting packages
    }
  },
  created() {
    // Fetch the token from localStorage
    this.token = localStorage.getItem('token')

    if (this.token) {
      this.fetchPackages() // Fetch packages when component is created
    } else {
      Swal.fire('Error', 'Token not found. Please log in.', 'error')
    }
  },
  computed: {
    membershipPackages() {
      return this.packages.filter(
        packageItem => packageItem.type === 'Membership',
      )
    },
    visitPackages() {
      return this.packages.filter(packageItem => packageItem.type === 'Visit')
    },
    sortedMembershipPackages() {
      return this.membershipPackages.sort((a, b) => {
        if (a[this.sortKey] < b[this.sortKey]) return -1
        if (a[this.sortKey] > b[this.sortKey]) return 1
        return 0
      })
    },
    sortedVisitPackages() {
      return this.visitPackages.sort((a, b) => {
        if (a[this.sortKey] < b[this.sortKey]) return -1
        if (a[this.sortKey] > b[this.sortKey]) return 1
        return 0
      })
    },
  },
  methods: {
    // Fetch packages from API
    async fetchPackages() {
      try {
        const response = await axios.get(
          'http://localhost:8081/api/v1/package/get',
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
          },
        )
        this.packages = response.data // Store fetched packages in the packages array
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch packages.', 'error')
      }
    },
    // Add a new package
    async addPackage() {
      try {
        const response = await axios.post(
          'http://localhost:8081/api/v1/package/add',
          this.newPackage,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        // Add the new package to the list
        this.packages.push(response.data)

        // Clear the form fields after adding
        this.newPackage = {
          packageName: '',
          price: '',
          type: '',
          visitNumber: '',
          duration: '',
          status: 'available', // Set status automatically
        }

        // Close the modal after adding the package
        this.isAddModalOpen = false

        // Show a success alert and refresh the page when the user presses "OK"
        Swal.fire({
          title: 'Package Added!',
          text: 'The new package has been added successfully.',
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
          'Failed to add the package. Please try again.',
          'error',
        )
      }
    },
    // Edit a package
    editPackage(packageId) {
      // Implement package editing logic here
    },
    // Delete a package
    deletePackage(packageId) {
      // Implement package deletion logic here
    },
    // Handle type change
    onTypeChange() {
      if (this.newPackage.type === 'Visit') {
        this.newPackage.duration = '' // Reset duration when type changes to Visit
      } else if (this.newPackage.type === 'Membership') {
        this.newPackage.visitNumber = '' // Reset visit number when type changes to Membership
      }
    },
    // Sort packages
    sort(key) {
      this.sortKey = key
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
