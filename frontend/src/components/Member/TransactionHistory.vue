<template>
  <div>
    <LazyNavbar />
    <div class="container mx-auto p-6 mt-6">
      <h1 class="text-center mb-6">Transaction History</h1>

      <!-- Loading Indicator -->
      <div v-if="loading" class="loading-indicator" aria-live="polite">
        <div class="spinner"></div>
        <span>Loading transactions...</span>
      </div>

      <!-- Error Message -->
      <div
        v-if="error"
        class="alert alert-danger text-center my-5"
        role="alert"
      >
        {{ error }}
      </div>

      <!-- Transactions Table -->
      <div v-if="!loading && transactions.length" class="card shadow-sm mb-6">
        <div
          class="card-header bg-dark text-white d-flex justify-content-between align-items-center"
        >
          <span>Submitted Transactions</span>
        </div>
        <div class="card-body">
          <table
            class="table table-hover table-responsive"
            v-if="transactions.length > 0"
          >
            <thead class="thead-light">
              <tr>
                <th>Transaction ID</th>
                <th>Transaction Date</th>
                <th>Package Type</th>
                <th>Payment Method</th>
                <th>Price</th>
                <th>Payment Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="transaction in transactions"
                :key="transaction.idTransaction"
                :class="statusClass(transaction.paymentStatus)"
              >
                <td>{{ transaction.idTransaction }}</td>
                <td>{{ formatDate(transaction.transactionDate) }}</td>
                <td>{{ transaction.paymentType }}</td>
                <td>{{ formatPaymentMethod(transaction.paymentMethod) }}</td>
                <td>{{ formatPrice(transaction.transactionPrice) }}</td>
                <td>{{ transaction.paymentStatus }}</td>
                <td>
                  <button
                    class="btn btn-info btn-sm"
                    @click="showDetails(transaction.idTransaction)"
                  >
                    View Proof
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else>
            <p class="text-center">No transactions found.</p>
          </div>
        </div>
      </div>

      <!-- Custom Modal for Transaction Details -->
      <div v-if="isModalOpen" class="custom-modal">
        <div class="custom-modal-content">
          <button class="btn-close" @click="closeModal">&times;</button>

          <!-- Loading Indicator Inside Modal -->
          <div v-if="isLoadingDetails" class="modal-loading">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
            <span>Loading transaction details...</span>
          </div>

          <!-- Transaction Details -->
          <div v-else>
            <h5 class="text-center mb-4">Transaction Details</h5>
            <p>
              <strong>Transaction ID:</strong>
              {{ selectedTransaction.idTransaction }}
            </p>
            <p>
              <strong>Member Name:</strong> {{ selectedTransaction.memberName }}
            </p>
            <p>
              <strong>Package Type:</strong>
              {{ selectedTransaction.paymentType }}
            </p>
            <p>
              <strong>Price:</strong>
              {{ formatPrice(selectedTransaction.transactionPrice) }}
            </p>
            <p v-if="selectedTransaction.paymentType === 'Visit'">
              <strong>Visit Number:</strong>
              {{ selectedTransaction.visitDetail.visitNumber }} Quota
            </p>
            <p v-else-if="selectedTransaction.paymentType === 'Membership'">
              <strong>Duration:</strong>
              {{ selectedTransaction.membershipDetail.duration }} Month
            </p>
            <p>
              <strong>Payment Method:</strong>
              {{ formatPaymentMethod(selectedTransaction.paymentMethod) }}
            </p>

            <div
              v-if="selectedTransaction.buktiTransfer"
              class="text-center mt-3"
            >
              <h5>Payment Proof</h5>
              <img
                :src="`data:image/jpeg;base64,${selectedTransaction.buktiTransfer}`"
                alt="Proof of Payment"
                class="img-fluid proof-image"
              />
            </div>
          </div>
          <p>
            <br />
            <strong>Decline Notes :</strong>
            {{ formatPaymentMethod(selectedTransaction.paymentNotes) }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import axios from 'axios'
import Swal from 'sweetalert2'
import { debounce } from 'lodash'
import { ref, onMounted, computed, defineAsyncComponent } from 'vue'

export default {
  name: 'TransactionHistory',
  components: {
    LazyNavbar: defineAsyncComponent(() => import('./Navbar.vue')),
  },
  setup() {
    const transactions = ref([])
    const selectedTransaction = ref({})
    const isModalOpen = ref(false)
    const isLoadingDetails = ref(false)
    const loading = ref(false)
    const error = ref(null)
    const formatDate = date => {
      return dayjs(date).format('dddd, DD MMM YYYY') // Example format: Monday, 01 Jan 2025
    }
    /**
     * Computed property to return all transactions without filtering
     */
    const filteredTransactions = computed(() => {
      return transactions.value
    })

    /**
     * Fetch transactions from the API
     */
    const fetchTransactions = async () => {
      loading.value = true
      error.value = null
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          Swal.fire(
            'Error',
            'Authentication token not found. Please log in.',
            'error',
          ).then(() => {
            // Redirect to Login page
            window.location.href = '/login' // Adjust the route as needed
          })
          return
        }

        const response = await axios.post(
          'https://mjtrainingcamp.my.id/api/v1/transaction/getByUser',
          {
            token: token, // Include token in the request body
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          },
        )

        // Assuming the API returns an array of transactions
        if (Array.isArray(response.data)) {
          transactions.value = response.data.map(transaction => ({
            ...transaction,
            // Remove the buktiTransfer field initially to optimize loading
            buktiTransfer: undefined,
          }))
        } else {
          console.warn(
            'Expected an array in response.data but received:',
            response.data,
          )
          transactions.value = [] // Fallback to an empty array if data is not in expected format
        }
      } catch (err) {
        console.error(err)
        Swal.fire('Error', 'Failed to load transactions.', 'error')
        error.value = 'Failed to load transactions.'
      } finally {
        loading.value = false
      }
    }

    /**
     * Fetch transaction details by ID (including the image)
     * @param {Number} id - Transaction ID
     */
    const fetchTransactionDetails = async id => {
      isLoadingDetails.value = true
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          Swal.fire(
            'Error',
            'Authentication token not found. Please log in.',
            'error',
          ).then(() => {
            window.location.href = '/login' // Adjust the route as needed
          })
          return
        }

        const response = await axios.get(
          `https://mjtrainingcamp.my.id/api/v1/transaction/get?id=${id}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        selectedTransaction.value = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to load transaction details.', 'error')
      } finally {
        isLoadingDetails.value = false
      }
    }

    /**
     * Show transaction details in modal by fetching data on-demand
     * @param {Number} id - Transaction ID
     */
    const showDetails = async id => {
      isModalOpen.value = true
      await fetchTransactionDetails(id)
    }

    /**
     * Close the modal
     */
    const closeModal = () => {
      isModalOpen.value = false
      selectedTransaction.value = {}
    }

    /**
     * Assign Bootstrap table classes based on payment status
     * @param {String} status - Payment status
     * @returns {String} - Bootstrap class
     */
    const statusClass = status => {
      if (status === 'VERIFIED') return 'table-success'
      if (status === 'DECLINED') return 'table-danger'
      return 'table-warning'
    }

    /**
     * Format price with thousand separators
     * @param {Number} price - Transaction price
     * @returns {String} - Formatted price
     */
    const formatPrice = price => {
      return new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        minimumFractionDigits: 0,
      }).format(price)
    }

    /**
     * Format payment method for better readability
     * @param {String} method - Payment method
     * @returns {String} - Formatted payment method
     */
    const formatPaymentMethod = method => {
      switch (method) {
        case 'credit_card':
          return 'Credit Card'
        case 'bank_transfer':
          return 'Bank Transfer'
        // Add more cases as needed
        default:
          return method
      }
    }

    onMounted(() => {
      fetchTransactions()
    })

    return {
      transactions,
      selectedTransaction,
      isModalOpen,
      isLoadingDetails,
      filteredTransactions,
      showDetails,
      closeModal,
      statusClass,
      formatPrice,
      formatPaymentMethod,
      loading,
      error,
      formatDate,
    }
  },
}
</script>

<style scoped>
/* Container Styling */
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  background-color: #f8f9fa;
  border-radius: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* Heading Styles */
h1 {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #ff4500;
}

.card-header {
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
}

.table th {
  vertical-align: middle;
}

.table td {
  vertical-align: middle;
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

/* Modal Styling */
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
  z-index: 1000;
}

.custom-modal-content {
  background-color: #fff;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  overflow-y: auto;
  max-height: 90vh; /* Prevents modal from exceeding viewport height */
}

.btn-close {
  position: absolute;
  top: 15px;
  right: 20px;
  font-size: 1.5rem;
  border: none;
  background: none;
  cursor: pointer;
  color: #333;
}

.btn-close:hover {
  color: #ff4500;
}

.modal-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.modal-loading .spinner-border {
  width: 3rem;
  height: 3rem;
  margin-bottom: 15px;
}

/* ProofImageStyling */
.proof-image {
  max-width: 300px; /* Adjust as needed */
  max-height: 300px; /* Adjust as needed */
  width: 100%;
  height: auto;
  object-fit: contain; /* Ensures the image maintains aspect ratio */
  border: 1px solid #ddd; /* Optional: Adds a subtle border */
  border-radius: 8px; /* Optional: Rounds the corners */
  padding: 5px; /* Optional: Adds padding around the image */
  background-color: #f9f9f9; /* Optional: Adds a background color */
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .container {
    padding: 30px 15px;
  }

  h1 {
    font-size: 2rem;
  }

  .custom-modal-content {
    padding: 20px;
  }

  .proof-image {
    max-width: 80%; /* Adjust for smaller screens */
    max-height: 200px;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 1.75rem;
  }

  .custom-modal-content {
    padding: 15px;
  }

  .proof-image {
    max-width: 100%;
    max-height: 150px;
  }
}

/* Additional Enhancements */
.table-responsive {
  overflow-x: auto;
}

.table-hover tbody tr:hover {
  background-color: #ffe5d9;
}

.btn-info {
  background-color: #17a2b8;
  border-color: #17a2b8;
}

.btn-info:hover {
  background-color: #138496;
  border-color: #117a8b;
}

.btn-success {
  background-color: #28a745;
  border-color: #28a745;
}

.btn-success:hover {
  background-color: #218838;
  border-color: #1e7e34;
}

.btn-danger {
  background-color: #dc3545;
  border-color: #dc3545;
}

.btn-danger:hover {
  background-color: #c82333;
  border-color: #bd2130;
}

.btn-secondary {
  background-color: #6c757d;
  border-color: #6c757d;
}

.btn-secondary:hover {
  background-color: #5a6268;
  border-color: #545b62;
}

/* Alert Styling */
.alert-danger {
  font-size: 1.3rem;
  font-weight: 600;
  color: #ff4500;
}
</style>
