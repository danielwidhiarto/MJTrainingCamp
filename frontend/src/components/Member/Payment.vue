<template>
  <div>
    <LazyNavbar />
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-10 col-lg-8">
          <div class="card shadow-lg p-5 payment-card">
            <h1 class="text-center mb-4 page-title">Complete Your Payment</h1>

            <div v-if="packageDetails">
              <h2 class="text-center text-accent mb-3">Package Overview</h2>
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
              <div class="d-flex justify-content-around my-3 payment-methods">
                <div class="form-check">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="paymentMethod"
                    id="qris"
                    value="QRIS"
                    v-model="selectedPaymentMethod"
                    aria-labelledby="qris-label"
                  />
                  <label class="form-check-label" for="qris" id="qris-label"
                    >QRIS</label
                  >
                </div>
                <div class="form-check">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="paymentMethod"
                    id="bank"
                    value="Bank Transfer"
                    v-model="selectedPaymentMethod"
                    aria-labelledby="bank-label"
                  />
                  <label class="form-check-label" for="bank" id="bank-label"
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
                  class="img-fluid qris-img"
                  loading="lazy"
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
                  aria-label="Upload Payment Proof"
                  accept="image/*"
                />
              </div>

              <!-- Image Preview Section -->
              <div
                v-if="paymentProofPreview"
                class="image-preview mt-3 text-center"
              >
                <h5>Preview of Uploaded Payment Proof:</h5>
                <img
                  :src="paymentProofPreview"
                  alt="Payment Proof Preview"
                  class="img-fluid preview-img"
                />
              </div>
              <!-- End of Image Preview Section -->

              <div
                class="d-flex justify-content-between align-items-center mt-4"
              >
                <h4>Total Payment: Rp {{ packageDetails.price }}</h4>
                <button
                  class="btn btn-success proceed-button"
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
import axios from 'axios'
import { onMounted, ref, defineAsyncComponent, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import dayjs from 'dayjs'

const LazyNavbar = defineAsyncComponent(() => import('./Navbar.vue'))

export default {
  name: 'PaymentPage',
  components: { LazyNavbar },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const packageDetails = ref(null)
    const selectedPaymentMethod = ref('')
    const paymentProof = ref(null)
    const paymentProofPreview = ref(null) // New Ref for Image Preview
    const startDate = ref('')
    const endDate = ref('')

    let previousObjectURL = null // To keep track of the previous object URL

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
        // Revoke the previous object URL if it exists
        if (previousObjectURL) {
          URL.revokeObjectURL(previousObjectURL)
        }

        paymentProof.value = file
        paymentProofPreview.value = URL.createObjectURL(file)
        previousObjectURL = paymentProofPreview.value

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
      fetchPackageDetails()
    })

    onBeforeUnmount(() => {
      // Revoke the object URL to free up memory
      if (previousObjectURL) {
        URL.revokeObjectURL(previousObjectURL)
      }
    })

    return {
      packageDetails,
      selectedPaymentMethod,
      paymentProof,
      paymentProofPreview, // Expose the preview ref
      startDate,
      endDate,
      handleFileUpload,
      processPayment,
    }
  },
}
</script>

<style scoped>
/* General container styling */
.container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: auto;
  background-color: #f8f9fa;
  text-align: center;
}

/* Card styling */
.payment-card {
  border-radius: 16px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* Page title */
.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #000;
  margin-bottom: 20px;
}

/* Payment method styling */
.payment-methods .form-check-input {
  width: 20px;
  height: 20px;
}

.payment-methods label {
  font-size: 1.2rem;
  margin-left: 5px;
}

/* QRIS Image */
.qris-img {
  max-width: 300px;
  margin-top: 10px;
}

/* Preview Image */
.preview-img {
  max-width: 100%;
  height: auto;
  border: 2px solid #ddd;
  border-radius: 8px;
  padding: 5px;
}

/* Proceed button styling */
.proceed-button {
  background-color: #ff4500; /* Orange-red button color */
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1.2rem;
  transition: background-color 0.3s ease;
  cursor: pointer;
}

.proceed-button:hover {
  background-color: #e03b00; /* Darker shade on hover */
}

.proceed-button:disabled {
  background-color: #ccc; /* Disabled button color */
  cursor: not-allowed;
}

/* Input file styling */
.form-control[type='file'] {
  border: 2px solid #ddd;
  border-radius: 8px;
  padding: 10px;
  font-size: 1rem;
  color: #555;
  margin-top: 10px;
  transition: border-color 0.3s;
}

.form-control[type='file']:hover {
  border-color: #ff4500; /* Highlight border on hover */
}

/* Total Payment */
h4 {
  font-size: 1.5rem;
  color: #000;
  font-weight: bold;
}

/* Image Preview Section */
.image-preview {
  margin-top: 20px;
}

.image-preview h5 {
  margin-bottom: 10px;
  color: #333;
}

.preview-img {
  max-width: 100%;
  height: auto;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 5px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .payment-card {
    padding: 20px;
  }

  .page-title {
    font-size: 2rem;
  }

  .proceed-button {
    padding: 10px 20px;
    font-size: 1rem;
  }

  .payment-methods label {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .qris-img {
    max-width: 250px;
  }

  .payment-card {
    padding: 15px;
  }

  .page-title {
    font-size: 1.8rem;
  }

  .proceed-button {
    padding: 8px 18px;
    font-size: 1rem;
  }

  h4 {
    font-size: 1.2rem;
  }
}
</style>
