<template>
  <div>
    <Navbar />
    <div class="container-fluid py-5 bg-light">
      <div class="row justify-content-center">
        <div class="col-md-10 col-lg-8">
          <div class="card shadow-lg p-5">
            <h1 class="text-center mb-4">Complete Your Payment</h1>

            <div v-if="packageDetails">
              <h2 class="text-center text-primary mb-3">Package Overview</h2>
              <div class="row mb-4">
                <div class="col-md-6">
                  <p>
                    <strong>Package Name:</strong>
                    {{ packageDetails.packageName }}
                  </p>
                </div>
                <div class="col-md-6">
                  <p><strong>Price:</strong> Rp {{ packageDetails.price }}</p>
                </div>
              </div>

              <div class="row mb-4">
                <div class="col-md-12">
                  <p>
                    <strong>Description:</strong>
                    {{ packageDetails.description }}
                  </p>
                </div>
              </div>

              <div v-if="packageDetails.type === 'Membership'" class="row mb-4">
                <div class="col-md-4">
                  <p>
                    <strong>Duration:</strong>
                    {{ packageDetails.duration }} month(s)
                  </p>
                </div>
                <div class="col-md-4">
                  <p><strong>Start Date:</strong> {{ startDate }}</p>
                </div>
                <div class="col-md-4">
                  <p><strong>End Date:</strong> {{ endDate }}</p>
                </div>
              </div>

              <hr class="my-4" />

              <h4 class="text-center mt-4">Select Payment Method</h4>
              <div class="d-flex justify-content-around my-3">
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
                  <label class="form-check-label" for="bank"
                    >Bank Transfer</label
                  >
                </div>
              </div>

              <div
                v-if="selectedPaymentMethod === 'QRIS'"
                class="text-center mt-3"
              >
                <h5>Scan QR Code to Pay</h5>
                <img
                  src="../../assets/qris.png"
                  alt="QR Code"
                  class="img-fluid"
                  style="max-width: 300px"
                />
              </div>

              <div
                v-if="selectedPaymentMethod === 'Bank Transfer'"
                class="mt-4"
              >
                <h5>Bank Transfer Details</h5>
                <p><strong>Bank:</strong> BCA</p>
                <p><strong>Account Number:</strong> 0661749406</p>
                <p><strong>Account Name:</strong> Your Company Name</p>
              </div>

              <hr class="my-4" />

              <h4 class="text-center mt-4">Upload Payment Proof</h4>
              <div class="form-group">
                <input
                  type="file"
                  class="form-control"
                  @change="handleFileUpload"
                />
              </div>

              <div
                class="d-flex justify-content-between align-items-center mt-4"
              >
                <h4>Total Payment: Rp {{ packageDetails.price }}</h4>
                <button
                  class="btn btn-success"
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
import dayjs from 'dayjs'

export default {
  name: 'PaymentPage',
  components: { Navbar },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const packageDetails = ref(null)
    const selectedPaymentMethod = ref('')
    const paymentProof = ref(null)
    const startDate = ref('')
    const endDate = ref('')

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
          { headers: { Authorization: `Bearer ${token}` } },
        )
        packageDetails.value = response.data

        if (packageDetails.value.type === 'Membership') {
          const { startDate: start, endDate: end } = calculateDates(
            packageDetails.value.duration,
          )
          startDate.value = start
          endDate.value = end
        }
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to load package details.', 'error')
      }
    }

    const calculateDates = duration => {
      const start = dayjs().format('YYYY-MM-DD')
      const end = dayjs().add(duration, 'month').format('YYYY-MM-DD')
      return { startDate: start, endDate: end }
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

    const processPayment = async () => {
      if (!selectedPaymentMethod.value) {
        Swal.fire('Error', 'Please select a payment method.', 'error')
        return
      }

      const token = localStorage.getItem('token')
      const formData = new FormData()
      formData.append('buktiTransfer', paymentProof.value)
      formData.append('paymentMethod', selectedPaymentMethod.value)

      if (packageDetails.value.type === 'Membership') {
        formData.append('startDate', startDate.value)
        formData.append('endDate', endDate.value)
        formData.append('duration', packageDetails.value.duration)
      }

      formData.append('price', packageDetails.value.price)
      formData.append('transactionPrice', packageDetails.value.price)
      formData.append('token', token)

      try {
        await axios.post(
          'http://localhost:8081/api/v1/membership/buy',
          formData,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'multipart/form-data',
            },
          },
        )
        Swal.fire('Payment', 'Payment completed.', 'success').then(() => {
          router.push({ name: 'MemberDashboard' })
        })
      } catch (error) {
        console.error(error)
        Swal.fire('Error', 'Failed to process payment.', 'error')
      }
    }

    onMounted(() => {
      if (!localStorage.getItem('refreshed')) {
        localStorage.setItem('refreshed', 'true')
        window.location.reload()
      }
      fetchPackageDetails()
    })

    return {
      packageDetails,
      selectedPaymentMethod,
      paymentProof,
      startDate,
      endDate,
      handleFileUpload,
      processPayment,
    }
  },
}
</script>

<style>
.container-fluid {
  background-color: #f8f9fa;
  min-height: 100vh;
}
.card {
  border-radius: 15px;
}
</style>
