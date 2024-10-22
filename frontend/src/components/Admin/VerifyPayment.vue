<template>
  <div>
    <Navbar />
    <div class="container mt-4">
      <h1 class="text-center mb-4">Manage Member Transactions</h1>

      <div class="card shadow-sm mb-4">
        <div
          class="card-header bg-primary text-white d-flex justify-content-between align-items-center"
        >
          <span>Submitted Transactions</span>
          <button class="btn btn-secondary btn-sm" @click="toggleSort">
            {{ showPendingOnly ? 'Show All' : 'Show Pending Only' }}
          </button>
        </div>
        <div class="card-body">
          <table
            class="table table-striped"
            v-if="filteredTransactions.length > 0"
          >
            <thead>
              <tr>
                <th>TransactionID</th>
                <th>Package Type</th>
                <th>Payment Method</th>
                <th>Price</th>
                <th>Duration (months)</th>
                <th>Payment Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(transaction, index) in filteredTransactions"
                :key="transaction.id"
                :class="statusClass(transaction.paymentStatus)"
              >
                <td>{{ transaction.idTransaction }}</td>
                <td>{{ transaction.paymentType }}</td>
                <td>{{ transaction.paymentMethod }}</td>
                <td>Rp. {{ transaction.transactionPrice }}</td>
                <td>{{ transaction.duration }}</td>
                <td>{{ transaction.paymentStatus }}</td>
                <td>
                  <button
                    v-if="!isVerifiedOrDeclined(transaction.paymentStatus)"
                    class="btn btn-info btn-sm"
                    @click="showDetails(transaction)"
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
          <h5 class="text-center mb-4">Transaction Details</h5>
          <p>
            <strong>Member Name:</strong> {{ selectedTransaction.memberName }}
          </p>
          <p>
            <strong>Package Name:</strong> {{ selectedTransaction.packageName }}
          </p>
          <p>
            <strong>Price:</strong> Rp
            {{ selectedTransaction.transactionPrice }}
          </p>
          <p>
            <strong>Duration:</strong> {{ selectedTransaction.duration }} months
          </p>
          <p>
            <strong>Payment Method:</strong>
            {{ selectedTransaction.paymentMethod }}
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

          <div
            v-if="!isVerifiedOrDeclined(selectedTransaction.paymentStatus)"
            class="mt-4 d-flex justify-content-between"
          >
            <button
              class="btn btn-success"
              @click="updateTransactionStatus('VERIFIED')"
            >
              Accept
            </button>
            <button
              class="btn btn-danger"
              @click="updateTransactionStatus('DECLINED')"
            >
              Decline
            </button>
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

export default {
  name: 'VerifyPayment',
  components: { Navbar },
  data() {
    return {
      transactions: [],
      selectedTransaction: {},
      isModalOpen: false,
      showPendingOnly: true, // Control for showing only pending transactions
    }
  },
  mounted() {
    this.fetchTransactions() // Load transactions when the component mounts
  },
  computed: {
    filteredTransactions() {
      return this.showPendingOnly
        ? this.transactions.filter(
            t =>
              t.paymentStatus !== 'VERIFIED' && t.paymentStatus !== 'DECLINED',
          )
        : this.transactions
    },
  },
  methods: {
    async fetchTransactions() {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(
          'http://localhost:8081/api/v1/transaction/get',
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        )
        this.transactions = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to load transactions.', 'error')
      }
    },
    showDetails(transaction) {
      this.selectedTransaction = transaction
      this.isModalOpen = true
    },
    closeModal() {
      this.isModalOpen = false
    },
    async updateTransactionStatus(status) {
      try {
        const token = localStorage.getItem('token')
        await axios.patch(
          `http://localhost:8081/api/v1/transaction/update/${this.selectedTransaction.idTransaction}`,
          { transactionStatus: status },
          {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          },
        )
        Swal.fire('Success', `Transaction ${status} successfully.`, 'success')
        this.closeModal()
        await this.fetchTransactions() // Refresh the list after updating
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to update transaction status.', 'error')
      }
    },
    isVerifiedOrDeclined(status) {
      return status === 'VERIFIED' || status === 'DECLINED'
    },
    statusClass(status) {
      if (status === 'VERIFIED') return 'table-success'
      if (status === 'DECLINED') return 'table-danger'
      return 'table-warning'
    },
    toggleSort() {
      this.showPendingOnly = !this.showPendingOnly
    },
  },
}
</script>

<style>
.container {
  max-width: 1200px;
  margin: auto;
}

.card {
  border-radius: 8px;
  margin-bottom: 20px;
}

.table {
  margin-top: 10px;
}

.table-success {
  background-color: #d4edda !important;
}

.table-danger {
  background-color: #f8d7da !important;
}

.table-warning {
  background-color: #fff3cd !important;
}

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
  padding: 20px;
  border-radius: 8px;
  width: 50%;
  max-width: 600px;
  position: relative;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.btn-close {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 1.5rem;
  border: none;
  background: none;
  cursor: pointer;
}
</style>
