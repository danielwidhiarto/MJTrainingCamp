<template>
  <div>
    <Navbar />
    <div class="container mx-auto p-6 mt-6">
      <h1 class="text-center mb-6">Manage Member Transactions</h1>

      <!-- Submitted Transactions List -->
      <div class="card shadow-sm mb-6">
        <div
          class="card-header bg-dark text-white d-flex justify-content-between align-items-center"
        >
          <span>Submitted Transactions</span>
          <button class="btn btn-secondary btn-sm" @click="debouncedToggleSort">
            {{ showPendingOnly ? 'Show All' : 'Show Pending Only' }}
          </button>
        </div>
        <div class="card-body">
          <table
            class="table table-hover table-responsive"
            v-if="filteredTransactions.length > 0"
          >
            <thead class="thead-light">
              <tr>
                <th>Transaction ID</th>
                <th>Package Type</th>
                <th>Payment Method</th>
                <th>Price</th>
                <th>Payment Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="transaction in filteredTransactions"
                :key="transaction.idTransaction"
                :class="statusClass(transaction.paymentStatus)"
              >
                <td>{{ transaction.idTransaction }}</td>
                <td>{{ transaction.paymentType }}</td>
                <td>{{ formatPaymentMethod(transaction.paymentMethod) }}</td>
                <td>{{ formatPrice(transaction.transactionPrice) }}</td>
                <td>{{ transaction.paymentStatus }}</td>
                <td>
                  <!-- Always show 'View Proof' button -->
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
                class="img-fluid"
                style="max-width: 400px"
              />
            </div>

            <!-- Conditionally show 'Accept' and 'Decline' buttons in modal if status is 'WAITING FOR APPROVAL' -->
            <div
              v-if="
                selectedTransaction.paymentStatus === 'WAITING FOR APPROVAL'
              "
              class="mt-4 d-flex justify-content-between"
            >
              <button
                class="btn btn-danger"
                @click="
                  updateTransactionStatus(
                    selectedTransaction.idTransaction,
                    'DECLINED',
                  )
                "
              >
                Decline
              </button>
              <button
                class="btn btn-success"
                @click="
                  updateTransactionStatus(
                    selectedTransaction.idTransaction,
                    'VERIFIED',
                  )
                "
              >
                Accept
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
import axios from 'axios'
import Swal from 'sweetalert2'
import { debounce } from 'lodash'
import { ref, onMounted, computed } from 'vue'

export default {
  name: 'VerifyPayment',
  components: { Navbar },
  setup() {
    const transactions = ref([])
    const selectedTransaction = ref({})
    const isModalOpen = ref(false)
    const isLoadingDetails = ref(false)
    const showPendingOnly = ref(true)

    /**
     * Debounced toggleSort to prevent rapid toggling
     */
    const toggleSort = () => {
      showPendingOnly.value = !showPendingOnly.value
    }

    const debouncedToggleSort = debounce(toggleSort, 300)

    /**
     * Computed property to filter transactions based on payment status
     */
    const filteredTransactions = computed(() => {
      if (showPendingOnly.value) {
        return transactions.value.filter(
          t => t.paymentStatus === 'WAITING FOR APPROVAL',
        )
      }
      return transactions.value
    })

    /**
     * Fetch transactions from the API (without images)
     */
    const fetchTransactions = async () => {
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

        const response = await axios.get(
          'http://https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/transaction/get',
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        // Assuming the initial API does not return 'buktiTransfer' to prevent loading all images
        transactions.value = response.data.map(transaction => ({
          ...transaction,
          // Remove the buktiTransfer field if present
          buktiTransfer: undefined,
        }))
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to load transactions.', 'error')
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
          `http://https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/transaction/get?id=${id}`,
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
     * Update transaction status (VERIFIED or DECLINED)
     * @param {Number} id - Transaction ID
     * @param {String} status - New status
     */
    const updateTransactionStatus = async (id, status) => {
      try {
        const token = localStorage.getItem('token')
        await axios.patch(
          `http://https://ec2-13-229-129-198.ap-southeast-1.compute.amazonaws.com/api/v1/transaction/update/${id}`,
          { transactionStatus: status },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          },
        )
        Swal.fire(
          'Success',
          `Transaction ${status.toLowerCase()} successfully.`,
          'success',
        )
        closeModal()
        await fetchTransactions() // Refresh transactions list
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to update transaction status.', 'error')
      }
    }

    /**
     * Check if transaction is VERIFIED or DECLINED
     * @param {String} status - Payment status
     * @returns {Boolean}
     */
    const isVerifiedOrDeclined = status => {
      return status === 'VERIFIED' || status === 'DECLINED'
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
      showPendingOnly,
      filteredTransactions,
      showDetails,
      closeModal,
      updateTransactionStatus,
      isVerifiedOrDeclined,
      statusClass,
      toggleSort,
      debouncedToggleSort,
      formatPrice,
      formatPaymentMethod,
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

/* Responsive Styles */
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

  .btn-success,
  .btn-danger {
    width: 48%;
  }
}

@media (max-width: 480px) {
  h1 {
    font-size: 1.75rem;
  }

  .custom-modal-content {
    padding: 15px;
  }

  .btn-success,
  .btn-danger {
    width: 100%;
    margin-bottom: 10px;
  }

  .btn-success:last-child,
  .btn-danger:last-child {
    margin-bottom: 0;
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
