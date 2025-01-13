<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="mb-4 text-center">Manage Packages</h1>

      <!-- Button to Trigger Add Package Modal -->
      <div class="d-flex justify-content-between mb-3">
        <h5>Available Packages</h5>
        <button class="btn add-package-button" @click="openAddModal">
          <i class="fas fa-plus"></i> Add New Package
        </button>
      </div>

      <!-- Membership Packages Table -->
      <div class="card shadow-sm mb-4">
        <div class="card-header bg-dark text-white">Membership Packages</div>
        <div class="card-body">
          <table class="table table-hover" v-if="membershipPackages.length > 0">
            <thead>
              <tr>
                <th @click="sort('packageName')">
                  Package Name
                  <i :class="getSortIcon('packageName')"></i>
                </th>
                <th @click="sort('price')">
                  Price
                  <i :class="getSortIcon('price')"></i>
                </th>
                <th @click="sort('duration')">
                  Duration (months)
                  <i :class="getSortIcon('duration')"></i>
                </th>
                <th @click="sort('status')">
                  Status
                  <i :class="getSortIcon('status')"></i>
                </th>
                <th>Actions</th>
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
                <td>
                  <span
                    :class="[
                      'badge',
                      packageItem.status === 'available'
                        ? 'bg-success'
                        : 'bg-secondary',
                    ]"
                  >
                    {{ packageItem.status }}
                  </span>
                </td>
                <td>
                  <button
                    class="btn btn-sm btn-primary me-2"
                    @click="editPackage(packageItem)"
                  >
                    <i class="fas fa-edit"></i> Edit
                  </button>
                  <button
                    class="btn btn-sm btn-danger"
                    @click="deletePackage(packageItem.id)"
                  >
                    <i class="fas fa-trash-alt"></i> Delete
                  </button>
                </td>
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
                <th @click="sort('packageName')">
                  Package Name
                  <i :class="getSortIcon('packageName')"></i>
                </th>
                <th @click="sort('price')">
                  Price
                  <i :class="getSortIcon('price')"></i>
                </th>
                <th @click="sort('visitNumber')">
                  Visit Number
                  <i :class="getSortIcon('visitNumber')"></i>
                </th>
                <th @click="sort('status')">
                  Status
                  <i :class="getSortIcon('status')"></i>
                </th>
                <th>Actions</th>
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
                <td>
                  <span
                    :class="[
                      'badge',
                      packageItem.status === 'available'
                        ? 'bg-success'
                        : 'bg-secondary',
                    ]"
                  >
                    {{ packageItem.status }}
                  </span>
                </td>
                <td>
                  <button
                    class="btn btn-sm btn-primary me-2"
                    @click="editPackage(packageItem)"
                  >
                    <i class="fas fa-edit"></i> Edit
                  </button>
                  <button
                    class="btn btn-sm btn-danger"
                    @click="deletePackage(packageItem.id)"
                  >
                    <i class="fas fa-trash-alt"></i> Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">No visit packages found.</p>
          </div>
        </div>
      </div>

      <!-- Add/Edit Package Modal -->
      <CustomModal :visible="isModalOpen" @close="closeModal">
        <h5 class="modal-title mb-4 text-center">
          {{ isEditMode ? 'Edit Package' : 'Add New Package' }}
        </h5>
        <form @submit.prevent="isEditMode ? updatePackage() : addPackage()">
          <div class="mb-3">
            <label for="packageName" class="form-label">Package Name</label>
            <input
              type="text"
              v-model="currentPackage.packageName"
              class="form-control"
              id="packageName"
              required
              placeholder="Enter package name"
              @input="sanitizePackageNameDebounced"
              aria-required="true"
              aria-describedby="packageNameHelp"
            />
            <small id="packageNameHelp" class="form-text text-muted">
              Please enter the name of the package.
            </small>
            <span v-if="errors.packageName" class="text-danger">{{
              errors.packageName
            }}</span>
          </div>
          <div class="mb-3">
            <label for="price" class="form-label">Price</label>
            <input
              type="number"
              v-model="currentPackage.price"
              class="form-control"
              id="price"
              required
              placeholder="Enter package price"
              @input="sanitizePriceDebounced"
              aria-required="true"
              aria-describedby="priceHelp"
            />
            <small id="priceHelp" class="form-text text-muted">
              Enter the price of the package in Rupiah.
            </small>
            <span v-if="errors.price" class="text-danger">{{
              errors.price
            }}</span>
          </div>
          <div class="mb-3">
            <label for="type" class="form-label">Type</label>
            <select
              v-model="currentPackage.type"
              class="form-select"
              @change="onTypeChange"
              required
              @input="sanitizeTypeDebounced"
              aria-required="true"
              aria-describedby="typeHelp"
            >
              <option value="" disabled>Select Package Type</option>
              <option value="Membership">Membership</option>
              <option value="Visit">Visit</option>
            </select>
            <small id="typeHelp" class="form-text text-muted">
              Select the type of the package.
            </small>
            <span v-if="errors.type" class="text-danger">{{
              errors.type
            }}</span>
          </div>
          <div v-if="currentPackage.type === 'Visit'" class="mb-3">
            <label for="visitNumber" class="form-label">Visit Number</label>
            <input
              type="number"
              v-model="currentPackage.visitNumber"
              class="form-control"
              id="visitNumber"
              required
              placeholder="Enter number of visits"
              @input="sanitizeVisitNumberDebounced"
              aria-required="true"
              aria-describedby="visitNumberHelp"
            />
            <small id="visitNumberHelp" class="form-text text-muted">
              Enter the number of visits allowed.
            </small>
            <span v-if="errors.visitNumber" class="text-danger">{{
              errors.visitNumber
            }}</span>
          </div>
          <div v-if="currentPackage.type === 'Membership'" class="mb-3">
            <label for="duration" class="form-label">Duration (months)</label>
            <select
              v-model="currentPackage.duration"
              class="form-select"
              required
              @input="sanitizeDurationDebounced"
              aria-required="true"
              aria-describedby="durationHelp"
            >
              <option value="" disabled>Select Duration</option>
              <option v-for="month in 24" :key="month" :value="month">
                {{ month }} months
              </option>
            </select>
            <small id="durationHelp" class="form-text text-muted">
              Select the duration of the membership.
            </small>
            <span v-if="errors.duration" class="text-danger">{{
              errors.duration
            }}</span>
          </div>
          <div class="mb-3">
            <label for="status" class="form-label">Status</label>
            <select
              v-model="currentPackage.status"
              class="form-select"
              required
              @input="sanitizeStatusDebounced"
              aria-required="true"
              aria-describedby="statusHelp"
            >
              <option value="available">Available</option>
              <option value="unavailable">Unavailable</option>
            </select>
            <small id="statusHelp" class="form-text text-muted">
              Set the status of the package.
            </small>
            <span v-if="errors.status" class="text-danger">{{
              errors.status
            }}</span>
          </div>
          <button type="submit" class="btn add-package-button w-100">
            {{ isEditMode ? 'Update Package' : 'Add Package' }}
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
  name: 'ManagePackage',
  components: {
    Navbar,
    CustomModal,
  },
  data() {
    return {
      packages: [],
      currentPackage: {
        id: null,
        packageName: '',
        price: '',
        type: '',
        visitNumber: '',
        duration: '',
        status: 'available',
      },
      token: null,
      isModalOpen: false,
      isEditMode: false,
      sortKey: 'packageName',
      sortOrders: {
        packageName: 1,
        price: 1,
        duration: 1,
        visitNumber: 1,
        status: 1,
      },
      errors: {}, // Object to hold validation error messages
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
      return this.sortedPackages(this.membershipPackages)
    },
    sortedVisitPackages() {
      return this.sortedPackages(this.visitPackages)
    },
  },
  methods: {
    // Fetch packages from the API
    async fetchPackages() {
      try {
        const response = await axios.get(
          'https://mjtrainingcamp.my.id/api/v1/package/get',
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

    // Open Add Package Modal
    openAddModal() {
      this.resetCurrentPackage()
      this.isEditMode = false
      this.isModalOpen = true
    },

    // Open Edit Package Modal
    editPackage(packageItem) {
      this.currentPackage = { ...packageItem }
      this.isEditMode = true
      this.isModalOpen = true
    },

    // Close Modal
    closeModal() {
      this.isModalOpen = false
      this.resetErrors()
    },

    // Reset Current Package Data
    resetCurrentPackage() {
      this.currentPackage = {
        id: null,
        packageName: '',
        price: '',
        type: '',
        visitNumber: '',
        duration: '',
        status: 'available',
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

    // Validation Methods
    sanitizePackageName() {
      this.currentPackage.packageName = this.sanitizeInput(
        this.currentPackage.packageName,
      )
      if (!this.currentPackage.packageName.trim()) {
        this.errors.packageName = 'Package Name is required.'
      } else {
        delete this.errors.packageName
      }
    },
    sanitizePackageNameDebounced: debounce(function () {
      this.sanitizePackageName()
    }, 300),

    sanitizePrice() {
      this.currentPackage.price = this.sanitizeInput(this.currentPackage.price)
      if (!this.currentPackage.price) {
        this.errors.price = 'Price is required.'
      } else if (this.currentPackage.price <= 0) {
        this.errors.price = 'Price must be a positive number.'
      } else {
        delete this.errors.price
      }
    },
    sanitizePriceDebounced: debounce(function () {
      this.sanitizePrice()
    }, 300),

    sanitizeType() {
      this.currentPackage.type = this.sanitizeInput(this.currentPackage.type)
      if (!this.currentPackage.type) {
        this.errors.type = 'Package Type is required.'
      } else {
        delete this.errors.type
      }
    },
    sanitizeTypeDebounced: debounce(function () {
      this.sanitizeType()
    }, 300),

    sanitizeVisitNumber() {
      if (this.currentPackage.type === 'Visit') {
        this.currentPackage.visitNumber = this.sanitizeInput(
          this.currentPackage.visitNumber,
        )
        if (!this.currentPackage.visitNumber) {
          this.errors.visitNumber = 'Visit Number is required.'
        } else if (this.currentPackage.visitNumber <= 0) {
          this.errors.visitNumber = 'Visit Number must be a positive integer.'
        } else {
          delete this.errors.visitNumber
        }
      }
    },
    sanitizeVisitNumberDebounced: debounce(function () {
      this.sanitizeVisitNumber()
    }, 300),

    sanitizeDuration() {
      if (this.currentPackage.type === 'Membership') {
        this.currentPackage.duration = this.sanitizeInput(
          this.currentPackage.duration,
        )
        if (!this.currentPackage.duration) {
          this.errors.duration = 'Duration is required.'
        } else if (this.currentPackage.duration <= 0) {
          this.errors.duration = 'Duration must be a positive integer.'
        } else {
          delete this.errors.duration
        }
      }
    },
    sanitizeDurationDebounced: debounce(function () {
      this.sanitizeDuration()
    }, 300),

    sanitizeStatus() {
      this.currentPackage.status = this.sanitizeInput(
        this.currentPackage.status,
      )
      if (!this.currentPackage.status) {
        this.errors.status = 'Status is required.'
      } else {
        delete this.errors.status
      }
    },
    sanitizeStatusDebounced: debounce(function () {
      this.sanitizeStatus()
    }, 300),

    // Handle Type Change
    onTypeChange() {
      if (this.currentPackage.type === 'Visit') {
        this.currentPackage.duration = ''
        delete this.errors.duration
      } else if (this.currentPackage.type === 'Membership') {
        this.currentPackage.visitNumber = ''
        delete this.errors.visitNumber
      }
      this.resetErrors()
    },

    // Sorting Logic
    sort(key) {
      if (this.sortKey === key) {
        this.sortOrders[key] = -this.sortOrders[key]
      } else {
        this.sortKey = key
      }
    },

    getSortIcon(key) {
      if (this.sortKey !== key) return 'fas fa-sort'
      return this.sortOrders[key] === 1 ? 'fas fa-sort-up' : 'fas fa-sort-down'
    },

    sortedPackages(packages) {
      return [...packages].sort((a, b) => {
        if (a[this.sortKey] < b[this.sortKey])
          return -1 * this.sortOrders[this.sortKey]
        if (a[this.sortKey] > b[this.sortKey])
          return 1 * this.sortOrders[this.sortKey]
        return 0
      })
    },

    // Add New Package
    async addPackage() {
      // Final validation
      this.sanitizePackageName()
      this.sanitizePrice()
      this.sanitizeType()
      if (this.currentPackage.type === 'Visit') {
        this.sanitizeVisitNumber()
      } else if (this.currentPackage.type === 'Membership') {
        this.sanitizeDuration()
      }
      this.sanitizeStatus()

      // Check for validation errors
      if (Object.keys(this.errors).length > 0) {
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
          'https://mjtrainingcamp.my.id/api/v1/package/add',
          this.currentPackage,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        this.packages.push(response.data)

        Swal.fire({
          title: 'Package Added!',
          text: 'The new package has been added successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          this.closeModal()
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

    // Update Existing Package
    async updatePackage() {
      // Final validation
      this.sanitizePackageName()
      this.sanitizePrice()
      this.sanitizeType()
      if (this.currentPackage.type === 'Visit') {
        this.sanitizeVisitNumber()
      } else if (this.currentPackage.type === 'Membership') {
        this.sanitizeDuration()
      }
      this.sanitizeStatus()

      // Check for validation errors
      if (Object.keys(this.errors).length > 0) {
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
          `https://mjtrainingcamp.my.id/api/v1/package/update/${this.currentPackage.id}`,
          this.currentPackage,
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${this.token}`,
            },
          },
        )

        const index = this.packages.findIndex(
          pkg => pkg.id === this.currentPackage.id,
        )
        if (index !== -1) {
          this.$set(this.packages, index, response.data)
        }

        Swal.fire({
          title: 'Package Updated!',
          text: 'The package has been updated successfully.',
          icon: 'success',
          confirmButtonText: 'OK',
        }).then(() => {
          this.closeModal()
        })
      } catch (error) {
        console.error(error)
        Swal.fire(
          'Error!',
          'Failed to update the package. Please try again.',
          'error',
        )
      }
    },

    // Delete Package
    async deletePackage(packageId) {
      Swal.fire({
        title: 'Are you sure?',
        text: 'Do you want to delete this package?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
      }).then(async result => {
        if (result.isConfirmed) {
          try {
            await axios.delete(
              `https://mjtrainingcamp.my.id/api/v1/package/delete/${packageId}`,
              {
                headers: {
                  Authorization: `Bearer ${this.token}`,
                },
              },
            )

            this.packages = this.packages.filter(pkg => pkg.id !== packageId)

            Swal.fire('Deleted!', 'The package has been deleted.', 'success')
          } catch (error) {
            console.error(error)
            Swal.fire(
              'Error!',
              'Failed to delete the package. Please try again.',
              'error',
            )
          }
        }
      })
    },
  },
  created() {
    this.token = localStorage.getItem('token')
    if (this.token) {
      this.fetchPackages()
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

.add-package-button {
  background-color: #ff4500;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  width: 100%;
  font-size: 1.1rem;
  transition: background-color 0.3s ease;
}

.add-package-button:hover {
  background-color: #e03b00;
}

/* Badge Styling */
.badge {
  font-size: 0.9rem;
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

  .add-package-button {
    padding: 8px 16px;
    font-size: 1rem;
  }
}
</style>
