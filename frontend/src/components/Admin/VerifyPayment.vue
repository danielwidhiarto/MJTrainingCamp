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
                :alt="`Proof of Payment for Transaction ID ${selectedTransaction.idTransaction}`"
                class="img-fluid proof-image"
                loading="lazy"
                @error="handleImageError"
              />
            </div>
            <div v-else class="text-center mt-3">
              <p>No proof of payment available.</p>
            </div>

            <!-- Notes input field -->
            <div class="mt-4">
              <label for="notes" class="form-label">Notes</label>
              <textarea
                id="notes"
                v-model="notes"
                class="form-control"
                :class="{ 'is-invalid': notesError }"
                rows="4"
                placeholder="Enter your notes here..."
              ></textarea>
              <!-- Error Message -->
              <div v-if="notesError" class="invalid-feedback">
                Notes are required when declining a transaction.
              </div>
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
                  handleDecline(selectedTransaction.idTransaction, 'DECLINED')
                "
                :disabled="!canDecline"
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
    const notes = ref('') // Notes field
    const notesError = ref(false) // Validation state for notes

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
     * Computed property to determine if Decline button should be enabled
     */
    const canDecline = computed(() => {
      return notes.value.trim() !== ''
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
          'https://mjtrainingcamp.my.id/api/v1/transaction/get',
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        transactions.value = response.data.map(transaction => ({
          ...transaction,
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
      notes.value = '' // Clear notes when closing the modal
      notesError.value = false // Reset validation state
    }

    /**
     * Handle Decline action with validation
     * @param {Number} id - Transaction ID
     * @param {String} status - New status ('DECLINED')
     */
    const handleDecline = async (id, status) => {
      if (notes.value.trim() === '') {
        notesError.value = true
        return
      }

      notesError.value = false // Reset error state

      // Proceed to update transaction status
      await updateTransactionStatus(id, status, notes.value)
    }

    /**
     * Update transaction status (VERIFIED or DECLINED)
     * @param {Number} id - Transaction ID
     * @param {String} status - New status
     * @param {String} [notesContent] - Notes from the input field (optional for ACCEPT)
     */
    const updateTransactionStatus = async (id, status, notesContent = '') => {
      try {
        const token = localStorage.getItem('token')
        await axios.patch(
          `https://mjtrainingcamp.my.id/api/v1/transaction/update/${id}`,
          { transactionStatus: status, notes: notesContent }, // Include notes if provided
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
        // Optionally, you can refetch transaction details if needed
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to update transaction status.', 'error')
      }
    }

    /**
     * Handle image load errors by setting a fallback image
     * @param {Event} event - Image load error event
     */
    const handleImageError = event => {
      event.target.src = 'assets/default-image.png' // Replace with your actual image path
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
      handleDecline,
      statusClass,
      toggleSort,
      debouncedToggleSort,
      formatPrice,
      formatPaymentMethod,
      notes, // Return notes to be used in the template
      notesError, // Return validation state
      canDecline, // Return computed property
      handleImageError, // Return the error handler
    }
  },
}
</script>
