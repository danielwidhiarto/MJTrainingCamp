<template>
  <div>
    <Navbar />
    <div class="container my-5">
      <h1 class="text-center mb-4">Payment Page</h1>
      <p class="text-center">Welcome to your Payment Page!</p>

      <div v-if="packageDetails" class="card shadow p-4">
        <h2 class="text-center mb-4">Package Details</h2>
        <div class="mb-3">
          <strong>Package Name:</strong> {{ packageDetails.packageName }}
        </div>
        <div class="mb-3">
          <strong>Price:</strong> Rp {{ packageDetails.price }}
        </div>
        <div class="mb-3">
          <strong>Description:</strong> {{ packageDetails.description }}
        </div>
        <hr />

        <h4 class="mt-4">Select Payment Method</h4>
        <div class="form-check">
          <input
            class="form-check-input"
            type="radio"
            name="paymentMethod"
            id="qris"
            value="QRIS"
            v-model="selectedPaymentMethod"
          />
          <label class="form-check-label" for="qris">QRIS</label>
        </div>
        <div class="form-check">
          <input
            class="form-check-input"
            type="radio"
            name="paymentMethod"
            id="bank"
            value="Bank Transfer"
            v-model="selectedPaymentMethod"
          />
          <label class="form-check-label" for="bank">Bank Transfer</label>
        </div>

        <div v-if="selectedPaymentMethod === 'QRIS'" class="text-center mt-4">
          <h5>Scan QR Code to Pay</h5>
          <img
            src="../../assets/qris.png"
            alt="QR Code"
            class="img-fluid"
            style="max-width: 300px"
          />
        </div>

        <div v-if="selectedPaymentMethod === 'Bank Transfer'" class="mt-4">
          <h5>Bank Transfer Details</h5>
          <p><strong>Bank:</strong> BCA</p>
          <p><strong>Account Number:</strong> 0661749406</p>
          <p><strong>Account Name:</strong> Your Company Name</p>
        </div>

        <hr />

        <h4 class="mt-4">Upload Payment Proof</h4>
        <div class="form-group">
          <input type="file" class="form-control" @change="handleFileUpload" />
        </div>

        <div class="d-flex justify-content-between align-items-center mt-4">
          <h4>Total Payment: Rp {{ packageDetails.price }}</h4>
          <button
            class="btn btn-primary"
            :disabled="!paymentProof"
            @click="processPayment"
          >
            Proceed to Payment
          </button>
        </div>
      </div>

      <div v-else class="text-center mt-5">
        <p>Loading package details...</p>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from './Navbar.vue'
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'

export default {
  name: 'PaymentPage',
  components: { Navbar },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const packageDetails = ref(null)
    const selectedPaymentMethod = ref('')
    const paymentProof = ref(null)

    const fetchPackageDetails = async () => {
      const idPackage = route.params.idPackage
      const token = localStorage.getItem('token')

      if (!token) {
        Swal.fire(
          'Error',
          'Authentication token is missing. Please log in again.',
          'error',
        )
        router.push({ name: 'Login' })
        return
      }

      try {
        const response = await axios.get(
          `http://localhost:8081/api/v1/package/get?id=${idPackage}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        )
        packageDetails.value = response.data
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to load package details.', 'error')
      }
    }

    const handleFileUpload = event => {
      const file = event.target.files[0]
      if (file) {
        paymentProof.value = file
        Swal.fire(
          'File Uploaded',
          'Payment proof uploaded successfully.',
          'success',
        )
      }
    }

    const processPayment = () => {
      if (!selectedPaymentMethod.value) {
        Swal.fire('Error', 'Please select a payment method.', 'error')
        return
      }

      const formData = new FormData()
      formData.append('paymentProof', paymentProof.value)
      formData.append('paymentMethod', selectedPaymentMethod.value)

      // Simulate sending payment proof to backend
      axios
        .post('http://localhost:8081/api/v1/payment/submit', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        })
        .then(() => {
          Swal.fire('Payment', 'Payment processing completed.', 'success')
        })
        .catch(error => {
          console.error(error)
          Swal.fire('Error', 'Failed to process payment.', 'error')
        })
    }

    onMounted(fetchPackageDetails)

    return {
      packageDetails,
      selectedPaymentMethod,
      paymentProof,
      handleFileUpload,
      processPayment,
    }
  },
}
</script>

<style>
.container {
  max-width: 600px;
  margin: auto;
}
.card {
  border-radius: 15px;
}
</style>
