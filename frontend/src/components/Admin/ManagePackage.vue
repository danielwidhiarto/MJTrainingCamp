<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Packages</h1>

      <!-- Button to Trigger Add Package Modal -->
      <div class="d-flex justify-content-between mb-3">
        <h5></h5>
        <button class="btn add-package-button" @click="isAddModalOpen = true">
          Add New Package
        </button>
      </div>

      <!-- Membership Packages Table -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white">Membership Packages</div>
        <div class="card-body">
          <table class="table table-hover" v-if="membershipPackages.length > 0">
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
                <td>Rp {{ packageItem.price.toLocaleString() }}</td>
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
        <div class="card-header bg-dark text-white">Visit Packages</div>
        <div class="card-body">
          <table class="table table-hover" v-if="visitPackages.length > 0">
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
                <td>Rp {{ packageItem.price.toLocaleString() }}</td>
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
          <button type="submit" class="btn add-package-button w-100">
            Add Package
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
  name: 'ManagePackage',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      packages: [],
      newPackage: {
        packageName: '',
        price: '',
        type: '',
        visitNumber: '',
        duration: '',
        status: 'available',
      },
      token: null,
      isAddModalOpen: false,
      sortKey: 'packageName',
    }
  },
  created() {
    this.token = localStorage.getItem('token')
    if (this.token) {
      this.fetchPackages()
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
      return this.membershipPackages.sort((a, b) =>
        a[this.sortKey] < b[this.sortKey] ? -1 : 1,
      )
    },
    sortedVisitPackages() {
      return this.visitPackages.sort((a, b) =>
        a[this.sortKey] < b[this.sortKey] ? -1 : 1,
      )
    },
  },
  methods: {
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
        this.packages = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to fetch packages.', 'error')
      }
    },
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

        this.packages.push(response.data)

        this.newPackage = {
          packageName: '',
          price: '',
          type: '',
          visitNumber: '',
          duration: '',
          status: 'available',
        }

        this.isAddModalOpen = false

        Swal.fire({
          title: 'Package Added!',
          text: 'The new package has been added successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
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
    onTypeChange() {
      if (this.newPackage.type === 'Visit') {
        this.newPackage.duration = ''
      } else if (this.newPackage.type === 'Membership') {
        this.newPackage.visitNumber = ''
      }
    },
    sort(key) {
      this.sortKey = key
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

.add-package-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  transition: background-color 0.3s ease;
}

.add-package-button:hover {
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

/* Responsive Styles */
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
